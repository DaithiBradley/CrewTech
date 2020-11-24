package com.aap.medicore.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.aap.medicore.BaseClasses.BaseActivity;

public class NetworkReciever extends BroadcastReceiver {

    View view;

    public NetworkReciever() {
    }

    public NetworkReciever(View view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context instanceof BaseActivity)
            if (!isOnline(context))
                view.setVisibility(View.VISIBLE);
            else
                view.setVisibility(View.GONE);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
