package com.xpg.hssy.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.easy.util.SPFile;
import com.xpg.hssy.cmdparse.DataUtil;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.engine.CmdManager;
import com.xpg.hssy.engine.CmdManager.OnCmdListener;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年2月6日
 */

public class ChargeRecordSyncService extends Service {
	private static final String tag = ChargeRecordSyncService.class.getSimpleName();
	private static final int PERIOD_TIME = 60000;
	public static final int MODE_DOWNLOAD_AND_UPLOAD = 1;
	public static final int MODE_UPLOAD_ALL = 2;
	public static final int MODE_UPLOAD_NEW = 3;

	public static final String RECEIVE_ACTION_IMMEDIATELE_SYN_RECORD_CACHE = "com.xpg.hssy.service.ChargeRecordSyncService.immediately.syn.recordCache";
	public static final String KEY_PER_SYN_RECORD_CACHE = "per_syn_record_cache";

	private String userId;
	private String pileId;
	private boolean acqedState;
	//	private boolean confirming;
	private boolean uploading;
	//	//	private boolean downloading;
//	private boolean isRegisted;
	private int tv_electric_serialNo;
	private Timer uploadTimer;
	private ThreadPoolExecutor singleThreadPoole;
	private WebResponseHandler<List<ChargeRecord>> uploadHandler;
	private RecordSynRecevier recordSynRecevier;

	// 桩内当前记录的index
	private Integer currentIndex;
	// 桩内总的记录条数
	private Integer count;

	private Set<ChargeRecordCache> perUploadChargeRecordCache;
	private Queue<Integer> perAcqHistoryIdQueue;
	private Queue<ChargeRecord> perConfirmRecordQueue;
	private AcqHistoryByIdThread acqHistoryByIdThread;
	private ConfirmHistoryThread confirmHistoryThread;
	private OnSynCmdListener onSynCmdListener = new OnSynCmdListener();

	private void uploadRecord() {
		Log.e("record", "start upload");
		List<ChargeRecordCache> crcs = DbHelper.getInstance(ChargeRecordSyncService.this).getChargeRecordCacheDao().loadAll();
		if (perUploadChargeRecordCache != null) {
			synchronized (perUploadChargeRecordCache) {
				perUploadChargeRecordCache.removeAll(crcs);
				for (ChargeRecordCache cache : perUploadChargeRecordCache) {
					Log.e("record", "sequence: " + cache.getSequence());
					DbHelper.getInstance(ChargeRecordSyncService.this).getChargeRecordCacheDao().insert(cache);
				}
				perUploadChargeRecordCache.clear();
			}
		}
		crcs = DbHelper.getInstance(ChargeRecordSyncService.this).getChargeRecordCacheDao().loadAll();
		if (crcs != null && crcs.size() > 0) {
			List<ChargeRecordCache.RecordData> recordDatas = new ArrayList<>();
			for (ChargeRecordCache cache : crcs) {
				Log.e("record", "sequence: " + cache.getSequence() + " data: " + cache.getData());
				recordDatas.add(cache.createRecordForUpload());
			}
			WebAPIManager.getInstance().uploadChargeRecord(recordDatas, uploadHandler);
		}
	}

	class UploadHandler extends WebResponseHandler<List<ChargeRecord>> {
		UploadHandler(String tag) {
			super(tag);
		}

		@Override
		public void onStart() {
			super.onStart();
			uploading = true;
		}

		@Override
		public void onFinish() {
			super.onFinish();
			uploading = false;
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
//			delayUpload(PERIOD_TIME);
			Intent intent = new Intent("data.broadcast.actionForChargePrice");
			intent.putExtra("success", false);
			sendBroadcast(intent);
		}

		@Override
		public void onFailure(WebResponse<List<ChargeRecord>> response) {
			super.onFailure(response);
//			delayUpload(PERIOD_TIME);
			Intent intent = new Intent("data.broadcast.actionForChargePrice");
			intent.putExtra("success", false);
			sendBroadcast(intent);
		}

