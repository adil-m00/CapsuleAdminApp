package com.muhsin.capsuleadminapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhsin.capsuleadminapp.R;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText email,password;
    private ImageView login;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        email = findViewById(R.id.editText2);
        password = findViewById(R.id.passwordText);
        login = findViewById(R.id.signIn);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if(Email.isEmpty() || Password.isEmpty())
                {
                    Toast.makeText(Login.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String userId = firebaseAuth.getCurrentUser().getUid().toString();
                                SharedPreferences sharedPreferences = getSharedPreferences("My-Ref",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId",userId);
                                editor.commit();
                                editor.apply();
                                startActivity(new Intent(Login.this,MainActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });




    }

}