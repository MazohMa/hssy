package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.adapter.CommentListAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.bean.Comment;
import com.xpg.hssy.bean.Like;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.CommentDialog;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.view.LikeAndCommentView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh 优易圈话题详情
 */
public class TopicDetailActivity extends BaseActivity implements View.OnClickListener {
	public final static int MODIFY_TYPE_ADD_COMMENT = 0x61;
	public final static int MODIFY_TYPE_DELETE = 0x62;

	public final static String MODIFY_TYPE = "modifyType";
	public final static String ARTICLE = "article";

	private final static String TIME_FORMAT_YMD_HMS = "MM-dd HH:mm";
	private ScrollView scoll;
	private ImageView img_author_head;
	private TextView tv_author_name, tv_address, tv_time, tv_content, tv_start, tv_good;
	private Button bt_delete, to_commtent;
	private RelativeLayout rl_comment;
	private EvaluateColumn eva_star_point;
	private LikeAndCommentView like_and_commend_view;
	private GridView gView;
	private MyGridAdapter adapter;
	private DisplayImageOptions options;
	private CommentListAdapter commentListAdapter;
	private List<String> datas = new ArrayList<String>();
	private List<String> urls = new ArrayList<String>();
	private LoadingDialog loadingDialog = null;
	private Article article;

	private String articleId;
	private String user_id;
	private String avatar = "";
	private SharedPreferences sp;
	private ImageButton btn_left;
	private String replyArtId = "";
	private String replyUserid = "";
	private String userName = "";
	private LinearLayout ll_all_layout;
	private RelativeLayout rl_content;
	private int width;
	private int rl_contentPadding;
	private int imageHeight;
	private ViewTreeObserver vto;
	private boolean hasMeasured = false;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_content: {
				jumpTODetail(article);
				break;
			}
			case R.id.bt_delete:
				deleteTopicInfo(articleId);
				break;
			case R.id.tv_good:
				if (article.isLike()) {
					doLike(articleId, user_id, Article.LIKE_NO);
				} else {
					doLike(articleId, user_id, Article.LIKE_YES);
				}
				break;
			case R.id.btn_left:
				onBackPressed();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case R.id.to_commtent:
				replyArtId = null;
				replyUserid = null;
				new CommentDialog(TopicDetailActivity.this)
						.setEditHintData(getString(R.string.show_your_mind))
						.setOnRepeatListener(new CommentDialog.OnRepeatListener() {
							@Override
							public void repeat(String content) {
								replyMoment(articleId, replyArtId, user_id, replyUserid, content);
							}
						})
						.show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(MODIFY_TYPE, MODIFY_TYPE_ADD_COMMENT);
		intent.putExtra(ARTICLE, article);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
		user_id = sp.getString("user_id", null);
		User user = DbHelper.getInstance(this).getUserDao().load(user_id);
		if (user != null) {
			avatar = user.getAvatarUrl();
			userName = user.getName();
		}
		Intent intent = getIntent();
		articleId = intent.getStringExtra("articleId");
		options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h25))).build();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_like_and_comment_topic);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTitle(R.string.topic_title);
		ll_all_layout = (LinearLayout) findViewById(R.id.ll_all_layout);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		vto = rl_content.getViewTreeObserver();
