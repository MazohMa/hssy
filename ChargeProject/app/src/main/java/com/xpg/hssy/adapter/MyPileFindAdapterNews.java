package com.xpg.hssy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.easy.adapter.EasyAdapter;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.main.activity.PileInfoNewActivity;
import com.xpg.hssy.main.activity.PileStationInfoActivity;
import com.xpg.hssy.main.fragment.PileFindFragment;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.ExtensionUtils;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.view.CircleImageView;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * @author Mazoh
 * @version 2.3.0
 * @description
 * @create 2015年7月31日
 */

public class MyPileFindAdapterNews extends EasyAdapter<Pile> {
	private Context context;
	private double myLatitude = -1;
	private double myLongitude = -1;
	private boolean imageLoadable = true;
	private DisplayImageOptions pileDisplayoption;
	private DisplayImageOptions userAvatarDisplayoption;
	private SPFile sp;
	private boolean itemRemoveable;
	private LoadingDialog loadingDialog;

	private boolean fromActivity = false ;

	public boolean isFromActivity() {
		return fromActivity;
	}

	public void setFromActivity(boolean fromActivity) {
		this.fromActivity = fromActivity;
	}
	{
		pileDisplayoption = ImageLoaderManager.createDisplayOptionsWtichImageResurces(R.drawable.find_sanyoubg, R.drawable.find_sanyoubg, R.drawable
				.find_sanyoubg);
		userAvatarDisplayoption = ImageLoaderManager.createDisplayOptionsWtichImageResurces(R.drawable.touxiang, R.drawable.touxiang, R.drawable.touxiang);
	}

	public MyPileFindAdapterNews(Context context, List<Pile> items, boolean itemRemoveable) {
		super(context, items);
		this.context = context;
		this.itemRemoveable = itemRemoveable;
		getLocation();
		sp = new SPFile(context, "config");
	}

	public MyPileFindAdapterNews(Context context) {
		super(context);
		this.context = context;
		getLocation();
		sp = new SPFile(context, "config");
	}

