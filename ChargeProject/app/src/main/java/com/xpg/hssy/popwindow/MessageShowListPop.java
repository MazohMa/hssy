package com.xpg.hssy.popwindow;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.easy.popup.EasyPopup;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年6月19日
 * @version 2.0.0
 */

public class MessageShowListPop extends EasyPopup implements OnClickListener {
	private Context context;
	private TextView unread, show_all, clear_all;
	private static final int UNREAD = 0;
	private static final int READ = 1;
	private static final int SHOW_ALL = 2;
	private static final int CLEAR_ALL = 3;

	public MessageShowListPop(Context context) {
		super(context, R.layout.message_show_list_pop,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.context = context;
		initView();
		initData();
		initListener();
	}

	private void initListener() {
		unread.setOnClickListener(this);
		show_all.setOnClickListener(this);
		clear_all.setOnClickListener(this);

	}

	private void initData() {

	}

	public void setTextNum(String str) {
		if (unread != null) {
			unread.setText("所有未读（" + str + "）");
		}
	}

	private void initView() {
		unread = (TextView) findViewById(R.id.unread);
		show_all = (TextView) findViewById(R.id.show_all);
		clear_all = (TextView) findViewById(R.id.clear_all);
		RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					
					MessageShowListPop.this.dismiss();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unread:
			if (itemOnClick != null) {
				MessageShowListPop.this.dismiss();
				itemOnClick.click(UNREAD);
			}
			break;
		case R.id.show_all:
			if (itemOnClick != null) {
				MessageShowListPop.this.dismiss();

				itemOnClick.click(SHOW_ALL);
			}
			break;
		case R.id.clear_all:
			if (itemOnClick != null) {
				MessageShowListPop.this.dismiss();

				itemOnClick.click(CLEAR_ALL);
			}
			break;
		default:
			break;
		}
	}

	private ItemOnClick itemOnClick;

	public ItemOnClick getItemOnClick() {
		return itemOnClick;
	}

	public void setItemOnClick(ItemOnClick itemOnClick) {
		this.itemOnClick = itemOnClick;
	}

	public interface ItemOnClick {
		public void click(int index);
	}
}
