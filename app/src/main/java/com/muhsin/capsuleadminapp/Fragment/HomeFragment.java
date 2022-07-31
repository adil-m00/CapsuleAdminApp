package com.muhsin.capsuleadminapp.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhsin.capsuleadminapp.Modal.StudyMaterialModal;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    MyAdapter myAdapter;
    ArrayList<StudyMaterialModal> studyMaterialModals;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference,catRef;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        getStudyMaterials();
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
}