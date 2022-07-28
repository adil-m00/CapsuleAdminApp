package com.muhsin.capsuleadminapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.muhsin.capsuleadminapp.R;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("My-Ref",MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId","");
        String userType = sharedPreferences.getString("userType","");

        Handler handler = new Handler();

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(userId!="")
                        {

                                startActivity(new Intent(Splash.this,MainActivity.class));
                                finish();

                        }
                        else
                        {
                            startActivity(new Intent(Splash.this,Login.class));
                            finish();
                        }

                    }
                },2500);
            }
        });


    }
}