package com.aap.medicore.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Adapters.AdapterSelectImages;
import com.aap.medicore.Adapters.VehicleCheckListSpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.VehicleChecklistForm;
import com.aap.medicore.Models.VehicleChecklistResponse;
import com.aap.medicore.Models.VehicleDetail;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;

public class VehicleCheckList extends BaseActivity {
    ImageView ivBack;
    TinyDB tinyDB;
    LinearLayout llViews;
    CustomButton btnSubmit;
    LinearLayout barcodelayout;
    int vehicleId = 0;
    HorizontalScrollView hsvLayout;
    CustomTextView subtitle;
    public static String vehicle_barcode;
    public static int vehicle_check = 2;
    RelativeLayout vehicleDetail;
    ImageView ivSelectImages;
    RecyclerView rvSelectImages;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    DatabaseHandler handler;
    AdapterSelectImages adapterSelectImages;
    private final int REQUEST_GALLERY_PERMISSION = 5568;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    ArrayList<SelectImagesModel> myImages;
    LinearLayoutManager imagesManager;

    LinearLayout barcodeviews;

    private LinearLayout llNoVehicleData, llVRegistration, llVName,llVehicleDetails;
    List<View> allViewInstance = new ArrayList<View>();
    private CustomTextView tvCallsCount, tvBedsCount, tvEngineHorsepower, tvColor, tvRegistrationNo, tvStatus, tvState, tvVehicleErrorMessage;
    Context context = VehicleCheckList.this;
    CustomTextView barcode_txt;
    String task_id = "", position;
    String formId = "", formTitle = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_check_list);
        getSupportActionBar().hide();


        tinyDB = new TinyDB(getApplicationContext());
        subtitle = (CustomTextView)findViewById(R.id.subtitle);
        ivSelectImages = findViewById(R.id.ivSelectImages);
        rvSelectImages = findViewById(R.id.rvImages);
        handler = new DatabaseHandler(VehicleCheckList.this);
        imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myImages = new ArrayList<>();

        barcodelayout = (LinearLayout) findViewById(R.id.barcodelayout);
        barcodeviews = (LinearLayout)findViewById(R.id.barcodeviews);


        vehicleId = tinyDB.getInt("vehicle_id");
        Log.d("ID","vehicle id is: " + vehicleId);
        hsvLayout = (HorizontalScrollView)findViewById(R.id.hsv);
        init();
        clickListeners();
        if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {
            llNoVehicleData.setVisibility(View.GONE);
            llVName.setVisibility(View.VISIBLE);
            llVRegistration.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String json = tinyDB.getString(Constants.vehicle_details);

            final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);

            tvBedsCount.setText(obj.getBedSpace() + "");
            tvColor.setText(obj.getVehicleName());
            tvEngineHorsepower.setText(obj.getEnginHouspower() + "");
            tvRegistrationNo.setText(obj.getRegistrationno());

            llVehicleDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(VehicleCheckList.this, VehicleDetails.class);
                    i.putExtra(Constants.user_id, obj.getUserId());
                    startActivity(i);
                }
            });


        } else {

            llNoVehicleData.setVisibility(View.VISIBLE);
            llVName.setVisibility(View.INVISIBLE);
            llVRegistration.setVisibility(View.INVISIBLE);

        }
        hitVehicleCheckList();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");



                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");

                        new Handler().post(new Runnable() {
                            public void run() {

                                final Dialog dialog = new Dialog(VehicleCheckList.this);
                                final CustomButton btnClose, btnYes;
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_alert);
                                dialog.setCancelable(false);
                                btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
                                btnClose = (CustomButton) dialog.findViewById(R.id.btnNo);
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                                btnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();

//                                        checkPermissions();
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });
                        break;
                }
                break;
        }

        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {

                ArrayList<String> img = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                for (int i = 0; i < img.size(); i++) {
                    String file = compressImage(img.get(i));

                    SelectImagesModel model = new SelectImagesModel();
                    model.setTempUri(Uri.parse(file));
                    File f = new File(file);
                    model.setFinalImage(f);
                    model.setName(f.getName());

                    myImages.add(model);
                    Log.e("myImages", img.get(i));
                }

                if (adapterSelectImages != null) {
                    adapterSelectImages.setItems(myImages);
                    adapterSelectImages.notifyDataSetChanged();
                } else {
                    adapterSelectImages = new AdapterSelectImages(myImages, VehicleCheckList.this, ivSelectImages);
                    rvSelectImages.setLayoutManager(imagesManager);
                    rvSelectImages.setAdapter(adapterSelectImages);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT ).getDialog();
            }
        }
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1280.0f;
        float maxWidth = 1024.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }



    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }


    @Override
    protected void onResume() {
        super.onResume();
        HorizontalScrollView horizontalScrollView;
        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontal);
        if(SettingValues.getBarcodeVal().equals("")){
            SettingValues.setBarcodeVal("Scan Barcode");

//            barcode_txt.setText(SettingValues.getBarcodeVal());
        }
        else {
            barcodeviews.removeAllViews();
//            barcode_txt.setText(SettingValues.getBarcodeVal());
            for(int i =0; i<SettingValues.getBarcodelist().size();i++) {
                CustomTextView txt = new CustomTextView(this);

                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                LinearLayout.LayoutParams txtperams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                txtperams.setMargins(10, 0, 10, 0);
                txt.setLayoutParams(txtperams);
                int val = i+1;
                txt.setText("Value"+val+": "+SettingValues.getBarcodelist().get(i)+",");
                txt.setTextSize(14);
                txt.setGravity(Gravity.CENTER_VERTICAL);
                barcodeviews.addView(txt);
            }
        }
    }
    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(VehicleCheckList.this);
        llViews = findViewById(R.id.llViews);
        btnSubmit = findViewById(R.id.btnLogin);
        llNoVehicleData = findViewById(R.id.llNoVehicleData);
        llVRegistration = findViewById(R.id.llVRegistration);
        llVName = findViewById(R.id.llVName);
        tvBedsCount = findViewById(R.id.tvBedsCount);
        tvEngineHorsepower = findViewById(R.id.tvEngineHorsepower);
        tvColor = findViewById(R.id.tvColor);

        tvRegistrationNo = findViewById(R.id.tvRegistrationNo);
        llVehicleDetails = findViewById(R.id.llVehicleDetails);
        vehicleDetail = (RelativeLayout)findViewById(R.id.vehicle_detail);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        barcodelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(VehicleCheckList.this,ScanActivity.class);
                startActivity(i);
            }
        });


        ivSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionAccess();

                } else {
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, Options.init().setRequestCode(REQUEST_GALLERY_PERMISSION));
                } else {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void permissionAccess() {
        if (!checkPermission(p1)) {
            Log.e("TAG", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG", p2);
            requestPermission(p2);
        } else {
            Options options = Options.init()
                    .setRequestCode(REQUEST_GALLERY_PERMISSION)                                           //Request code for activity results
                    .setCount(30 - myImages.size())                                                   //Number of images to restict selection count
                    .setFrontfacing(false)                                         //Front Facing camera on start
                    .setExcludeVideos(true)      ;                                 //Option to exclude videos

            Pix.start(this, options);
            /*
            Pix.start(VehicleCheckList.this,                    //Activity or Fragment Instance
                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
                    30 - myImages.size());*/
//            Toast.makeText(CreateJob.this, "All permission granted", Toast.LENGTH_LONG).getDialog();
        }

    }


    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(VehicleCheckList.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(VehicleCheckList.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VehicleCheckList.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }


    public void hitVehicleCheckList() {
        retrofit2.Call<VehicleChecklistResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().getVehicleChecklist(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<VehicleChecklistResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VehicleChecklistResponse> call, final Response<VehicleChecklistResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        getFormDetail(response);
                    } else if (response.body().getStatus() == 404) {

                    }
                    else
                    {
                        llNoVehicleData.setVisibility(View.VISIBLE);
                    }
                }else if (response.code()==401){
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(VehicleCheckList.this).getDialog().show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<VehicleChecklistResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("NewApi")
    public void getFormDetail(final Response<VehicleChecklistResponse> response) {

        final VehicleChecklistForm obj = response.body().getVehicleChecklistForm();
            subtitle.setText(obj.getSub_title());
            if(obj.getBarcode_enabled().equals("yes")){

                barcodelayout.setVisibility(View.VISIBLE);
            }



        for (int position = 0; position < obj.getVehicleChecklistFields().size(); position++) {
            vehicleDetail.setVisibility(View.VISIBLE);


            if (obj.getVehicleChecklistFields().get(position).getType().equalsIgnoreCase("file")) {

                findViewById(R.id.hsv).setVisibility(View.VISIBLE);


                allViewInstance.add(hsvLayout);
                Log.d("SIZE", "Image size is: " + myImages.size());
                if (myImages.size() > 0) {
                    if (adapterSelectImages != null) {
                        adapterSelectImages.setItems(myImages);
                        adapterSelectImages.notifyDataSetChanged();
                    } else {
                        adapterSelectImages = new AdapterSelectImages(myImages, VehicleCheckList.this, ivSelectImages);
                        rvSelectImages.setLayoutManager(imagesManager);
                        rvSelectImages.setAdapter(adapterSelectImages);
                    }
                }
            }


         else   if (obj.getVehicleChecklistFields().get(position).getType().equalsIgnoreCase("text") && !(obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("Time")) && !(obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("signature"))) {
                RelativeLayout ettxt_start = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                ettxt_start.setGravity(Gravity.CENTER);
                ettxt_start.setLayoutParams(perams);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);

                CustomEditText etText = new CustomEditText(context);
                etText.setLayoutParams(perams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getVehicleChecklistFields().get(position).getPlaceholder());
                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                etparams.setMargins(0, 0, 0, 0);
                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setSingleLine();
                etText.setLayoutParams(etparams);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(10, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                ettxt_start.addView(etText);
                ettxt_start.addView(star , layoutParams);
//                ettxt_start.addView(star);

                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    ettxt_start.setBackground(getDrawable(R.drawable.required));
                }
                llViews.addView(ettxt_start);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


            } else if (obj.getVehicleChecklistFields().get(position).getType().equalsIgnoreCase("number")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);

                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                etparams.setMargins(0, 0, 0, 0);
                CustomEditText etText = new CustomEditText(context);

                etText.setLayoutParams(etparams);
                etText.setTextSize(12);
                etText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getVehicleChecklistFields().get(position).getPlaceholder());

                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);



                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(10, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);

                llViews.addView(relativeLayout);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("checkbox-group")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvCheckBoxTitle = new CustomTextView(context);
                tvCheckBoxTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                tvCheckBoxTitle.setTextSize(14);

                tvCheckBoxTitle.setTypeface(tvCheckBoxTitle.getTypeface(), Typeface.NORMAL);
                tvCheckBoxTitle.setTextColor(Color.parseColor("#616060"));
                tvCheckBoxTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvCheckBoxTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvCheckBoxTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().size(); i++) {
                    AppCompatCheckBox checkBox = new AppCompatCheckBox(context);
                    LinearLayout.LayoutParams cbPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                    cbPerams.setMargins(8, 8, 8, 8);
                    checkBox.setText(obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getLabel());
                    checkBox.setLayoutParams(cbPerams);
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    checkBox.setTypeface(font);
                    checkBox.setTextColor(Color.parseColor("#616060"));


                    checkBox.setTextSize(13);

                    checkBox.setTag(obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getId() + "," + obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getLabel());


                    ll.addView(checkBox);
                }
                llViews.addView(ll);

                allViewInstance.add(ll);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("radio-group")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);

                perams.setMargins(20, 20, 20, 20);

                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);

                tvRadioButtonTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());

                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));
                tvRadioButtonTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);

                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                RadioGroup rg = new RadioGroup(context);
                rg.setTag(obj.getVehicleChecklistFields().get(position).getFieldId() + "");

                for (int i = 0; i < obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().size(); i++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getLabel());
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    rb.setTypeface(font);
                    rb.setTextSize(13);
                    rb.setTextColor(Color.parseColor("#616060"));
                    rg.addView(rb);

                    rb.setTag(obj.getVehicleChecklistFields().get(position).getFieldId()+ "," + obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getId() + "," + obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions().get(i).getLabel());
                }

                rg.setLayoutParams(perams);

                llViews.addView(rg);
                allViewInstance.add(rg);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("textarea")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);

                CustomEditText etTextArea = new CustomEditText(context);

                etTextArea.setPadding(20, 10, 20, 10);
                etTextArea.setHint(obj.getVehicleChecklistFields().get(position).getPlaceholder());

                etTextArea.setGravity(Gravity.TOP);
                etTextArea.setTextColor(getResources().getColor(R.color.colorPrimary));
                etTextArea.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etTextArea.setMinHeight(60);
                etTextArea.setMinimumHeight(60);
                etTextArea.setTextSize(12);
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                etTextArea.setLayoutParams(prams);

                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,4,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    etTextArea.setBackground(getDrawable(R.drawable.required));

                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etTextArea);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);
                allViewInstance.add(etTextArea);

                llViews.addView(view);

            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("select")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 0, 0);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());

                perams.setMargins(4, 4, 0, 0);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);

                perams.setMargins(20, 20, 20, 20);

                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);
                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                AppCompatSpinner spinner = new AppCompatSpinner(context);
                VehicleCheckListSpinnerArrayAdapter adapter = new VehicleCheckListSpinnerArrayAdapter(context,
                        R.layout.custom_spinner_item, obj.getVehicleChecklistFields().get(position).getVehicleChecklistOptions());
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                llViews.addView(spinner);

                allViewInstance.add(spinner);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("text") && (obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("Time"))) {


                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);

                final String lable = obj.getVehicleChecklistFields().get(position).getLabel();
                final CustomTextView etText = new CustomTextView(context);
                // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getVehicleChecklistFields().get(position).getPlaceholder());
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setLayoutParams(prams);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                etText.setText("  " + selectedHour + " : " + selectedMinute);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });
            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("date")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);

                final String lable = obj.getVehicleChecklistFields().get(position).getLabel();
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                final CustomTextView etText = new CustomTextView(context);
                etText.setLayoutParams(prams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getVehicleChecklistFields().get(position).getPlaceholder());


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getVehicleChecklistFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);



                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(100);
                etText.setMinimumHeight(100);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);
                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        //String myFormat = " MM - dd - yyyy"; //In which you need put here
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        etText.setText(" " + sdf.format(myCalendar.getTime()));
                    }

                };

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("header")) {
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(8, 8, 8, 8);

                etText.setLayoutParams(perams);
                if (obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("h1")) {
                    etText.setTextSize(20);
                } else if (obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("h2")) {
                    etText.setTextSize(18);
                } else if (obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("h3")) {
                    etText.setTextSize(16);
                } else if (obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("h4")) {
                    etText.setTextSize(14);
                }
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setText(obj.getVehicleChecklistFields().get(position).getLabel());



                etText.setPadding(28, 28, 28, 28);
                etText.setMinHeight(140);
                etText.setMinimumHeight(140);
                etText.setGravity(Gravity.CENTER);
                etText.setSingleLine();
                etText.setTypeface(etText.getTypeface(), Typeface.NORMAL);
                etText.setTextColor(Color.parseColor("#616060"));
                etText.setTag(obj.getVehicleChecklistFields().get(position).getPlaceholder());
                Log.e("Tag", obj.getVehicleChecklistFields().get(position).getPlaceholder());

                llViews.addView(etText);

                // TEXTVIEW


                allViewInstance.add(etText);


            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("file")) {
//                NestedScrollView nestedScrollView = new NestedScrollView(TaskDetails.this);
//                LinearLayout.LayoutParams nsvPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                nsvPerams.setMargins(8, 8, 8, 8);
//
//                nestedScrollView.setLayoutParams(nsvPerams);
//
//                LinearLayout rl = new LinearLayout(TaskDetails.this);
//                LinearLayout.LayoutParams rlPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                rlPerams.setMargins(12, 0, 12, 0);
//                rl.setLayoutParams(nsvPerams);
//                rl.setOrientation(LinearLayout.HORIZONTAL);
//
//                ImageView iv = new ImageView(TaskDetails.this);
//                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(140, 140);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                ivPerams.setMargins(12, 20, 20, 20);
//                ivPerams.gravity = Gravity.CENTER_VERTICAL;
//
//                iv.setLayoutParams(ivPerams);
//                iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_pictures));
//
//                iv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            permissionAccess();
//
//                        } else {
//                            Pix.start(TaskDetails.this,                    //Activity or Fragment Instance
//                                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
//                                    30 - imagesList.size());
//                        }
//                    }
//                });
//
//                rvSelectImages = new RecyclerView(TaskDetails.this);
//                LinearLayout.LayoutParams rvPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                rvPerams.setMargins(20, 20, 20, 20);
//                rvSelectImages.setLayoutParams(rvPerams);
//                rvSelectImages.setNestedScrollingEnabled(false);
//
//                rvSelectImages.setLayoutManager(imagesManager);
//                rvSelectImages.setAdapter(adapterSelectImages);
//
//                rl.addView(iv);
//                rl.addView(rvSelectImages);
//
//                nestedScrollView.addView(rl);
//
//                llViews.addView(nestedScrollView);

            } else if (obj.getVehicleChecklistFields().get(position).getType().equals("text") && obj.getVehicleChecklistFields().get(position).getPlaceholder().equalsIgnoreCase("signature")) {
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams llPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                llPerams.setMargins(8, 8, 8, 8);
                ll.setLayoutParams(llPerams);
                ll.setOrientation(LinearLayout.VERTICAL);
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(16, 16, 16, 16);

                etText.setText(obj.getVehicleChecklistFields().get(position).getPlaceholder());
                etText.setTextSize(16);
                etText.setLayoutParams(perams);
                etText.setTypeface(null, Typeface.NORMAL);
                etText.setTextColor(Color.parseColor("#616060"));

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                startparams.gravity = Gravity.RIGHT;
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getVehicleChecklistFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    etText.setTextColor(Color.RED);
                }
                ll.addView(star);
                ll.addView(etText);
                final ImageView iv = new ImageView(context);
                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                iv.setLayoutParams(ivPerams);
                ll.addView(iv);
                iv.setVisibility(View.GONE);
                etText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getSignatures(etText, iv);
                    }
                });


