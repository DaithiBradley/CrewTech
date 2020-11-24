package com.aap.medicore.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Adapters.AdapterSelectImages;
import com.aap.medicore.Adapters.EqipmentCheckListSpinnerArrayAdapter;
import com.aap.medicore.Adapters.EquipmentAccessoriesAdapter;
import com.aap.medicore.Adapters.SpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.ChecklistForm;
import com.aap.medicore.Models.Field;
import com.aap.medicore.Models.Form;
import com.aap.medicore.Models.Option;
import com.aap.medicore.Models.Option2;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SaveField;
import com.aap.medicore.Models.SaveField2;
import com.aap.medicore.Models.SaveForm;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.NetworkReciever;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.aap.medicore.Utils.Utils;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.raed.drawingview.DrawingView;
import com.raed.drawingview.brushes.BrushSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.aap.medicore.Utils.UIUtils.convertToDp;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FormSection extends BaseActivity implements Serializable, EquipmentAccessoriesAdapter.OnClinicalItemClickedListener {
    private static final int PIN_REQUEST = 102;
    ImageView ivBack;
    TinyDB tinyDB;
    CustomButton btnSubmit, saveDrawingBtn;
    Context context = FormSection.this;
    LinearLayout barcodeviews;
    DatabaseHandler handler;
    String barcode = "true";
    Boolean isSignatureSaved = false;
    HorizontalScrollView hsvLayout;
    LinearLayout llViews, llViews1;
    File photo = null;
    ChecklistForm objj;
    TaskDetails td;
    List<View> allViewInstance = new ArrayList<View>();
    LinearLayoutManager imagesManager;
    TextView subtitle;
    ArrayList<SelectImagesModel> myImages;
    ImageView ivSelectImages;
    RecyclerView rvSelectImages;
    AdapterSelectImages adapterSelectImages;
    NestedScrollView nonDrawView;
    LinearLayout drawingLayout;
    Form obj;
    Bitmap img;
    CustomButton clear;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    JSONObject mainObj;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private Location mLocation;
    LinearLayout barcodelayout;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0, lng = 0.0;
    String task_id = "", position;
    public static CustomTextView barcode_text;
    private final int REQUEST_GALLERY_PERMISSION = 5568;
    private DrawingView drawingView;
    Boolean isDrawOverImageForm = false;
    String formId = "", formTitle = "";
    //    CustomTextView notes;
    TextView heading;
    public static String barcode_value = null;
    Bitmap signBitmap;
    boolean isMandatoryFilled = true;
    String notes, dob, ref, returnFacility;
    double lattitude, longtitude;
    private boolean isSubmitted;
    private HashMap<Integer, List<Integer>> toggleHash2;
    private HashMap<Integer, Integer> hashmap5;
    private ArrayList<Option> list;
    private HashMap<Integer, List<Option>> field2checkList;
    private TextView selectedTV;
    private int numSelected = 0;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private RecyclerView rvCarousel;
    private HashMap<Integer, EquipmentCheckList.Arrows> hashmap4;
    private HashMap<Integer, Integer> hashmap3;
    private int currentImageViewId;
    private HashMap<Integer, List<Option>> toggleHash;
    private View ltNoNetwork;
    private BroadcastReceiver broadcastReceiver;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_section);
        getSupportActionBar().hide();
        if (getIntent() != null) {
            task_id = getIntent().getStringExtra(Constants.task_id);
            Log.d("ID", "task id is: " + task_id);
            position = getIntent().getStringExtra("position");
            formId = getIntent().getStringExtra("tab_id");
            formTitle = getIntent().getStringExtra("title");
            barcodeviews = (LinearLayout) findViewById(R.id.barcodeviews);
            SettingValues.setFormId(formId);
        }

        toggleHash2 = new HashMap<>();
        hashmap5 = new HashMap<>();
        hashmap4 = new HashMap<>();
        list = new ArrayList<>();
        hashmap3 = new HashMap<>();
        field2checkList = new HashMap<>();
        toggleHash = new HashMap<>();
        init();
        clickListeners();
        broadcastReceiver = new NetworkReciever(ltNoNetwork);
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (formTitle.contains("vital sign form")) {
//            hitEquipmentCheckList();
            getFormDetail();

        } else {
            getFormDetail();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    //    public void hitEquipmentCheckList() {
//        retrofit2.Call<CheckListForms> call;
//
//        call = RetrofitClass.getInstance().getWebRequestsInstance().getEquipmentChecklist(tinyDB.getString(Constants.token), formId);
//
//        call.enqueue(new Callback<CheckListForms>() {
//            @Override
//            public void onResponse(retrofit2.Call<CheckListForms> call, final Response<CheckListForms> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        objj = response.body().getChecklistForm();
//                        Log.e("equip response", "Equp responseeeee" + response.body());
//                        getFormDetail(response);
//
//                    } else if (response.body().getStatus() == 404) {
//                        Log.e("equip response", "fail Equp responseeeee" + response.body());
//                    }
//                } else if (response.code() == 401) {
//                    if (getApplicationContext() != null)
//                        new SessionTimeoutDialog(FormSection.this).getDialog().show();
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<CheckListForms> call, Throwable t) {
//                Log.e("equip response", "fail Equp responseeeee" + t);
//                t.printStackTrace();
//            }
//        });
//    }

    public RequestBody prepareData(JSONObject object) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("job_id", task_id);
            obj.put("form_id", formId);
            JSONArray array = new JSONArray();
            array.put(object);
            obj.put("forms", array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), obj.toString());
        return bodyRequest;
    }

    private void clickListeners() {

        barcodelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormSection.this, ScanActivity.class);
                i.putExtra("formid", formId);
//                Toast.makeText(context, ""+formId, Toast.LENGTH_SHORT).getDialog();
                startActivity(i);
            }

        });
        if (SettingValues.getBarcodeVal().isEmpty()) {
            barcode_text.setText("Scan Barcode");
        } else {
            barcode_text.setText(SettingValues.getBarcodeVal());
        }

//        Toast.makeText(context, ""+barcode_value, Toast.LENGTH_SHORT).getDialog();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
//                onBackPressed();
                onReturn();
                finish();
            }
        });

        ivSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionAccess();

                } else {
                    permissionAccess();
                }
            }
        });
    }


    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(FormSection.this);
        handler = new DatabaseHandler(FormSection.this);
        nonDrawView = findViewById(R.id.nonDrawView);
        drawingLayout = findViewById(R.id.drawing_layout);
        hsvLayout = findViewById(R.id.hsv);
        barcodelayout = (LinearLayout) findViewById(R.id.barcodelayout);
        heading = findViewById(R.id.heading);
        subtitle = findViewById(R.id.tvTaskLocation);
        barcode_text = (CustomTextView) findViewById(R.id.barcode_text);
//        subtitle.setText(obj.getSub_title());
        clear = (CustomButton) findViewById(R.id.clear);
        if (formTitle != null) {
            if (!formTitle.isEmpty()) {
                heading.setText(formTitle);
            }
        }

        if (barcode.equals("true")) {
        } else {
            barcodelayout.setVisibility(View.GONE);
        }
        ltNoNetwork = findViewById(R.id.ltNoNetwork);
        ivSelectImages = findViewById(R.id.ivSelectImages);
        llViews = findViewById(R.id.llViews);
        llViews1 = findViewById(R.id.llViews1);
        btnSubmit = findViewById(R.id.btnSubmit);
        saveDrawingBtn = findViewById(R.id.btnSave1);
        imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myImages = new ArrayList<>();
//        adapterSelectImages = new AdapterSelectImages(imagesList, context, ivSelectImages);
        rvSelectImages = findViewById(R.id.rvImages);
//        rvSelectImages.setLayoutManager(imagesManager);
//        rvSelectImages.setAdapter(adapterSelectImages);
        TaskDetails.sectionImagesList = new ArrayList<>();
        drawingView = findViewById(R.id.drawing_view);

//        KeyboardVisibilityEvent.setEventListener(

