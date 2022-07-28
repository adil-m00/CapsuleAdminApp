package com.muhsin.capsuleadminapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.muhsin.capsuleadminapp.Fragment.AllMCQS;
import com.muhsin.capsuleadminapp.Fragment.Category;
import com.muhsin.capsuleadminapp.Fragment.HomeFragment;
import com.muhsin.capsuleadminapp.Fragment.StudyMaterial;
import com.muhsin.capsuleadminapp.Fragment.SubCatMCQS;
import com.muhsin.capsuleadminapp.Fragment.SubCategory;
import com.muhsin.capsuleadminapp.Fragment.UpdateSettings;
import com.muhsin.capsuleadminapp.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private ImageView menuIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuIcon = findViewById(R.id.menuIcon);

        dl = (DrawerLayout)findViewById(R.id.drawer);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();



        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.dashboard:
                        homeFragment();
                        break;
                    case R.id.studyMaterials:
                        studyMaterials();
                        break;
                    case R.id.categories:
                        categories();
                        break;
                    case R.id.subCat:
                        subCat();
                        break;
                    case R.id.allmcqs:
                        allmcqs();
                        break;
                    case R.id.subCatMcqs:
                        subCatMcqs();
                        break;
                    case R.id.settings:
                        settings();
                        break;

                }
                return true;

            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(Gravity.LEFT);
            }
        });


        homeFragment();
    }

    private void settings() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new UpdateSettings();
        loadFragment(fragment);
    }

    private void subCatMcqs() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new SubCatMCQS();
        loadFragment(fragment);
    }

    private void allmcqs() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new AllMCQS();
        loadFragment(fragment);
    }

    private void subCat() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new SubCategory();
        loadFragment(fragment);
    }

    private void categories() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new Category();
        loadFragment(fragment);
    }

    private void studyMaterials() {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new StudyMaterial();
        loadFragment(fragment);
    }

    public void homeFragment()
    {
        dl.closeDrawer(Gravity.LEFT);
        final Fragment fragment;
        fragment = new HomeFragment();
        loadFragment(fragment);
    }

    public void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void logout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure you want to logout?.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences = getSharedPreferences("My-Ref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId",null);
                        editor.putString("userType",null);
                        editor.commit();
                        editor.apply();
                        startActivity(new Intent(MainActivity.this,Login.class));
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure to exit?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}