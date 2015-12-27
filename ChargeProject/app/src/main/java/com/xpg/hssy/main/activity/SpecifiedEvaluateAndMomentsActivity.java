package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.adapter.CommentBriefAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.bean.Comment;
import com.xpg.hssy.bean.Like;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.PileScore;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.popwindow.ShowEditTextPop;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.view.LikeAndCommentView;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by black-Gizwits on 2015/10/15.
 */
public class SpecifiedEvaluateAndMomentsActivity extends BaseActivity {

	public final static String KEY_SPECIIED_TYPE = "specifiedType";
	public final static int SPECIIED_TYPE_PILE_OR_STATION = 0x00;
	public final static int SPECIIED_TYPE_PERSION = 0x01;

	private final static int REQUEST_REFRESH = 0x51;
	private final static int REQUEST_MODIF_ARTCILE = 0x52;

	private final static int MAX_COMMENT_COUNT = 3;
	private final static int MAX_PAGER_ITEM_COUNT = 10;

	private final static String TIME_FORMAT_YMD_HMS = "MM-dd HH:mm";

	private final DecimalFormat scoreFormat = new DecimalFormat("0.#");

	private int specifiedType;
	private View headView;
	private TextView tv_pile_name;
	private EvaluateColumn eva_evaluate_star_line;
	private TextView tv_avg_evaluate_point;
	private TextView tv_avg_environment_point;
	private TextView tv_avg_exterior_point;
	private TextView tv_avg_performance_point;


	private ImageButton btn_left;
	private TextView tv_center;
	private RefreshListView rlv_charge_time_line;
	private ShowEditTextPop showEditTextPop;
	private ChargeTimeLineAdapter chargeTimeLineAdapter;
	private long currentTime;
	private String userId = "";
	private String pileId = "";
	private String pileName = "";
	private DisplayImageOptions options;
	private int momentPager = 0;
	private int selectedArticleIndex = -1;
	private String selectedArticleId = "";
	private SPFile sp;
	private LoadingDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		specifiedType = getIntent().getIntExtra(KEY_SPECIIED_TYPE, SPECIIED_TYPE_PERSION);
		if (specifiedType == SPECIIED_TYPE_PILE_OR_STATION) {
			pileName = getIntent().getStringExtra(KEY.INTENT.PILE_NAME);
			pileId = getIntent().getStringExtra(KEY.INTENT.PILE_ID);
			headView = getLayoutInflater().inflate(R.layout.layout_pile_or_station_avg_evaluate, null);
			headView.setVisibility(View.GONE);
			tv_pile_name = (TextView) headView.findViewById(R.id.tv_pile_name);
			tv_avg_evaluate_point = (TextView) headView.findViewById(R.id.tv_avg_evaluate_point);
			tv_avg_environment_point = (TextView) headView.findViewById(R.id.tv_avg_environment_point);
			tv_avg_exterior_point = (TextView) headView.findViewById(R.id.tv_avg_exterior_point);
			tv_avg_performance_point = (TextView) headView.findViewById(R.id.tv_avg_performance_point);
			eva_evaluate_star_line = (EvaluateColumn) headView.findViewById(R.id.eva_evaluate_star_line);
			rlv_charge_time_line.addHeaderView(headView);
		}