//                FormSection.this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                        Log.e("board", isOpen + "");
//
//                        if (isOpen) {
//                            findViewById(R.id.llViews).animate().translationY(-200);
//                        } else {
//                            findViewById(R.id.llViews).animate().translationY(0);
//                        }
//
//                    }
//                });
    }


    public void vitaldata(View v, final ChecklistForm response) {
        try {

            JSONArray jsonArray = new JSONArray();
            JSONObject parentObj = new JSONObject();
//          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
            parentObj.put("related_data_id", task_id);

            for (int noOfViews = 0; noOfViews < response.getFields().size(); noOfViews++) {
                JSONObject fieldsObj = new JSONObject();


                if (noOfViews == 0) {
                    fieldsObj.put("equip_barcode", SettingValues.getBarcodeVal());
                    SettingValues.setBarcodeVal("");
                }

                if (noOfViews == 0) {
                    JSONArray barcodelist = new JSONArray();
                    for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {

                        JSONObject barcodeobj = new JSONObject();

                        barcodelist.put("" + SettingValues.getBarcodelist().get(i));

                    }
                    fieldsObj.put("equip_barcode" +
                            "", "" + barcodelist);
//                    jsonArray.put(fieldsObj);
                }


                if (response.getFields().get(noOfViews).getType().equals("select")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
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

                if (response.getFields().get(noOfViews).getType().equals("radio-group")) {

//                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
//                        try {
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//
//                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");
//
//                            String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id",
//                                    "" + a[0]);
//                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[1]);
//                            fieldsObj.put("type", "radio-group");
//                            optionsObj.put("value", "" + a[2]);
//                            optionsArray.put(optionsObj);
////                    }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    } else {
//                        try {
//
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//
//                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");
//
//                            String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id",
//                                    "" + a[0]);
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[1]);
//                            optionsObj.put("value", "" + a[2]);
//                            optionsArray.put(optionsObj);
////                    }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
////                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());
//
//                            Log.e("rg_id", radioGroup.getTag().toString() + "");
//
//                            fieldsObj.put("field_id",
//                                    radioGroup.getTag().toString());
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
////                            Log.e("f", a[0]);
////                            Log.e("s", a[1]);
////                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "");
//                            optionsObj.put("value", "");
//                            optionsArray.put(optionsObj);
////                        }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
////
////                            return;
//                        }
//                    }

                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "radio-group");
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
                if (response.getFields().get(noOfViews).getType().equals("checkbox-group")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        Boolean empty = true;
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("text") && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time")) && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "text");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                    disableLoader();

                }
                if (response.getFields().get(noOfViews).getType().equals("number")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("textarea")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;
                        EditText textView = (EditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";

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
                        EditText textView = (EditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";


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
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("date")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("True")) {
                        //Date
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("header")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            String field_id, text;

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";

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
            obj.put("checklist_type", "equipment_checklist");
            obj.put("form", allFields);
            obj.put("lat", TaskDetails.wayLatitude);
            obj.put("lng", TaskDetails.wayLongitude);


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);


            parentObj.put("form-section", allFields);
//            mainObj.put("barcode","1234")

            QueueModel model = new QueueModel();
            model.setId(formId + "");
            model.setJson(formFields + "");
            model.setState(Constants.StateAdded);
            model.setTitle("");
            model.setMessage("");
            model.setTaskID(task_id);
            handler.deleteDraftIncidenceOnIndidenceId(formId, task_id);
            handler.addDraftIncidenceOnIncidenceID(model);
            Log.e("taskandformid", "VTASK_ID IS THISSSS" + task_id + " " + "FORM ID IS THISSSS" + formId);
            Log.d("optionsObj", parentObj + "");

            TaskDetails.sectionImagesList.addAll(myImages);
            Log.d("optionsObj", "image list size is:" + TaskDetails.sectionImagesList.size());

            if (isMandatoryFilled) {
                Toast.makeText(context, "Form saved successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
            }


            TaskDetails td = new TaskDetails();
            td.resetView(position, parentObj.toString(), FormSection.this, isMandatoryFilled, isSubmitted);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public static final TypeAdapter<Number> UNRELIABLE_INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NUMBER:
                case STRING:
                    String s = in.nextString();
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        return (int) Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {
                    }
                    return null;
                case NULL:
                    in.nextNull();
                    return null;
                case BOOLEAN:
                    in.nextBoolean();
                    return null;
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapterFactory UNRELIABLE_INTEGER_FACTORY = TypeAdapters.newFactory(int.class, Integer.class, UNRELIABLE_INTEGER);


    @SuppressLint("NewApi")
    public void getFormDetail() {

        AssignedIncidencesModel model = new AssignedIncidencesModel();
        model = handler.getIncidenceOnId(task_id);
        Gson gson = new Gson();
        String json = model.getJson();
        final TaskList taskList = gson.fromJson(json, TaskList.class);
        int pos = Integer.parseInt(position);
        Log.d("POS", "POSITION IS: " + pos);
        if (pos < taskList.getForm().size()) {
            obj = taskList.getForm().get(Integer.parseInt(position));

        } else {
            Toast.makeText(context, "Form has been updated!", Toast.LENGTH_SHORT).show();
            Log.e("taskk", "taskkkkk" + taskList.getForm().size());
            return;

        }

        subtitle.setText(taskList.getForm().get(Integer.parseInt(position)).getSub_title());

        if (taskList.getForm().get(Integer.parseInt(position)).getBarcode_enabled().equals("yes")) {

            barcodelayout.setVisibility(View.VISIBLE);
        } else if (taskList.getForm().get(Integer.parseInt(position)).getBarcode_enabled().equals("no")) {

        }

        QueueModel model1 = handler.getDraftIncidenceStateOnIncidenceID(formId, task_id);

        Gson gson1 = new GsonBuilder()
                .registerTypeAdapterFactory(UNRELIABLE_INTEGER_FACTORY)
                .create();
        String json1 = model1.getJson();
        Log.d("DATA", "result data is: " + json1);

        SaveForm taskList1 = gson1.fromJson(json1, SaveForm.class);
        Typeface bold = ResourcesCompat.getFont(context, R.font.open_sans_bold);
        Typeface semiBold = ResourcesCompat.getFont(context, R.font.open_sans_semibold);
        Typeface regular = ResourcesCompat.getFont(context, R.font.open_sans_regular);
        if (obj.getImage() != null && obj.getImage().equalsIgnoreCase("yes")) {
            isDrawOverImageForm = true;
            findViewById(R.id.nonDrawView).setVisibility(View.GONE);
            drawingLayout.setVisibility(View.VISIBLE);
            drawingView.setVisibility(View.VISIBLE);
            BrushSettings brushSettings = drawingView.getBrushSettings();
            brushSettings.setSelectedBrushSize((float) 0.01);
            drawingView.setUndoAndRedoEnable(true);
            findViewById(R.id.hsv).setVisibility(View.VISIBLE);

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = taskList.getDrawOverImage();
                    String drawImg= Utils.tryParseImagePathWithError(s);
                    //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                    new ConvertUrlToBitmap().execute(drawImg);
                }
            });

            if (taskList1 != null) {

                if (taskList1.getImages().size() > 0) {
                    for (int i = 0; i < taskList1.getImages().size(); i++) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        final Bitmap bitmap = BitmapFactory.decodeFile(taskList1.getImages().get(0), bmOptions);
                        drawingView.setBackgroundImage(bitmap);

                    }


                } else {
                    String s = taskList.getDrawOverImage();
                    String drawImg= Utils.tryParseImagePathWithError(s);
                    //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                    new ConvertUrlToBitmap().execute(drawImg);
                }
            } else {
                String s = taskList.getDrawOverImage();
                String drawImg= Utils.tryParseImagePathWithError(s);
                //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                new ConvertUrlToBitmap().execute(drawImg);
            }

        } else {
            findViewById(R.id.nonDrawView).setVisibility(View.VISIBLE);

            drawingLayout.setVisibility(View.GONE);
            isDrawOverImageForm = false;
            drawingView.setVisibility(View.GONE);
            findViewById(R.id.hsv).setVisibility(View.GONE);
        }
        Log.e("size", obj.toString());

        for (int position = 0; position < obj.getFields().size(); position++) {
            String form_name = obj.getTitle();
            Log.d("BARCOADEEEEEE", "bar coeeeeeeeeeeeee: " + obj.getFields().get(position).getPlaceholder());
            Log.d("TYPE", "FIELD TYPE IS: " + obj.getFields().get(position).getType());
            if (obj.getFields().get(position).getType().equalsIgnoreCase("file")) {

                if (taskList1 != null) {
                    Log.d("SIZE", "Image size is: " + taskList1.getImages().size());


                    if (taskList1.getImages().size() > 0)
                        for (int i = 0; i < taskList1.getImages().size(); i++) {
                            String path = taskList1.getImages().get(i);
                            SelectImagesModel imageModel = new SelectImagesModel();
                            imageModel.setTempUri(Uri.parse(path));
                            File f = new File(path);
                            imageModel.setFinalImage(f);
                            imageModel.setName(f.getName());
                            myImages.add(imageModel);
                        }
                }

                HorizontalScrollView hsvLayout = new HorizontalScrollView(this);
                hsvLayout.setFillViewport(true);
                hsvLayout.setVerticalScrollBarEnabled(false);
                hsvLayout.setHorizontalScrollBarEnabled(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(24, this), convertToDp(16, this), convertToDp(24, this));
                hsvLayout.setLayoutParams(params);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setGravity(Gravity.CENTER);
                HorizontalScrollView.LayoutParams params1 = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMarginEnd(convertToDp(10, this));
                params1.setMarginStart(convertToDp(10, this));
                relativeLayout.setLayoutParams(params1);

                ivSelectImages = new ImageView(this);
                ivSelectImages.setId(View.generateViewId());
                ivSelectImages.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_add_pictures));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(convertToDp(90, this), convertToDp(90, this));
                params2.setMargins(convertToDp(16, this), convertToDp(16, this), convertToDp(16, this), convertToDp(16, this));
                params2.addRule(RelativeLayout.CENTER_VERTICAL);
                ivSelectImages.setLayoutParams(params2);
                ivSelectImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentImageViewId = view.getId();
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            permissionAccess();

                        } else {

                            permissionAccess();
                        }
                    }
                });
                rvSelectImages = new RecyclerView(this);
                rvSelectImages.setId(View.generateViewId());
                rvSelectImages.setNestedScrollingEnabled(false);
                params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setMarginEnd(convertToDp(16, this));
                params2.addRule(RelativeLayout.RIGHT_OF, ivSelectImages.getId());
                rvSelectImages.setLayoutParams(params2);
                hashmap5.put(ivSelectImages.getId(), rvSelectImages.getId());
                imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                rvSelectImages.setLayoutManager(imagesManager);
                relativeLayout.addView(ivSelectImages);
                relativeLayout.addView(rvSelectImages);
                hsvLayout.addView(relativeLayout);
                llViews.addView(hsvLayout);


                relativeLayout = new RelativeLayout(this);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeLayout.setLayoutParams(params);

                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setText("Upload Image");

                textView.setVisibility(View.GONE);
                params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setMargins(convertToDp(16, this), 0, convertToDp(16, this), convertToDp(32, this));
                textView.setLayoutParams(params2);
                params2.addRule(RelativeLayout.CENTER_IN_PARENT);
                relativeLayout.addView(textView);

                ImageView imageView = new ImageView(this);
                imageView.setId(View.generateViewId());
                imageView.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.red_astrik));
                imageView.setVisibility(View.GONE);
                params2 = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                params2.setMarginStart(convertToDp(16, this));
                params2.addRule(RelativeLayout.RIGHT_OF, textView.getId());
                imageView.setLayoutParams(params2);

                relativeLayout.addView(imageView);
                llViews.addView(relativeLayout);

                hsvLayout.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    imageView.setVisibility(View.VISIBLE);
                }

                allViewInstance.add(hsvLayout);
                Log.d("SIZE", "Image size is: " + myImages.size());
                if (myImages.size() > 0) {
                    if (adapterSelectImages != null) {
                        adapterSelectImages.setItems(myImages);
                        adapterSelectImages.notifyDataSetChanged();
                    } else {
                        adapterSelectImages = new AdapterSelectImages(myImages, FormSection.this, ivSelectImages);
                        rvSelectImages.setLayoutManager(imagesManager);
                        rvSelectImages.setAdapter(adapterSelectImages);
                    }
                }
            }

            else if (obj.getFields().get(position).getType().equalsIgnoreCase("text") && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time")) && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature"))) {

                Field field = obj.getFields().get(position);
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                relativeLayout.setLayoutParams(linearParams);

                TextInputLayout inputLayout=new TextInputLayout(this);
                TextInputEditText editText = new TextInputEditText(inputLayout.getContext());
                editText.setId(View.generateViewId());
                editText.setBackgroundColor(getColor(android.R.color.transparent));
                editText.setTextColor(getColor(R.color.itemText));
                editText.setTypeface(semiBold);
                editText.setTextSize(COMPLEX_UNIT_SP, 14);

                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputLayout.addView(editText,editTextParams);
                inputLayout.setHintEnabled(true);
                inputLayout.setHint(field.getLabel());
                inputLayout.setBackgroundColor(getColor(android.R.color.transparent));

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);


                View view = new View(this);
                view.setBackgroundColor(getColor(R.color.divider));

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                inputLayout.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToDp(2, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setLayoutParams(relativeParams);

                relativeLayout.addView(inputLayout);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(view);

                allViewInstance.add(editText);
                llViews.addView(relativeLayout);

                if (field.getLabel() != null && (field.getLabel().contains("PIN") || field.getLabel().contains("Pin"))) {
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setFocusable(false);
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormSection.this, PINVerificationActivity.class);
                            intent.putExtra("ViewId", editText.getId());
                            startActivityForResult(intent, PIN_REQUEST);
                        }
                    });
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {

                                Intent intent = new Intent(FormSection.this, PINVerificationActivity.class);
                                intent.putExtra("ViewId", editText.getId());
                                startActivityForResult(intent, PIN_REQUEST);
                            }
                        }
                    });
                }

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                editText.setText(f.getValue());
                            }
                        }
                }


            } else if (obj.getFields().get(position).getType().equalsIgnoreCase("number")) {
                Field field = obj.getFields().get(position);

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                relativeLayout.setLayoutParams(linearParams);

                TextInputLayout inputLayout=new TextInputLayout(this);
                TextInputEditText editText = new TextInputEditText(inputLayout.getContext());
                editText.setId(View.generateViewId());
                editText.setBackgroundColor(getColor(android.R.color.transparent));
                editText.setTextColor(getColor(R.color.itemText));
                editText.setTypeface(semiBold);
                editText.setTextSize(COMPLEX_UNIT_SP, 14);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputLayout.addView(editText,editTextParams);
                inputLayout.setHintEnabled(true);
                inputLayout.setHint(field.getLabel());
                inputLayout.setBackgroundColor(getColor(android.R.color.transparent));

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);

                View view = new View(this);
                view.setBackgroundColor(getColor(R.color.divider));

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                inputLayout.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToDp(2, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setLayoutParams(relativeParams);

                relativeLayout.addView(inputLayout);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(view);

                llViews.addView(relativeLayout);
                allViewInstance.add(editText);

                if (field.getValue() != null)
                    editText.setText(field.getValue());

                if (field.getLabel() != null && (field.getLabel().contains("PIN") || field.getLabel().contains("Pin"))) {
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setFocusable(false);
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormSection.this, PINVerificationActivity.class);
                            intent.putExtra("ViewId", editText.getId());
                            startActivityForResult(intent, PIN_REQUEST);
                        }
                    });
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {

                                Intent intent = new Intent(FormSection.this, PINVerificationActivity.class);
                                intent.putExtra("ViewId", editText.getId());
                                startActivityForResult(intent, PIN_REQUEST);
                            }
                        }
                    });
                }

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {
                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                editText.setText(f.getValue());
                            }
                        }
                }

            } else if (obj.getFields().get(position).getType().equals("checkbox-group")) {

                list = obj.getFields().get(position).getOptions();
                if (taskList1 != null) {
                    List<Option> savedOptions = taskList1.getFields().get(position).getOptions();
                    for (Option option : list) {
                        for (Option savedOption : savedOptions) {
                            if (option.getOptionId().equals(savedOption.getOptionId())) {
                                option.setSelected(true);
                                numSelected++;
                            }
                        }
                    }
                }

                field2checkList.put(obj.getFields().get(position).getFieldId(), list);
                RelativeLayout relativeLayout = new RelativeLayout(this);
                TextView tvCheckBoxTitle = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(4, this));
                relativeLayout.setLayoutParams(perams);
                RelativeLayout.LayoutParams perams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                tvCheckBoxTitle.setId(View.generateViewId());
                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                tvCheckBoxTitle.setTextColor(getColor(R.color.itemText));
                tvCheckBoxTitle.setTypeface(semiBold);
                tvCheckBoxTitle.setTextSize(COMPLEX_UNIT_SP, 16);
                RelativeLayout.LayoutParams startparams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                tvCheckBoxTitle.setLayoutParams(perams1);
                relativeLayout.addView(tvCheckBoxTitle);
                relativeLayout.addView(star);
                selectedTV = new TextView(this);
                selectedTV.setId(View.generateViewId());
                selectedTV.setTextSize(COMPLEX_UNIT_SP, 14);
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //or MATCH_PARENT
                tvParams.addRule(RelativeLayout.RIGHT_OF, tvCheckBoxTitle.getId());
                tvParams.setMargins(convertToDp(8, this), convertToDp(2, this), 0, 0);

                selectedTV.setLayoutParams(tvParams);
                selectedTV.setTextColor(getColor(R.color.colorOrange));
                selectedTV.setText(numSelected + "/" + list.size()); /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                relativeLayout.addView(selectedTV);
                llViews.addView(relativeLayout);

                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout relativeLayout1 = new RelativeLayout(this);
                relativeLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                leftArrow = new ImageView(this);
                leftArrow.setId(View.generateViewId());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.setMargins(16, 0, 16, 0);
                leftArrow.setLayoutParams(params);
                leftArrow.setPadding(convertToDp(16, this), 0, 0, 0);
                leftArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_l));
                leftArrow.setVisibility(View.INVISIBLE);


                rightArrow = new ImageView(this);
                rightArrow.setId(View.generateViewId());
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.setMargins(16, 0, 16, 0);
                rightArrow.setLayoutParams(params);
                rightArrow.setImageDrawable(getDrawable(R.drawable.ic_slide_r));
                rightArrow.setPadding(0, 0, convertToDp(16, this), 0);

                LinearLayout linearLayout = new LinearLayout(this);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, leftArrow.getId());
                params.addRule(RelativeLayout.LEFT_OF, rightArrow.getId());
                params.setMargins(8, convertToDp(8, this), 8, convertToDp(8, this));
                linearLayout.setLayoutParams(params);

                rvCarousel = new RecyclerView(this);
                rvCarousel.setId(View.generateViewId());
                LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rvCarousel.setLayoutParams(rvParams);
                linearLayout.addView(rvCarousel);

                relativeLayout1.addView(leftArrow);
                relativeLayout1.addView(linearLayout);
                relativeLayout1.addView(rightArrow);
                hashmap5.put(leftArrow.getId(), rvCarousel.getId());
                hashmap3.put(rightArrow.getId(), rvCarousel.getId());
                hashmap5.put(rightArrow.getId(), list.size());
                EquipmentCheckList.Arrows arrows = new EquipmentCheckList.Arrows();
                arrows.setLeftArrow(leftArrow.getId());
                arrows.setRightArrow(rightArrow.getId());
                arrows.setListSize(list.size());
                hashmap4.put(rvCarousel.getId(), arrows);
                setUpCarousel();
                hashmap3.put(rvCarousel.getId(), selectedTV.getId());
                hashmap5.put(rvCarousel.getId(), obj.getFields().get(position).getFieldId());
                removeView(relativeLayout1);
                llViews.addView(relativeLayout1);

                allViewInstance.add(ll);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(convertToDp(16, this), 16, convertToDp(16, this), 16);
                view.setLayoutParams(p);
                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("radio-group")) {

                Field field = obj.getFields().get(position);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setTypeface(semiBold);
                textView.setTextSize(COMPLEX_UNIT_SP, 16);
                textView.setTextColor(getColor(R.color.itemText));
                textView.setText(field.getLabel());

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);

                List<Option> options = field.getOptions();
                AppCompatSpinner spinner = new AppCompatSpinner(this);
                spinner.setId(View.generateViewId());
                spinner.setPadding(convertToDp(8, this), 0, convertToDp(8, this), 0);
                SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(context, R.layout.custom_spinner_item, options);
                spinner.setAdapter(adapter);

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                textView.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.topMargin = convertToDp(8, this);
                relativeParams.addRule(RelativeLayout.BELOW, textView.getId());
                spinner.setLayoutParams(relativeParams);

                relativeLayout.setLayoutParams(linearParams);
                relativeLayout.addView(textView);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(spinner);
                llViews.addView(relativeLayout);
                allViewInstance.add(spinner);

                List<Integer> list = new ArrayList<>();
                for (int i = position; i < obj.getFields().size() - 1; i++) {
                    if (obj.getFields().get(i + 1).getType().equalsIgnoreCase("number")) {
                        list.add(obj.getFields().get(i + 1).getFieldId());
                    } else if (obj.getTitle().equalsIgnoreCase("Adverse Incident / Near Miss Form") && obj.getFields().get(i + 1).getType().equalsIgnoreCase("text")) {
                        list.add(obj.getFields().get(i + 1).getFieldId());
                        break;
                    } else
                        break;

                }

                toggleHash2.put(spinner.getId(), list);
                hashmap5.put(spinner.getId(), field.getFieldId());

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView textView = view.findViewById(R.id.tvEventName);
                        String text = textView.getText().toString();
                        for (int j : toggleHash2.get(adapterView.getId()))
                            if (text.equalsIgnoreCase("yes")) {
                                RelativeLayout relativeLayout1 = findViewById(hashmap5.get(j));
                                relativeLayout1.setVisibility(View.VISIBLE);
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout1.getLayoutParams();
                                layoutParams.setMargins(convertToDp(36, context), convertToDp(0, context), convertToDp(36, context), convertToDp(8, context));
                                RelativeLayout relativeLayout2 = (RelativeLayout) adapterView.getParent();
                                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) relativeLayout1.getLayoutParams();
                                layoutParams.bottomMargin = convertToDp(0, FormSection.this);
                            } else {
                                findViewById(hashmap5.get(j)).setVisibility(View.GONE);
                                RelativeLayout relativeLayout1 = (RelativeLayout) adapterView.getParent();
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout1.getLayoutParams();
                                layoutParams.bottomMargin = convertToDp(8, FormSection.this);
                                relativeLayout.setLayoutParams(layoutParams);
                            }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {

                    if (taskList1 != null) {
                        if (taskList1.getFields().size() > 0)
                            for (SaveField f : taskList1.getFields()) {
                                if (f.getOptions() != null && f.getOptions().size() > 0)
                                    for (Option o : f.getOptions()) {
                                        if (obj.getFields().get(position).getOptions().get(i).getOptionId().equals(o.getOptionId())) {
                                            Log.d("WOW", "Value matcheeeeeeeeeeeeeed");
                                            spinner.setSelection(i);
                                        }
                                    }
                            }
                    }

                }
            } else if (obj.getFields().get(position).getType().equals("textarea")) {

                Field field = obj.getFields().get(position);

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                relativeLayout.setLayoutParams(linearParams);

                TextInputLayout inputLayout=new TextInputLayout(this);
                TextInputEditText editText = new TextInputEditText(inputLayout.getContext());
                editText.setId(View.generateViewId());
                editText.setBackgroundColor(getColor(android.R.color.transparent));
                editText.setTextColor(getColor(R.color.itemText));
                editText.setTypeface(semiBold);
                editText.setTextSize(COMPLEX_UNIT_SP, 14);

                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputLayout.addView(editText,editTextParams);
                inputLayout.setHintEnabled(true);
                inputLayout.setHint(field.getLabel());
                inputLayout.setBackgroundColor(getColor(android.R.color.transparent));

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);

                View view = new View(this);
                view.setBackgroundColor(getColor(R.color.divider));

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                inputLayout.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToDp(2, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setLayoutParams(relativeParams);

                relativeLayout.addView(inputLayout);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(view);

                allViewInstance.add(editText);
                llViews.addView(relativeLayout);

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                editText.setText(f.getValue());
                            }
                        }
                }

            } else if (obj.getFields().get(position).getType().equals("select")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(4, this));
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                tvRadioButtonTitle.setTextColor(getColor(R.color.itemText));
                tvRadioButtonTitle.setTypeface(semiBold);
                tvRadioButtonTitle.setTextSize(COMPLEX_UNIT_SP, 16);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this)); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeLayout.addView(star, layoutParams);

                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.addView(tvRadioButtonTitle, layoutParams);
                llViews.addView(relativeLayout);

                perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(convertToDp(18, this), convertToDp(2, this), convertToDp(18, this), convertToDp(0, this));

                AppCompatSpinner spinner = new AppCompatSpinner(context);
                spinner.setLayoutParams(perams);
                spinner.setPadding(convertToDp(8, this), 0, convertToDp(8, this), 0);
                List<Option> spinnerOptionsList=obj.getFields().get(position).getOptions();

                SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(context,
                        R.layout.custom_spinner_item, spinnerOptionsList);
                spinner.setAdapter(adapter);

                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {

                    if (taskList1 != null) {
                        if (taskList1.getFields().size() > 0)
                            for (SaveField f : taskList1.getFields()) {
                                if (f.getOptions() != null && f.getOptions().size() > 0)
                                    for (Option o : f.getOptions()) {
                                        if (obj.getFields().get(position).getOptions().get(i).getOptionId().equals(o.getOptionId())) {
                                            Log.d("WOW", "Value matcheeeeeeeeeeeeeed");
                                            spinner.setSelection(i);
                                        }
                                    }
                            }
                    }

                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                llViews.addView(spinner);

                allViewInstance.add(spinner);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.divider));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(8, this));
                view.setLayoutParams(p);

                llViews.addView(view);


            } else if (obj.getFields().get(position).getType().equals("text") && (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time"))) {


                Field field = obj.getFields().get(position);

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                relativeLayout.setLayoutParams(linearParams);

                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setBackground(null);
                textView.setPadding(convertToDp(0, this), convertToDp(8, this), convertToDp(0, this), convertToDp(8, this));
                textView.setTextColor(getColor(R.color.itemText));
                textView.setTypeface(semiBold);
                textView.setTextSize(COMPLEX_UNIT_SP, 14);
                textView.setHint(field.getLabel());

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);

                View view = new View(this);
                view.setBackgroundColor(getColor(R.color.divider));

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                textView.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToDp(2, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setLayoutParams(relativeParams);

                relativeLayout.addView(textView);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(view);

                allViewInstance.add(textView);
                llViews.addView(relativeLayout);

                textView.setOnClickListener(new View.OnClickListener() {

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
//                                etText.setText("  " + selectedHour + " : " + selectedMinute);

                                if (selectedHour < 10 && selectedMinute < 10) {
                                    textView.setText( "0" + selectedHour + " : " + "0" + selectedMinute);
                                } else if (selectedHour < 10) {
                                    textView.setText( "0" + selectedHour + " : " + selectedMinute);
                                } else if (selectedMinute < 10) {
                                    textView.setText( selectedHour + " : " + "0" + selectedMinute);
                                } else {
                                    textView.setText( selectedHour + " : " + selectedMinute);
                                }


                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });
                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                textView.setText(f.getValue());
                            }
                        }
                }
            } else if (obj.getFields().get(position).getType().equals("date")) {

                Field field = obj.getFields().get(position);

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                relativeLayout.setLayoutParams(linearParams);

                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setBackground(null);
                textView.setPadding(convertToDp(8, this), convertToDp(8, this), convertToDp(8, this), convertToDp(8, this));
                textView.setTextColor(getColor(R.color.itemText));
                textView.setTypeface(semiBold);
                textView.setTextSize(COMPLEX_UNIT_SP, 14);
                textView.setHint(field.getLabel());

                ImageView ivMandatory = new ImageView(this);
                ivMandatory.setId(View.generateViewId());
                ivMandatory.setImageDrawable(getDrawable(R.drawable.red_astrik));
                if (field.getRequired().equalsIgnoreCase("true"))
                    ivMandatory.setVisibility(View.VISIBLE);
                else
                    ivMandatory.setVisibility(View.GONE);

                View view = new View(this);
                view.setBackgroundColor(getColor(R.color.divider));

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                textView.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                ivMandatory.setLayoutParams(relativeParams);

                relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertToDp(2, this));
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setLayoutParams(relativeParams);

                relativeLayout.addView(textView);
                relativeLayout.addView(ivMandatory);
                relativeLayout.addView(view);

                allViewInstance.add(textView);
                llViews.addView(relativeLayout);

                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                      //  String myFormat = " MM - dd - yyyy"; //In which you need put here
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        textView.setText("" + sdf.format(myCalendar.getTime()));
                    }

                };

                textView.setOnClickListener(new View.OnClickListener() {

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

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                textView.setText(f.getValue());
                            }
                        }
                }
            } else if (obj.getFields().get(position).getType().equals("header")) {
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setLayoutParams(perams);
                relativeLayout.setElevation(convertToDp(2, this));
                relativeLayout.setBackgroundColor(getColor(R.color.colorWhite));

                final TextView etText = new TextView(context);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));
                etText.setLayoutParams(layoutParams);
                if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h1")) {
                    etText.setTextSize(20);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h2")) {
                    etText.setTextSize(18);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h3")) {
                    etText.setTextSize(16);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h4")) {
                    etText.setTextSize(14);
                }
                etText.setTextColor(getColor(R.color.headerBackground));
                etText.setText(obj.getFields().get(position).getLabel());
                etText.setGravity(Gravity.START);
                etText.setSingleLine();
                etText.setSingleLine();
                etText.setTypeface(semiBold);
                etText.setTag(obj.getFields().get(position).getPlaceholder());
                relativeLayout.addView(etText);
                Log.e("Tag", obj.getFields().get(position).getPlaceholder());
                if (isDrawOverImageForm) {
                    if (relativeLayout.getParent() != null) {
                        ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout); // <- fix
                    }
                    llViews1.addView(relativeLayout);
                } else {
                    llViews.addView(relativeLayout);
                }

                allViewInstance.add(etText);


            } else if (obj.getFields().get(position).getType().equals("text") && obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature")) {
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams llPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                llPerams.setMargins(convertToDp(18, this), convertToDp(12, this), convertToDp(18, this), convertToDp(12, this));
                ll.setLayoutParams(llPerams);
                ll.setOrientation(LinearLayout.VERTICAL);
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(0, 0, 0, convertToDp(4, this));

                etText.setText(obj.getFields().get(position).getLabel());
                etText.setTextSize(COMPLEX_UNIT_SP, 14);
                etText.setTextColor(getColor(R.color.itemText));
                etText.setLayoutParams(perams);
                etText.setTypeface(semiBold);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 0, 4, 10);
                startparams.gravity = Gravity.RIGHT;
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);

                ll.addView(star);
                ll.addView(etText);


                final ImageView iv = new ImageView(context);
                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                ivPerams.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(4, this));
                iv.setLayoutParams(ivPerams);
                iv.setBackground(getDrawable(R.drawable.dotted));

                ll.addView(iv);

                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
