package com.aap.medicore.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentReport extends BaseActivity {
    ImageView ivBack;
    TinyDB tinyDB;
    ImageView ivSign;
    CustomTextView tvTime, tvDate, tvSignature;
    String incident_date = "", incident_time = "", reported_by = "", location = "", affected_persona_details = "", did_anybody_suffer_harm = "", assistance_given = "", incident_reported_to_other_agency = "", incident_witness = "", details_of_the_incident = "", job_id = "", signature = "";
    CustomEditText etLocation, etReportedBy, etAffectedPersonDetails, etHarm, etWasAssistanced, etReportedToAgency, etWitness, etDetailsOfIncident;
    CustomButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
        getSupportActionBar().hide();

        if (getIntent() != null)
            job_id = getIntent().getStringExtra("id");

        Log.e("id", job_id + "");

        init();
        clickListeners();
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(IncidentReport.this);
        ivSign = findViewById(R.id.ivSign);
        tvSignature = findViewById(R.id.tvSignature);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        etLocation = findViewById(R.id.etLocation);
        etReportedBy = findViewById(R.id.etReportedBy);
        etHarm = findViewById(R.id.etHarm);
        etWasAssistanced = findViewById(R.id.etWasAssistanced);
        etReportedToAgency = findViewById(R.id.etReportedToAgency);
        etWitness = findViewById(R.id.etWitness);
        etDetailsOfIncident = findViewById(R.id.etDetailsOfIncident);
        etAffectedPersonDetails = findViewById(R.id.etAffectedPersonDetails);

        btnSubmit = findViewById(R.id.btnLogin);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        //String myFormat = " MM-dd-yyyy"; //In which you need put here
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        tvDate.setText(" " + sdf.format(myCalendar.getTime()));
                        incident_date = " " + sdf.format(myCalendar.getTime());
                    }

                };

                tvDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new DatePickerDialog(IncidentReport.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(IncidentReport.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int selectedMinute) {


                        incident_time = hourOfDay + " : " + selectedMinute;
                        if (hourOfDay >= 0 && hourOfDay < 12) {
                            incident_time = hourOfDay + ":" + selectedMinute + " AM";
                            tvTime.setText("  " + hourOfDay + ": " + selectedMinute + " AM");
                        } else {
                            if (hourOfDay == 12) {
                                incident_time = hourOfDay + ":" + selectedMinute + "PM";
                                tvTime.setText("  " + hourOfDay + ":" + selectedMinute + " PM");
                            } else {
                                hourOfDay = hourOfDay - 12;
                                incident_time = hourOfDay + " : " + selectedMinute + "PM";
                                tvTime.setText("  " + hourOfDay + ":" + selectedMinute + " PM");
                            }
                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        tvSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(IncidentReport.this, SignatureActivity.class);
                startActivityForResult(i, 109);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (!signature.equals("") && !incident_time.equals("") && !incident_date.equals("") && !location.equals("") && !reported_by.equals("")) {
                    if (isConnected(IncidentReport.this)) {

                        findViewById(R.id.progress).setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        submitIncidentReportForm();
                    } else {
                        Toast.makeText(IncidentReport.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                } else if (incident_time.equals("")) {
                    Toast.makeText(IncidentReport.this, "Please fill the time field", Toast.LENGTH_SHORT).show();
                } else if (incident_date.equals("")) {
                    Toast.makeText(IncidentReport.this, "Please fill the date field", Toast.LENGTH_SHORT).show();
                } else if (location.equals("")) {
                    Toast.makeText(IncidentReport.this, "Please fill the location field", Toast.LENGTH_SHORT).show();
                } else if (reported_by.equals("")) {
                    Toast.makeText(IncidentReport.this, "Please fill the reported by  field", Toast.LENGTH_SHORT).show();
                } else if (signature.equals("")) {
                    Toast.makeText(IncidentReport.this, "Please take signature first", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void submitIncidentReportForm() {
        reported_by = etReportedBy.getText().toString();
        location = etLocation.getText().toString();
        affected_persona_details = etAffectedPersonDetails.getText().toString();
        did_anybody_suffer_harm = etHarm.getText().toString();
        assistance_given = etWasAssistanced.getText().toString();
        incident_reported_to_other_agency = etReportedToAgency.getText().toString();
        incident_witness = etWitness.getText().toString();
        details_of_the_incident = etDetailsOfIncident.getText().toString();


        RequestBody jobId, mIncident_date, mIncident_time, mReported_by, mLocation, mAffected_persona_details, mDid_anybody_suffer_harm, mAssistance_given, mIncident_reported_to_other_agency, mIncident_witness, mDetails_of_the_incident;
        MultipartBody.Part mSignature;
        Log.e("id", job_id + "");
        jobId = RequestBody.create(MediaType.parse("multipart/form-data"), job_id);
        mIncident_date = RequestBody.create(MediaType.parse("multipart/form-data"), incident_date);
        mIncident_time = RequestBody.create(MediaType.parse("multipart/form-data"), incident_time);
        mReported_by = RequestBody.create(MediaType.parse("multipart/form-data"), reported_by);
        mLocation = RequestBody.create(MediaType.parse("multipart/form-data"), location);
        mAffected_persona_details = RequestBody.create(MediaType.parse("multipart/form-data"), affected_persona_details);
        mDid_anybody_suffer_harm = RequestBody.create(MediaType.parse("multipart/form-data"), did_anybody_suffer_harm);

        mAssistance_given = RequestBody.create(MediaType.parse("multipart/form-data"), assistance_given);
        mIncident_reported_to_other_agency = RequestBody.create(MediaType.parse("multipart/form-data"), incident_reported_to_other_agency);
        mIncident_witness = RequestBody.create(MediaType.parse("multipart/form-data"), incident_witness);
        mDetails_of_the_incident = RequestBody.create(MediaType.parse("multipart/form-data"), details_of_the_incident);

        File file1 = new File(String.valueOf(signature));
        mSignature = MultipartBody.Part.createFormData("signature", file1.getName(), RequestBody.create(MediaType.parse("image/*"), file1));

        retrofit2.Call<SubmitFormResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().reportIncident(tinyDB.getString(Constants.token), mIncident_date, mIncident_time, mReported_by, mLocation, mAffected_persona_details, mDid_anybody_suffer_harm, mAssistance_given, mIncident_reported_to_other_agency, mIncident_witness, mDetails_of_the_incident, jobId, mSignature);

        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        Log.e("incident report", "responseeeee1" + mAffected_persona_details);
                        Log.e("incident report", "responseeeee2" + mAffected_persona_details);
                        Toast.makeText(IncidentReport.this, "Successfully Reported to Admin Panel!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(IncidentReport.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 401) {
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(IncidentReport.this).getDialog().show();
                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(IncidentReport.this, "Internal Server Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                Toast.makeText(IncidentReport.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                findViewById(R.id.progress).setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 109) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                ivSign.setVisibility(View.VISIBLE);
                signature = result;

                Glide.with(IncidentReport.this).load(result).into(ivSign);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}