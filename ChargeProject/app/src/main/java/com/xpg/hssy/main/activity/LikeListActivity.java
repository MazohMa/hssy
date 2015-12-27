package com.xpg.hssy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.IntentUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.adapter.CommentBriefAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.bean.Like;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.view.LikeAndCommentView;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 点赞列表
 * @author Mazoh
 * @version 2.0.0
 */

public class LikeListActivity extends BaseActivity {
	private ImageButton btn_right;
	private LikeAdapter likeAdapter ;
	private ListView like_lv ;
	private List<Like> likes = new ArrayList<>();
	private LoadingDialog loadingDialog ;
	private DisplayImageOptions options;
    private String articleId = "" ;
	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		articleId = intent.getStringExtra("articleId") ;
		Log.i("tag","articleId:" + articleId) ;
		options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h25))).build();
	}
	public void getTopicInfo(final String articleId) {
		WebAPIManager.getInstance().getTopicInfo(articleId, new WebResponseHandler<Article>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(LikeListActivity.this, R.string.loading);
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
				Article article = response.getResultObj();
				if (article != null) {
					likes = article.getLikes();
					likeAdapter.addDatas(likes);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();
			}
		});
	}
	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.like_list);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		like_lv  = (ListView)findViewById(R.id.like_lv) ;
		likeAdapter = new LikeAdapter(this,likes) ;
		like_lv.setAdapter(likeAdapter);
		setTitle("点赞");
		getTopicInfo(articleId) ;
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	public class LikeAdapter extends EasyAdapter<Like> {
		private List<Like> items ;
		public LikeAdapter(Context context) {
			super(context);
		}

		public LikeAdapter(Context context, List<Like> items) {
			super(context, items);
			this.items = items ;
		}
		private void addDatas(List<Like> items){
			this.add(items);
		}
		@Override
		protected ViewHolder newHolder() {
			return new LikeViewHolder();
		}
		class LikeViewHolder extends ViewHolder {
			private ImageView img_user_icon;
			private TextView like_name;
			@Override
			protected View init(LayoutInflater layoutInflater) {
				View view = layoutInflater.inflate(R.layout.like_list_item, null);
				img_user_icon = (ImageView) view.findViewById(R.id.img_user_icon);
				like_name = (TextView) view.findViewById(R.id.like_name);
				return view;
			}
			@Override
			protected void update() {
				Like like = get(position) ;
				String avatar = like.getAvatar() ;
				String name = like.getUserName() ;
				ImageLoaderManager.getInstance().displayImage(avatar, img_user_icon, options, true);
				like_name.setText(name + "");
			}
		}
	}
}