//                    etText.setTextColor(Color.RED);

                }

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                if (f.getValue().isEmpty()) {

                                } else {

                                    String base64Image = f.getValue();

                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    String path = addJpgSignatureToGallery(decodedByte);
                                    etText.setTag(path);


                                    iv.setImageBitmap(decodedByte);
                                    iv.setBackground(getDrawable(R.drawable.dotted));

                                    isSignatureSaved = true;
                                }

                            }

                        }
                } else {

                }
                if (isSignatureSaved) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                }
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

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(convertToDp(18, context), convertToDp(4, this), convertToDp(18, context), convertToDp(8, context));
                view.setLayoutParams(p);

                llViews.addView(view);

            }


        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLoader();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hideKeyboard(FormSection.this);
                if (isDrawOverImageForm) {
                    Bitmap bitmap = drawingView.exportDrawingWithoutBackground();
                    Log.d("valuie", "bitmap value is: " + bitmap);

                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        Log.d("PAth", "image path is : " + path);

                        String file = compressImage(path);
                        SelectImagesModel model1 = new SelectImagesModel();
                        model1.setTempUri(Uri.parse(file));
                        File f = new File(file);
                        model1.setFinalImage(f);
                        model1.setName(f.getName());
                        Log.d("IMAGE", "image name is: " + f.getName());
                        myImages.add(model1);
                    }
                    Log.d("SIZE", "images size is: " + myImages.size());
                }

                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
            }
        });
        saveDrawingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hideKeyboard(FormSection.this);
                if (isDrawOverImageForm) {
                    Bitmap bitmap = drawingView.exportDrawing();
                    Log.d("valuie", "bitmap value is: " + bitmap);

                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        Log.d("PAth", "image path is : " + path);

                        String file = compressImage(path);
                        SelectImagesModel model1 = new SelectImagesModel();
                        model1.setTempUri(Uri.parse(file));
                        File f = new File(file);
                        model1.setFinalImage(f);
                        model1.setName(f.getName());
                        Log.d("IMAGE", "image name is: " + f.getName());
                        myImages.add(model1);
                    }
                    Log.d("SIZE", "images size is: " + myImages.size());
                }

                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
            }
        });


    }

    private String saveImage(Bitmap image) {
        String savedImagePath = null;

        String imageFileName = "Draw_OVER_IMAGE" + ".jpg";
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + "/Draw_OVER_IMAGES");

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
        }
        return savedImagePath;
    }

    @Override
    public void onClinicalItemClicked(int recyclerViewId, List<Option> list) {
        TextView textView = findViewById(hashmap3.get(recyclerViewId));
        numSelected = 0;
        for (Option model : list) {
            if (model.isSelected())
                numSelected++;
        }
        String s = numSelected + "/" + list.size();
        textView.setText(s);
        field2checkList.put(hashmap5.get(recyclerViewId), list);
    }


    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bitmap;
            } catch (Exception e) {
                Log.e("", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap image) {
            super.onPostExecute(image);

            Log.d("VALUE", "Bitmpa value is: " + image);
            // download was successful
            // if you want to update your UI, make sure u do it on main thread. like this:
            if (image != null) {
                img = image;
                if (isDrawOverImageForm) {
                    FormSection.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update UI here
                            drawingView.setBackgroundImage(image);
                        }
                    });
                } else {
//                    Toast.makeText(context, "saving", Toast.LENGTH_SHORT).getDialog();
//                    String path = saveImage(img);
//
//                    String file = compressImage(path);
//
//                    SelectImagesModel img = new SelectImagesModel();
//                    img.setTempUri(Uri.parse(file));
//                    File f = new File(file);
//                    img.setFinalImage(f);
//                    img.setName(f.getName());
//
//                    myImages.add(img);
                }

            }


        }
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
                iv.setBackground(getDrawable(R.drawable.dotted));

                Toast.makeText(context, "Signature saved", Toast.LENGTH_SHORT).show();
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

    private void setUpCarousel() {


        EquipmentAccessoriesAdapter adapter = new EquipmentAccessoriesAdapter(list, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        rvCarousel.setAdapter(adapter);
        rvCarousel.setLayoutManager(manager);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
//        if (screenWidth / manager.getChildAt(0).getWidth()>list.size()){
//            leftArrow.setVisibility(View.GONE);
//            rightArrow.setVisibility(View.GONE);
//        }

        final int finalScreenWidth = screenWidth;
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rvCarousel = findViewById(hashmap5.get(v.getId()));
                LinearLayoutManager manager = (LinearLayoutManager) rvCarousel.getLayoutManager();
                if (manager.findFirstVisibleItemPosition() > finalScreenWidth / manager.getChildAt(0).getWidth()) {
                    rvCarousel.smoothScrollToPosition(manager.findFirstCompletelyVisibleItemPosition() - finalScreenWidth / manager.getChildAt(0).getWidth() + 1);
                } else {
                    rvCarousel.smoothScrollToPosition(0);
                }

            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rvCarousel = findViewById(hashmap3.get(v.getId()));
                int size = hashmap5.get(v.getId());
                LinearLayoutManager manager = (LinearLayoutManager) rvCarousel.getLayoutManager();
                if (size - manager.findLastCompletelyVisibleItemPosition() > finalScreenWidth / manager.getChildAt(0).getWidth() - 1)
                    rvCarousel.smoothScrollToPosition(manager.findLastCompletelyVisibleItemPosition() + finalScreenWidth / manager.getChildAt(0).getWidth() - 1);
                else rvCarousel.smoothScrollToPosition(size - 1);

            }
        });

        rvCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                ImageView leftArrow = findViewById(hashmap4.get(recyclerView.getId()).getLeftArrow());
                ImageView rightArrow = findViewById(hashmap4.get(recyclerView.getId()).getRightArrow());
                int size = hashmap4.get(recyclerView.getId()).getListSize();
                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    leftArrow.setVisibility(View.INVISIBLE);
                } else {
                    leftArrow.setVisibility(View.VISIBLE);
                }

                if (manager.findLastCompletelyVisibleItemPosition() == size - 1) {

                    rightArrow.setVisibility(View.INVISIBLE);
                } else
                    rightArrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                ImageView leftArrow = findViewById(hashmap4.get(recyclerView.getId()).getLeftArrow());
                ImageView rightArrow = findViewById(hashmap4.get(recyclerView.getId()).getRightArrow());
                int size = hashmap4.get(recyclerView.getId()).getListSize();

                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    leftArrow.setVisibility(View.INVISIBLE);
                } else {
                    leftArrow.setVisibility(View.VISIBLE);
                }

                if (manager.findLastCompletelyVisibleItemPosition() == size - 1) {

                    rightArrow.setVisibility(View.INVISIBLE);
                } else
                    rightArrow.setVisibility(View.VISIBLE);
            }
        });
    }

    public String addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        String path = "";
        try {
            File photo = new File(getAlbumStorageDir(".SignaturePad"), String.format("Signature_%d.jpeg", System.currentTimeMillis()));
            path = photo.getPath();
            Log.e("path", path);
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {

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


    public void getDataFromDynamicViews(View v, final Form response) {
        try {

            mainObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
//            JSONObject parentObj = new JSONObject();
////          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
//            parentObj.put("related_data_id", task_id);

            for (int noOfViews = 0; noOfViews < response.getFields().size(); noOfViews++) {
                JSONObject fieldsObj = new JSONObject();

                if (SettingValues.getBarcodeVal() != null) {
                    if (noOfViews == 0) {
                        fieldsObj.put("barcodee", SettingValues.getBarcodeVal());
                    }
                } else {
                    fieldsObj.put("barcodee", "null");

                }

                if (noOfViews == 0) {
                    JSONArray barcodelist = new JSONArray();
                    for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {

                        JSONObject barcodeobj = new JSONObject();

                        barcodelist.put("" + SettingValues.getBarcodelist().get(i));

                    }
                    fieldsObj.put("barcode_scan", "" + barcodelist);
//                    jsonArray.put(fieldsObj);
                }

                Log.d("BARCODDE", "barcode information is: " + response.getFields().get(noOfViews).getPlaceholder());


                if (response.getFields().get(noOfViews).getType().equals("select")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getOptionId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
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

                if (response.getFields().get(noOfViews).getType().equals("radio-group")) {

//                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
//                        try {
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//
//                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");
//
//                            String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id",
//                                    "" + a[0]);
//                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[1]);
//                            fieldsObj.put("type", "radio-group");
//                            optionsObj.put("value", "" + a[2]);
//                            optionsArray.put(optionsObj);
////                    }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
////                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
//                            isMandatoryFilled = false;
////                            return;
//                        }
//                    } else {
//                        try {
//
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//
//                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");
//
//                            String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id",
//                                    "" + a[0]);
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[1]);
//                            optionsObj.put("value", "" + a[2]);
//                            optionsArray.put(optionsObj);
////                    }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
////                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());
//
//                            Log.e("rg_id", radioGroup.getTag().toString() + "");
//
//                            fieldsObj.put("field_id",
//                                    radioGroup.getTag().toString());
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
////                            Log.e("f", a[0]);
////                            Log.e("s", a[1]);
////                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "");
//                            optionsObj.put("value", "");
//                            optionsArray.put(optionsObj);
////                        }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
////
////                            return;
//                        }
//                    }
//                    try {
//                        Switch toggle = (Switch) allViewInstance.get(noOfViews);
//                        fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
//                        fieldsObj.put("value", "");
//
//
//                        JSONArray optionsArray = new JSONArray();
//
//                        JSONObject optionsObj = new JSONObject();
//                        Option optionYes = new Option();
//                        Option optionNo = new Option();
//                        for (Option option : toggleHash.get(toggle.getId())) {
//                            if (option.getLabel().equalsIgnoreCase("yes")) {
//                                optionYes = option;
//                            } else if (option.getLabel().equalsIgnoreCase("no")) {
//                                optionNo = option;
//                            }
//
//                        }
//
//                        if (toggle.isChecked()) {
//                            optionsObj.put("option_id", "" + optionYes.getOptionId());
//                            fieldsObj.put("type", "radio-group");
//                            optionsObj.put("value", "" + optionYes.getLabel());
//                        } else {
//                            optionsObj.put("option_id", "" + optionNo.getOptionId());
//                            fieldsObj.put("type", "radio-group");
//                            optionsObj.put("value", "" + optionNo.getLabel());
//                        }
//                        optionsArray.put(optionsObj);
////                    }
//                        fieldsObj.put("option", optionsArray);
//                        jsonArray.put(fieldsObj);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
//                        btnSubmit.setEnabled(true);
//                        return;
//                    }

                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getOptionId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "radio-group");
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
                if (response.getFields().get(noOfViews).getType().equals("checkbox-group")) {

//                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
//                        Boolean empty = true;
//                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
//                        fieldsObj.put("field_id", field_id);
//                        fieldsObj.put("type", "checkbox-group");
//                        fieldsObj.put("value", "");
//                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
//                        JSONArray optionsArray = new JSONArray();
//                        for (int i = 0; i < ll.getChildCount(); i++) {
//                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
//                            if (tempChkBox.isChecked()) {
//                                empty = false;
//                                String a[] = tempChkBox.getTag().toString().split(",");
//
//                                Log.e("f", a[0]);
//                                Log.e("s", a[1]);
//
//                                JSONObject optionsObj = new JSONObject();
//                                optionsObj.put("option_id", "" + a[0]);
//                                optionsObj.put("value", "" + a[1]);
//                                optionsArray.put(optionsObj);
//                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
//                            }
//                        }
//                        if (empty) {
////                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
//                            isMandatoryFilled = false;
////                            return;
//                        } else {
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//                        }
//                    } else {
//                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
//                        fieldsObj.put("field_id", field_id);
//                        fieldsObj.put("type", "checkbox-group");
//                        fieldsObj.put("value", "");
//                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
//                        JSONArray optionsArray = new JSONArray();
//                        for (int i = 0; i < ll.getChildCount(); i++) {
//                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
//                            if (tempChkBox.isChecked()) {
//                                String a[] = tempChkBox.getTag().toString().split(",");
//
//                                Log.e("f", a[0]);
//                                Log.e("s", a[1]);
//
//                                JSONObject optionsObj = new JSONObject();
//                                optionsObj.put("option_id", "" + a[0]);
//                                optionsObj.put("value", "" + a[1]);
//                                optionsArray.put(optionsObj);
//                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
//                            }
//                        }
//
//                        fieldsObj.put("option", optionsArray);
//                        jsonArray.put(fieldsObj);
//                    }
                    JSONArray optionsArray = new JSONArray();
                    String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                    List<Option> checklistFormOptionList = field2checkList.get(Integer.parseInt(field_id));
                    fieldsObj.put("field_id", field_id);
                    fieldsObj.put("type", "checkbox-group");
                    fieldsObj.put("value", "");

                    for (Option model : checklistFormOptionList) {
                        if (model.isSelected()) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + model.getOptionId());
                            optionsObj.put("value", "" + model.getLabel());
                            optionsArray.put(optionsObj);
                        }
                    }
                    fieldsObj.put("option", optionsArray);
                    jsonArray.put(fieldsObj);

                }
                if (response.getFields().get(noOfViews).getType().equals("text") && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time")) && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        TextView textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "text");
                            fieldsObj.put("value", text);
                        } else {

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "text");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        TextView textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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


                if (response.getFields().get(noOfViews).getType().equals("number")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "number");
                            fieldsObj.put("value", text);
                        } else {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "number");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;
                        EditText textView = (EditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("textarea")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;
                        EditText textView = (EditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "textarea");
                            fieldsObj.put("value", text);
                        } else {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "textarea");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;
                        EditText textView = (EditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";


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
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "time");
                            fieldsObj.put("value", text);
                        } else {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "time");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("date")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("True")) {
                        //Date
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "date");
                            fieldsObj.put("value", text);
                        } else {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "date");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        //Date
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
                if (response.getFields().get(noOfViews).getType().equals("header")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "header");
                            fieldsObj.put("value", text);
                        } else {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "header");
                            fieldsObj.put("value", text);
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).getDialog();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
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