	public MyPileFindAdapterNews(Context context, double myLongitude, double myLatitude) {
		super(context);
		this.context = context;
		this.myLongitude = myLongitude;
		this.myLatitude = myLatitude;
		getLocation();
		sp = new SPFile(context, "config");
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			public Pile pile;
			private ImageView iv_icon;
			private TextView tv_name;
			private TextView tv_location;
			private TextView tv_distance;
			private LinearLayout ll_price;
			private TextView tv_price;
			private Button btn_navigate;
			private EvaluateColumn eva_star_point;
			private Button btn_appoint;
			private Button btn_pile_type;
			private TextView tv_idle;
			private CircleImageView circleImageView;
			private ImageView iv_collect;
			private OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
						case R.id.btn_appoint:
							SPFile sp = new SPFile(context, "config");
							//地图中心点
							String longitude = sp.getString(KEY.INTENT.LONGITUDE, "-1");
							String latitude = sp.getString(KEY.INTENT.LATITUDE, "-1");
							Intent intentToPileInfo = null;
							if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
								if (isLogin()) {
									if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
										intentToPileInfo = new Intent(context, PileInfoNewActivity.class);
										intentToPileInfo.putExtra(KEY.INTENT.LONGITUDE, Double.valueOf(longitude));
										intentToPileInfo.putExtra(KEY.INTENT.LATITUDE, Double.valueOf(latitude));
										intentToPileInfo.putExtra(KEY.INTENT.GPRS_TYPE, pile.getGprsType());
										intentToPileInfo.putExtra(KEY.INTENT.POSITION, position);
										intentToPileInfo.putExtra(KEY.INTENT.OPERATOR, pile.getOperator());
										intentToPileInfo.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
										((Activity) context).startActivityForResult(intentToPileInfo, PileFindFragment.REQUEST_REFRESH);
									} else {
										//GPRS桩跳至扫码充电
										if (itemRemoveable) {
											if (context instanceof Activity) ((Activity) context).finish();
										}
										context.sendBroadcast(new Intent(KEY.ACTION.ACTION_IMMEDIATELY_CHARGE));
										if(isFromActivity()){
											((Activity) context).finish();
											return ;
										}else{

										}
									}
								} else {
									Intent intentLogin = new Intent(context, LoginActivity.class);
									context.startActivity(intentLogin);
								}
								return;
							} else {
								intentToPileInfo = new Intent(context, PileStationInfoActivity.class);
								intentToPileInfo.putExtra(KEY.INTENT.POSITION, position);
								intentToPileInfo.putExtra(KEY.INTENT.OPERATOR, pile.getOperator());
								intentToPileInfo.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
								((Activity) context).startActivityForResult(intentToPileInfo, PileFindFragment.REQUEST_REFRESH);
							}

							break;
						case R.id.btn_navigate:
							callNavigation(pile);
							break;
						case R.id.iv_collect:
							if (pile.getFavor() != null && pile.getFavor() == PileStation.FAVOR_YES) {
								cancelCollect();
							} else {
								collect();
							}
							break;
					}
				}
			};

			private void collect() {
				if (pile == null) {
					return;
				}

				final Pile pileTemp = pile;
				String userid = sp.getString("user_id", null);
				if (!isLogin() || userid == null) {
					Intent intentLogin = new Intent(context, LoginActivity.class);
					context.startActivity(intentLogin);
					return;
				}

				WebAPIManager.getInstance().addFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(context) {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						ToastUtil.show(context, R.string.collect_fail);
					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						ToastUtil.show(context, R.string.collect_fail);
					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);
						ToastUtil.show(context, R.string.collect_success);
						// setResult(RESULT_OK);
						pileTemp.setFavor(PileStation.FAVOR_YES);
						notifyDataSetChanged();
//						iv_ppp_favor.setImageResource(R.drawable.find_collect02);
//						isCollectedPile = true;
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (loadingDialog != null) loadingDialog.dismiss();
					}

					@Override
					public void onStart() {
						super.onStart();
						if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
						loadingDialog = new LoadingDialog(context, R.string.loading);
						loadingDialog.showDialog();
					}
				});
			}

			private void cancelCollect() {
				if (pile == null) {
					return;
				}

				final Pile pileTemp = pile;
				String userid = sp.getString("user_id", null);
				if (!isLogin() || userid == null) {
					Intent loginIntent = new Intent(context, LoginActivity.class);
					loginIntent.putExtra("isMainActivity", false);
					context.startActivity(loginIntent);
					return;
				}

				WebAPIManager.getInstance().removeFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(context) {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						ToastUtil.show(context, R.string.cancel_collect_fail);
					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						ToastUtil.show(context, R.string.cancel_collect_fail);
					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);
						ToastUtil.show(context, R.string.cancel_collect_success);
						if (itemRemoveable) {
							remove(position);
							Intent intent = new Intent(KEY.ACTION.ACTION_REFRESH_PILE_LIST);
							intent.putExtra(KEY.INTENT.IS_REFRESH, true);
							context.sendBroadcast(intent);
						} else {
							pileTemp.setFavor(PileStation.FAVOR_NOT);
							notifyDataSetChanged();
						}
//						iv_ppp_favor.setImageResource(R.drawable.find_collect03);
//						isCollectedPile = false;

						// setResult(RESULT_OK);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (loadingDialog != null) loadingDialog.dismiss();
					}

					@Override
					public void onStart() {
						super.onStart();
						if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
						loadingDialog = new LoadingDialog(context, R.string.loading);
						loadingDialog.showDialog();
					}
				});
			}

			private void setEvent() {
				btn_appoint.setOnClickListener(listener);
				btn_navigate.setOnClickListener(listener);
				iv_collect.setOnClickListener(listener);
			}

			@Override
			protected void update() {
				pile = get(position);
				if (imageLoadable) {
					String coverImg = pile.getCoverImg();
					ImageLoaderManager.getInstance().displayImage(coverImg, iv_icon, pileDisplayoption);
					//TODO 增加桩主头像
					String circle_url = pile.getOwnerAvatar();
					Log.e("pileFind", circle_url + "");
					ImageLoaderManager.getInstance().displayImage(circle_url, circleImageView, userAvatarDisplayoption, true);
				}
				//TODO 是否收藏桩在此接口未实现
				if (pile.getFavor() != null) {
					if (pile.getFavor() == Pile.FAVOR_YES) {//1代表收藏，0代表未收藏
						iv_collect.setImageResource(R.drawable.collect_heart_active);
					} else if (pile.getFavor() == Pile.FAVOR_NOT) {
						iv_collect.setImageResource(R.drawable.collect_heart);
					} else {
						iv_collect.setVisibility(View.GONE);
					}
				}
				// 名字
				tv_name.setText(pile.getPileNameAsString());
				// 地址
				tv_location.setText(pile.getLocationAsString());
				// 评分
				if (pile.getAvgLevel() != null) {
					eva_star_point.setEvaluate(pile.getAvgLevel());
				} else {
					eva_star_point.setEvaluate(0);
				}
				btn_appoint.setTextSize(ExtensionUtils.px2dip(context, context.getResources().getDimension(R.dimen.w20)));

				// 拥有者类别图标
				if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
					// 电流类别图标
					if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
						btn_appoint.setText(R.string.book_charge);
						if (pile.getShareState() != null && pile.getShareState() == Pile.SHARE_STATUS_YES) {
							// 已发布
							btn_appoint.setBackgroundResource(R.drawable.find_daohang01);
							btn_appoint.setEnabled(true);
							tv_idle.setText(R.string.had_shared);
							tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
							// 充电价格
							if (pile.getPrice() == null) {
								ll_price.setVisibility(View.INVISIBLE);
							} else {
								ll_price.setVisibility(View.VISIBLE);
								tv_price.setText(CalculateUtil.formatDefaultNumber(pile.getPrice()));
							}
						} else {
							// 未发布
							ll_price.setVisibility(View.INVISIBLE);
							btn_appoint.setBackgroundResource(R.drawable.find_daohang03);
							btn_appoint.setEnabled(false);
							tv_idle.setText(R.string.not_shared);
							tv_idle.setTextColor(context.getResources().getColor(R.color.text_gray_light));
						}
					} else {
						btn_appoint.setText(R.string.immediately_charge);
						btn_appoint.setBackgroundResource(R.drawable.find_daohang01);
						btn_appoint.setEnabled(true);
						if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) {
							tv_idle.setText(R.string.idle);
							tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
						} else if (pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile.STATE_PERSION_CHARGE_FINISHED) {
							tv_idle.setText(R.string.charging);
							tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
						} else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT) {
							tv_idle.setText(R.string.occupy);
							tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
						} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_BOOK) {
							tv_idle.setText(R.string.booked);
							tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
						} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT) {
							tv_idle.setText(R.string.maintenance);
							tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
						} else if (pile.getRunStatus() == Pile.STATE_PERSION_GPRS_DISCONNECT) {
							tv_idle.setText(R.string.offline);
							tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
						} else {
							tv_idle.setText(R.string.unknown);
							tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
						}
