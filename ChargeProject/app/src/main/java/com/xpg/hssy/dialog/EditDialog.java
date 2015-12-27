package com.xpg.hssy.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easy.util.ToastUtil;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class EditDialog extends BaseDialog {

	private EditText et_content;
	private Button btn_ok;
	private Context context;

	public EditDialog(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.edit_dialog);
		et_content = (EditText) findViewById(R.id.et_content);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_ok:
			ToastUtil.show(context, "发送成功");
			break;

		default:
			break;
		}
	}

	@Override
	public void setContent(String content) {
		et_content.setText(content);
	}

	@Override
	public void setContent(int contentId) {
		et_content.setText(contentId);
	}

	public String getContent() {
		return et_content.getText().toString();
	}
}
