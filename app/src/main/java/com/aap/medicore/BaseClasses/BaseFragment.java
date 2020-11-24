package com.aap.medicore.BaseClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aap.medicore.R;

public class BaseFragment extends Fragment implements OnBackPressed{


    public long mLastClickTime = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void addFragmentWithBackstack(int containerId, Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().
                add(containerId, fragment, tag)
                .addToBackStack(tag).commit();
    }

    public void addFragment(int containerId, Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(containerId, fragment, tag).commit();
    }

    public void replaceFragmentWithBackstack(int containerId, Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(containerId, fragment, tag)
                .addToBackStack(tag).commit();
    }

    public void replaceFragment(int containerId, Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, tag).commit();
    }

//    public Boolean isConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            //we are connected to a network
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    @Override
    public void onBackPressed() {

    }
}
