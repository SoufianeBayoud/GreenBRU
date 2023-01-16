package com.example.greenbru;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class descfrag extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String title, description, imageURL;
    Button delete, go_back;
    DatabaseReference root;
    public descfrag() {

    }
    public descfrag(String title, String description, String imageURL) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
    }


    public static descfrag newInstance(String param1, String param2) {
        descfrag fragment = new descfrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_descfrag, container, false);
        ImageView imageholder = (ImageView) view.findViewById(R.id.imageholder);
        TextView titleholder = view.findViewById(R.id.nameholder);
        TextView descriptionholder = view.findViewById(R.id.courseholder);
        delete = view.findViewById(R.id.delete_btn_fragment);
        go_back = view.findViewById(R.id.go_back_descfrag);

        titleholder.setText(title);
        descriptionholder.setText(description);
        Glide.with(getContext()).load(imageURL).into(imageholder);
        String url = "https://greenbru-5e1b0-default-rtdb.europe-west1.firebasedatabase.app/";
        String titleInDB = titleholder.getText().toString();
        root = FirebaseDatabase.getInstance(url).getReference("Signals").child(titleInDB);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Item collected", Toast.LENGTH_SHORT).show();
                root.removeValue();
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);

            }
        });

        //We call the recfragment back
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity)getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper, new recfragment())
                        .addToBackStack(null)
                        .commit();
            }
        });



        return view;
    }



}