		@Override
		public void onSuccess(WebResponse<List<ChargeRecord>> response) {
			super.onSuccess(response);
			Log.v("upload", "upload onSuccess");
			// 上传成功，清空未上传记录表
			DbHelper.getInstance(ChargeRecordSyncService.this).getChargeRecordCacheDao().deleteAll();
			// 过滤有效的记录
			if (response.getResultObj() != null) {
				List<ChargeRecord> crs = new ArrayList<ChargeRecord>();
				for (ChargeRecord cr : response.getResultObj()) {
					Log.e("record", "上传成功的记录" + cr.getSequence());
					Log.e("record", "记录pileId: " + cr.getPileId());
					Log.e("record", "本地pileId: " + pileId);
					if (cr.getReceipt() != null && !cr.getReceipt().equals("") && cr.getPileId().equals(pileId)) {
						crs.add(cr);
						if (tv_electric_serialNo == cr.getSequence()) {
							Intent intent = new Intent("data.broadcast.actionForChargePrice");
							String chargePrice = "0.00";
							if (cr.getChargePrice() != null) {
								chargePrice = cr.getChargePrice().toString();
							}
							intent.putExtra(KEY.INTENT.CHARGE_PRICE, chargePrice);
							intent.putExtra("success", true);
							sendBroadcast(intent);
						}
					} else {
						Log.e("record", "未同步到电桩的充电记录" + cr.getSequence());
					}
				}

				if (perConfirmRecordQueue != null) {
					for (ChargeRecord record : crs) {
						Log.e("record", "receipt: " + record.getReceipt());
						if (record.getReceipt() != null) {
							perConfirmRecordQueue.add(record);
						}
					}
					synchronized (perConfirmRecordQueue) {
						perConfirmRecordQueue.notify();
					}
				}
			}
		}
	}

	class ImmediatelyUploadHandler extends WebResponseHandler<List<ChargeRecord>> {
		ChargeRecordCache recordCache;

		ImmediatelyUploadHandler(String tag, ChargeRecordCache recordCache) {
			super(tag);
			this.recordCache = recordCache;
		}

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onFinish() {
			super.onFinish();
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
			Intent intent = new Intent("data.broadcast.actionForChargePrice");
			intent.putExtra("success", false);
			sendBroadcast(intent);
		}

		@Override
		public void onFailure(WebResponse<List<ChargeRecord>> response) {
			super.onFailure(response);
			Intent intent = new Intent("data.broadcast.actionForChargePrice");
			intent.putExtra("success", false);
			sendBroadcast(intent);
		}

		@Override
		public void onSuccess(WebResponse<List<ChargeRecord>> response) {
			super.onSuccess(response);
			Log.v("upload", "upload onSuccess");
			// 过滤有效的记录
			singleThreadPoole.execute(new Runnable() {
				@Override
				public void run() {
					synchronized (perUploadChargeRecordCache) {
						boolean success = perUploadChargeRecordCache.remove(recordCache);
						Log.e("record", "remove record cache " + success);
					}
				}
			});
			if (response.getResultObj() != null) {
				List<ChargeRecord> crs = response.getResultObj();
				ChargeRecord record = crs.get(0);
				Log.e("record", "上传成功的记录" + record.getSequence());
				if (record.getReceipt() != null && !record.getReceipt().equals("") && record.getPileId().equals(pileId)) {
					if (tv_electric_serialNo == record.getSequence()) {
						Intent intent = new Intent("data.broadcast.actionForChargePrice");
						String chargePrice = "0.00";
						if (record.getChargePrice() != null) {
							chargePrice = record.getChargePrice().toString();
						}
						intent.putExtra(KEY.INTENT.CHARGE_PRICE, chargePrice);
						intent.putExtra("success", true);
						sendBroadcast(intent);
					}
				}
				Log.e("record", "receipt: " + record.getReceipt());

				String orderId = record.getOrderId();
				ChargeOrder chargeOrder = DbHelper.getInstance(ChargeRecordSyncService.this).getChargeOrderDao().load(orderId);
				if (chargeOrder != null) {
					Log.i("record", " chargeOrderID" + chargeOrder.getOrderId());
					Log.i("record", " getUserid" + chargeOrder.getUserid());
				}
				// TODO 很有可能存在脏数据以及硬件问题，导致主人桩或家人桩也有订单号，但是正确的订单号一定是10开头
				if (chargeOrder == null || !chargeOrder.getUserid().equals(userId)) {
					//主人、家人
					// 家人桩或主人桩
					Log.i("record", "  家人桩或主人桩");
				} else {
					//TODO 调用立即支付接口
					Log.e("record", "go to play");
				}
				if (perConfirmRecordQueue != null) {
					perConfirmRecordQueue.addAll(crs);
					synchronized (perConfirmRecordQueue) {
						perConfirmRecordQueue.notify();
					}
				}
			}
		}
	}

