package com.aap.medicore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aap.medicore.R;

public class PINVerificationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText otpCodeEnterEt;
    ImageView ivBack, ivNext;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv0;
    ImageView ivDelete;
    private static final int PIN_RESULT = 121212;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.layout_pin_verification);
        otpCodeEnterEt = findViewById(R.id.etCode);
        ivNext = findViewById(R.id.ivNext);
        ivBack = findViewById(R.id.ivBack);
        tv0 = findViewById(R.id.button_no_0);
        tv1 = findViewById(R.id.button_no_1);
        tv2 = findViewById(R.id.button_no_2);
        tv3 = findViewById(R.id.button_no_3);
        tv4 = findViewById(R.id.button_no_4);
        tv5 = findViewById(R.id.button_no_5);
        tv6 = findViewById(R.id.button_no_6);
        tv7 = findViewById(R.id.button_no_7);
        tv8 = findViewById(R.id.button_no_8);
        tv9 = findViewById(R.id.button_no_9);
        ivDelete = findViewById(R.id.button_remove);
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.next:
                mVerificationId = sharedPrefClass.getmVerificationId();
                String code = otpCodeEnterEt.getText().toString();
                if (!TextUtils.isEmpty(code))
                    otpVerification.verifyPhoneNumberWithCode(mVerificationId, code);
                break;*/
            case R.id.button_no_1:
                otpCodeEnterEt.append("1");
                break;
            case R.id.button_no_2:
                otpCodeEnterEt.append("2");
                break;
            case R.id.button_no_3:
                otpCodeEnterEt.append("3");
                break;
            case R.id.button_no_4:
                otpCodeEnterEt.append("4");
                break;
            case R.id.button_no_5:
                otpCodeEnterEt.append("5");
                break;
            case R.id.button_no_6:
                otpCodeEnterEt.append("6");
                break;
            case R.id.button_no_7:
                otpCodeEnterEt.append("7");
                break;
            case R.id.button_no_8:
                otpCodeEnterEt.append("8");
                break;
            case R.id.button_no_9:
                otpCodeEnterEt.append("9");
                break;
            case R.id.button_no_0:
                otpCodeEnterEt.append("0");
                break;
            case R.id.button_remove:
                int otpCodeLength = otpCodeEnterEt.getText().length();
                /*if (!otpCodeLength.isEmpty()) {
                    otpCodeEnterEt.setText(otpCodeLength.substring(0, otpCodeLength.length() - 1));
                }*/
                int pos = otpCodeEnterEt.getSelectionStart();
                if (pos > 0) {

                    otpCodeEnterEt.setText(otpCodeEnterEt.getText().delete(pos - 1, pos).toString());
                    otpCodeEnterEt.setSelection(pos - 1);
                }

                otpCodeEnterEt.setFocusableInTouchMode(true);

                break;

            case R.id.ivNext:
                Intent intent = new Intent();
                intent.putExtra("PinValue",otpCodeEnterEt.getText().toString());
                intent.putExtra("ViewId",getIntent().getIntExtra("ViewId",0));
                setResult(PIN_RESULT,intent);
                finish();

            case R.id.ivBack:
                onBackPressed();
        }
    }
}