package com.muhsin.capsuleadminapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhsin.capsuleadminapp.Modal.CategoriesModal;
import com.muhsin.capsuleadminapp.R;

import java.util.HashMap;

public class EditCategory extends AppCompatActivity {
    private EditText category;
    private Button addCategory;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        category = findViewById(R.id.category);
        addCategory = findViewById(R.id.addCategory);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        String categId = getIntent().getStringExtra("categId");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getCategorys(categId);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CategoryName = category.getText().toString();
                if(CategoryName.isEmpty())
                {
                    Toast.makeText(EditCategory.this, "Please Enter category", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Category",CategoryName);
                    databaseReference.child(categId).updateChildren(hashMap);
                    Toast.makeText(EditCategory.this, "Category has been updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void getCategorys(String id)
    {
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                    CategoriesModal modal = new CategoriesModal();
                    modal.setCategory(snapshot.child("Category").getValue().toString());
                    modal.setId(snapshot.getKey());
                category.setText(modal.getCategory());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}