package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textName;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    public String name;
    public String name1;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("name", MODE_PRIVATE);
        name1 = preferences.getString("key", name);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        textName = findViewById(R.id.textName);
        imageView = findViewById(R.id.imageView);
        final Intent intent = getIntent();
        String name = intent.getStringExtra(EditNameActivity.NEW_TEXT_KEY);
        textName.setText(name);
        getUserInfo();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(i,"Select"),RESULT_LOAD_IMAGE);

//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                intent.setType("image/*");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

            }
        });
loadText();

    }
    private void saveText(){
        preferences=getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("",textName.getText().toString());
        editor.commit();

    }
    private void loadText(){
        preferences = getPreferences(MODE_PRIVATE);
        String text=preferences.getString("","");
        textName.setText(text);

    }

    private void getUserInfo() {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String name = task.getResult().getString("name");

//                            textName.setText(name);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickEdit(View view) {
        Intent intent = new Intent(this, EditNameActivity.class);
        startActivityForResult(intent,102);

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

    }

//    private void getUserInfoListener() {
//        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser()
//                .getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if(documentSnapshot != null && documentSnapshot.exists()){
//                    String name = documentSnapshot.getString("name");
//                    textName.setText(name);
//                }
//            }
//        });

//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (requestCode==102&& resultCode==RESULT_OK&&data!=null){
            String name1=data.getStringExtra("key");
            textName.setText(name1);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveText();
    }
}
