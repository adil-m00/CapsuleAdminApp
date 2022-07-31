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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhsin.capsuleadminapp.Modal.AllMcqsModal;
import com.muhsin.capsuleadminapp.Modal.CategoriesModal;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;
import java.util.HashMap;


public class AllMCQS extends Fragment {
    MyAdapter myAdapter;
    ArrayList<AllMcqsModal> allMCQS;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private RelativeLayout addLayout;
    private EditText questions,option1,option2,option3,option4;
    private Spinner answer;
    private Button addMCQS;
    private ImageView images;
    private Uri imageUri;
    StorageReference mStorageRef;
    private String imageSelect="";

    public AllMCQS() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_m_c_q_s, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        images = view.findViewById(R.id.images);
        questions = view.findViewById(R.id.questions);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        answer = view.findViewById(R.id.answer);
        addMCQS = view.findViewById(R.id.addMCQS);
        addLayout = view.findViewById(R.id.addLayout);

        view.findViewById(R.id.addCategoryBtns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.GONE);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("MCQS");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        allMCQS = new ArrayList<>();

        myAdapter=new MyAdapter(getActivity(),getActivity(),allMCQS);
        recyclerView.setAdapter(myAdapter);

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent , 100);;
            }
        });

        addMCQS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Questions = questions.getText().toString();
                String Option1 = option1.getText().toString();
                String Option2 = option2.getText().toString();
                String Option3 = option3.getText().toString();
                String Option4 = option4.getText().toString();

                if(imageSelect == "" || Questions.isEmpty() || Option1.isEmpty() || Option2.isEmpty() || Option3.isEmpty() || Option4.isEmpty())
                {
                    Toast.makeText(getActivity(), "all fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Wait");
                    progressDialog.setMessage("MCQS is posting");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    final StorageReference riversRef = mStorageRef.child("MCQS/_"+System.currentTimeMillis());

                    riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();

                                    HashMap<String,Object> hashMap  = new HashMap<>();
                                    hashMap.put("Question",Questions);
                                    hashMap.put("Option1",Option1);
                                    hashMap.put("Option2",Option2);
                                    hashMap.put("Option3",Option3);
                                    hashMap.put("image",uri.toString());
                                    hashMap.put("Option4",Option4);
                                    hashMap.put("Answer",answer.getSelectedItem().toString());
                                    databaseReference.child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                                    Toast.makeText(getActivity(), "MCQS has been added", Toast.LENGTH_SHORT).show();
                                    addLayout.setVisibility(View.GONE);
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

        getAllMcqs();
    }
    public void getAllMcqs()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                allMCQS.clear();
                for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                {
                    AllMcqsModal  modal = new AllMcqsModal();
                    modal.setAnswer(dataSnapshot2.child("Answer").getValue().toString());
                    modal.setId(dataSnapshot2.getKey());
                    modal.setQ1(dataSnapshot2.child("Option1").getValue().toString());
                    modal.setQ2(dataSnapshot2.child("Option2").getValue().toString());
                    modal.setQ3(dataSnapshot2.child("Option3").getValue().toString());
                    modal.setQ4(dataSnapshot2.child("Option4").getValue().toString());
                    modal.setQ4(dataSnapshot2.child("Option4").getValue().toString());
                    modal.setImage(dataSnapshot2.child("image").getValue().toString());
                    modal.setQustions(dataSnapshot2.child("Question").getValue().toString());

                    allMCQS.add(modal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<AllMcqsModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView questions,q1,q2,q3,q4;
            public MyViewHolder(View view)
            {
                super(view);
                questions = view.findViewById(R.id.questions);
                q1 = view.findViewById(R.id.q1);
                q2 = view.findViewById(R.id.q2);
                q3 = view.findViewById(R.id.q3);
                q4 = view.findViewById(R.id.q4);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<AllMcqsModal> CompanyJobModal){
            this.data =CompanyJobModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mcqs_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            AllMcqsModal modal = data.get(position);

            viewHolder.questions.setText(modal.getQustions());
            viewHolder.q1.setText(modal.getQ1());
            viewHolder.q2.setText(modal.getQ2());
            viewHolder.q3.setText(modal.getQ3());
            viewHolder.q4.setText(modal.getQ4());
        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<AllMcqsModal> newList){
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
            imageSelect="123";
            images.setImageURI(imageUri);
        }
    }
}