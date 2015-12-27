package com.xpg.hssy.adapter;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class AuthorizedUserManageAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater mInflater;
	private List<User> users;
	private User user;
	private String pile_id;
	private Long time;
	private String userName;
	private String userNo;
	private ImageView imageView_delete;
	private final static String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public AuthorizedUserManageAdapter() {
	}

	public AuthorizedUserManageAdapter(Context context, List<User> users,
			String pile_id) {
		this.context = (Activity) context;
		this.users = users;
		this.pile_id = pile_id;
		mInflater = LayoutInflater.from(this.context);

	}

	@Override
	public int getCount() {
		return this.users.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.users.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {

			view = mInflater.inflate(
					R.layout.authority_user_manage_item_layout, null);
		}
		user = users.get(position);
		userName = user.getName();
		userNo = user.getPhone();
		List<Key> keys = DbHelper.getInstance(context).getKeyDao().loadAll();
		for (Iterator<Key> iterator = keys.iterator(); iterator.hasNext();) {
			Key key = iterator.next();
			if (key.getUserId().equals(user.getUserid())) {
				time = key.getAddTime();
				break;
			}
		}

		TextView shouquan_usertime = (TextView) view
				.findViewById(R.id.shouquan_usertime);
		TextView shouquan_userno = (TextView) view
				.findViewById(R.id.shouquan_userno);
		TextView shouquan_username = (TextView) view
				.findViewById(R.id.shouquan_username);
		imageView_delete = (ImageView) view.findViewById(R.id.imageView_delete);
		shouquan_usertime.setText("添加于 "
				+ TimeUtil.format(time, TIME_FORMAT).substring(0, 10));
		shouquan_userno.setText(userNo);
		shouquan_username.setText(userName);
		imageView_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 删除用户
				removeFamily();
			}

			private void removeFamily() {
				WebAPIManager.getInstance().removeFamily(pile_id, userNo,
						new WebResponseHandler<User>(context) {

							@Override
							public void onError(Throwable e) {
								super.onError(e);
								// ToastUtil.show(context, "请求出错");
								TipsUtil.showTips(context, e);
							}

							@Override
							public void onSuccess(WebResponse<User> response) {
								super.onSuccess(response);
								Log.i("用户", response.getClass().toString());
								ToastUtil.show(context, "删除用户成功");
								/*
								 * DbHelper.getInstance(context).getUserDao()
								 * .deleteByKey(userNo); List<Key> keys =
								 * DbHelper.getInstance(context)
								 * .getKeyDao().loadAll(); List<User> familys =
								 * new ArrayList<User>(); for (Key k : keys) {
								 * if (k.getPileId().equals(pile_id) &&
								 * k.getKeyType() == Key.TYPE_FAMILY) { User
								 * user = DbHelper .getInstance(context)
								 * .getUserDao() .load(k.getUserId());
								 * familys.add(user); }
								 * 
								 * } users = familys;
								 */

								WebAPIManager.getInstance().getFamily(
										pile_id,
										new WebResponseHandler<List<User>>(
												context) {

											@Override
											public void onError(Throwable e) {
												super.onError(e);
												TipsUtil.showTips(context, e);
											}

											@Override
											public void onFailure(
													WebResponse<List<User>> response) {
												super.onFailure(response);
												// ToastUtil.show(context,response.getMessage());
												TipsUtil.showTips(context,
														response);
											}

											@Override
											public void onSuccess(
													WebResponse<List<User>> response) {
												super.onSuccess(response);
												// TODO
												users = response.getResultObj();
												if (users.size() == 0) {
													ToastUtil.show(context,
															"0:删除用户成功");
													notifyDataSetChanged();
													context.setContentView(R.layout.authorized_user_manage_list_layout);
												} else {
													LogUtils.i("授权用户", "授权用户");
													notifyDataSetChanged();
													ToastUtil.show(context,
															"删除用户成功");

												}
											}

										});
							}

							@Override
							public void onFailure(WebResponse<User> response) {
								super.onFailure(response);
								ToastUtil.show(context, "删除用户失败");

							}

						});

			}
		});
		return view;
	}

}
