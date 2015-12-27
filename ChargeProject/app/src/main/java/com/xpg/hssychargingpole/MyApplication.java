package com.xpg.hssychargingpole;

import android.app.Application;
import android.view.Gravity;

import com.baidu.mapapi.SDKInitializer;
import com.easy.activity.EasyActivity;
import com.easy.util.AppInfo;
import com.easy.util.LogUtil;
import com.easy.util.NetWorkUtil;
import com.easy.util.SPFile;
import com.easy.util.SystemInfo;
import com.easy.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

import cn.beecloud.BeeCloud;
import cn.jpush.android.api.JPushInterface;

/**
 * MyApplication
 *
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年7月4日
 */

public class MyApplication extends Application implements
        UncaughtExceptionHandler {
    private static MyApplication instance;

    // private BleService mService;
    // private IBle mBle;

    private UncaughtExceptionHandler mUncaughtExceptionHandler;

    private boolean isShowingForceUpdateDialog = false;
    private boolean isFirstUpdated = false;
    private SPFile sp;

    private Map<Integer,String> operatorMap;
    private Map<Integer,String> pileTypeMap;
    private Map<Integer,String> pileStationTypeMap;

    // private final ServiceConnection mServiceConnection = new
    // ServiceConnection() {
    // @Override
    // public void onServiceConnected(ComponentName className,
    // IBinder rawBinder) {
    // mService = ((BleService.LocalBinder) rawBinder).getService();
    // mBle = mService.getBle();
    // if (mBle != null && !mBle.adapterEnabled()) {
    // // TODO: enalbe adapter
    // Log.e("-----", "----mBle is null");
    // }else{
    // Log.e("-----", "----mBle is not null");
    // }
    // }
    //
    // @Override
    // public void onServiceDisconnected(ComponentName classname) {
    // mService = null;
    // }
    // };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sp = new SPFile(this, "config");
        // 百度地图在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        // Jpush init
        JPushInterface.init(getApplicationContext());
        // 加载地区信息表
        LocationInfos.getInstance().init(this);
        // 初始化百度定位服务
        LbsManager.getInstance().init(this);
        // 初始化网络请求服务
        String mac = NetWorkUtil.getMacAddress(this);
        WebAPIManager.getInstance().setClientId(
                mac == null ? "00:00:00:00" : mac);
        WebAPIManager.getInstance().setAppVersion(AppInfo.getVersionName(this));
        // 设置全局Toast提示
        ToastUtil.setGravity(Gravity.CENTER, 0, 0);
        // 初始化友盟
        UmengUpdateAgent.setUpdateAutoPopup(false);
        MobclickAgent.setCatchUncaughtExceptions(false);
        // 捕获全局异常
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        // Intent bindIntent = new Intent(this, BleService.class);
        // bindService(bindIntent, mServiceConnection,
        // Context.BIND_AUTO_CREATE);
        // 设置退出提示语
        EasyActivity.setExitTips(R.string.exit_tips);

        ImageLoaderManager.getInstance().init(getApplicationContext());

        // 判断版本是否更新
        int oldVersion = sp.getInt(KEY.CONFIG.APP_VERSION, -1);
//        boolean cityLoaded = sp.getBoolean(KEY.CONFIG.CITY_LOADED, false);
        if (BuildConfig.VERSION_CODE > oldVersion) {
            sp.put(KEY.CONFIG.APP_VERSION, BuildConfig.VERSION_CODE);
            sp.put(KEY.CONFIG.CITY_LOADED, false);
            new Thread() {
                public void run() {
                    DbHelper.getInstance(getApplicationContext())
                            .getDistrictDataDao().deleteAll();
                    DbHelper.getInstance(getApplicationContext())
                            .initDistrictData();
                    sp.put(KEY.CONFIG.CITY_LOADED, true);
                }
            }.start();
        }

        WebAPIManager.getInstance().getOperatorMap(new WebResponseHandler<Map<Integer, String>>() {
            @Override
            public void onSuccess(WebResponse<Map<Integer, String>> response) {
                super.onSuccess(response);
                if (response.getResultObj() != null) {
                    operatorMap = response.getResultObj();
                }
            }

            @Override
            public void onFailure(WebResponse<Map<Integer, String>> response) {
                super.onFailure(response);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
        WebAPIManager.getInstance().getPileTypeMap(new WebResponseHandler<Map<Integer, String>>() {
            @Override
            public void onSuccess(WebResponse<Map<Integer, String>> response) {
                super.onSuccess(response);
                if (response.getResultObj() != null) {
                    pileTypeMap = response.getResultObj();
                }
            }

            @Override
            public void onFailure(WebResponse<Map<Integer, String>> response) {
                super.onFailure(response);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
        WebAPIManager.getInstance().getPileStationTypeMap(new WebResponseHandler<Map<Integer, String>>() {
            @Override
            public void onSuccess(WebResponse<Map<Integer, String>> response) {
                super.onSuccess(response);
                if (response.getResultObj() != null) {
                    pileStationTypeMap = response.getResultObj();
                }
            }

            @Override
            public void onFailure(WebResponse<Map<Integer, String>> response) {
                super.onFailure(response);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        LogUtils.e("","appid:"+BuildConfig.BEECLOUD_APPID + "   secretid:"+ BuildConfig.BEECLOUD_SECRET);
        BeeCloud.setAppIdAndSecret(BuildConfig.BEECLOUD_APPID, BuildConfig.BEECLOUD_SECRET);
//        BeeCloud.setAppIdAndSecret("49552fd4-9018-429a-8432-579e553bcde4", "948f3448-8f4a-4b52-8be1-77626aa6197b");
        //for test
//        BeeCloud.setAppIdAndSecret("c5d1cba1-5e3f-4ba0-941d-9b0a371fe719", "39a7a518-9ac8-4a9e-87bc-7885f33cf18c");

    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Device Model: " + SystemInfo.getModel() + "\n");
            sb.append("OS: " + SystemInfo.getOS() + "\n");
            sb.append("App Code: " + AppInfo.getVersionCode(this) + "\n");
            sb.append("App Name: " + AppInfo.getVersionName(this) + "\n");
            sb.append("Error By: " + ex.getClass().getName() + ":"
                    + ex.getMessage() + "\n");
            sb.append("Error Detail: ");
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append(ste + "\n");
            }
            Throwable th = ex.getCause();
            if (th != null) {
                sb.append("Caused By: " + th.getClass().getName() + ":"
                        + th.getMessage() + "\n");
                sb.append("Caused Detail: ");
                for (StackTraceElement ste : th.getStackTrace()) {
                    sb.append(ste + "\n");
                }
            }
            LogUtil.log2File(MyConstant.PATH + "/log_crash", sb.toString());
            // umeng
            MobclickAgent.reportError(this, sb.toString());
        }

        exit();

        mUncaughtExceptionHandler.uncaughtException(thread, ex);

    }

    // public IBle getIBle() {
    // return mBle;
    // }

    public static MyApplication getInstance() {
        return instance;
    }

    public void exit() {
        isFirstUpdated = false;
        isShowingForceUpdateDialog = false;
        BTManager.getInstance().disconnect();
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(0);
        System.gc();
    }

    @Override
    public void onTerminate() {
        LogUtil.e("app", "onTerminate!!!!!!!!!!!!!!!");
        super.onTerminate();
    }

    public boolean isShowingForceUpdateDialog() {
        return isShowingForceUpdateDialog;
    }

    public void setShowingForceUpdateDialog(boolean isShowingForceUpdateDialog) {
        this.isShowingForceUpdateDialog = isShowingForceUpdateDialog;
    }

    public boolean isFirstUpdated() {
        return isFirstUpdated;
    }

    public void setFirstUpdated(boolean isFirstUpdated) {
        this.isFirstUpdated = isFirstUpdated;
    }


    public Map<Integer, String> getOperatorMap() {
        return operatorMap;
    }

	public String getPileTypeByKey(Integer key) {
		String type = "";
		if (pileTypeMap != null) {
			type = pileTypeMap.get(key);
		}
		type = type == null ? getString(R.string.pile_type_other) : type;
		return type;
	}
	public String getPileStationTypeByKey(Integer key) {
		String type = "";
		if (pileStationTypeMap != null) {
			type = pileStationTypeMap.get(key);
		}
		type = type == null ? getString(R.string.pile_type_other) : type;
		return type;
	}

    public void setOperatorMap(Map<Integer, String> operatorMap) {
        this.operatorMap = operatorMap;
    }

    public Map<Integer, String> getPileTypeMap() {
        return pileTypeMap;
    }

    public void setPileTypeMap(Map<Integer, String> pileTypeMap) {
        this.pileTypeMap = pileTypeMap;
    }

    public Map<Integer, String> getPileStationTypeMap() {
        return pileStationTypeMap;
    }

    public void setPileStationTypeMap(Map<Integer, String> pileStationTypeMap) {
        this.pileStationTypeMap = pileStationTypeMap;
    }
}
