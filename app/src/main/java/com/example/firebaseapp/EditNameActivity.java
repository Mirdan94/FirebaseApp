package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNameActivity extends AppCompatActivity {
    private EditText editText;
    static final int REQUEST_CODE = 100;
    static final String NEW_TEXT_KEY = "key";
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        editText = findViewById(R.id.editText);
        editText.setText(name);
    }

    public void onClick(View view) {
        String name = editText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) return;
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditNameActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditNameActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        Intent intent1=getIntent();
        intent1.putExtra("key",editText.getText().toString());
        setResult(RESULT_OK,intent1);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(EditNameActivity.NEW_TEXT_KEY);
                this.name = name;
                Log.d("ololo", name);

            }
        }
    }
}
