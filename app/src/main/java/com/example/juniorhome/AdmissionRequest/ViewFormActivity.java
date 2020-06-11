package com.example.juniorhome.AdmissionRequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.example.juniorhome.R;
import com.example.juniorhome.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class ViewFormActivity extends AppCompatActivity {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private int userCount=0,childCount=0,parentCount=0;

    TextView nameView2,ageTextView2,dateDetailTextView2,genderTextView;
    ImageView formDetailImageView;
    Button addChild;
    private StorageReference mStorageRef;
    char related = 'N';
    int cid=-1,pid=-1;
    private String pname = "", userId="-1",mcnic,fcnic;
    private StorageTask mUploadTask;
    private ProgressBar uploadProgressBar2;
    AdmissionFormItemClass form;

    private void initializeWidgets(){
        nameView2= findViewById(R.id.nameDetailTextView2);
        ageTextView2= findViewById(R.id.ageTextView2);
        dateDetailTextView2= findViewById(R.id.dateDetailTextView2);
        genderTextView= findViewById(R.id.genderTextView);
        formDetailImageView=findViewById(R.id.formDetailImageView);
        addChild = findViewById(R.id.addChild);
        uploadProgressBar2 = findViewById(R.id.progress_bar2);
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date=new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewform_detail);

        initializeWidgets();
        uploadProgressBar2.setVisibility(View.INVISIBLE);
        //RECEIVE DATA VIA INTENT
        Intent i=this.getIntent();

        form = (AdmissionFormItemClass)i.getSerializableExtra("FORM_DATA");

        assert form != null;
        String age="Age: " + String.valueOf(form.getAge());
        nameView2.setText(form.getName());
        genderTextView.setText(form.getGender());
        ageTextView2.setText(age);
        dateDetailTextView2.setText(form.getDateSubmit());
        //experienceTextView.setText(cat);

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("images/" + form.getImg());

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download directly from StorageReference using Glide
                Glide.with(ViewFormActivity.this)
                        .load(uri)
                        .into(formDetailImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewFormActivity.this,"Can not load Image", Toast.LENGTH_SHORT).show();
            }
        });

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ViewFormActivity.this, "An Upload is Still in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    generateId();
                }
            }
        });
    }

    int minStaff=5;
    int staffID=0;
    private void generateId() {

        final String mcnic = form.getMcnic();
        final String fcnic = form.getFcnic();
        final String parentName = form.getMname().split(" ")[0].toLowerCase();

        String name = form.getName();

        db.collection("Staff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        int assigned = document.getLong("assignedNum").intValue();
                        if(assigned < minStaff)
                        {
                            minStaff = assigned;
                            staffID = document.getLong("sid").intValue();
                            Toast.makeText(ViewFormActivity.this,String.valueOf(staffID),Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(staffID==0)
                    {
                        showNoSpaceAlert();
                    }

                    else
                        {
                            db.collection("Children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            childCount++;
                                        }
                                    }
                                    cid = childCount+1;

                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    userCount++;
                                                    String userCnic = document.getString("cnic");
                                                    String role = document.getString("role");
                                                    if( role.equals("parent"))
                                                        parentCount++;

                                                    if(userCnic.equals(mcnic) || userCnic.equals(fcnic))
                                                    {
                                                        if (role.equals("parent"))
                                                            userId = document.getString("uid");
                                                        related = 'P'; //Parent is already a user
                                                    }
                                                }
                                            } else {
                                                userCount = -1;
                                                Log.d( "AddParent", "Error getting documents: ", task.getException());
                                            }

                                            if(related == 'P'){
                                                addChildOnly(userId,cid,staffID);
                                            }

                                            else{
                                                if(userCount > 0)
                                                {
                                                    userCount = 1000 + userCount;
                                                    userId = parentName + userCount;
                                                    pid = parentCount+1;
                                                }
                                                else
                                                    pid = -1;

                                                //Toast.makeText(AddStaffActivity.this,id,Toast.LENGTH_LONG).show();
                                                addData(userId,pid,cid,staffID);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                }
            }
        });
    }

    private void addData(final String userId, final int pid, final int cid,final int sid) {

        if(!(pid==-1) && !(cid==-1)){
            final String cName = form.getName();

            uploadProgressBar2.setVisibility(View.VISIBLE);
            uploadProgressBar2.setIndeterminate(true);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadProgressBar2.setVisibility(View.VISIBLE);
                    uploadProgressBar2.setIndeterminate(false);
                    uploadProgressBar2.setProgress(0);
                }}, 500);
            String cname = form.getName();
            ChildItemClass child = new ChildItemClass(cid,userId,form.getName(),form.getGender(),sid,form.getAddress(),form.getBday(),form.getFname(),form.getFphno(),form.getFoccp(),form.getFqual(),form.getFcnic(),form.getMname(),form.getMphno(),form.getMoccp(),form.getMqual(),form.getMcnic(),form.getInfo(),form.getImg(),form.getPay(),form.getProg(),form.getTelno(),form.getEmail(),form.getDname(),form.getDphno(),form.getGname(),form.getGphno(),getDateToday());

            String parentName,cnic,occp,qual,phno;
            String parentStatus="M";

            //if mother's information is provided, it is preferred to add mother as a user
            if(!(form.getMname().equals(""))) {
                parentName =form.getMname();
                phno = form.getFphno();
                cnic = form.getFcnic();
                qual = form.getFqual();
                occp = form.getFoccp();
            }

            else if(!(form.getFname().equals(""))){
                parentStatus = "F";
                parentName = form.getFname();
                phno = form.getFphno();
                cnic = form.getFcnic();
                qual = form.getFqual();
                occp = form.getFoccp();
            }

            else{
                parentStatus="G";
                parentName = form.getGname();
                phno = form.getGphno();
                occp="";
                qual="";
                cnic=form.getFcnic();
            }

            ParentItemClass parent = new ParentItemClass(pid,userId,parentName,parentStatus,form.getAddress(),phno,occp,qual,cnic,form.getTelno(),form.getEmail(),getDateToday());

            db.collection("Children").document().set(child).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ViewFormActivity.this,"Child is added",Toast.LENGTH_SHORT).show();
                    Log.d("Add Child", "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Add Child", "Error writing document", e);
                }
            });
            UserModel user = new UserModel(parentName,userId,"parent",phno,"abc",form.getEmail(),cnic);

            db.collection("users").document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Adding User", "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Adding User", "Error writing document", e);
                        }
                    });
            db.collection("Parents").document().set(parent).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Adding Parent", "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Adding Parent", "Error writing document", e);
                        }
                    });

            //Delete form record from database
            db.collection("Form").whereEqualTo("img",form.getImg()).
                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                                Toast.makeText(ViewFormActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(ViewFormActivity.this,"Error deleting form",Toast.LENGTH_SHORT).show();
                            }
                            uploadProgressBar2.setVisibility(View.INVISIBLE);
                            showAlert();
                        }
                    }).addOnFailureListener(new OnFailureListener () {@Override
                        public void onFailure(@NonNull Exception e) {
                            uploadProgressBar2.setVisibility(View.INVISIBLE);
                            Toast.makeText(ViewFormActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addChildOnly(final String parentUID, final int cid, final int sid){

        if(!(parentUID.equals("-1")) && !(cid==-1)){
            final String cName = form.getName();

            uploadProgressBar2.setVisibility(View.VISIBLE);
            uploadProgressBar2.setIndeterminate(true);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadProgressBar2.setVisibility(View.VISIBLE);
                    uploadProgressBar2.setIndeterminate(false);
                    uploadProgressBar2.setProgress(0);
                }
                }, 500);

            Toast.makeText(ViewFormActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

            String cname = form.getName();
            ChildItemClass child = new ChildItemClass(cid,parentUID,form.getName(),form.getGender(),sid,form.getAddress(),form.getBday(),form.getFname(),form.getFphno(),form.getFoccp(),form.getFqual(),form.getFcnic(),form.getMname(),form.getMphno(),form.getMoccp(),form.getMqual(),form.getMcnic(),form.getInfo(),form.getImg(),form.getPay(),form.getProg(),form.getTelno(),form.getEmail(),form.getDname(),form.getDphno(),form.getGname(),form.getGphno(),getDateToday());

            db.collection("Children").document().set(child).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Add Child", "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Add Child", "Error writing document", e);
                        }
                    });
            uploadProgressBar2.setVisibility(View.INVISIBLE);

            //Delete form record from database
            db.collection("Form").whereEqualTo("img",form.getImg()).
                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            }
                            else {
                                Toast.makeText(ViewFormActivity.this,"Error deleting form",Toast.LENGTH_SHORT).show();
                            }
                            uploadProgressBar2.setVisibility(View.INVISIBLE);
                            showAlert();
                        }
                    });
            }
    }

    private void showAlert()
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //Setting message manually and performing action on button click
        builder.setMessage("Record Added Successfully!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(ViewFormActivity.this,ViewRequestsActivity.class);
                        startActivity(i);
                        //finish();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Task Successful");
        alert.show();
    }

    private void showNoSpaceAlert()
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //Setting message manually and performing action on button click
        builder.setMessage("Each Caretaker is already assigned 5 child. No space left!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(ViewFormActivity.this,ViewRequestsActivity.class);
                        startActivity(i);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Add Task Failed!");
        alert.show();
    }
}
