package com.xpg.hssy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * PileMessageCommandAdapter 桩评价adapter
 *
 * @author Mazoh
 */
public class PileMessageCommandAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Command> commands;
	private Context context;
	private Command command;

	public PileMessageCommandAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return commands.size();
	}

	@Override
	public Object getItem(int position) {
		return commands.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHandler handler = null;
		if (convertView == null) {
			convertView = this.mInflater.inflate(R.layout.pile_message_command_item_layout, null);
			handler = new ViewHandler(convertView);
			convertView.setTag(handler);
		} else {
			handler = (ViewHandler) convertView.getTag();
		}
		handler.set(position);

		return convertView;
	}

	public class ViewHandler {

		private final static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
		private int position;
		private TextView tv_user_name;
		private TextView date_y_m;
		private TextView date_h_m;
		private TextView command_content;
		private EvaluateColumn eva_star_point;

		private long createTime;

		public ViewHandler(View convertView) {
			initUI_handler(convertView);
		}

		private void initUI_handler(View view) {

			// 实例化UI
			tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
			date_y_m = (TextView) view.findViewById(R.id.date_y_m);
			date_h_m = (TextView) view.findViewById(R.id.date_h_m);
			eva_star_point = (EvaluateColumn) view.findViewById(R.id.eva_star_point);
			command_content = (TextView) view.findViewById(R.id.command_content);
		}

		private void set(int position) {
			this.position = position;
			// set数据
			command = commands.get(position);
			//
			// StringBuilder strb = new StringBuilder(command.getUserPhone()) ;
			// int maxIndex = strb.length() > 7 ? 7 : strb.length();
			// int minIndex = strb.length() > 3 ? 3 : 1;
			// for (int i = minIndex; i < maxIndex; i++) {
			// strb.setCharAt(i, '*') ;
			// }
			if (command.getUserPhone() != null) {
				//需求改作优先显示昵称
				if (EmptyUtil.notEmpty(command.getUserName())) {
					tv_user_name.setText(command.getUserName());
				} else {
					tv_user_name.setText(command.getUserPhone().replaceFirst("(.{3}).{4}(.*)" + "", "$1****$2"));
				}
			} else {
				tv_user_name.setText("");
			}
			command.getCreateTime();
			date_y_m.setText(TimeUtil.format(command.getCreateTime(), TIME_FORMAT).substring(0, 10));
			date_h_m.setText(TimeUtil.format(command.getCreateTime(), TIME_FORMAT).substring(11, 16));
			command_content.setText(command.getEvaluate());
			int count = command.getLevel();
			eva_star_point.setEvaluate(count);

		}

	}

}