//                iv.setVisibility(View.GONE);

                llViews.addView(ll);
                allViewInstance.add(etText);
            }

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
                SettingValues.getBarcodelist().clear();

            }
        });
    }

    private void getSignatures(final CustomTextView tv, final ImageView iv) {
        final Dialog dialog = new Dialog(context);
        final SignaturePad mSignaturePad;
        final Button mClearButton;
        final Button mSaveButton;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        mClearButton = (Button) dialog.findViewById(R.id.clear_button);
        mSaveButton = (Button) dialog.findViewById(R.id.save_button);
        mSignaturePad = (SignaturePad) dialog.findViewById(R.id.signature_pad);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).getDialog();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });


        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
//                temp = addJpgSignatureToGallery(signatureBitmap);
                String path = addJpgSignatureToGallery(signatureBitmap);
//                Log.e("beforeReturningPath", temp);
                tv.setTag(path);
                iv.setVisibility(View.VISIBLE);
                Glide.with(context).load(path).into(iv);

                dialog.dismiss();
//                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
//                    Toast.makeText(SignatureActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).getDialog();
//                } else {
//                    Toast.makeText(SignatureActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).getDialog();
//                }
            }
        });
        dialog.show();


    }

    public String addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        String path = "";
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpeg", System.currentTimeMillis()));
            path = photo.getPath();
            Log.e("path", path);
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            Toast.makeText(context, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            return path;
        } else {
            Toast.makeText(context, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void getDataFromDynamicViews(View v, final VehicleChecklistForm response) {
        try {


            JSONArray jsonArray = new JSONArray();
//            JSONObject parentObj = new JSONObject();
//          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
//            parentObj.put("related_data_id", task_id);

            for (int noOfViews = 0; noOfViews < response.getVehicleChecklistFields().size(); noOfViews++) {
                JSONObject fieldsObj = new JSONObject();
                if(noOfViews == 0){
                    fieldsObj.put("vehicle_barcode", SettingValues.getBarcodeVal());
                    SettingValues.setBarcodeVal("");
                }

                if(noOfViews==0) {
                    JSONArray barcodelist = new JSONArray();
                    for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {

                        JSONObject barcodeobj = new JSONObject();

                        barcodelist.put("" + SettingValues.getBarcodelist().get(i));

                    }
                    fieldsObj.put("vehicle_barcode", "" + barcodelist);
//                    jsonArray.put(fieldsObj);
                }


                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("select")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getVehicleChecklistFields().get(noOfViews).getVehicleChecklistOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getVehicleChecklistFields().get(noOfViews).getVehicleChecklistOptions().get(spinner.getSelectedItemPosition()).getId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getVehicleChecklistFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "select");
                    fieldsObj.put("value", "");

                    JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                    JSONObject optionsObj = new JSONObject();
                    optionsObj.put("option_id", "" + selected_id);
                    optionsObj.put("value", "" + selected_name);
                    optionsArray.put(optionsObj);
//                    }
                    fieldsObj.put("option", optionsArray);
                    jsonArray.put(fieldsObj);
                }

                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("radio-group")) {

                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            fieldsObj.put("type", "radio-group");
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {

                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());

                            Log.e("rg_id", radioGroup.getTag().toString() + "");

                            fieldsObj.put("field_id",
                                    radioGroup.getTag().toString());
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "");
                            optionsObj.put("value", "");
                            optionsArray.put(optionsObj);
//                        }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
//
//                            return;
                        }
                    }
                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("checkbox-group")) {

                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        Boolean empty = true;
                        String field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                empty = false;
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }
                        if (empty) {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        }
                    } else {
                        String field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }

                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);
                    }

                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("text") && !(response.getVehicleChecklistFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time")) && !(response.getVehicleChecklistFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {

                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "text");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "text");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("number")) {

                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "number");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "number");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("textarea")) {

                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "textarea");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "textarea");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("text") && (response.getVehicleChecklistFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time"))) {
                    //Time
                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "time");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "time");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("date")) {
                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("True")) {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "date");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "date");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }


                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("header")) {
                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "header");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "header");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }
                if (response.getVehicleChecklistFields().get(noOfViews).getType().equals("text") && (response.getVehicleChecklistFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {
                    //Time
                    if (response.getVehicleChecklistFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            String field_id, text;

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                            text = textView.getTag().toString();

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                            if (!text.equalsIgnoreCase("")) {
                                fieldsObj.put("field_id", field_id);
                                fieldsObj.put("type", "signature");
                                fieldsObj.put("value", encodedImage);
                            } else {
                                Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {


                            String field_id, text;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";
                            text = textView.getTag().toString();

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", encodedImage);


                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();

                            String field_id;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getVehicleChecklistFields().get(noOfViews).getFieldId() + "";

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", "");

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        }
                    }
                }
            }








            JSONObject allFields = new JSONObject();
            allFields.put("fields", jsonArray);

            JSONObject formFields = new JSONObject();
            formFields.put("fields", jsonArray);


            JSONArray imagesArray = new JSONArray();
            JSONArray imagesPathArray = new JSONArray();
            for (int i = 0; i < myImages.size(); i++) {
                imagesArray.put(myImages.get(i).getName());
                imagesPathArray.put(myImages.get(i).getTempUri());
                Log.d("URI", "images uri is: " + myImages.get(i).getTempUri());
            }


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);

            JSONObject obj = new JSONObject();
            obj.put("vehicle_id", vehicleId);
            obj.put("checklist_type", "equipment_checklist");
            obj.put("form", allFields);
            obj.put("form",formFields);

//            submitData(obj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void submitData(String data) {
//
//        ArrayList<MultipartBody.Part> images = new ArrayList<>();
//
//        for (int i = 0; i < myImages.size(); i++) {
//            File file1 = new File(String.valueOf(myImages.get(i).getTempUri()));
//            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), myImages.get(i).getFinalImage())));
//        }
//
//        retrofit2.Call<SubmitFormResponse> call;
//        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), data.toString());
//        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(vehicleId));
//        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "vehicle_checklist");
//
//        call = RetrofitClass.getInstance().getWebRequestsInstance().submitChecklistData(tinyDB.getString(Constants.token), bodyRequest,images,id, type);
//        call.enqueue(new Callback<SubmitFormResponse>() {
//            @Override
//            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        findViewById(R.id.progress).setVisibility(View.GONE);
//                        btnSubmit.setVisibility(View.VISIBLE);
//
//                        Toast.makeText(VehicleCheckList.this, "Successfully Incidence saved to server !", Toast.LENGTH_SHORT).getDialog();
//                        finish();
//                    } else {
//                        Toast.makeText(VehicleCheckList.this, response.body().getMessage(), Toast.LENGTH_SHORT).getDialog();
//                        findViewById(R.id.progress).setVisibility(View.GONE);
//                        btnSubmit.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    findViewById(R.id.progress).setVisibility(View.GONE);
//                    btnSubmit.setVisibility(View.VISIBLE);
//                    Toast.makeText(VehicleCheckList.this, "Internal Server Error !", Toast.LENGTH_SHORT).getDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
//                Toast.makeText(VehicleCheckList.this, "Can't connect to server !", Toast.LENGTH_SHORT).getDialog();
//                findViewById(R.id.progress).setVisibility(View.GONE);
//                btnSubmit.setVisibility(View.VISIBLE);
//                t.printStackTrace();
//            }
//        });
//    }


}
