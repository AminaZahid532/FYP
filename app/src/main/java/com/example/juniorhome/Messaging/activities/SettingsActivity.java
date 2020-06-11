package com.example.juniorhome.Messaging.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.juniorhome.R;
import com.example.juniorhome.SessionManager;
import com.example.juniorhome.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private String currentUserID;

    private SessionManager session;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;

    private EditText userName;
    private Toolbar SettingsToolBar;
    private ProgressDialog loadingBar;
    private Button UpdateAccountSettings;
    private CircleImageView userProfileImage;

    private static final int GalleryPick = 1;

    public String docId;
    public String userPass;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManager(this);
        if (getIntent() != null && getIntent().hasExtra("visit_user_id")) {
            currentUserID = getIntent().getStringExtra("visit_user_id");
        }
        else {
            currentUserID = session.getUserId();
        }

        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();
        RetrieveUserInfo();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, GalleryPick);

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        if(!currentUserID.equals(session.getUserId())){
            UpdateAccountSettings.setVisibility(View.GONE);
            findViewById(R.id.btn_change_password).setVisibility(View.GONE);
        }
    }

    private void InitializeFields() {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);

        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
//        loadingBar.setTitle("Set Profile Image");
        loadingBar.setMessage("Please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }

    public void changePassword(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setTitle("Change Password");
        final EditText oldPass = new EditText(SettingsActivity.this);
        final EditText newPass = new EditText(SettingsActivity.this);
        final EditText confirmPass = new EditText(SettingsActivity.this);

        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll = new LinearLayout(SettingsActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);

        ll.addView(oldPass);

        ll.addView(newPass);
        ll.addView(confirmPass);
        builder.setView(ll);
        builder.setPositiveButton("Change",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final String old = oldPass.getText().toString();
                        final String newPassword = newPass.getText().toString();
                        final String confirm = confirmPass.getText().toString();

                        session = new SessionManager(getApplicationContext());
                        String uid = session.getUserId();

                        db.collection("users").whereEqualTo("uid", uid).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().isEmpty()) {
                                                Toast.makeText(SettingsActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();
                                            }
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                userPass = document.getString("password");
                                                docId = document.getId();
                                            }
                                        }
                                        else {
                                            Toast.makeText(SettingsActivity.this, "Error getting documents." + task.getException(), Toast.LENGTH_SHORT).show();
                                        }

                                        if (userPass.equals(old)) {
                                            if (newPassword.equals(confirm)) {
                                                db.collection("users").document(docId)
                                                        .update("password", newPassword);
                                                Toast.makeText(SettingsActivity.this, "Password Changed Successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(SettingsActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(SettingsActivity.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please Enter your UserName Firstly", Toast.LENGTH_SHORT).show();
        }
        else if (userDocumentKey.equals("")) {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
        }
        else {
            userModel.setUname(userName.getText().toString().trim());
            if (selectedFileUri != null) {
                uploadProfilePicture();
            }
            else {
                updateUserInfo();
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MessagingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private UserModel userModel;
    private String userDocumentKey = "";

    private void RetrieveUserInfo() {
        db.collection("users").whereEqualTo("uid", currentUserID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                userDocumentKey = queryDocumentSnapshot.getId();
                                userModel = queryDocumentSnapshot.toObject(UserModel.class);
                                String retrieveUserName = userModel.getUname();
                                String retrieveProfileImage = userModel.getProfilePicUrl();

                                userName.setText(retrieveUserName);
                                if (userModel.getProfilePicUrl().equals("")) {
                                    Toast.makeText(SettingsActivity.this, "No Profile Picture Found!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                                }
                            }
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        RootRef.child("Users").child(currentUserID)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))) {
//                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
//                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
//
//                            userName.setText(retrieveUserName);
//                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
//                        }
//                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
//                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
//                            userName.setText(retrieveUserName);
//                        }
//                        else {
//                            Toast.makeText(SettingsActivity.this, "Please Set & Update Your Profile", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    private void uploadProfilePicture() {
        loadingBar.show();
        final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");
        if (selectedFileUri != null) {
            filePath.putFile(selectedFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loadingBar.dismiss();
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            userModel.setProfilePicUrl(downloadUrl);
                            updateUserInfo();
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(this, "Invalid Image File!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo() {
        loadingBar.show();
        db.collection("users").document(userDocumentKey).set(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Update Successfully!", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Uri selectedFileUri = null;

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
//            Uri ImageUri = data.getData();
//
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .start(this);
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                selectedFileUri = result.getUri();
                Picasso.get().load(selectedFileUri).into(userProfileImage);
            }
        }
    }
}
