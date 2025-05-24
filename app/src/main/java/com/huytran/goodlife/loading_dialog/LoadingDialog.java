package com.huytran.goodlife.loading_dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.huytran.goodlife.R;

public class LoadingDialog {
    private Dialog dialog;

    public LoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
    }

    public void show(String message) {
        TextView messageText = dialog.findViewById(R.id.loading_message);
        messageText.setText(message);
        dialog.show();
    }

    public void hide() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}


