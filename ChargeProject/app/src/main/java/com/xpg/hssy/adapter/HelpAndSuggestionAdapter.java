package com.xpg.hssy.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.bean.HelpAndSuggestion;
import com.xpg.hssychargingpole.R;

/**
 * HelpAndSuggestionAdapter
 * 
 * @author Mazoh 帮助与反馈
 * 
 */
public class HelpAndSuggestionAdapter extends EasyAdapter<HelpAndSuggestion> {

	public HelpAndSuggestionAdapter(Context context,
			List<HelpAndSuggestion> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			TextView title;
			TextView content;

			@Override
			protected View init(LayoutInflater arg0) {
				View v = arg0.inflate(R.layout.help_suggestion_item,
						null);
				title = (TextView) v.findViewById(R.id.title);
				content = (TextView) v.findViewById(R.id.content);
				return v;
			}

			@Override
			protected void update() {
				HelpAndSuggestion message = items.get(position);
				title.setText(message.getTitle());
				content.setText(message.getContent());
			}
		};
	}

}
