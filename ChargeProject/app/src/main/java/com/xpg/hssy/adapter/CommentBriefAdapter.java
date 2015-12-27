package com.xpg.hssy.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.bean.Comment;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by black-Gizwits on 2015/10/15.
 */
public class CommentBriefAdapter extends EasyAdapter<Comment> {
	public CommentBriefAdapter(Context context) {
		super(context);
	}

	public CommentBriefAdapter(Context context, List items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new CommentBriefViewHolder();
	}

	class CommentBriefViewHolder extends ViewHolder {
		TextView tv_commend_detail;
		String user_name;
		String reply_name;
		String commend_context;

		@Override
		protected View init(LayoutInflater layoutInflater) {
			View v = layoutInflater.inflate(R.layout.layout_comment__brief_list_item, null);
			tv_commend_detail = (TextView) v.findViewById(R.id.tv_comment_detail);
			return v;
		}

		@Override
		protected void update() {
			Comment item = get(position);
			if (!TextUtils.isEmpty(item.getUserid())) {
				if (TextUtils.isEmpty(item.getUserName())) {
					user_name=context.getString(R.string.unnamed);
				} else {
					user_name = item.getUserName();
				}
			}
			commend_context=item.getContent();
			if (!TextUtils.isEmpty(item.getReplyUserid())) {
				if (TextUtils.isEmpty(item.getReplyUserName())) {
					reply_name=context.getString(R.string.unnamed);
				}else{
					reply_name=item.getReplyUserName();
				}
				String commend_detail = context.getString(R.string.commend_string,user_name," 回复 ",reply_name,commend_context);
				tv_commend_detail.setText(Html.fromHtml(commend_detail));
			}else{
				String commend_detail = context.getString(R.string.commend_string,user_name,"","",commend_context);
				tv_commend_detail.setText(Html.fromHtml(commend_detail));
			}
		}
	}
}
