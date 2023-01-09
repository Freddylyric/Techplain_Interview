package com.example.loginprac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    // create object to access database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://techusers-d80c6-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //declare variables
        final EditText name = findViewById(R.id.inputName);
        final EditText email = findViewById(R.id.inputEmail);
        final EditText password = findViewById(R.id.inputPassword);
        final Button saveButton = findViewById(R.id.saveButton);

        loader = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userName = name.getText().toString();
                final String userEmail = email.getText().toString();
                final String userPassword = password.getText().toString();



                if (userName.isEmpty()|| userEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter your Name and Email", Toast.LENGTH_SHORT).show();
                }
                else{

                        loader.setMessage("Registering you...");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (!task.isSuccessful()) {
                                    String error = task.getException().toString();
                                    Toast.makeText(MainActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                                } else {
                                    String currentUserId = mAuth.getCurrentUser().getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(currentUserId);


                                    //creating hash keys
                                    HashMap<String, Object> userInfo = new HashMap<String, Object>();
                                    userInfo.put("id", currentUserId);
                                    userInfo.put("name", userName);
                                    userInfo.put("email", userEmail);


                                    databaseReference.updateChildren(userInfo).addOnCompleteListener((OnCompleteListener<Void>) task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Information Received Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, Objects.requireNonNull(task1.getException()).toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();

                                        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
                                        startActivity(intent);
                                        finish();
                                        loader.dismiss();
                                        //loader.dismiss();

                                    });


                                }


                            }
                        });
                }
            }
        });
    }
}