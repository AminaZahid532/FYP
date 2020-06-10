package com.example.juniorhome;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SessionManager session;
    public String userPass;
    public String docId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(getApplicationContext());

        TextView view = findViewById(R.id.name);
        final ImageView img = findViewById(R.id.profileImg);

        String uid = session.getUserId();
        String name = session.getUsername();
        String role = session.getRole();

        view.setText(name);

        if(role.equals("admin")) {
            db.collection("users").whereEqualTo("uid", uid).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        Toast.makeText(UserProfile.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        String imgPath = document.getString("img");
                                        //String email = document.getString("email");

                                        StorageReference storageRef = storage.getReference();
                                        StorageReference pathReference = storageRef.child("staff/images/" + imgPath);

                                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                // Download directly from StorageReference using Glide
                                                Glide.with(UserProfile.this)
                                                        .load(uri)
                                                        .into(img);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(UserProfile.this,"Can not load Image", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        }
        else if(role.equals("staff"))
        {
            db.collection("Staff").whereEqualTo("uid", uid).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        Toast.makeText(UserProfile.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        String imgPath = document.getString("img");
                                        //String exp = document.getString("experience");

                                        StorageReference storageRef = storage.getReference();
                                        StorageReference pathReference = storageRef.child("staff/images/"+imgPath);
                                        Glide.with(UserProfile.this)
                                                .load(pathReference)
                                                .into(img);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    public void changePassword(View view)
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setTitle("Change Password");
        final EditText oldPass = new EditText(UserProfile.this);
        final EditText newPass = new EditText(UserProfile.this);
        final EditText confirmPass = new EditText(UserProfile.this);

        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll=new LinearLayout(UserProfile.this);
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

                        db.collection("users").whereEqualTo("uid",  uid).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().isEmpty()) {
                                                Toast.makeText(UserProfile.this, "Connection Problem", Toast.LENGTH_SHORT).show();
                                            }
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                userPass = document.getString("password");
                                                docId = document.getId();
                                            }
                                        }
                                        else {
                                            Toast.makeText(UserProfile.this,"Error getting documents."+task.getException(), Toast.LENGTH_SHORT).show();
                                        }

                                        if (userPass.equals(old)) {
                                            if(newPassword.equals(confirm))
                                            {
                                                db.collection("users").document(docId)
                                                        .update("password",newPassword);
                                                Toast.makeText(UserProfile.this,"Password Changed Successfully!",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(UserProfile.this,"Password did not match",Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserProfile.this, "Incorrect Password", Toast.LENGTH_LONG).show();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        final AlertDialog.Builder mybuilder;
        View v = findViewById(R.id.action_logout);
        switch (item.getItemId()) {
            case R.id.action_settings :
                return true;

            case R.id.action_logout:
                mybuilder = new AlertDialog.Builder(this);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Uncomment the below code to Set the message and title from the strings.xml file
                        //builder.setMessage("Do you want to Logout?") .setTitle("Logout");

                        //Setting message manually and performing action on button click
                        mybuilder.setMessage("Do you want to Logout?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SessionManager session = new SessionManager(getApplicationContext());
                                        session.clear();
                                        Intent i = new Intent(UserProfile.this,Login.class);
                                        startActivity(i);
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = mybuilder.create();
                        //Setting the title manually
                        alert.setTitle("Logout");
                        alert.show();
                    }
                });

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
