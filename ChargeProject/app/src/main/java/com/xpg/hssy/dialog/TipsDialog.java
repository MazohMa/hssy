package com.xpg.hssy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xpg.hssychargingpole.R;

/**
 * Created by Guitian on 2015/10/21.
 */
public class TipsDialog extends Dialog{

    public TipsDialog(Context context,String tips){
        super(context, R.style.dialog_no_frame);
        setContentView(R.layout.login_fail_tips2);
        ((TextView)findViewById(R.id.tv_tips)).setText(tips);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return super.onTouchEvent(event);
    }

}
