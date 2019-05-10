package com.haoyd.printerlib.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoyd.printerlib.R;

public class PrinterStatusDialog extends Dialog {

    public static final int MODE_CANCEL = 0;
    public static final int MODE_PRINTING = 1;
    public static final int MODE_SUCCESS = 2;
    public static final int MODE_ERROR = 3;

    public PrinterStatusDialog(@NonNull Context context) {
        super(context, R.style.PrinterOutputDialog);
    }

    public static class Builder {
        private PrinterStatusDialog dialog;
        private Context context;

        private View contentView;
        private ImageView icon;
        private TextView alertText;

        private int currentMode = MODE_CANCEL;

        private boolean isShowing = false;

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case 1:
                            if (dialog != null && context != null) {
                                dialog.cancel();
                            }
                            break;
                        case 2:
                            if (currentMode != MODE_PRINTING) {
                                return;
                            }

                            if (dialog != null && context != null && isShowing) {
                                dialog.cancel();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        public Builder(Context context) {
            this.context = context;

            dialog = new PrinterStatusDialog(context);
            contentView = View.inflate(context, R.layout.gp_dialog_printer_output, null);
            icon = contentView.findViewById(R.id.iv_printer_dialog);
            alertText = contentView.findViewById(R.id.tv_printer_dialog);
            dialog.setContentView(contentView);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isShowing = false;
                }
            });
        }

        public void show() {
            if (context == null) {
                return;
            }
            if (context instanceof Activity && ((Activity) context).isFinishing()) {
                return;
            }

            dialog.show();
            isShowing = true;
        }

        public void cancel() {
            if (dialog != null) {
                dialog.cancel();
                currentMode = MODE_CANCEL;
                isShowing = false;
            }
        }

        public void setModePrint() {
            icon.setImageResource(R.mipmap.gp_ic_printer_going);
            alertText.setText("正在打印");
            currentMode = MODE_PRINTING;
            show();
        }

        public void setModeSuccess() {
            icon.setImageResource(R.mipmap.gp_ic_printer_success);
            alertText.setText("打印成功");
            currentMode = MODE_SUCCESS;
            show();
            handler.sendEmptyMessageDelayed(1, 500);
        }

        public void setModeError() {
            icon.setImageResource(R.mipmap.gp_ic_printer_error);
            alertText.setText("打印机异常");
            currentMode = MODE_ERROR;
            show();
            handler.sendEmptyMessageDelayed(1, 500);
        }

        public int getMode() {
            return currentMode;
        }
    }

}
