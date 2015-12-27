package com.xpg.hssy.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.xpg.hssychargingpole.R;

/**
 * Created by Administrator on 2015/11/5.
 */
public class PileNameSpan extends ClickableSpan{
	Context context;

	public PileNameSpan(Context context){
		super();
		this.context = context;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(context.getResources().getColor(R.color.water_blue));
	}


	@Override
	public void onClick(View view) {
		view.invalidate();
	}
}
