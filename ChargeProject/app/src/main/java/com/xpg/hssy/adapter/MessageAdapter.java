package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.bean.Message;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

import java.util.List;

public class MessageAdapter extends EasyAdapter<Message> {

	private final static String TIME_FORMAT_MDHM = "yyyy-MM-dd HH:mm:ss";

	public MessageAdapter(Context context, List<Message> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			ImageView iv_isRead;
			TextView tv_title;
			TextView tv_content;
			TextView tv_date;

			@Override
			protected View init(LayoutInflater arg0) {
				View v = arg0.inflate(R.layout.item_message,
						null);
				iv_isRead = (ImageView) v.findViewById(R.id.iv_is_read);
				tv_title = (TextView) v.findViewById(R.id.tv_title);
				tv_content = (TextView) v.findViewById(R.id.tv_content);
				tv_date = (TextView) v.findViewById(R.id.tv_date);
				return v;
			}

			@Override
			protected void update() {
				Message message = items.get(position);
				tv_title.setText(EmptyUtil.isEmpty(message.getTitle()) ? "未命名"
						: message.getTitle());
				tv_content
						.setText(EmptyUtil.isEmpty(message.getContent()) ? "-"
								: message.getContent());
				tv_date.setText(EmptyUtil.isEmpty(message.getTitle()) ? "未命名"
						: TimeUtil.toLastTimeString(System.currentTimeMillis(),
								message.getCreateTime()));
				if (message.getStatus() == Message.HAVE_READ) {
					iv_isRead.setVisibility(View.INVISIBLE) ;
				} else {
					iv_isRead.setVisibility(View.VISIBLE) ;
				}
			}
		};
	}
}