//		rl_content.measure(0,0);
//
		Log.i("tag", "imageWidth :" + imageHeight);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		scoll = (ScrollView) findViewById(R.id.scoll);
		scoll.setVisibility(View.INVISIBLE);
		img_author_head = (ImageView) findViewById(R.id.img_author_head);
		tv_author_name = (TextView) findViewById(R.id.tv_author_name);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		gView = (GridView) findViewById(R.id.gView);
		gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new MyGridAdapter(this, datas);
		gView.setAdapter(adapter);
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_good = (TextView) findViewById(R.id.tv_good);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		eva_star_point = (EvaluateColumn) findViewById(R.id.eva_star_point);
		like_and_commend_view = (LikeAndCommentView) findViewById(R.id.like_and_commend_view);
		rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
		to_commtent = (Button) findViewById(R.id.to_commtent);

		commentListAdapter = new CommentListAdapter(this);
		like_and_commend_view.setCommentListAdapter(commentListAdapter);
		like_and_commend_view.setLikeAvatarSplitLineVisible(View.VISIBLE);
		rl_comment.setVisibility(View.VISIBLE);
		getTopicInfo(articleId);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		tv_content.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		tv_good.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		to_commtent.setOnClickListener(this);

		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					width = rl_content.getMeasuredWidth();
					//获取到宽度和高度后，可用于计算
					rl_contentPadding = rl_content.getPaddingLeft();
					rl_contentPadding = rl_contentPadding * 2;
					imageHeight = (width - rl_contentPadding) / 3;
					hasMeasured = true;

				}
				return true;
			}
		});

		like_and_commend_view.setLikeLineOnclickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//点赞列表
				Intent intent = new Intent(TopicDetailActivity.this, LikeListActivity.class);
				intent.putExtra(KEY.INTENT.ARTICLE_ID, articleId);
				startActivity(intent);
			}
		});
		gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(self, PilePhotoActivity.class);
				intent.putExtra(KEY.INTENT.IMAGE_INDEX, i);
				intent.putExtra(KEY.INTENT.IMAGE_URLS, (Serializable) urls);
				self.startActivity(intent);
			}
		});
		like_and_commend_view.setCommentItemOnClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Comment comment = (Comment) like_and_commend_view.getCommentListAdapter().getItem(i);
				String commentUserName = comment.getUserName();
				replyArtId = comment.getId();
				replyUserid = comment.getUserid();
				new CommentDialog(TopicDetailActivity.this)
						.setEditHintData("回复" + commentUserName)
						.setOnRepeatListener(new CommentDialog.OnRepeatListener() {
							@Override
							public void repeat(String content) {
								replyMoment(articleId, replyArtId, user_id, replyUserid, content);
							}
						})
						.show();
				Log.i("tag", "replyArtId :" + replyArtId);
				Log.i("tag", "repyUserid :" + replyUserid);
			}
		});
	}


	public void getTopicInfo(final String articleId) {
		WebAPIManager.getInstance().getTopicInfo(articleId, new WebResponseHandler<Article>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(TopicDetailActivity.this, R.string.loading);
				loadingDialog.showDialog();
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
				article = response.getResultObj();
				if (article != null) {
					scoll.setVisibility(View.VISIBLE);
					String url = article.getAvatar();
					ImageLoaderManager.getInstance().displayImage(url, img_author_head, options);
					tv_author_name.setText(article.getUserName() + "");
					if (TextUtils.isEmpty(article.getLocation())) {
						tv_address.setText(R.string.unknow);
					} else {
						tv_address.setText(article.getLocation());

					}
					tv_time.setText(TimeUtil.toLastTimeString(System.currentTimeMillis(), article.getCreateAt()));
					setViewByArticleType(article);
					if (user_id.equals(article.getUserid())) {
						bt_delete.setVisibility(View.VISIBLE);
					} else {
						bt_delete.setVisibility(View.GONE);
					}
					if (article.getImages() != null) {
						adapter.clearDatas();
						adapter.setDatas(article.getImages());
						urls.clear();
						urls.addAll(article.getImages());
					}
					like_and_commend_view.removeAllLikeUserAvatar();//清空点赞列表
					List<Like> likes = article.getLikes();
					article.updateLikeStateByUserId(user_id);
					tv_good.setSelected(article.isLike());

					if (likes != null && likes.size() > 0) {
						tv_good.setText(article.getLikes().size() + "");
						like_and_commend_view.setLlLikeVisible(true);
						for (int i = 0; i < likes.size(); i++) {
							like_and_commend_view.addLikeUserAvatar(likes.get(i).getAvatar());
						}
					} else {
						tv_good.setText("");
						like_and_commend_view.setLlLikeVisible(false);
					}
					List<Comment> comments = article.getComments();
					if (comments != null) {
						commentListAdapter.clear();
						commentListAdapter.add(comments);
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();
			}
		});
	}

	private void setViewByArticleType(Article article) {
		switch (article.getType()) {
			case Article.TYPE_PILE: {
				String content = getString(R.string.charge_time_line_format, getString(R.string.charge_experience), article.getPileName(), article.getContent
						());
				tv_content.setText(Html.fromHtml(content));
				tv_content.setEnabled(true);
				tv_start.setVisibility(View.VISIBLE);
				eva_star_point.setVisibility(View.VISIBLE);
				if (article.getPileScore() != null) {
					eva_star_point.setEvaluate(article.getPileScore());
				} else {
					eva_star_point.setEvaluate(0);
				}
				break;
			}
			case Article.TYPE_STATION: {
				String content = getString(R.string.charge_time_line_format, getString(R.string.charge_experience) + " ", article.getPileName(), article
						.getContent());
				tv_content.setText(Html.fromHtml(content));
				tv_content.setEnabled(true);
				tv_start.setVisibility(View.VISIBLE);
				eva_star_point.setVisibility(View.VISIBLE);
				if (article.getPileScore() != null) {
					eva_star_point.setEvaluate(article.getPileScore());
				} else {
					eva_star_point.setEvaluate(0);
				}
				break;
			}
			case Article.TYPE_TIME_LINE: {
				tv_content.setText(article.getContent());
				tv_content.setEnabled(false);
				tv_start.setVisibility(View.GONE);
				eva_star_point.setVisibility(View.GONE);
				break;
			}
			default: {
				tv_content.setText(article.getContent());
				tv_content.setEnabled(false);
				tv_start.setVisibility(View.GONE);
				eva_star_point.setVisibility(View.GONE);
				break;
			}
		}
	}

	private void jumpTODetail(Article article) {
		switch (article.getType()) {
			case Article.TYPE_PILE: {
				Intent intent = new Intent(this, PileInfoNewActivity.class);
				intent.putExtra(KEY.INTENT.PILE_ID, article.getPileId());
				this.startActivity(intent);
				break;
			}
			case Article.TYPE_STATION: {
				Intent intent = new Intent(this, PileStationInfoActivity.class);
				intent.putExtra(KEY.INTENT.PILE_ID, article.getPileId());
				this.startActivity(intent);
				break;
			}
			case Article.TYPE_TIME_LINE: {
				break;
			}
		}
	}

	public void doLike(final String articleId, String userid, final int like) {
		WebAPIManager.getInstance().doLike(articleId, userid, like, new WebResponseHandler<Object>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(TopicDetailActivity.this, R.string.loading);
				loadingDialog.showDialog();
				tv_good.setEnabled(false);
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
				List<Like> likes = article.getLikes();
				like_and_commend_view.removeAllLikeUserAvatar();

				//根据article改当前的点赞状态改变(取反)
				if (article.isLike()) {
					tv_good.setSelected(false);
					article.setLike(false);
					if (likes != null && likes.size() > 0) {
						Like oldLike = null;
						for (Like like : likes) {
							if (like.getUserid().equals(user_id)) {
								oldLike = like;
								break;
							}
						}
						if (oldLike != null) {
							likes.remove(oldLike);
						}
						article.setLikes(likes);
						tv_good.setText(likes.size() + "");
						article.setLikeCount(likes.size());
						for (Like like : likes) {
							like_and_commend_view.addLikeUserAvatar(like.getAvatar());
						}
					} else {
						article.setLikeCount(0);
					}
				} else {
					tv_good.setSelected(true);
					article.setLike(true);
					tv_good.setText(0 + "");
					Like myLike = new Like();
					myLike.setUserid(user_id);
					myLike.setAvatar(avatar);
					if (likes == null) {
						likes = new ArrayList<Like>();
					}
					likes.add(myLike);
					article.setLikes(likes);
					tv_good.setText(likes.size() + "");
					article.setLikeCount(likes.size());
					for (Like like : likes) {
						like_and_commend_view.addLikeUserAvatar(like.getAvatar());
					}
				}

			}

			@Override
			public void onFinish() {
				super.onFinish();
				tv_good.setEnabled(true);
				loadingDialog.dismiss();
			}
		});
	}

	public void replyMoment(final String articleId, String replyArtId, String userid, String replyUserid, String content) {
		WebAPIManager.getInstance().replyMoment(articleId, replyArtId, userid, replyUserid, content, new WebResponseHandler<Object>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(TopicDetailActivity.this, R.string.loading);
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
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();
				getTopicInfo(articleId);
			}
		});
	}

	public void deleteTopicInfo(final String articleId) {
		WebAPIManager.getInstance().deleteTopicInfo(articleId, new WebResponseHandler<Object>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(TopicDetailActivity.this, R.string.loading);
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
				ToastUtil.show(self, "删除成功");
				Intent intent = new Intent();
				intent.putExtra(MODIFY_TYPE, MODIFY_TYPE_DELETE);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
			this.datas = datas;
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
				linearParams.height = imageHeight;
				holder.image.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url =datas.get(position);
			ImageLoaderManager.getInstance().displayImage(url, holder.image, my0ptions);
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}
	}


	private SpannableString getClickableSpan(String title, String content) {
		String str = "#" + title + content;
		View.OnClickListener l = new View.OnClickListener() {
			//如下定义自己的动作
			public void onClick(View v) {
				Toast.makeText(self, "Click Success", Toast.LENGTH_SHORT).show();
			}
		};
		SpannableString spanableInfo = new SpannableString(str);
		int start = 0;
		int end = title.length() + 1;
//		int end = spanableInfo.length();
		spanableInfo.setSpan(new Clickable(l), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanableInfo;
	}
}

class Clickable extends ClickableSpan implements View.OnClickListener {
	private final View.OnClickListener mListener;

	public Clickable(View.OnClickListener l) {
		mListener = l;
	}

	@Override
	public void onClick(View v) {
		mListener.onClick(v);
	}
}
