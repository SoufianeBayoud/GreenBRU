package com.example.greenbru;

import static android.content.Intent.getIntent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.Navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login, register;
    ImageView googleBtn;
    String url = "https://greenbru-5e1b0-default-rtdb.europe-west1.firebasedatabase.app/";
    public static String value;
    private static final String SHARED_NAME= "user";
    private static final String USERNAME= "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        login = findViewById(R.id.login_main_btn);
        register = findViewById(R.id.register_login_main);
        username = findViewById(R.id.username_login_main);
        password = findViewById(R.id.password_login_main);
        //googleBtn = findViewById(R.id.google_btn_login_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUsername() | !validatePassword()){
                }else {
                    value = username.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USERNAME,username.getText().toString());
                    editor.apply();
                    checkUser();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }



    public Boolean validateUsername(){
        String value = username.getText().toString();

        if(value.isEmpty()){
            username.setError("Username cannot be empty");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String value = password.getText().toString();
        if(value.isEmpty()){
            password.setError("Username cannot be empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userUsername = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance(url).getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    username.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    //passwordFromDB.equals(userPassword)
                    if(!Objects.equals(passwordFromDB, password)){
                        username.setError(null);

                        //Pass the data using intent
                        String nameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String firstnameFromDB = snapshot.child(userUsername).child("first_name").getValue(String.class);
                        String lastnameFromDB = snapshot.child(userUsername).child("last_name").getValue(String.class);
                        String imageURLFromDB = snapshot.child(userUsername).child("imageURL").getValue(String.class);


                        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("first_name", firstnameFromDB);
                        intent.putExtra("last_name", lastnameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        intent.putExtra("imageURL", imageURLFromDB);
                        startActivity(intent);







                    } else {
                        password.setError("Invalid credentials");
                        password.requestFocus();

                    }
                } else {
                    username.setError("Invalid credentials");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}