		switch (specifiedType) {
			case SPECIIED_TYPE_PERSION: {
				options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R
						.drawable.find_sanyoubg2).showImageOnFail(R.drawable.find_sanyoubg2).showImageOnLoading(R.drawable.find_sanyoubg2).displayer(new
						RoundedBitmapDisplayer((int) getResources().getDimension(R.dimen.h25))).build();
				tv_center.setText(R.string.my_all_evaluate);
				break;
			}
			case SPECIIED_TYPE_PILE_OR_STATION: {
				options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R
						.drawable.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer(
						(int) getResources().getDimension(R.dimen.h25))).build();
				tv_center.setText(R.string.all_evaluate);
				break;
			}
		}

		refreshMomentList();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.specified_all_evaluate);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		tv_center = (TextView) findViewById(R.id.tv_center);
		rlv_charge_time_line = (RefreshListView) findViewById(R.id.rlv_charge_time_line);
		chargeTimeLineAdapter = new ChargeTimeLineAdapter(self);
		rlv_charge_time_line.setAdapter(chargeTimeLineAdapter);
		showEditTextPop = new ShowEditTextPop(self);
		tv_center.setText(R.string.all_evaluate);
	}

	protected void initData() {
		sp = new SPFile(this, "config");
		userId = sp.getString(KEY.CONFIG.USER_ID, "");
	}

	private boolean isLogin() {
		return sp.getBoolean("isLogin", false);
	}

	@Override
	protected void initEvent() {
		btn_left.setOnClickListener(this);
		rlv_charge_time_line.setOnDropDownListener(new RefreshListView.OnDropDownListener() {
			@Override
			public void onDropDown() {
				refreshMomentList();
			}
		});
		rlv_charge_time_line.setOnBottomListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMomentListPager(++momentPager);
			}
		});
		rlv_charge_time_line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Log.i("tag", "onItemClick");
				if (isLogin()) {
					selectedArticleIndex = i - rlv_charge_time_line.getHeaderViewsCount();
					Article article = chargeTimeLineAdapter.get(selectedArticleIndex);
					if (article != null) {
						selectedArticleId = article.getArticleId();
						if (!TextUtils.isEmpty(selectedArticleId)) {
							Intent intent = new Intent(self, TopicDetailActivity.class);
							intent.putExtra(KEY.INTENT.ARTICLE_ID, selectedArticleId);
							self.startActivityForResult(intent, REQUEST_MODIF_ARTCILE);
						}
					}
				} else {
					Intent intentLogin = new Intent(self, LoginActivity.class);
					self.startActivity(intentLogin);
				}
			}
		});
	}

	private void refreshMomentList() {
		currentTime = System.currentTimeMillis();
		rlv_charge_time_line.showRefreshing(true);
		rlv_charge_time_line.setLoadable(false);
		rlv_charge_time_line.completeLoad();
		momentPager = 0;
		loadMomentListPager(momentPager);
		if (specifiedType == SPECIIED_TYPE_PILE_OR_STATION) {
			loadPileScore(pileId);
		}
	}

	private void loadPileScore(String pileId) {
		WebAPIManager.getInstance().getPileScore(pileId, new WebResponseHandler<PileScore>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(WebResponse<PileScore> response) {
				super.onSuccess(response);
				PileScore pileScore = response.getResultObj();
				if (null != pileScore) {
					headView.setVisibility(View.VISIBLE);
					tv_pile_name.setText(pileName);
					eva_evaluate_star_line.setEvaluate(pileScore.getPileScore());
					tv_avg_evaluate_point.setText(scoreFormat.format(pileScore.getPileScore()));
					tv_avg_environment_point.setText(scoreFormat.format(pileScore.getEnvirScore()));
					tv_avg_exterior_point.setText(scoreFormat.format(pileScore.getFacedScore()));
					tv_avg_performance_point.setText(scoreFormat.format(pileScore.getFuncScore()));
				} else {
					headView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void loadMomentListPager(final int pagerNum) {
		switch (specifiedType) {
			case SPECIIED_TYPE_PERSION: {
				loadMyMoments(pagerNum);
				break;
			}
			case SPECIIED_TYPE_PILE_OR_STATION: {
				loadPileMoments(pagerNum);
				break;
			}
		}
	}

	private void loadPileMoments(final int pagerNum) {
		WebAPIManager.getInstance().getPileMoment(pileId, pagerNum, new WebResponseHandler<List<Article>>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (rlv_charge_time_line.isRefreshing()) {
					rlv_charge_time_line.showRefreshFail();
				} else {
					rlv_charge_time_line.showLoadFail();
				}
			}

			@Override
			public void onFailure(WebResponse<List<Article>> response) {
				super.onFailure(response);
				if (rlv_charge_time_line.isRefreshing()) {
					rlv_charge_time_line.showRefreshFail();
				} else {
					rlv_charge_time_line.showLoadFail();
				}
			}

			@Override
			public void onSuccess(WebResponse<List<Article>> response) {
				super.onSuccess(response);
				List<Article> articles = response.getResultObj();
				if (articles != null) {
					checkArticlesLike(articles.toArray(new Article[0]));
					if (pagerNum == 0) {
						chargeTimeLineAdapter.clear();
					}
					chargeTimeLineAdapter.add(articles);
					if (rlv_charge_time_line.isRefreshing()) {
						rlv_charge_time_line.completeRefresh();
					} else {
						rlv_charge_time_line.completeLoad();
					}
					if (articles.size() < MAX_PAGER_ITEM_COUNT) {
						rlv_charge_time_line.showNoMore();
					} else {
						rlv_charge_time_line.prepareLoad();
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void loadMyMoments(final int pagerNum) {
		WebAPIManager.getInstance().getMyMoment(userId, pagerNum, new WebResponseHandler<List<Article>>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (rlv_charge_time_line.isRefreshing()) {
					rlv_charge_time_line.showRefreshFail();
				} else {
					rlv_charge_time_line.showLoadFail();
				}
			}

			@Override
			public void onFailure(WebResponse<List<Article>> response) {
				super.onFailure(response);
				if (rlv_charge_time_line.isRefreshing()) {
					rlv_charge_time_line.showRefreshFail();
				} else {
					rlv_charge_time_line.showLoadFail();
				}
			}

			@Override
			public void onSuccess(WebResponse<List<Article>> response) {
				super.onSuccess(response);
				List<Article> articles = response.getResultObj();
				if (articles != null) {
					checkArticlesLike(articles.toArray(new Article[0]));
					if (pagerNum == 0) {
						chargeTimeLineAdapter.clear();
					}
					chargeTimeLineAdapter.add(articles);
					if (rlv_charge_time_line.isRefreshing()) {
						rlv_charge_time_line.completeRefresh();
					} else {
						rlv_charge_time_line.completeLoad();
					}
					if (articles.size() < MAX_PAGER_ITEM_COUNT) {
						rlv_charge_time_line.showNoMore();
					} else {
						rlv_charge_time_line.prepareLoad();
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void checkArticlesLike(Article... articles) {
		if (articles != null) {
			for (Article article : articles) {
				article.updateLikeStateByUserId(userId);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		userId = sp.getString(KEY.CONFIG.USER_ID, "");
		synchronized (chargeTimeLineAdapter) {
			List<Article> articles = chargeTimeLineAdapter.getItems();
			if (articles != null) {
				checkArticlesLike(articles.toArray(new Article[0]));
			}
			chargeTimeLineAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_REFRESH: {
					refreshMomentList();
					break;
				}
				case REQUEST_MODIF_ARTCILE: {
					int modifyType = data.getIntExtra(TopicDetailActivity.MODIFY_TYPE, -1);
					if (modifyType == TopicDetailActivity.MODIFY_TYPE_DELETE) {
						Article perDeleteArticle = chargeTimeLineAdapter.get(selectedArticleIndex);
						if (perDeleteArticle != null && perDeleteArticle.getArticleId().equals(selectedArticleId)) {
							chargeTimeLineAdapter.remove(perDeleteArticle);
							selectedArticleIndex = -1;
							selectedArticleId = "";
						} else {
							refreshMomentList();
						}
					} else if (modifyType == TopicDetailActivity.MODIFY_TYPE_ADD_COMMENT) {
						Article updateArticle = (Article) data.getSerializableExtra(TopicDetailActivity.ARTICLE);
						Article oldArticle = chargeTimeLineAdapter.get(selectedArticleIndex);
						if (updateArticle != null && oldArticle != null) {
							chargeTimeLineAdapter.remove(oldArticle);
							chargeTimeLineAdapter.add(selectedArticleIndex, updateArticle);
						} else {
							refreshMomentList();
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			case R.id.btn_display_topic: {
				if (isLogin()) {
					Intent intent = new Intent(self, ThemeDisplayTopicActivity.class);
					self.startActivityForResult(intent, REQUEST_REFRESH);
				} else {
					Intent intentLogin = new Intent(self, LoginActivity.class);
					self.startActivity(intentLogin);
				}
				break;
			}
			case R.id.btn_left: {
				onBackPressed();
				break;
			}
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	public class ChargeTimeLineAdapter extends EasyAdapter<Article> implements View.OnClickListener {

		public ChargeTimeLineAdapter(Context context) {
			super(context);
		}

		public ChargeTimeLineAdapter(Context context, List<Article> items) {
			super(context, items);
		}

		public List<Article> getItems() {
			return items;
		}

		@Override
		protected ViewHolder newHolder() {
			return new TimeLineViewHolder();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_content: {
					TimeLineViewHolder holder = (TimeLineViewHolder) v.getTag();
					Article article = get(holder.getPosition());
					jumpTODetail(article);
					break;
				}
				case R.id.iv_good://这个跟下面执行的代码一样
				case R.id.tv_good: {
					if (isLogin()) {
						TimeLineViewHolder holder = (TimeLineViewHolder) v.getTag();
						Article article = get(holder.getPosition());
						if (v.isSelected()) {
							doLike(holder.getPosition(), article.getArticleId(), userId, Article.LIKE_NO);
						} else {
							doLike(holder.getPosition(), article.getArticleId(), userId, Article.LIKE_YES);
						}
					} else {
						Intent intentLogin = new Intent(self, LoginActivity.class);
						startActivity(intentLogin);
					}
					break;
				}
				case R.id.tv_comment://这个跟下面执行的代码一样
				case R.id.iv_comment: {
					if (isLogin()) {
						TimeLineViewHolder holder = (TimeLineViewHolder) v.getTag();
						final Article article = get(holder.getPosition());
						final int itemPosition = holder.getPosition();
						if (showEditTextPop != null && showEditTextPop.isShowing()) {
							showEditTextPop.dismiss();
						}
						showEditTextPop = new ShowEditTextPop(self);
						showEditTextPop.setEditHintData(getString(R.string.show_your_mind));
						showEditTextPop.setFocusable(true);
						showEditTextPop.setFocusable(true);
						showEditTextPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams
								.SOFT_INPUT_ADJUST_RESIZE);

						showEditTextPop.setOnClickButton(new ShowEditTextPop.OnClickButton() {
							@Override
							public void click(String content) {
								replyMoment(itemPosition, article.getArticleId(), null, userId, null, content);
							}
						});
						showEditTextPop.showPop(rlv_charge_time_line);
					} else {
						Intent intentLogin = new Intent(self, LoginActivity.class);
						self.startActivity(intentLogin);
					}
					break;
				}
				case R.id.bt_delete: {
					TimeLineViewHolder holder = (TimeLineViewHolder) v.getTag();
					final Article article = get(holder.getPosition());
					deleteArticle(holder.getPosition(), article.getArticleId());
					break;
				}
			}
		}


		public void doLike(final int itemPosition, final String articleId, String userid, final int likeType) {
			WebAPIManager.getInstance().doLike(articleId, userid, likeType, new WebResponseHandler<Object>() {
				@Override
				public void onStart() {
					super.onStart();
					if (loadingDialog != null && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					TipsUtil.showTips(self, e);
				}

				@Override
				public void onFailure(WebResponse<Object> response) {
					super.onFailure(response);
					TipsUtil.showTips(self, response);
				}


				@Override
				public void onSuccess(WebResponse<Object> response) {
					super.onSuccess(response);
					refreshArticle(articleId, itemPosition);
				}

				@Override
				public void onFinish() {
					super.onFinish();
					if (loadingDialog != null) loadingDialog.dismiss();
				}
			});
		}

		public void replyMoment(final int itemPosition, final String articleId, String replyArtId, String userid, String replyUserid, String content) {
			WebAPIManager.getInstance().replyMoment(articleId, replyArtId, userid, replyUserid, content, new WebResponseHandler<Object>() {
				@Override
				public void onStart() {
					super.onStart();
					if (loadingDialog != null && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
				}

				@Override
				public void onFailure(WebResponse<Object> response) {
					super.onFailure(response);
				}

				@Override
				public void onSuccess(WebResponse<Object> response) {
					super.onSuccess(response);
					showEditTextPop.dismiss();
					showEditTextPop.setEditTextData("");
					refreshArticle(articleId, itemPosition);
				}

				@Override
				public void onFinish() {
					super.onFinish();
					loadingDialog.dismiss();
				}
			});
		}

		public void refreshArticle(final String articleId, final int itemPosition) {
			WebAPIManager.getInstance().getTopicInfo(articleId, new WebResponseHandler<Article>() {
				@Override
				public void onStart() {
					super.onStart();
//					if (loadingDialog != null && loadingDialog.isShowing()) {
//						loadingDialog.dismiss();
//					}
//					loadingDialog = new LoadingDialog(self, R.string.loading);
//					loadingDialog.showDialog();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
				}

				@Override
				public void onFailure(WebResponse<Article> response) {
					super.onFailure(response);
				}

				@Override
				public void onSuccess(WebResponse<Article> response) {
					super.onSuccess(response);
					Article article = response.getResultObj();
					Article oldArticle = get(itemPosition);
					if (article != null && oldArticle != null && article.getArticleId().equals(oldArticle.getArticleId())) {
						checkArticlesLike(article);
						remove(itemPosition);
						add(itemPosition, article);
					}
				}

				@Override
				public void onFinish() {
					super.onFinish();
					if (loadingDialog != null) loadingDialog.dismiss();
				}
			});
		}

		public void deleteArticle(final int itemPostition, final String articleId) {
			WebAPIManager.getInstance().deleteTopicInfo(articleId, new WebResponseHandler<Object>() {
				@Override
				public void onStart() {
					super.onStart();
					if (loadingDialog != null && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					ToastUtil.show(self, "删除失败");

				}

				@Override
				public void onFailure(WebResponse<Object> response) {
					super.onFailure(response);
					ToastUtil.show(self, "删除失败");
				}

				@Override
				public void onSuccess(WebResponse<Object> response) {
					super.onSuccess(response);
					ToastUtil.show(self, "删除成功");
				}

				@Override
				public void onFinish() {
					Article perDeleteArticle = get(itemPostition);
					if (perDeleteArticle != null && perDeleteArticle.getArticleId().equals(articleId)) {
						remove(perDeleteArticle);
					}
					super.onFinish();
					loadingDialog.dismiss();

				}
			});
		}

		private void jumpTODetail(Article article) {
			switch (article.getType()) {
				case Article.TYPE_PILE: {
					Intent intent = new Intent(self, PileInfoNewActivity.class);
					intent.putExtra(KEY.INTENT.PILE_ID, article.getPileId());
					startActivity(intent);
					break;
				}
				case Article.TYPE_STATION: {
					Intent intent = new Intent(self, PileStationInfoActivity.class);
					intent.putExtra(KEY.INTENT.PILE_ID, article.getPileId());
					startActivity(intent);
					break;
				}
				case Article.TYPE_TIME_LINE: {
					break;
				}
			}
		}

		class TimeLineViewHolder extends ViewHolder {
			private ImageView iv_avatar;
			private TextView tv_name;
			private TextView tv_address;
			private TextView tv_content;
			private TextView tv_time;
			private GridView gView;
			private TextView tv_start;
			private EvaluateColumn eva_star_point;
			private ImageView iv_good;
			private TextView tv_good;
			private ImageView iv_comment;
			private TextView tv_comment;
			private LikeAndCommentView lacv_like_comment_view;
			private TextView tv_view_more;
			private Button bt_delete;

			private CommentBriefAdapter commentBriefAdapter;
			private MyGridAdapter myGridadapter;

			@Override
			protected View init(LayoutInflater layoutInflater) {

				View view = getViewBySpecifiedType(specifiedType, layoutInflater);
				iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tv_address = (TextView) view.findViewById(R.id.tv_address);
				tv_content = (TextView) view.findViewById(R.id.tv_content);
				tv_time = (TextView) view.findViewById(R.id.tv_time);
				gView = (GridView) view.findViewById(R.id.gView);
				iv_good = (ImageView) view.findViewById(R.id.iv_good);
				iv_comment = (ImageView) view.findViewById(R.id.iv_comment);
				tv_comment = (TextView) view.findViewById(R.id.tv_comment);
				tv_start = (TextView) view.findViewById(R.id.tv_start);
				eva_star_point = (EvaluateColumn) view.findViewById(R.id.eva_star_point);
				tv_good = (TextView) view.findViewById(R.id.tv_good);
				lacv_like_comment_view = (LikeAndCommentView) view.findViewById(R.id.lacv_like_comment_view);
				tv_view_more = (TextView) view.findViewById(R.id.tv_view_more);
				bt_delete = (Button) view.findViewById(R.id.bt_delete);
				commentBriefAdapter = new CommentBriefAdapter(self);
				lacv_like_comment_view.setCommentListAdapter(commentBriefAdapter);
				gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				myGridadapter = new MyGridAdapter(self, new ArrayList<String>());
				gView.setAdapter(myGridadapter);

				tv_content.setOnClickListener(ChargeTimeLineAdapter.this);
				tv_good.setOnClickListener(ChargeTimeLineAdapter.this);
				iv_good.setOnClickListener(ChargeTimeLineAdapter.this);
				tv_comment.setOnClickListener(ChargeTimeLineAdapter.this);
				iv_comment.setOnClickListener(ChargeTimeLineAdapter.this);
				bt_delete.setOnClickListener(ChargeTimeLineAdapter.this);
				lacv_like_comment_view.setLikeLineOnclickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final Article article = get(position);
						if (article != null) {
							//点赞列表
							Intent intent = new Intent(self, LikeListActivity.class);
							intent.putExtra(KEY.INTENT.ARTICLE_ID, article.getArticleId());
							startActivity(intent);
						}
					}
				});
				lacv_like_comment_view.setCommentItemOnClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						if (isLogin()) {
							final Article article = get(position);
							Comment comment = (Comment) lacv_like_comment_view.getCommentListAdapter().getItem(i);
							String commentUserName = comment.getUserName();
							final String replyArtId = comment.getId();
							final String replyUserId = comment.getUserid();
							if (showEditTextPop != null && showEditTextPop.isShowing()) {
								showEditTextPop.dismiss();
							}
							showEditTextPop = new ShowEditTextPop(self);
							showEditTextPop.setEditHintData("回复" + commentUserName);
							showEditTextPop.setFocusable(true);
							showEditTextPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams
									.SOFT_INPUT_ADJUST_RESIZE);
							showEditTextPop.setOnClickButton(new ShowEditTextPop.OnClickButton() {
								@Override
								public void click(String content) {
									replyMoment(position, article.getArticleId(), replyArtId, userId, replyUserId, content);
								}
							});
							showEditTextPop.showPop(view);
						} else {
							Intent intent = new Intent(self, LoginActivity.class);
							startActivity(intent);
						}
					}
				});
				gView.setFocusable(false);
				gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						TimeLineViewHolder holder = (TimeLineViewHolder) adapterView.getTag();
						Article article = get(holder.getPosition());
						if (article != null && article.getImages() != null && article.getImages().size() > 0) {
							Intent intent = new Intent(self, PilePhotoActivity.class);
							intent.putExtra(KEY.INTENT.IMAGE_INDEX, i);
							intent.putExtra(KEY.INTENT.IMAGE_URLS, (Serializable) article.getImages());
							startActivity(intent);
						}
					}
				});
				tv_content.setTag(this);
				gView.setTag(this);
				tv_good.setTag(this);
				iv_good.setTag(this);
				tv_comment.setTag(this);
				iv_comment.setTag(this);
				bt_delete.setTag(this);
				return view;
			}

			@Override
			protected void update() {
				Article article = get(position);
				if (TextUtils.isEmpty(article.getLocation())) {
					tv_address.setText(R.string.unknow);
				} else {
					tv_address.setText(article.getLocation());

				}
				tv_time.setText(TimeUtil.toLastTimeString(currentTime, article.getCreateAt()));
				setViewByArticleType(article);
				if (userId.equals(article.getUserid())) {
					bt_delete.setVisibility(View.VISIBLE);
				} else {
					bt_delete.setVisibility(View.GONE);
				}
				if (article.getLikeCount() > 0) {
					tv_good.setText(article.getLikeCount() + "");
				} else {
					tv_good.setText("");
				}
				if (article.isLike()) {
					iv_good.setSelected(true);
					tv_good.setSelected(true);
				} else {
					iv_good.setSelected(false);
					tv_good.setSelected(false);
				}
				myGridadapter.setDatas(article.getImages());
				List<Like> likes = article.getLikes();
				lacv_like_comment_view.removeAllLikeUserAvatar();
				if (likes != null) {
					//添加点赞用户头像
					for (Like like : likes) {
						lacv_like_comment_view.addLikeUserAvatar(like.getAvatar());
					}
				}
				List<Comment> comments = article.getComments();
				if (comments.size() > MAX_COMMENT_COUNT) {
					comments = comments.subList(0, MAX_COMMENT_COUNT);
				}
				commentBriefAdapter.clear();
				commentBriefAdapter.add(comments);
				int viewMoreCount = article.getCommentCount() - MAX_COMMENT_COUNT;
				if (comments.size() >= MAX_COMMENT_COUNT && viewMoreCount > 0) {
					tv_view_more.setText(getResources().getString(R.string.view_more, viewMoreCount));
					tv_view_more.setVisibility(View.VISIBLE);
				} else {
					tv_view_more.setVisibility(View.GONE);
				}
			}

			private void setViewByArticleType(final Article article) {
				if ((article.getType() == Article.TYPE_PILE || article.getType() == Article.TYPE_STATION)) {
					if (specifiedType == SPECIIED_TYPE_PILE_OR_STATION) {//桩或者站的所有评价
						ImageLoaderManager.getInstance().displayImage(article.getAvatar(), iv_avatar, options, true);
						tv_name.setText(article.getUserName());
						tv_content.setText(article.getContent());
						tv_content.setEnabled(false);
						tv_start.setVisibility(View.VISIBLE);
					} else if (specifiedType == SPECIIED_TYPE_PERSION) {//用户对桩或者站的所有评价
						ImageLoaderManager.getInstance().displayImage(article.getAvatar(), iv_avatar, options, true);
						tv_name.setText(article.getPileName());
						String content = getString(R.string.charge_comment_format, getString(R.string.charge_comment), article.getPileName(), article
								.getContent());
						tv_content.setText(Html.fromHtml(content));
						tv_content.setEnabled(true);
						tv_start.setVisibility(View.INVISIBLE);
					}
					eva_star_point.setVisibility(View.VISIBLE);
					if (article.getPileScore() != null) {
						eva_star_point.setEvaluate(article.getPileScore());
					} else {
						eva_star_point.setEvaluate(0);
					}
				} else if (article.getType() == Article.TYPE_TIME_LINE) {
					tv_content.setText(article.getContent());
					tv_content.setEnabled(false);
					tv_start.setVisibility(View.GONE);
					eva_star_point.setVisibility(View.GONE);
				} else {
					tv_content.setText(article.getContent());
					tv_content.setEnabled(false);
					tv_start.setVisibility(View.GONE);
					eva_star_point.setVisibility(View.GONE);
				}
			}
		}

		private View getViewBySpecifiedType(int specifiedType, LayoutInflater layoutInflater) {
			View view = null;
			switch (specifiedType) {
				case SPECIIED_TYPE_PERSION: {
					view = layoutInflater.inflate(R.layout.layout_pile_comment_item, null);
					break;
				}
				case SPECIIED_TYPE_PILE_OR_STATION: {
					view = layoutInflater.inflate(R.layout.layout_charge_time_line_item, null);
					break;
				}
				default: {
					view = layoutInflater.inflate(R.layout.layout_charge_time_line_item, null);
					break;
				}
			}
			return view;
		}
	}

	@SuppressLint("HandlerLeak")
	public class MyGridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<String> datas;
		private DisplayImageOptions my0ptions;


		public List<String> getDatas() {
			return datas;
		}

		public void setDatas(List<String> datas) {
			if (datas == null) {
				datas = new ArrayList<>();
			}
			this.datas.clear();
			this.datas.addAll(datas);
			notifyDataSetChanged();
		}

		public void clearDatas() {
			if (this.datas != null) {
				this.datas.clear();
				notifyDataSetChanged();
			}
		}

		public MyGridAdapter(Context context, List<String> datas) {
			inflater = LayoutInflater.from(context);
			this.datas = datas;
			my0ptions = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R
					.drawable.find_sanyoubg2).showImageOnLoading(R.drawable.find_sanyoubg2).showImageOnFail(R.drawable.find_sanyoubg2).build();
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int i) {
			return datas.get(i);
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida_mathparent, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.image.getLayoutParams(); //取控件ImageView当前的布局参数
				linearParams.height = (int) getResources().getDimension(R.dimen.w80);
				holder.image.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url = datas.get(position);
			ImageLoaderManager.getInstance().displayImage(url, holder.image, my0ptions);
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}
	}


}
