package com.example.juniorhome.StaffListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.example.juniorhome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    TextView nameDetailTextView,qualificationTextView,dateDetailTextView,experienceTextView,assigned;
    ImageView teacherDetailImageView;

    private void initializeWidgets(){
        nameDetailTextView= findViewById(R.id.nameDetailTextView);
        qualificationTextView= findViewById(R.id.qualificationTextView);
        dateDetailTextView= findViewById(R.id.dateDetailTextView);
        experienceTextView= findViewById(R.id.experienceTextView);
        assigned = findViewById(R.id.assign);
        teacherDetailImageView=findViewById(R.id.teacherDetailImageView);
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date=new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewstaff_detail);

        initializeWidgets();

        //RECEIVE DATA FROM ITEMSACTIVITY VIA INTENT
        Intent i=this.getIntent();
        String name=i.getExtras().getString("NAME_KEY");
        String experience="Experience: " + i.getExtras().getString("EXPERIENCE_KEY");
        String imageURL=i.getExtras().getString("IMAGE_KEY");
        String qualification = "Qualification: " + i.getExtras().getString("QUALIFICATION_KEY");
        String assign = "Assigned Children: " +  i.getExtras().getString("ASSIGNED_KEY");
        String Date = "Date Added: " + i.getExtras().getString("DATE_KEY");

        //SET RECEIVED DATA TO TEXTVIEWS AND IMAGEVIEWS
        nameDetailTextView.setText(name);
        experienceTextView.setText(experience);
        qualificationTextView.setText(qualification);
        dateDetailTextView.setText(Date);
        assigned.setText(assign);

        final FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("staff/images/" + imageURL);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download directly from StorageReference using Glide
                Glide.with(DetailsActivity.this)
                        .load(uri)
                        .into(teacherDetailImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DetailsActivity.this,"Can not load Image", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
