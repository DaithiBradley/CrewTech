package com.aap.medicore.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aap.medicore.R;
import com.google.android.material.snackbar.Snackbar;

public class UIUtils {


    public static void shakeView(Context context, View view) {
        Animation shake;
        shake = AnimationUtils.loadAnimation(context, R.anim.animation_shake);
        view.startAnimation(shake); // starts animation
    }

    public static void showSnackbar(Context context, View view, String message, int length) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, length);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }

    public static int convertToDp(int value, Context context) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (value * d);
    }

    public static void goToSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static boolean hasRequiredPermissions(Context context, String[] permissions){
        if (context!=null){
            for (String permission:permissions){
                if (ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
}