//                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equals("barcode"))) {
//
//                    Toast.makeText(context, "you enter barcode in place holder ", Toast.LENGTH_SHORT).getDialog();
//
//                }
                if (obj.getFields().get(noOfViews).getType().equalsIgnoreCase("file")) {
                    fieldsObj.put("field_id", response.getFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "file");
                    if (myImages.size() > 0) {
                        SelectImagesModel selectImagesModel = myImages.get(0);
                        fieldsObj.put("value", selectImagesModel.getFinalImage().getAbsolutePath());
                    } else {
                        if (obj.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true"))
                            isMandatoryFilled = false;
                        fieldsObj.put("value", "");
                    }
                    jsonArray.put(fieldsObj);

                }

                if (obj.getFields().get(noOfViews).getType().equalsIgnoreCase("text") && obj.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("barcode")) {
//                    Toast.makeText(context, "barcode json", Toast.LENGTH_SHORT).getDialog();
                }

                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            String field_id, text;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
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
//                                Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
//                                return;
                                fieldsObj.put("field_id", field_id);
                                fieldsObj.put("type", "signature");
                                fieldsObj.put("value", "");
                                isMandatoryFilled = false;
                            }

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                            isMandatoryFilled = false;
                            fieldsObj.put("field_id", response.getFields().get(noOfViews).getFieldId() + "");
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", "");
                            isMandatoryFilled = false;

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
//                            return;
                        }
                    } else {
                        try {

                            String field_id, text = "";

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
                            if (textView.getTag() != null)
                                text = textView.getTag().toString();


                            Log.d("SIGNATURE", "Signature data is: " + text);

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String path = addJpgSignatureToGallery(bm);

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
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";

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
            Log.d("COUNT", "images json count: " + jsonArray.length());
            Log.d("COUNT", "images json count: " + myImages.size());

            JSONArray jsonArray1 = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.get("type") != null && !object.get("type").equals("file"))
                    jsonArray1.put(object);
            }
            JSONObject formFields = new JSONObject();
            formFields.put("fields", jsonArray);
            JSONObject allFields = new JSONObject();
            allFields.put("fields", jsonArray1);


            JSONArray imagesArray = new JSONArray();
            JSONArray imagesPathArray = new JSONArray();
            for (int i = 0; i < myImages.size(); i++) {
                imagesArray.put(myImages.get(i).getName());
                imagesPathArray.put(myImages.get(i).getTempUri());
                Log.d("URI", "images uri is: " + myImages.get(i).getTempUri());
            }


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);


            mainObj.put("form-section", allFields);
