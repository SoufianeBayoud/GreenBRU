package com.example.greenbru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editFirst_name, editLast_name, editPassword, editEmail;
    Button updateBtn, goBacktoMapBtn;
    AppCompatButton imgProfile;
   // String nameUser, emailUser, firstnameUser, lastnameUser, passwordUser;
    DatabaseReference reference;
    public static String nameUser, emailUser, firstnameUser, lastnameUser, passwordUser, imgURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        editName = findViewById(R.id.username_update);
        editFirst_name = findViewById(R.id.first_name_update);
        editLast_name = findViewById(R.id.last_name_update);
        editPassword = findViewById(R.id.password_update);
        editEmail = findViewById(R.id.email_update);
        updateBtn = findViewById(R.id.update_btn_editprofile);
        goBacktoMapBtn = findViewById(R.id.go_back_to_map);
        imgProfile = findViewById(R.id.add_picture_edit);

        nameUser = EditProfileActivity.nameUser;
        emailUser = EditProfileActivity.emailUser;
        firstnameUser = EditProfileActivity.firstnameUser;
        lastnameUser = EditProfileActivity.lastnameUser;
        passwordUser = EditProfileActivity.passwordUser;
        imgURL = EditProfileActivity.imgURL;

        System.out.println(nameUser);
        System.out.println(emailUser);
        System.out.println(firstnameUser);
        System.out.println(lastnameUser);
        System.out.println(passwordUser);
        System.out.println(imgURL);




        reference = FirebaseDatabase.getInstance().getReference("Users");
        showUserData();
        //passUserData();
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MapActivity.class);

                isEmailChanged();
                isFirstNameChanged();
                isLastNameChanged();
                isPasswordChanged();
                isPictureChanged();
                isNameChanged();

                startActivity(intent);
            }
        });

        goBacktoMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }


    public void showUserData(){

        String userUsername = EditProfileActivity.nameUser;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String firstNameFromDB = snapshot.child(userUsername).child("first_name").getValue(String.class);
                    String lastNameFromDB = snapshot.child(userUsername).child("last_name").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    String imageURLFromDB = snapshot.child(userUsername).child("imageURL").getValue(String.class);
                    System.out.println(imageURLFromDB);
                    System.out.println(nameFromDB);
                    editName.setText(nameFromDB);
                    editEmail.setText(emailFromDB);
                    editPassword.setText(passwordFromDB);
                    editFirst_name.setText(firstNameFromDB);
                    editLast_name.setText(lastNameFromDB);
                   // imgProfile.setText(imageURLFromDB);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

   /* public void passUserData(){
        String userUsername = EditProfileActivity.data;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userUsername);
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String firstNameFromDB = snapshot.child(userUsername).child("first_name").getValue(String.class);
                    String lastNameFromDB = snapshot.child(userUsername).child("last_name").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    editName.setText(nameFromDB);
                    editEmail.setText(emailFromDB);
                    editPassword.setText(passwordFromDB);
                    editFirst_name.setText(firstNameFromDB);
                    editLast_name.setText(lastNameFromDB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });    }*/


    public boolean isNameChanged(){

        if(!nameUser.equals(editName.getText().toString())){
            reference.child(nameUser).child("username").setValue(editName.getText().toString());
            nameUser = editName.getText().toString();
            return true;
        } else {
            return false;
        }

    }
    public boolean isEmailChanged(){
        if(!emailUser.equals(editEmail.getText().toString())){
            reference.child(nameUser).child("email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString();
            return true;
        } else {
            return false;
        }
    }
    public boolean isPasswordChanged(){
        if(!passwordUser.equals(editPassword.getText().toString())){
            reference.child(nameUser).child("password").setValue(editPassword.getText().toString());
            passwordUser = editPassword.getText().toString();
            return true;
        } else {
            return false;
        }
    }
    public boolean isFirstNameChanged(){
        if(!firstnameUser.equals(editFirst_name.getText().toString())){
            reference.child(nameUser).child("first_name").setValue(editFirst_name.getText().toString());
            firstnameUser = editFirst_name.getText().toString();
            return true;
        } else {
            return false;
        }
    }
    public boolean isLastNameChanged(){
        if(!lastnameUser.equals(editLast_name.getText().toString())){
            reference.child(nameUser).child("last_name").setValue(editLast_name.getText().toString());
            lastnameUser = editLast_name.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPictureChanged(){
        System.out.println(imgProfile.getText().toString() + "KFJSSKFJLKDDSF");
        System.out.println(imgURL + " SDJDSJSDFJSDQFMSDF");
        if(!imgURL.equals(imgProfile.getText().toString())){
            reference.child(nameUser).child("imgURL").setValue(imgProfile.getText().toString());
            imgURL = imgProfile.getText().toString();
            return true;
        } else {
            return false;
        }
    }

}
