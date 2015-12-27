package com.xpg.hssy.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpg.hssychargingpole.R;

public class DialogUtil {
	private static Dialog showingDialog;
	private static AnimationDrawable adAnimationDrawable;

	public static void showLoadingDialog(final Activity context, final int msgId) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Dialog loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
				View v = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
				ImageView iv = (ImageView) v.findViewById(R.id.iv);
				adAnimationDrawable = (AnimationDrawable) iv.getBackground();
				adAnimationDrawable.start();
				((TextView) v.findViewById(R.id.tv)).setText(msgId);
				loadingDialog.addContentView(v, new LayoutParams(-1, -1));
				loadingDialog.setCanceledOnTouchOutside(false);
				loadingDialog.show();

				showingDialog = loadingDialog;
			}
		});
	}

	public static void showLoadingDialog3(final Activity context, final int msgId, final int imageId) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Dialog loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
				View v = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
				ImageView iv = (ImageView) v.findViewById(R.id.iv);
				if (imageId != 0) {
					iv.setBackgroundResource(imageId);
				}
				// frameAnimation
				adAnimationDrawable = (AnimationDrawable) iv.getBackground();
				adAnimationDrawable.start();

				((TextView) v.findViewById(R.id.tv)).setText(msgId);
				loadingDialog.addContentView(v, new LayoutParams(-1, -1));
				loadingDialog.setCanceledOnTouchOutside(true);
				loadingDialog.show();

				showingDialog = loadingDialog;
			}
		});
	}

	public static void showLoadingDialog2(final Activity context, final int msgId) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog();

				Dialog loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
				View v = LayoutInflater.from(context).inflate(R.layout.loading_dialog_tran, null);
				((TextView) v.findViewById(R.id.tv)).setText(msgId);
				loadingDialog.addContentView(v, new LayoutParams(-1, -1));
				loadingDialog.setCancelable(true);
				loadingDialog.show();

				showingDialog = loadingDialog;
			}
		});
	}

	public static void showDialog(final Activity context, final View view, final boolean cancelable) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog();

				Dialog dialogBuilder = new Dialog(context, R.style.dialog_no_frame);

				dialogBuilder.setCancelable(cancelable);

				if (view != null) {
					dialogBuilder.setContentView(view);
				}

				showingDialog = dialogBuilder;
				showingDialog.show();
			}
		});
	}

	public static void showDialog(final Activity context, final Integer viewId, final Integer tvId, final Integer msgId, final Integer btn1Id, final Integer
			btn1TextId, final View.OnClickListener btn1Listener, final Integer btn2Id, final Integer btn2textId) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog();
				Dialog dialogBuilder = new Dialog(context, R.style.dialog_no_frame);

				View view = context.getLayoutInflater().inflate(viewId, null);
				if (view != null) {
					dialogBuilder.setContentView(view);
					if (tvId != null && msgId != null) {
						((TextView) view.findViewById(tvId)).setText(msgId);
					}
					if (btn1Id != null && btn1TextId != null) {
						Button btn1 = (Button) view.findViewById(btn1Id);
						btn1.setText(btn1TextId);
						if (btn1Listener != null) {
							btn1.setOnClickListener(btn1Listener);
						} else {
							btn1.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									dismissDialog();
								}
							});
						}
					}
					if (btn2Id != null && btn2textId != null) {
						Button btn2 = (Button) view.findViewById(btn2Id);
						btn2.setVisibility(View.VISIBLE);
						btn2.setText(btn2textId);
						btn2.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dismissDialog();
							}
						});
					}
				}

				showingDialog = dialogBuilder;
				showingDialog.show();
			}
		});
	}

	public static void showDialog(final Activity context, final Integer titleId, final Integer msgId, final Integer posBtnId, final OnClickListener
			posListener, final Integer negBtnId, final OnClickListener negListener, final boolean cancelable, final OnCancelListener canListener) {
		if (context == null || context.isFinishing()) {
			return;
		}

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog();

				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

				dialogBuilder.setCancelable(cancelable);
				dialogBuilder.setOnCancelListener(canListener);

				if (titleId != null) {
					dialogBuilder.setTitle(context.getString(titleId));
				}
				if (msgId != null) {
					dialogBuilder.setMessage(context.getString(msgId));
				}
				if (posBtnId != null) {
					dialogBuilder.setPositiveButton(context.getString(posBtnId), posListener);
				}
				if (negBtnId != null) {
					dialogBuilder.setNegativeButton(context.getString(negBtnId), negListener);
				}

				showingDialog = dialogBuilder.create();
				showingDialog.show();
			}
		});
	}

	public static void dismissDialog() {
		if (showingDialog != null) {
			try {
				showingDialog.dismiss();
				if (adAnimationDrawable != null) {

					adAnimationDrawable.stop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			showingDialog = null;
		}
	}
}
