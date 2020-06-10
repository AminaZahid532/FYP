package com.example.juniorhome.ParentListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.juniorhome.AdmissionRequest.ParentItemClass;
import com.example.juniorhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PDetailsActivity extends AppCompatActivity {

    TextView nameDetailTextView,emailTextView,dateDetailTextView,cNameView;
    ImageView childDetailImageView;
    Button editP;
    ParentItemClass parent;
    String cname,img;

    private void initializeWidgets(){
        nameDetailTextView= findViewById(R.id.parentNameDetailView);
        emailTextView= findViewById(R.id.emailTextView2);
        dateDetailTextView= findViewById(R.id.dateAddedViewP);
        cNameView= findViewById(R.id.childName2);
        childDetailImageView=findViewById(R.id.childDetailImageView2);
        editP = findViewById(R.id.editParentInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewparent_detail);

        initializeWidgets();

        Intent i=this.getIntent();

        parent = (ParentItemClass)i.getSerializableExtra("PARENT_DATA");

        assert parent != null;
        //String age="Age: " + getAge(child.getBday());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Children").whereEqualTo("parentUid",parent.getUsrId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cname = document.getString("name");
                                img = document.getString("img");
                                Toast.makeText(PDetailsActivity.this,"Name: "+cname,Toast.LENGTH_SHORT).show();
                            }
                            String email ="Email: "+parent.getEmail();
                            String cName = "Child: " + cname;
                            String dateAdded = "Date Added: "+ parent.getDateAdded();
                            nameDetailTextView.setText(parent.getName());
                            cNameView.setText(cName);
                            emailTextView.setText(email);
                            dateDetailTextView.setText(dateAdded);
                            //experienceTextView.setText(cat);
                            FirebaseStorage storage = FirebaseStorage.getInstance();

                            StorageReference storageRef = storage.getReference();
                            StorageReference pathReference = storageRef.child("images/" + img);

                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Download directly from StorageReference using Glide
                                    Glide.with(PDetailsActivity.this)
                                            .load(uri)
                                            .into(childDetailImageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(PDetailsActivity.this,"Can not load Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
        editP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