//						if (pile.getRunStatus() == Pile.STATE_PERSION_FREE){
//							tv_idle.setText(R.string.idle);
//							tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
//						}
//						else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT || pile.getRunStatus
// () ==
//								Pile.STATE_PERSION_GPRS_DISCONNECT){
//							tv_idle.setText(R.string.maintenance);
//							tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
//						}
//						else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT || pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus
// () ==
//								Pile.STATE_PERSION_CHARGE_FINISHED){
//							tv_idle.setText(R.string.occupy);
//							tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
//						}
//						else {
//							tv_idle.setText(R.string.unknown);
//							tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
//						}
						if (pile.getPrice() == null) {
							ll_price.setVisibility(View.INVISIBLE);
						} else {
							ll_price.setVisibility(View.VISIBLE);
							tv_price.setText(CalculateUtil.formatDefaultNumber(pile.getPrice()));
						}
					}
					//电桩类型
					String pileType = MyApplication.getInstance().getPileTypeByKey(pile.getType());
					btn_pile_type.setText(pileType);
				} else {
					btn_appoint.setBackgroundResource(R.drawable.find_daohang01);
					//电站类型
					String pileType = MyApplication.getInstance().getPileStationTypeByKey(pile.getType());
					btn_pile_type.setText(pileType);
					if (pile.getIsIdle() == PileStation.STATE_STATION_HAVE_FREE) {
						tv_idle.setText(R.string.has_idle);
						tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
					} else if (pile.getIsIdle() == PileStation.STATE_STATION_FULL_LOAD) {
						tv_idle.setText(R.string.full_load);
						tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
					} else if (pile.getIsIdle() == PileStation.STATE_STATION_OFFLINE) {
						tv_idle.setText(R.string.offline);
						tv_idle.setTextColor(context.getResources().getColor(R.color.text_gray_light));
					} else {
						tv_idle.setText(R.string.unknown);
						tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
					}
//					if (pile.getIsIdle() == Pile.IDLE) {
//						tv_idle.setText(R.string.has_idle);
//					} else {
//						tv_idle.setText(R.string.full_load);
//					}
					int gprsNum = 0;
					int freeNum = 0;
					if (pile.getGprsNum() != null) {
						gprsNum = pile.getGprsNum();
					}
					if (pile.getFreeNum() != null) {
						freeNum = pile.getFreeNum();
					}

					int dcNum = 0;
					int acNum = 0;
					if (pile.getDirectNum() != null) {
						dcNum = pile.getDirectNum();
					}
					if (pile.getAlterNum() != null) {
						acNum = pile.getAlterNum();
					}
					btn_appoint.setTextSize(ExtensionUtils.px2dip(context, context.getResources().getDimension(R.dimen.h15)));
					btn_appoint.setText(context.getResources().getString(R.string.default_studio_annotation, dcNum, acNum));
					ll_price.setVisibility(View.VISIBLE);
					tv_price.setText(CalculateUtil.formatDefaultNumber(pile.getPrice()));
//					if (gprsNum > 0) {
//						btn_appoint.setBackgroundResource(R.drawable.find_daohang01);
//						btn_appoint.setEnabled(true);
//						btn_appoint.setText(context.getResources().getString(R.string.studio_pile_num, gprsNum, freeNum));
//					} else {
//						btn_appoint.setBackgroundResource(R.drawable.find_daohang03);
//						btn_appoint.setText(R.string.studio_offline);
//						btn_appoint.setEnabled(false);
//					}
				}

				// 计算距离
				if (myLatitude > 0 && myLongitude > 0 && pile.getLatitude() != null && pile.getLongitude() != null) {
					tv_distance.setVisibility(View.VISIBLE);
					LatLng sLatLng = new LatLng(myLatitude, myLongitude);
					LatLng eLatLng = new LatLng(pile.getLatitude(), pile.getLongitude());
					CalculateUtil.infuseDistance(context, tv_distance, eLatLng);
				} else {
					tv_distance.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			protected View init(LayoutInflater arg0) {
				View root = arg0.inflate(R.layout.adapter_item_pile_find_new, null);
				iv_icon = (ImageView) root.findViewById(R.id.iv_icon);
				tv_name = (TextView) root.findViewById(R.id.tv_name);
				iv_collect = (ImageView) root.findViewById(R.id.iv_collect);
				tv_location = (TextView) root.findViewById(R.id.tv_location);
				tv_distance = (TextView) root.findViewById(R.id.tv_distance);
				ll_price = (LinearLayout) root.findViewById(R.id.ll_price);
				tv_price = (TextView) root.findViewById(R.id.tv_price);
				btn_navigate = (Button) root.findViewById(R.id.btn_navigate);
				eva_star_point = (EvaluateColumn) root.findViewById(R.id.eva_star_point);
				btn_appoint = (Button) root.findViewById(R.id.btn_appoint);
				btn_pile_type = (Button) root.findViewById(R.id.btn_pile_type);
				tv_idle = (TextView) root.findViewById(R.id.tv_idle);
				//TODO 增加用户头像字段
				circleImageView = (CircleImageView) root.findViewById(R.id.civ_user_avatar);
				setEvent();
				return root;
			}
		};
	}

	private void callNavigation(Pile pile) {
		WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(context);
		if (mLocation == null) {
			ToastUtil.show(context, "定位失败,请重试");
			return;
		}
		if (pile.getLatitude() == null || pile.getLatitude() == null) {
			return;
		}
		waterBlueDialogComfirmNav.show(mLocation.getLatitude(), mLocation.getLongitude(), pile.getLatitude(), pile.getLongitude());
	}

	private BDLocation mLocation = null;

	private void getLocation() {
		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String cityStr = location.getCity();
				if (cityStr != null) {
					mLocation = location;
				}
			}

		});
	}

	private boolean isLogin() {
		return sp.getBoolean("isLogin", false);
	}

	public boolean isImageLoadable() {
		return imageLoadable;
	}

	public void setImageLoadable(boolean imageLoadable) {
		this.imageLoadable = imageLoadable;
	}
}
