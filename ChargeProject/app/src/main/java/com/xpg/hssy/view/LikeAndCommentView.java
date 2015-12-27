package com.xpg.hssy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easy.adapter.EasyAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.bean.Comment;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by black-Gizwits on 2015/10/14.
 */
public class LikeAndCommentView extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener {


	private LinearLayout ll_like_avatar_line;
	private FixHeightListView fhlv_comment_list;
	private View split_line;
	private List<String> user_avatarList;
	private DisplayImageOptions options;

	private EasyAdapter commentListAdapter;

	private OnClickListener likeLineOnclickListener;
	private AdapterView.OnItemClickListener commentItemOnClickListener;

	public LikeAndCommentView(Context context) {
		this(context, null);
	}

	public LikeAndCommentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LikeAndCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(getContext()).inflate(R.layout.layout_like_and_comment_view, this, true);
		initAttribute(context, attrs);
		initChild();
		initData();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public LikeAndCommentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		LayoutInflater.from(getContext()).inflate(R.layout.layout_like_and_comment_view, this, true);
		initAttribute(context, attrs);
		initChild();
		initData();
	}

	private void initAttribute(Context context, AttributeSet attrs) {
	}

	private void initChild() {
		ll_like_avatar_line = (LinearLayout) findViewById(R.id.ll_like_avatar_line);
		fhlv_comment_list = (FixHeightListView) findViewById(R.id.fhlv_comment_list);
		split_line = findViewById(R.id.split_line);
		ll_like_avatar_line.setOnClickListener(this);
		fhlv_comment_list.setOnItemClickListener(this);
	}

	private void initData() {
		user_avatarList = new ArrayList<>();
		if (!isInEditMode()) {
			options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
					.touxiang).showImageOnLoading(R.drawable.touxiang).showImageOnFail(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
					getResources().getDimension(R.dimen.h18))).build();

		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (user_avatarList.size() == 0) {
			ll_like_avatar_line.setVisibility(GONE);
		} else {
			ll_like_avatar_line.setVisibility(VISIBLE);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void onClick(View v) {
		if (likeLineOnclickListener != null) likeLineOnclickListener.onClick(v);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (commentItemOnClickListener != null) commentItemOnClickListener.onItemClick(parent, view, position, id);
	}

	public OnClickListener getLikeLineOnclickListener() {
		return likeLineOnclickListener;
	}

	public void setLikeLineOnclickListener(OnClickListener likeLineOnclickListener) {
		this.likeLineOnclickListener = likeLineOnclickListener;
	}

	public AdapterView.OnItemClickListener getCommentItemOnClickListener() {
		return commentItemOnClickListener;
	}

	public void setCommentItemOnClickListener(AdapterView.OnItemClickListener commentItemOnClickListener) {
		this.commentItemOnClickListener = commentItemOnClickListener;
	}

	public void setLlLikeVisible(boolean visible) {
		if (ll_like_avatar_line != null) {
			if (visible) {
				ll_like_avatar_line.setVisibility(VISIBLE);
			} else {
				ll_like_avatar_line.setVisibility(GONE);
			}
		}
	}

	public void addLikeUserAvatar(String url) {
		Log.i("tag", "addLikeUserAvatar");
		ImageView avatar = new ImageView(getContext(), null);
		LayoutParams parames = new LayoutParams((int) getResources().getDimension(R.dimen.h30), (int) getResources().getDimension(R.dimen.h30));
		avatar.setLayoutParams(parames);
		avatar.setPadding(5, 5, 5, 5);
		avatar.setImageResource(R.drawable.touxiang);
		ll_like_avatar_line.addView(avatar);
		user_avatarList.add(url);
		ImageLoaderManager.getInstance().displayImage(url, avatar, options, true);
		ll_like_avatar_line.setVisibility(VISIBLE);
	}

	public void removeLikeUserAvatar(String url) {
		int index = -1;
		for (int i = 0; i < user_avatarList.size(); i++) {
			if (user_avatarList.get(i).equals(url)) {
				index = i;
				break;
			}
		}
		if (index != -1) removeLikeUserAvatar(index);
	}

	public void removeAllLikeUserAvatar() {
		if (user_avatarList != null && user_avatarList.size() > 0) {
			user_avatarList.clear();
			ll_like_avatar_line.removeAllViews();
			ll_like_avatar_line.setVisibility(GONE);
		}
	}

	public void removeLikeUserAvatar(int index) {
		ll_like_avatar_line.removeViewAt(index);
		user_avatarList.remove(index);
		if (user_avatarList.size() == 0) {
			ll_like_avatar_line.setVisibility(GONE);
		}
		Log.i("tag", "removeLikeUserAvatar");
	}

	public Adapter getCommentListAdapter() {
		return commentListAdapter;
	}

	public void setCommentListAdapter(EasyAdapter commentListAdapter) {
		this.commentListAdapter = commentListAdapter;
		if (commentListAdapter != null) {
			fhlv_comment_list.setAdapter(commentListAdapter);
			commentListAdapter.notifyDataSetChanged();
		}
	}

	public void setLikeAvatarSplitLineVisible(int visible)
	{
		split_line.setVisibility(visible);
	}

	public void addComment(Comment comment) {
		if (this.commentListAdapter != null && comment != null) {
			this.commentListAdapter.add(comment);
		}
	}

	public void removeAllComments(List<Comment> comments) {
		if (this.commentListAdapter != null) {
			this.commentListAdapter.remove(comments);
		}
	}
}
