package com.xpg.hssy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.bean.Comment;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Mazoh on 2015/10/16.
 */
public class CommentListAdapter extends EasyAdapter<Comment> {
	private final static String TIME_FORMAT_YMD_HMS = "MM-dd HH:mm";
	private DisplayImageOptions options;

	public CommentListAdapter(Context context) {
		super(context);
		options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnLoading(R.drawable.touxiang).showImageOnFail(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int) context
				.getResources().getDimension(R.dimen.h18))).build();
	}

	public CommentListAdapter(Context context, List items) {
		super(context, items);
		options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnLoading(R.drawable.touxiang).showImageOnFail(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int) context
				.getResources().getDimension(R.dimen.h18))).build();
	}

	@Override
	protected ViewHolder newHolder() {
		return new CommentListViewHolder();
	}

	class CommentListViewHolder extends ViewHolder {
		TextView tv_reply_name;
		TextView tv_reply;
		TextView tv_user_name;
		TextView tv_context;
		ImageView img_author_head;
		TextView tv_time;

		@Override
		protected View init(LayoutInflater layoutInflater) {
			View v = layoutInflater.inflate(R.layout.layout_comment__detail_list_item, null);
			tv_reply_name = (TextView) v.findViewById(R.id.tv_reply_name);
			tv_reply = (TextView) v.findViewById(R.id.tv_reply);
			tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
			tv_context = (TextView) v.findViewById(R.id.tv_context);
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			img_author_head = (ImageView) v.findViewById(R.id.img_author_head);
			return v;
		}


		@Override
		protected void update() {
			Comment item = get(position);
			String url = item.getAvatar();
			ImageLoaderManager.getInstance().displayImage(url, img_author_head, options, true);
			tv_time.setText(TimeUtil.format(item.getCreateAt(), TIME_FORMAT_YMD_HMS));
			if (!TextUtils.isEmpty(item.getUserid())) {
				tv_user_name.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(item.getUserName())) {
					tv_user_name.setText(R.string.unnamed);
				} else {
					tv_user_name.setText(item.getUserName());
				}
				tv_context.setText(item.getContent());
			} else {
				tv_user_name.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(item.getReplyUserid())) {
				if (TextUtils.isEmpty(item.getReplyUserName())) {
					tv_reply_name.setText(R.string.unnamed);
				} else {
					tv_reply_name.setText(item.getReplyUserName());
				}
				tv_reply_name.setVisibility(View.VISIBLE);
				tv_reply.setVisibility(View.VISIBLE);
			} else {
				tv_reply_name.setVisibility(View.GONE);
				tv_reply.setVisibility(View.GONE);
			}
		}
	}
}
