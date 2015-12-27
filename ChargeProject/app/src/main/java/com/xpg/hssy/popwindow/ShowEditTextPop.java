package com.xpg.hssy.popwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.easy.util.KeyboardUtil;
import com.easy.util.MeasureUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 */
public class ShowEditTextPop extends BasePop implements OnClickListener {
	private Context context;
	private EditText ed_group_discuss;
	private Button btn_repeat;


	@SuppressLint("DefaultLocale")
	public ShowEditTextPop(Context context) {
		super(context);// 必须调用父类的构造函数
		this.context = context;
	}

	public void init() {
	}
	public void showPop(View view) {
		this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		ed_group_discuss.requestFocus();
		KeyboardUtil.show(context, ed_group_discuss);
	}

	public void setEditTextData(String str) {
		if (ed_group_discuss != null) {
			ed_group_discuss.setText(str);
		}
	}
	public void setEditHintData(String str) {
		if (ed_group_discuss != null) {
			ed_group_discuss.setHint(str);
		}
	}
	@Override
	protected void initData() {
		super.initData();

	}


	@SuppressLint("InflateParams")
	@Override
	protected void initUI() {
		super.initUI();
		View v = LayoutInflater.from(mActivity).inflate(R.layout.editext_pop, null);
		//关于pop，我们也可以在构造函数中传入view，而不必setContentView，因为构造函数中的view，其实最终也要setContentView
		setContentView(v);
		ed_group_discuss = (EditText) v.findViewById(R.id.ed_group_discuss);
		btn_repeat = (Button) v.findViewById(R.id.btn_repeat);
		setWidth((MeasureUtil.screenWidth));
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_repeat.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_repeat:
				if(onClickButton != null){
					String content = ed_group_discuss.getText().toString().trim() ;
					if(content.equals("")){
						ToastUtil.show(context,"输入内容不能为空");
						return ;
					}
					onClickButton.click(content);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		super.setOnDismissListener(onDismissListener);
		if(KeyboardUtil.isShowing(context)){
			KeyboardUtil.hide(context, ed_group_discuss);
		}
	}
	public OnClickButton getOnClickButton() {
		return onClickButton;
	}

	public void setOnClickButton(OnClickButton onClickButton) {
		this.onClickButton = onClickButton;
	}

	private OnClickButton onClickButton ;
	public interface OnClickButton{
		public void click(String content) ;
	}
}
