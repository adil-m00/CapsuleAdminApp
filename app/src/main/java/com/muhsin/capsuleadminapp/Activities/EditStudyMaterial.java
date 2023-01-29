package com.muhsin.capsuleadminapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhsin.capsuleadminapp.Modal.StudyMaterialModal;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EditStudyMaterial extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference,catRef;
    private EditText titles,description;
    private Spinner everyone,category,SubCategory,videoType;
    private Button addCategory;
    private String videoorpdfSelect="";
    private Uri imageUri;
    StorageReference mStorageRef;
    private String VideoSelect=null;
    private TextView video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_study_material);
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        video = findViewById(R.id.video);
        titles = findViewById(R.id.titles);
        description = findViewById(R.id.description);
        everyone = findViewById(R.id.everyone);
        category = findViewById(R.id.category);
        SubCategory = findViewById(R.id.SubCategory);
        videoType = findViewById(R.id.videoType);
        addCategory = findViewById(R.id.addCategory);
        String materialId = getIntent().getStringExtra("materialId");

        videoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (position == 1) {
                        VideoSelect = "Video";
                    } else {
                        VideoSelect = "PDF";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoSelect == null) {
                    Toast.makeText(EditStudyMaterial.this, "Please First select type", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (VideoSelect.equals("Video")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 100);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/pdf");
                        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");
                        startActivityForResult(intent, 200);
                    }
                }
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("StudyMaterial");
        catRef = FirebaseDatabase.getInstance().getReference("");
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = titles.getText().toString();
                String Description = description.getText().toString();
                if (Title.isEmpty() || Description.isEmpty()) {
                    Toast.makeText(EditStudyMaterial.this, "all fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(VideoSelect == null)
                {
                    progressDialog.dismiss();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Title", Title);
                    hashMap.put("Description", Description);
                    hashMap.put("Type", VideoSelect);
                    hashMap.put("Status", everyone.getSelectedItem().toString());
                    hashMap.put("category", category.getSelectedItem().toString());
                    hashMap.put("subCategory", SubCategory.getSelectedItem().toString());
                    databaseReference.child(materialId).updateChildren(hashMap);
                    Toast.makeText(EditStudyMaterial.this, "Study material has been updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
                else {
                    progressDialog = new ProgressDialog(EditStudyMaterial.this);
                    progressDialog.setTitle(" Wait");
                    progressDialog.setMessage("Material is posting");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    final StorageReference riversRef = mStorageRef.child("Files/_" + System.currentTimeMillis());

                    riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("Title", Title);
                                    hashMap.put("Description", Description);
                                    hashMap.put("File", uri.toString());
                                    hashMap.put("Type", VideoSelect);
                                    hashMap.put("Status", everyone.getSelectedItem().toString());
                                    hashMap.put("category", category.getSelectedItem().toString());
                                    hashMap.put("subCategory", SubCategory.getSelectedItem().toString());
                                    databaseReference.child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                                    Toast.makeText(EditStudyMaterial.this, "Study material has been added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditStudyMaterial.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catRef.child("subCategories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> categories = new ArrayList<>();
                        for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                        {
                            if(dataSnapshot2.child("Category").getValue().equals(category.getSelectedItem())) {
                                categories.add(dataSnapshot2.child("SubCategory").getValue().toString());
                            }
                        }
                        ArrayAdapter adapter = new ArrayAdapter(EditStudyMaterial.this, android.R.layout.simple_spinner_item, categories);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        SubCategory.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCategorys();

        getStudyMaterials(materialId);
    }
    public void getCategorys()
    {
        catRef.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                ArrayList<String> categories = new ArrayList<>();
                for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                {
                    categories.add(dataSnapshot2.child("Category").getValue().toString());
                }
                ArrayAdapter adapter = new ArrayAdapter(EditStudyMaterial.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                category.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getStudyMaterials(String ids)
    {
        databaseReference.child(ids).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    StudyMaterialModal modal = new StudyMaterialModal();
                    modal.setId(snapshot.getKey());
                    modal.setCategory(snapshot.child("category").getValue().toString());
                    modal.setMaterialType(snapshot.child("Type").getValue().toString());
                    modal.setStatus(snapshot.child("Status").getValue().toString());
                    modal.setSubCategory(snapshot.child("subCategory").getValue().toString());
                    modal.setTitle(snapshot.child("Title").getValue().toString());
                    modal.setDescription(snapshot.child("Description").getValue().toString());
                    modal.setFile(snapshot.child("File").getValue().toString());

                    titles.setText(modal.getTitle());
                    description.setText(modal.getDescription());
                    if(modal.getStatus().equals("Free"))
                    {
                        everyone.setSelection(1);
                    }
                    else
                    {
                        everyone.setSelection(2);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && resultCode==RESULT_OK){
            imageUri = data.getData();
            videoorpdfSelect  = "123";
        }
        if(requestCode==200 && resultCode==RESULT_OK){
            imageUri = data.getData();
            videoorpdfSelect = "123";
        }
    }

}