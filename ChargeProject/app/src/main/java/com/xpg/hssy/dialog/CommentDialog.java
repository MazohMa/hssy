package com.xpg.hssy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.easy.util.ToastUtil;
import com.xpg.hssychargingpole.R;

/**
 * Created by Gunter on 2015/12/7.
 */
public class CommentDialog extends Dialog implements View.OnClickListener {

    private EditText et_comment;
    private Context context;
    private String hintContent;
    private InputMethodManager imm;
    private OnRepeatListener onRepeatListener;

    public CommentDialog(Context context) {
        this(context, R.style.dialog_full_screen);
        this.context = context;
    }

    private CommentDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);
        init();
    }

    private void init() {
        setCancelable(true);
        //初始化Dialog的大小和位置
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        et_comment = (EditText) findViewById(R.id.ed_group_discuss);
        if (hintContent!=null && !hintContent.equals("")) {
            et_comment.setHint(hintContent);
        }
        findViewById(R.id.btn_repeat).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_repeat:
                dismiss();
                if(onRepeatListener != null){
                    String content = et_comment.getText().toString().trim() ;
                    if(content.equals("")){
                        ToastUtil.show(context, "输入内容不能为空");
                        return ;
                    }
                    onRepeatListener.repeat(content);
                }
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        Log.e("Gunter", getWindow().getAttributes().softInputMode + "");
        et_comment.requestFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
    }

    public CommentDialog setEditHintData(String str) {
        hintContent = str;
        if (et_comment != null) {
            et_comment.setHint(str);
        }
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //隐藏软键盘
        if(imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
        }
    }

    public CommentDialog setOnRepeatListener(OnRepeatListener onRepeatListener) {
        this.onRepeatListener = onRepeatListener;
        return this;
    }

    public interface OnRepeatListener {
        void repeat(String content);
    }

}
