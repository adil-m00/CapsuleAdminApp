package com.muhsin.capsuleadminapp.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhsin.capsuleadminapp.Activities.EditCategory;
import com.muhsin.capsuleadminapp.Modal.CategoriesModal;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Category extends Fragment {

    MyAdapter myAdapter;
    ArrayList<CategoriesModal> categoriesModals;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private ImageView close;
    private RelativeLayout addLayout;
    private EditText category;
    private Button addCategory;
    public Category() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        close = view.findViewById(R.id.close);
        addLayout = view.findViewById(R.id.addLayout);
        category = view.findViewById(R.id.category);
        addCategory = view.findViewById(R.id.addCategory);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.addCategoryBtns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.VISIBLE);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        categoriesModals = new ArrayList<>();

        myAdapter=new MyAdapter(getActivity(),getActivity(),categoriesModals);
        recyclerView.setAdapter(myAdapter);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CategoryName = category.getText().toString();
                if(CategoryName.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please Enter category", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Category",CategoryName);
                    databaseReference.child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                    Toast.makeText(getActivity(), "Category has been added", Toast.LENGTH_SHORT).show();
                    addLayout.setVisibility(View.GONE);
                }
            }
        });
        getCategorys();
    }
    public void getCategorys()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                {
                    CategoriesModal modal = new CategoriesModal();
                    modal.setCategory(dataSnapshot2.child("Category").getValue().toString());
                    modal.setId(dataSnapshot2.getKey());
                    categoriesModals.add(modal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<CategoriesModal> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView categoryName;
            private ImageView editImage,deleteImage;
            public MyViewHolder(View view) {
                super(view);
                categoryName = view.findViewById(R.id.categoryName);
                editImage = view.findViewById(R.id.editImage);
                deleteImage = view.findViewById(R.id.deleteImage);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<CategoriesModal> CompanyJobModal){
            this.data =CompanyJobModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.categories_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {
            CategoriesModal modal = data.get(position);

            viewHolder.categoryName.setText(modal.getCategory());
            viewHolder.editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), EditCategory.class).putExtra("categId",modal.getId()));
                }
            });
            viewHolder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idss = modal.getId();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Are you sure you want to delete?.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseReference.child(idss).removeValue();
                                    Toast.makeText(getActivity(), "Material has been deleted", Toast.LENGTH_SHORT).show();
                                    myAdapter.notifyDataSetChanged();
                                    return;
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
            });
        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<CategoriesModal> newList){
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