package com.example.greenbru;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    EditText username, first_name, last_name, password, email;
    Button register, login;
    AppCompatButton updateButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        username = findViewById(R.id.username_register_main);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        password = findViewById(R.id.password_register_main);
        email = findViewById(R.id.email);

        //2buttons
        register = findViewById(R.id.register);
        login = findViewById(R.id.signin);
        updateButton = findViewById(R.id.add_picture_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ici on mets tout dans le Realtime DB
                String url = "https://greenbru-5e1b0-default-rtdb.europe-west1.firebasedatabase.app/";
                database = FirebaseDatabase.getInstance(url);
                reference = database.getReference("Users");

                String name = username.getText().toString();
                String firstName = first_name.getText().toString();
                String lastName = last_name.getText().toString();
                String pass = password.getText().toString();
                String email_adress = email.getText().toString();
                String pass_uri = imageUri.toString();
                System.out.println(imageUri);

                storageReference = FirebaseStorage.getInstance().getReference();
               /* HelperClass helperClass = new HelperClass(name, firstName, lastName, pass, email_adress);
                reference.child(name).setValue(helperClass);*/
                if( name != null && firstName != null && lastName != null && pass != null && email_adress != null && imageUri != null) {
                    uploadToFirebase(imageUri);
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else {
                     Toast.makeText(RegisterActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                 }

                 //On choppe le name de l'image pour le mettre dans le realtime DB

                        //storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri)).getDownloadUrl().toString();
                //On va l'envoyer dans le settings et le navigation drawer layout





            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 2);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();


        }
    }

    private void uploadToFirebase(Uri uri){
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //On le mets dans realtime DB
                        reference = database.getReference("Users");
                        String name = username.getText().toString();
                        String firstName = first_name.getText().toString();
                        String lastName = last_name.getText().toString();
                        String pass = password.getText().toString();
                        String email_adress = email.getText().toString();
                        String imageURL = uri.toString();

                        HelperClass helperClass = new HelperClass(name, firstName, lastName, pass, email_adress, uri.toString());
                        reference.child(name).setValue(helperClass);

                        Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}
