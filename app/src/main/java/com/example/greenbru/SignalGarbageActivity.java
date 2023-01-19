package com.example.greenbru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignalGarbageActivity extends AppCompatActivity {
    EditText title, description;
    AppCompatButton takePicture, selectPicture;
    Button signal_garbage_btn, go_back_btn;
    ProgressBar progressBar;
    DatabaseReference root;
    StorageReference reference;
    Uri imageUri;
    static Bundle saved;

    private static final int CAMERA_REQUEST_CODE = 1;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_garbage);
        title = findViewById(R.id.title_signal_garbage);
        description = findViewById(R.id.description_signal_garbage);
        takePicture = findViewById(R.id.add_picture_signal_garbage);
        selectPicture = findViewById(R.id.select_picture_register);
        signal_garbage_btn = findViewById(R.id.signal_garbage_btn);
        go_back_btn = findViewById(R.id.go_back_to_map_signal_garbage_btn);
        progressBar = findViewById(R.id.progress_bar_signal_garbage);
        progressBar.setVisibility(View.INVISIBLE);
        String url = "https://greenbru-5e1b0-default-rtdb.europe-west1.firebasedatabase.app/";
        root = FirebaseDatabase.getInstance(url).getReference("Signals");
        reference = FirebaseStorage.getInstance().getReference();

       if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("latitude")){
                double lat = getIntent().getDoubleExtra("latitude", 0.0);
                double lon = getIntent().getDoubleExtra("longitude", 0.0);
                saved = new Bundle();
                saved.putDouble("lat", lat);
                saved.putDouble("lon", lon);
            }
        }

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestCameraPermission();

            }
        });

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        signal_garbage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_signal = title.getText().toString();
                String description_signal = description.getText().toString();

                if(imageUri != null && title_signal != null && description_signal != null){
                    uploadToFirebase(imageUri);
                    finish();
                }else {
                    Toast.makeText(SignalGarbageActivity.this, "Please fill in the information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        go_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignalGarbageActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            // Permission has already been granted
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                finish();
            } else {
                // Permission has been denied
                Toast.makeText(this, "CAMERA permission has been denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //We use the if else if because we use requestcode in 2 different places
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
        } else if(requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //photo taken successfully
                Log.d("photo is taken", "photo is taken");
            }}
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String title_signal = title.getText().toString();
                        String description_signal = description.getText().toString();
                        latitude = saved.getDouble("lat");
                        longitude = saved.getDouble("lon");
                        progressBar.setVisibility(View.INVISIBLE);
                        AlertsHelperClass alertsHelperClass = new AlertsHelperClass(title_signal, description_signal, uri.toString(), latitude, longitude);
                        root.child(title_signal).setValue(alertsHelperClass);
                        Toast.makeText(SignalGarbageActivity.this, "Upload succes!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(SignalGarbageActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}

