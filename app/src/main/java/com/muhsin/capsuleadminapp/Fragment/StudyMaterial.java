package com.muhsin.capsuleadminapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.muhsin.capsuleadminapp.Modal.CategoriesModal;
import com.muhsin.capsuleadminapp.Modal.StudyMaterialModal;
import com.muhsin.capsuleadminapp.Modal.SubCategoy;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;
import java.util.HashMap;


public class StudyMaterial extends Fragment {
    MyAdapter myAdapter;
    ArrayList<StudyMaterialModal> studyMaterialModals;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference,catRef;
    private RelativeLayout addLayoutManager;
    private EditText titles,description;
    private Spinner everyone,category,SubCategory,videoType;
    private Button addCategory;
    private String videoorpdfSelect="";
    private Uri imageUri;
    StorageReference mStorageRef;
    private String VideoSelect=null;
    private TextView video;
    public StudyMaterial() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_material, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        addLayoutManager = view.findViewById(R.id.addLayout);
        video = view.findViewById(R.id.video);
        titles = view.findViewById(R.id.titles);
        description = view.findViewById(R.id.description);
        everyone = view.findViewById(R.id.everyone);
        category = view.findViewById(R.id.category);
        SubCategory = view.findViewById(R.id.SubCategory);
        videoType = view.findViewById(R.id.videoType);
        addCategory = view.findViewById(R.id.addCategory);

        videoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    if(position == 1)
                    {
                        VideoSelect = "Video";
                    }
                    else
                    {
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
                if(VideoSelect == "")
                {
                    Toast.makeText(getActivity(), "Please First select type", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if(VideoSelect.equals("Video"))
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent , 100);;
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/pdf");
                        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");
                        startActivityForResult(intent, 200);
                    }
                }
            }
        });
        view.findViewById(R.id.addCategoryBtns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayoutManager.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayoutManager.setVisibility(View.GONE);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("StudyMaterial");
        catRef = FirebaseDatabase.getInstance().getReference("");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        studyMaterialModals = new ArrayList<>();

        myAdapter=new MyAdapter(getActivity(),getActivity(),studyMaterialModals);
        recyclerView.setAdapter(myAdapter);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = titles.getText().toString();
                String Description = description.getText().toString();
                if(Title.isEmpty() || Description.isEmpty() || videoorpdfSelect == "" || VideoSelect == null)
                {
                    Toast.makeText(getActivity(), "all fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle(" Wait");
                    progressDialog.setMessage("Material is posting");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    final StorageReference riversRef = mStorageRef.child("Files/_"+System.currentTimeMillis());

                    riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();

                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("Title",Title);
                                    hashMap.put("Description",Description);
                                    hashMap.put("File",uri.toString());
                                    hashMap.put("Type",VideoSelect);
                                    hashMap.put("Status",everyone.getSelectedItem().toString());
                                    hashMap.put("category",category.getSelectedItem().toString());
                                    hashMap.put("subCategory",SubCategory.getSelectedItem().toString());
                                    databaseReference.child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                                    Toast.makeText(getActivity(), "Study material has been added", Toast.LENGTH_SHORT).show();
                                    addLayoutManager.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });
        getCategorys();


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
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categories);
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
        getStudyMaterials();
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
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                category.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getStudyMaterials()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                studyMaterialModals.clear();
                for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                {
                   StudyMaterialModal modal = new StudyMaterialModal();
                   modal.setId(dataSnapshot2.getKey());
                   modal.setCategory(dataSnapshot2.child("category").getValue().toString());
                   modal.setMaterialType(dataSnapshot2.child("Type").getValue().toString());
                   modal.setStatus(dataSnapshot2.child("Status").getValue().toString());
                   modal.setSubCategory(dataSnapshot2.child("subCategory").getValue().toString());
                   modal.setTitle(dataSnapshot2.child("Title").getValue().toString());
                   modal.setDescription(dataSnapshot2.child("Description").getValue().toString());
                   modal.setFile(dataSnapshot2.child("File").getValue().toString());
                    studyMaterialModals.add(modal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<StudyMaterialModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView title,category,SubCategory,available,videoStatus,description;
            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                category = view.findViewById(R.id.category);
                SubCategory = view.findViewById(R.id.SubCategory);
                available = view.findViewById(R.id.available);
                videoStatus = view.findViewById(R.id.videoStatus);
                description = view.findViewById(R.id.description);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<StudyMaterialModal> CompanyJobModal){
            this.data =CompanyJobModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.material_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            StudyMaterialModal modal = data.get(position);

            viewHolder.available.setText("Available for : "+modal.getStatus());
            viewHolder.category.setText("Category : "+modal.getCategory());
            viewHolder.SubCategory.setText("Sub Category : "+modal.getSubCategory());
            viewHolder.description.setText(modal.getDescription());
            viewHolder.title.setText(modal.getTitle());
            viewHolder.videoStatus.setText("File Type : "+modal.getMaterialType());
        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<StudyMaterialModal> newList){
            data=new ArrayList<>();
            data.addAll(newList);
            notifyDataSetChanged();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
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
            VideoSelect="123";
            videoorpdfSelect  = "123";
        }
        if(requestCode==200 && resultCode==RESULT_OK){
            imageUri = data.getData();
            VideoSelect="123";
            videoorpdfSelect = "123";
        }
    }

}