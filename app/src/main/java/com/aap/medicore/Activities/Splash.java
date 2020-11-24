package com.aap.medicore.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aap.medicore.Models.Version;
import com.aap.medicore.Models.VersionCheck;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.DateUtils;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    int SPLASH_TIME_OUT = 2000;
    Handler handler = new Handler();
    TinyDB tinyDB;
    public static String localVersion, latestVersion;
    public static final int PERMISSION_REQUEST_CODE = 1;
    //    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String[] requiredPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FirebaseAnalytics mFirebaseAnalytics;
    String tokenFCM;
    private static final String TAG = "Splash";
    private boolean flag;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            localVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            localVersion = "";
        }
        tinyDB = new TinyDB(Splash.this);
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    tokenFCM = task.getResult().getToken();
                    tinyDB.putString(Constants.tokenFCM, tokenFCM);

                    // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, tokenFCM);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).getDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        versioncheck();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[3]) == PackageManager.PERMISSION_GRANTED) {
//            findViewById(R.id.progress).setVisibility(View.VISIBLE);
//            new ForceUpdateAsync(localVersion, this).execute();
            versioncheck();
            flag = true;

        } else
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[2]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[3]) == PackageManager.PERMISSION_GRANTED)
//            if (!flag)
//                versioncheck();

    }

    public void versioncheck() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        retrofit2.Call<Version> call;
        call = RetrofitClass.getInstance().getWebRequestsInstance().versioncheck(localVersion);
        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(retrofit2.Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        permissionAccess();
//                    }
//                    else{
//                        logoutConfirmDialogBox();
//                    }
//                    permissionAccess();
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    proceed();

                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    showToast();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Version> call, Throwable t) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                showToast();
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null &&
                intent.getAction() != null &&
                intent.getAction().equals(Constants.OPEN_INBOX_MESSAGE_ACTION) &&
                intent.getExtras() != null) {
            tinyDB.putBoolean(Constants.OPEN_INBOX_MESSAGE_ACTION, true);
            tinyDB.putString(Constants.INBOX_MESSAGE_ID, intent.getExtras().getString(Constants.INBOX_MESSAGE_ID));
        }
    }

    private void showToast() {
        Toast.makeText(Splash.this, "Please try again after some time", Toast.LENGTH_SHORT).show();
    }

    private void proceed() {
        if (tinyDB.getBoolean(Constants.LoggedIn)) {
            Intent intent = new Intent(Splash.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(Splash.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(Splash.this);
        final CustomButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.version);
        btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }


    private void permissionAccess() {
//        if (!checkPermission(p1)) {
//            Log.e("TAG", p1);
//            requestPermission(p1);
//        } else if (!checkPermission(p2)) {
//            Log.e("TAG", p2);
//            requestPermission(p2);
//        } else {
//            findViewById(R.id.progress).setVisibility(View.VISIBLE);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                findViewById(R.id.progress).setVisibility(View.GONE);
//                    if (tinyDB.getBoolean(Constants.LoggedIn)) {
//                        Intent intent = new Intent(Splash.this, Home.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(Splash.this, Login.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }, SPLASH_TIME_OUT);
//        }


    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(Splash.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(Splash.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
//                Log.e("TAG", "val " + grantResults[0]);
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    permissionAccess();
//                } else {
//                    Toast.makeText(Splash.this, "The app was not allowed permission. Hence, it cannot function properly. Please consider granting it this permission.", Toast.LENGTH_LONG).show();
//                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
//                    findViewById(R.id.progress).setVisibility(View.VISIBLE);
//                    new ForceUpdateAsync(localVersion, this).execute();
                    versioncheck();
                } else {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) i = 0;
                    if (grantResults[1] != PackageManager.PERMISSION_GRANTED) i = 1;
                    if (grantResults[2] != PackageManager.PERMISSION_GRANTED) i = 2;
                    if (grantResults[3] != PackageManager.PERMISSION_GRANTED) i = 3;
                    Snackbar.make(findViewById(R.id.progress), "This app needs these permissions to function properly.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Allow", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(TAG, "Snackbar onClick: starts");
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, permissions[i])) {
                                                Log.d(TAG, "Snackbar onClick: calling requestPermissions");
                                                ActivityCompat.requestPermissions(Splash.this, permissions, PERMISSION_REQUEST_CODE);
                                            } else {
                                                // The user has permanently denied the permission, so take them to the settings
                                                Log.d(TAG, "Snackbar onClick: launching settings");
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", Splash.this.getPackageName(), null);
                                                Log.d(TAG, "Snackar onClick: Intent Uri is " + uri.toString());
                                                intent.setData(uri);
                                                Splash.this.startActivity(intent);
                                            }
                                            Log.d(TAG, "Snackbar onClick: ends");
                                        }
                                    }

                            ).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showForceUpdateDialog() {
        final Dialog dialog = new Dialog(Splash.this);
        final CustomButton btnUpdate, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.version);
        btnUpdate = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean(Constants.UPDATE_REMINDER, false);
                goToPlaystore();
                dialog.dismiss();
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void goToPlaystore() {
        Splash.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Splash.this.getPackageName())));

    }

    private void showUpdateDialog() {
        final Dialog dialog = new Dialog(Splash.this);
        final CustomButton btnUpdate, btnCancel;
        CheckBox checkBox;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_update_dialog);
        checkBox = dialog.findViewById(R.id.cbReminder);
        btnUpdate = (CustomButton) dialog.findViewById(R.id.btnUpdate);
        btnCancel = dialog.findViewById(R.id.btnNo);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean(Constants.UPDATE_REMINDER, false);
                goToPlaystore();
                dialog.dismiss();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean(Constants.UPDATE_REMINDER, checkBox.isChecked());
                proceed();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, String> {

        private String latestVersion;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e(TAG, "doInBackground: " + "ver " + latestVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return latestVersion;
        }

        @Override
        protected void onPostExecute(String latestVersion) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                    retrofit2.Call<VersionCheck> call;
                    call = RetrofitClass.getInstance().getWebRequestsInstance().versioncheck();
                    call.enqueue(new Callback<VersionCheck>() {
                        @Override
                        public void onResponse(retrofit2.Call<VersionCheck> call, Response<VersionCheck> response) {
                            if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        permissionAccess();
//                    }
//                    else{
//                        logoutConfirmDialogBox();
//                    }
//                    permissionAccess();
                                findViewById(R.id.progress).setVisibility(View.GONE);
                                boolean flag = false;

                                for (Version version : response.body().getVersionList()) {
                                    if (version.getAppVersion().equalsIgnoreCase(latestVersion)) {
                                        flag = true;
                                        switch (version.getAction()) {

                                            case "Minor change":
                                                if (tinyDB.getBoolean(Constants.UPDATE_REMINDER))
                                                    proceed();
                                                else
                                                    showUpdateDialog();
                                                break;

                                            case "Major change":
                                                showForceUpdateDialog();
                                                break;

                                            case "No change":
                                                proceed();
                                                break;
                                        }
                                    }
                                }
                                if (!flag)
                                    Toast.makeText(Splash.this, "Please contact admin to add latest version to the admin panel", Toast.LENGTH_SHORT).show();

                            } else {
                                findViewById(R.id.progress).setVisibility(View.GONE);
                                showToast();
                                Log.e(TAG, "onResponse: ");
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<VersionCheck> call, Throwable t) {
                            findViewById(R.id.progress).setVisibility(View.GONE);
                            showToast();
                            Log.e(TAG, "onFailure: ");
                            t.printStackTrace();
                        }
                    });

                } else {
                    proceed();
                    finish();
                }
            } else {
                findViewById(R.id.progress).setVisibility(View.GONE);
                showToast();
            }
            super.onPostExecute(latestVersion);
        }
    }
}