//            mainObj.put("barcode","1234")

            QueueModel model = new QueueModel();
            model.setId(formId + "");
            model.setJson(formFields + "");
            model.setState(Constants.StateAdded);
            model.setTitle("");
            model.setMessage("");
            model.setTaskID(task_id);
            handler.deleteDraftIncidenceOnIndidenceId(formId, task_id);
            handler.addDraftIncidenceOnIncidenceID(model);

            Log.e("taskandformid", "TASK_ID IS THISSSS" + task_id + " " + "FORM ID IS THISSSS" + formId);

            Log.d("optionsObj", mainObj + "");

            TaskDetails.sectionImagesList.addAll(myImages);
            Log.d("optionsObj", "image list size is:" + TaskDetails.sectionImagesList.size());
            td = new TaskDetails();
            if (isMandatoryFilled) {
                submitData(prepareData(mainObj));
            } else {
                disableLoader();
                Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
            }


            td.resetView(position, mainObj.toString(), FormSection.this, isMandatoryFilled, isSubmitted);
//            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitData(RequestBody body) {
        ArrayList<MultipartBody.Part> images = new ArrayList<>();
        retrofit2.Call<SubmitFormResponse> call;

        for (int i = 0; i < myImages.size(); i++) {
            File file1 = new File(String.valueOf(myImages.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), myImages.get(i).getFinalImage())));
        }
        if (myImages.size() == 0) {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmitWitoutImages(tinyDB.getString(Constants.token), body);
            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
        } else {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmit(tinyDB.getString(Constants.token), body, images);
            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
        }
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

