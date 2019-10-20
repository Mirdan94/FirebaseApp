package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private EditText editText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private boolean isCodeSent;
    private Button sendCode;
    private EditText editText2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        editText = findViewById(R.id.editText);
        sendCode = findViewById(R.id.sendCode);
        editText2 = findViewById(R.id.editText2);
        final String code = editText2.getText().toString().trim();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull final PhoneAuthCredential phoneAuthCredential) {
                Log.e("TAG", "onVerificationCompleted");
                if (isCodeSent) {
                    verifyPhoneNumberWithCode(phoneAuthCredential, code);
                } else {
                    signIn(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("TAG", "onVerificationFailed" + e.getMessage());
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(PhoneActivity.this, "Время ожидания прошло", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                isCodeSent = true;
            }
        };

        editText2.setVisibility(View.GONE);
        sendCode.setVisibility(View.GONE);

    }


    private void verifyPhoneNumberWithCode(PhoneAuthCredential verificationId, String code) {
        // [START verify_with_code]
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(String.valueOf(verificationId), editText2.getText().toString().trim());
        // [END verify_with_code]
        signIn(credential);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        startActivity(new Intent(PhoneActivity.this, MainActivity.class));

            }
        });
    }


    public void onClick(View view) {
        String phone = editText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            editText.setError("Напишите номер");
        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,
                    30,
                    TimeUnit.SECONDS,
                    this,
                    callbacks);
            view.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            editText2.setVisibility(View.VISIBLE);
            sendCode.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneActivity.this, "Успеешно", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.e("TAG", "Ошибка авторизации");
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}


