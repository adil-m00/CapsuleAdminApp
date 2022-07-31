package com.muhsin.capsuleadminapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.muhsin.capsuleadminapp.R;


public class UpdateSettings extends Fragment {


    private EditText password,Confim;
    private Button updateProfile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public UpdateSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        password = view.findViewById(R.id.password);
        Confim = view.findViewById(R.id.Confim);
        updateProfile = view.findViewById(R.id.updateProfile);
    }
}