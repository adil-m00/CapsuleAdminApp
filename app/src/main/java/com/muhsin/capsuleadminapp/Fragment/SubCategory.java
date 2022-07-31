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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhsin.capsuleadminapp.Modal.CategoriesModal;
import com.muhsin.capsuleadminapp.Modal.SubCategoy;
import com.muhsin.capsuleadminapp.R;

import java.util.ArrayList;
import java.util.HashMap;


public class SubCategory extends Fragment {

    MyAdapter myAdapter;
    ArrayList<SubCategoy> subCategoys;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private ImageView close;
    private RelativeLayout addLayout;
    private Spinner category;
    private EditText subCategory;
    private Button addCategory;

    public SubCategory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        close = view.findViewById(R.id.close);
        addLayout = view.findViewById(R.id.addLayout);
        category = view.findViewById(R.id.category);
        subCategory = view.findViewById(R.id.subCategory);
        addCategory = view.findViewById(R.id.addSub);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.VISIBLE);
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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        subCategoys = new ArrayList<>();

        myAdapter=new MyAdapter(getActivity(),getActivity(),subCategoys);
        recyclerView.setAdapter(myAdapter);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SubCate = subCategory.getText().toString();
                if(SubCate.isEmpty())
                {
                    Toast.makeText(getActivity(), "all fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("Category",category.getSelectedItem().toString());
                    hashMap.put("SubCategory",SubCate);
                    databaseReference.child("subCategories").child(String.valueOf(System.currentTimeMillis())).updateChildren(hashMap);
                    Toast.makeText(getActivity(), "Sub-Category has been added", Toast.LENGTH_SHORT).show();
                    addLayout.setVisibility(View.GONE);
                }
            }
        });
        getCategorys();
        getSubCategories();
    }
    public void getSubCategories()
    {
        databaseReference.child("subCategories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                subCategoys.clear();
                for(DataSnapshot dataSnapshot2:snapshot.getChildren())
                {
                    SubCategoy modal = new SubCategoy();
                    modal.setCategory(dataSnapshot2.child("Category").getValue().toString());
                    modal.setSubCategory(dataSnapshot2.child("SubCategory").getValue().toString());
                    modal.setId(dataSnapshot2.getKey());
                    subCategoys.add(modal);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getCategorys()
    {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
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
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<SubCategoy> data;
        Context context;
        Activity activity;
        String TAG;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            private TextView categoryName;
            public MyViewHolder(View view) {
                super(view);
                categoryName = view.findViewById(R.id.categoryName);
            }
        }
        public MyAdapter(Context c, Activity a , ArrayList<SubCategoy> CompanyJobModal){
            this.data =CompanyJobModal;
            context=c;
            activity=a;
            TAG="***Adapter";
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sub_category_adapter, parent, false);
            return new MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder viewHolder, final int position) {

        }
        @Override
        public int getItemCount() {
//        return  5;
            return data.size();
        }

        public void setFilter(ArrayList<SubCategoy> newList){
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