package com.aap.medicore.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.aap.medicore.Activities.Login;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.R;

public class SessionTimeoutDialog {

    private Activity activity;
    private Dialog dialog;
    private TinyDB tinyDB;

    public SessionTimeoutDialog(BaseActivity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        tinyDB = new TinyDB(activity);
    }

    public Dialog getDialog(){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_session_timeout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tinyDB.putBoolean(Constants.LoggedIn, false);
                tinyDB.remove(Constants.user_id);
                tinyDB.remove(Constants.email);
                tinyDB.remove(Constants.first_name);
                tinyDB.remove(Constants.last_name);
                tinyDB.remove(Constants.username);
                Intent i = new Intent(activity, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);
                dialog.hide();
                activity.finish();
            }
        });
        return dialog;
    }
}
