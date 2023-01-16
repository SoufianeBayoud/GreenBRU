package com.example.greenbru;



import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.SQLOutput;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    TextView profileLastname, profileFirstname, profileEmail, profileUsername, profilePassword;
    TextView user_textView;

    Button editProfile;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileUsername = view.findViewById(R.id.profileUsername);
        profileFirstname = view.findViewById(R.id.profileName);
        profileLastname = view.findViewById(R.id.lastprofileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePassword = view.findViewById(R.id.profilePassword);
        editProfile = view.findViewById(R.id.editButton_profile);

        Bundle intent = getArguments();
        String usernameUser = intent.getString("name");
        String emailUser = intent.getString("email");
        String firstnameUser = intent.getString("first_name");
        String lastnameUser = intent.getString("last_name");
        String passwordUser = intent.getString("password");
        String imgUser = intent.getString("imgURL");

        profileUsername.setText(usernameUser);
        profileFirstname.setText(firstnameUser);
        profileLastname.setText(lastnameUser);
        profileEmail.setText(emailUser);
        profilePassword.setText(passwordUser);

        //another method to pass the data; with global variable
        EditProfileActivity.nameUser = usernameUser;
        EditProfileActivity.emailUser = emailUser;
        EditProfileActivity.firstnameUser = firstnameUser;
        EditProfileActivity.lastnameUser = lastnameUser;
        EditProfileActivity.passwordUser = passwordUser;
        EditProfileActivity.imgURL = intent.getString("imageURL");

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}