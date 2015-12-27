package com.xpg.hssy.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xpg.hssychargingpole.R;

public class ToastView {
	LayoutInflater inflater;
	Context context;
	View toastRoot;
	private static ToastView instance;

	private ToastView(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		// this.toastRoot = inflater.inflate(R.layout.toast, null) ;
	}

	public static ToastView getInstance(Context context) {
		if (instance == null) {
			instance = new ToastView(context);
		}
		return instance;
	}

	public void initToast() {

		// TextView message = (TextView) toastRoot.findViewById(R.id.message);
		// message.setText("My Toast");

		Toast toast = new Toast(context);

		toast = Toast.makeText(context, "密码用户名错误哦，请重新输入吧！", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);

		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(context);
		toastView.setBackgroundResource(R.drawable.dialog_box_middle);
		imageCodeProject.setImageResource(R.drawable.not_have_user);
		toastView.addView(imageCodeProject, 0);
		toast.show();

	}
}