//                        handler.deleteDraftIncidencesTable();
//                        handler.deleteTabsData();
//                        handler.deleteTabsImagesData();
                        isSubmitted = true;
                        Toast.makeText(FormSection.this, "Successfully Incidence saved to Server !", Toast.LENGTH_SHORT).show();
//                        handler.deleteDraftIncidenceOnIndidenceId(formId,task_id);
                        finish();
                        disableLoader();

//                        handler.deleteAssignedIncidenceOnIndidenceId(task_id);
                    } else {
                        disableLoader();
                        Toast.makeText(FormSection.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 401) {
                    disableLoader();
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(FormSection.this).getDialog().show();
                } else {
                    disableLoader();
                    Toast.makeText(FormSection.this, "Internal Server Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                disableLoader();
                Toast.makeText(FormSection.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

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

//        float maxHeight = 1280.0f;
//        float maxWidth = 1024.0f;

        float maxHeight = 720.0f;
        float maxWidth = 520.0f;
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

    public String getFilename() {
        File file = new File(getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

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


    /* Initiate Google API Client  */

    /* Check Location Permission for Marshmallow Devices */
//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(context,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED)
//                requestLocationPermission();
//            else
//                showSettingDialog();
//        } else
//            showSettingDialog();
//
//    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(FormSection.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(FormSection.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(FormSection.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    public void removeView(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view); // <- fix
        }
    }
    /* Show Location Access Dialog */
//    private void showSettingDialog() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true); //this is the key ingredient to getDialog dialog always when GPS is off
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        if (mGoogleApiClient == null) {
//                            initGoogleAPIClient();
//                        }
//
//                        //updateGPSStatus("GPS is Enabled in your device");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(FormSection.this, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't getDialog the dialog.
//                        break;
//                }
//            }
//        });
//    }


    @Override
    protected void onResume() {
        HorizontalScrollView horizontalScrollView;
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal);
        if (!isConnected(this))
            ltNoNetwork.setVisibility(View.VISIBLE);
        else
            ltNoNetwork.setVisibility(View.GONE);
        super.onResume();
        if (SettingValues.getBarcodeVal().equals("")) {
            SettingValues.setBarcodeVal("Scan Barcode");

            barcode_text.setText(SettingValues.getBarcodeVal());
        } else {
            barcodeviews.removeAllViews();
            barcode_text.setText(SettingValues.getBarcodeVal());
            for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {
                CustomTextView txt = new CustomTextView(this);

                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                LinearLayout.LayoutParams txtperams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                txtperams.setMargins(10, 0, 10, 0);
                txt.setLayoutParams(txtperams);
                int val = i + 1;
                txt.setText("Value" + val + ": " + SettingValues.getBarcodelist().get(i) + ",");
                txt.setTextSize(14);
                txt.setGravity(Gravity.CENTER_VERTICAL);
                barcodeviews.addView(txt);
            }
        }
//        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }


    //Method to update GPS status text
//    private void updateGPSStatus(String status) {
//        gps_status.setText(status);
//    }


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */

    @Override
    public void onBackPressed() {
        onReturn();
        super.onBackPressed();

    }

    public void onReturn() {
        Log.e("Splash", "onpause");
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        hideKeyboard(FormSection.this);
        if (isDrawOverImageForm) {
            Bitmap bitmap = drawingView.exportDrawing();
            Log.d("valuie", "bitmap value is: " + bitmap);

            if (bitmap != null) {
                String path = saveImage(bitmap);
                Log.d("PAth", "image path is : " + path);

                String file = compressImage(path);
                SelectImagesModel model1 = new SelectImagesModel();
                model1.setTempUri(Uri.parse(file));
                File f = new File(file);
                model1.setFinalImage(f);
                model1.setName(f.getName());
                Log.d("IMAGE", "image name is: " + f.getName());
                myImages.add(model1);
            }
            Log.d("SIZE", "images size is: " + myImages.size());
        }
        vitaldata(getWindow().getDecorView().findViewById(android.R.id.content), objj);
        getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
        SettingValues.getBarcodelist().clear();
    }

    @Override
    protected void onPause() {
        super.onPause();


//        SettingValues.setBarcodeVal("");
//        if (photo != null) {
//
//            File file = new File(photo.getPath());
//            boolean deleted = file.delete();
//            if (deleted) {
//                Uri contentUri = Uri.fromFile(file);
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
//                sendBroadcast(mediaScanIntent);
//            }
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
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

                                final Dialog dialog = new Dialog(FormSection.this);
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

                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });
                        break;
                }
                break;
            case PIN_REQUEST:
                if (data != null) {
                    EditText editText = findViewById(data.getIntExtra("ViewId", 0));
                    editText.setText(data.getStringExtra("PinValue"));
                }
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
                    adapterSelectImages = new AdapterSelectImages(myImages, FormSection.this, ivSelectImages);
                    rvSelectImages = findViewById(hashmap5.get(currentImageViewId));
                    rvSelectImages.setAdapter(adapterSelectImages);
                }
//                adapterSelectImages = new AdapterSelectImages(myImages, this, ivSelectImages);
////                    rvSelectImages.setLayoutManager(imagesManager);
//                rvSelectImages.setAdapter(adapterSelectImages);

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT ).getDialog();
            }
        }
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
           /* Pix.start(FormSection.this,                    //Activity or Fragment Instance
                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
                    30 - myImages.size());*/
//            Toast.makeText(CreateJob.this, "All permission granted", Toast.LENGTH_LONG).getDialog();
        }

    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(FormSection.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(FormSection.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormSection.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

    private void enableLoader() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
        for (View view : allViewInstance) {
            view.setEnabled(false);
        }
    }

    private void disableLoader() {
        findViewById(R.id.progress).setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        for (View view : allViewInstance) {
            view.setEnabled(true);
        }
    }
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int)ev.getRawX();
                int rawY = (int)ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
//                    hideKeyboard(this);
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}