	// 监听蓝牙状态
	private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// BluetoothDevice device = intent
			// .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// 断开连接
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
				acqedState = false;
				if (acqHistoryByIdThread != null) {
					acqHistoryByIdThread.interrupt();
					acqHistoryByIdThread = null;
				}
				if (confirmHistoryThread != null) {
					confirmHistoryThread.interrupt();
					confirmHistoryThread = null;
				}
				if (perAcqHistoryIdQueue != null) {
					perAcqHistoryIdQueue.clear();
				}
				if (perConfirmRecordQueue != null) {
					perConfirmRecordQueue.clear();
				}
				pileId = null;
				Log.e("record", "ACTION_ACL_DISCONNECTED");
				// 尝试上传
//				startUpload();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(tag, "onCreate");
		recordSynRecevier = new RecordSynRecevier();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RECEIVE_ACTION_IMMEDIATELE_SYN_RECORD_CACHE);
		registerReceiver(recordSynRecevier, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("onStartCommand", "onStartCommand");
		String newUserId = new SPFile(this, "config").getString("user_id", "");
		if (!newUserId.equals(userId)) {
			// TODO 切换用户？
		}
		userId = newUserId;

		// 判断模式
		int mode = intent == null ? 0 : intent.getIntExtra("mode", 0);
		tv_electric_serialNo = intent == null ? 0 : intent.getIntExtra("tv_electric_serialNo", 0);
		Log.e("mode", "mode: " + mode);

		switch (mode) {
			case MODE_DOWNLOAD_AND_UPLOAD: {
				Log.e("onStartCommand", "upload record");
				pileId = intent == null ? null : intent.getStringExtra(KEY.INTENT.PILE_ID);
				IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
				registerReceiver(mBTBroadcastReceiver, intentFilter);
				// 监听蓝牙通信
				CmdManager.getInstance().removeOnCmdListener(onSynCmdListener);
				CmdManager.getInstance().addOnCmdListener(onSynCmdListener);
				if (perConfirmRecordQueue == null) {
					perConfirmRecordQueue = new ConcurrentLinkedQueue<>();
				}
				if (perAcqHistoryIdQueue == null) {
					perAcqHistoryIdQueue = new ConcurrentLinkedQueue<>();
				}
				if (acqHistoryByIdThread == null) {
					acqHistoryByIdThread = new AcqHistoryByIdThread();
					acqHistoryByIdThread.start();
				}
				if (confirmHistoryThread == null) {
					confirmHistoryThread = new ConfirmHistoryThread();
					confirmHistoryThread.start();
				}
				if (pileId != null) {
					CmdManager.getInstance().acqHistoryState();
				}
				break;
			}

//		case MODE_UPLOAD_ALL:
//			if (!uploading)
//				startUpload();
//			break;
//
//		case MODE_UPLOAD_NEW:
//			startUpload();
//			break;

			default:
				break;
		}

		if (perUploadChargeRecordCache == null) {
			perUploadChargeRecordCache = Collections.synchronizedSet(new HashSet<ChargeRecordCache>());
		}
		if (uploadHandler == null) {
			uploadHandler = new UploadHandler(tag);
		}

		if (singleThreadPoole == null || singleThreadPoole.isShutdown()) {
			singleThreadPoole = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}

		if (uploadTimer == null) {
			uploadTimer = new Timer();
			uploadTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Log.e("record", "start schedule");
					if (!uploading) uploadRecord();
				}
			}, 0, PERIOD_TIME);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(tag, "onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		Log.v(tag, "onDestroy");
		super.onDestroy();
		// 取消监听
		try {
			unregisterReceiver(mBTBroadcastReceiver);
		} catch (Exception e) {
		}
		unregisterReceiver(recordSynRecevier);
		CmdManager.getInstance().removeOnCmdListener(onSynCmdListener);
	}

	private void logIntent(Intent intent) {
		Log.e(tag, intent.getAction());
		String s1 = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
		Log.e(tag, "EXTRA_EXTRA_INFO: " + s1);
		String s2 = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		Log.e(tag, "EXTRA_REASON: " + s2);
	}

	private class OnSynCmdListener extends OnCmdListener {
		private final int MAX_TRY_COUNT = 3;

		private int tryCounter = 0;

		// 获取历史记录状态
		@Override
		protected void onAcqHistoryState(int currentIndex, int count, List<Integer> historyIds, int currentSerial) {
			// 如果已经获取过状态了就直接退出
			if (acqedState) {
				return;
			}
			acqedState = true;
			Log.e(tag, "onAcqHistoryState:　" + currentIndex + "," + count);
			ChargeRecordSyncService.this.currentIndex = currentIndex;
			ChargeRecordSyncService.this.count = count;
			if (null == historyIds || historyIds.isEmpty()) {
				Log.e("onAcqHistoryState", "充电记录id列表为空");
				// 桩内没记录，上传手机本地记录
//				downloading = false;
				unregisterReceiver(mBTBroadcastReceiver);
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						CmdManager.getInstance().removeOnCmdListener(onSynCmdListener);
					}
				});
				acqedState = false;
			} else {
				perAcqHistoryIdQueue.addAll(historyIds);
				for (Integer id : historyIds) {
					Log.e("onAcqHistoryState", "id: " + id);
				}
				// 获取最后一条历史记录
				synchronized (perAcqHistoryIdQueue) {
					perAcqHistoryIdQueue.notify();
				}
			}
		}

		// 获取历史记录
		@Override
		protected void onAcqHistoryById(final ChargeRecordCache record) {
			Log.e(tag, "onAcqHistoryById:　" + record.getSequence() + "," + record.getData());
			Log.e(tag, "userId:　" + record.getUserid());
			if (null != record) {
				singleThreadPoole.execute(new Runnable() {
					@Override
					public void run() {
						if (perUploadChargeRecordCache != null) {
							synchronized (perUploadChargeRecordCache) {
								perUploadChargeRecordCache.add(record);
							}
						}
					}
				});
			}
			synchronized (perAcqHistoryIdQueue) {
				perAcqHistoryIdQueue.notify();
			}
		}

		@Override
		protected void onConfirmHistoryByKey(boolean success) {
			if (success) {
				Log.e("record", "confirm success");
				tryCounter = 0;
				perConfirmRecordQueue.poll();
			} else if (tryCounter < MAX_TRY_COUNT) {
				tryCounter++;
			} else {
				Log.e("record", "confirm fail");
				tryCounter = 0;
				perConfirmRecordQueue.poll();
			}
			synchronized (perConfirmRecordQueue) {
				perConfirmRecordQueue.notify();
			}
		}
	}

	private class RecordSynRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case RECEIVE_ACTION_IMMEDIATELE_SYN_RECORD_CACHE: {
					final ChargeRecordCache recordCache = (ChargeRecordCache) intent.getSerializableExtra(KEY_PER_SYN_RECORD_CACHE);
					if (recordCache != null) {
						List<ChargeRecordCache.RecordData> recordDatas = new ArrayList<>();
						recordDatas.add(recordCache.createRecordForUpload());
						WebAPIManager.getInstance().uploadChargeRecord(recordDatas, new ImmediatelyUploadHandler(tag, recordCache));
						break;
					}
				}
			}
		}
	}

	private class AcqHistoryByIdThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted() && perAcqHistoryIdQueue != null) {
				Integer acqId = perAcqHistoryIdQueue.poll();
				if (acqId != null) CmdManager.getInstance().acqHistoryById(acqId);
				synchronized (perAcqHistoryIdQueue) {
					try {
						perAcqHistoryIdQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class ConfirmHistoryThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted() && perConfirmRecordQueue != null) {
				ChargeRecord record = perConfirmRecordQueue.peek();
				if (record != null) CmdManager.getInstance().confirmHistoryByKey(DataUtil.hexStringToBytes(record.getReceipt()));
				synchronized (perConfirmRecordQueue) {
					try {
						perConfirmRecordQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
