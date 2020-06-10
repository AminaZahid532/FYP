package com.example.juniorhome.StaffListView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.example.juniorhome.StaffListView.StaffItemClass;
import com.example.juniorhome.UserItemClass;
import com.example.juniorhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddStaffActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private EditText nameEditText,expEditText,emailEditText,phoneEditText;
    private EditText addressEditText,cnicEditText,qualificationEditText;
    private ImageView chosenImageView;
    private ProgressBar uploadProgressBar;
    private int userCount=0,staffCount=0;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private String name = "", id="-1";
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_upload );

        Button chooseImageBtn;
        Button uploadBtn;

        chooseImageBtn = findViewById(R.id.button_choose_image);
        uploadBtn = findViewById(R.id.uploadBtn);
        nameEditText = findViewById(R.id.nameEditText);
        expEditText = findViewById ( R.id.expEditText );
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        qualificationEditText = findViewById(R.id.qualificationEditText);
        cnicEditText = findViewById(R.id.cnicEditText);
        chosenImageView = findViewById(R.id.chosenImageView);
        uploadProgressBar = findViewById(R.id.progress_bar);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddStaffActivity.this, "An Upload is Still in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    generateStaffId();
                }
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            chosenImageView.setImageURI(mImageUri);
            //chosenImageView.bringToFront();
            //Glide.with(this).load(mImageUri).into(chosenImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void generateStaffId(){

        name = nameEditText.getText().toString().split(" ")[0].toLowerCase();

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        userCount++;
                        if( document.getString("role").equals("staff"))
                            staffCount++;
                    }
                } else {
                    userCount = -1;
                    Log.d( "AddStaff", "Error getting documents: ", task.getException());
                }

                if(userCount > 0)
                {
                    userCount = 1000 + userCount;
                    id = name + userCount;
                }
                else
                    id = "-1";

                //Toast.makeText(AddStaffActivity.this,id,Toast.LENGTH_LONG).show();
                uploadFile(id);
            }
        });
    }

    private void uploadFile(final String id) {
        if (mImageUri != null) {
            if(!id.equals( "-1")) {
            final String imgPath = id + "." + getFileExtension(mImageUri);
            StorageReference storageRef = storage.getReference();
            final StorageReference pathReference = storageRef.child("staff/images/"+ imgPath);

            uploadProgressBar.setVisibility(View.VISIBLE);
            uploadProgressBar.setIndeterminate(true);

            mUploadTask = pathReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadProgressBar.setVisibility(View.VISIBLE);
                                    uploadProgressBar.setIndeterminate(false);
                                    uploadProgressBar.setProgress(0);
                                }
                            }, 500);

                            String sname = nameEditText.getText().toString().trim();
                            UserItemClass user = new UserItemClass(sname,id,"staff",phoneEditText.getText().toString().trim(),
                                    "abc",emailEditText.getText().toString().trim(),cnicEditText.getText().toString().trim());

                            StaffItemClass staff = new StaffItemClass(sname,staffCount+1,id,getDateToday(),imgPath,expEditText.getText().toString().trim(),
                                    qualificationEditText.getText().toString().trim(),addressEditText.getText().toString().trim(),0);

                            db.collection("users").document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Add User", "Error writing document", e);
                                        }
                                    });

                            db.collection("Staff").document().set(staff).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    uploadProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AddStaffActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Adding Staff", "Error writing document", e);
                                        }
                                    });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener () {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddStaffActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            uploadProgressBar.setProgress((int) progress);
                        }
                    });
            }
        } else {
            Toast.makeText(this, "You haven't Selected Any file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date=new Date();
        return dateFormat.format(date);
    }
}