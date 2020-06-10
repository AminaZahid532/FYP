package com.example.juniorhome.ChildrenListView;

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
import com.example.juniorhome.AdmissionRequest.ChildItemClass;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CDetailsActivity extends AppCompatActivity {

    TextView nameDetailTextView,emailTextView,dateDetailTextView,assignedToView2;
    ImageView childDetailImageView;
    Button editChild;
    ChildItemClass child;

    private void initializeWidgets(){
        nameDetailTextView= findViewById(R.id.childNameDetailView);
        emailTextView= findViewById(R.id.emailTextView);
        dateDetailTextView= findViewById(R.id.dateAddedView);
        assignedToView2= findViewById(R.id.assignedToView2);
        childDetailImageView=findViewById(R.id.childDetailImageView);
        editChild = findViewById(R.id.editChildInfo);
    }

    String sname;
    private String getStaffName(int staffID){
        sname="";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Staff").whereEqualTo("sid",staffID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    sname = document.getString("name");
                                }
                        }
                    }
                });
        Toast.makeText(CDetailsActivity.this,staffID+"Name: "+sname,Toast.LENGTH_SHORT).show();
        return sname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewchild_detail);

        initializeWidgets();

        Intent i=this.getIntent();

        child = (ChildItemClass)i.getSerializableExtra("CHILD_DATA");

        assert child != null;
        //String age="Age: " + getAge(child.getBday());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Staff").whereEqualTo("sid",child.getAssignedTo()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sname = document.getString("sname");
                                Toast.makeText(CDetailsActivity.this,"Name: "+sname,Toast.LENGTH_SHORT).show();
                            }
                            String email ="Email: "+child.getEmail();
                            String staffName = "Assigned To: "+sname;
                            String dateAdded = "Date Added: "+ child.getDateAdded();
                            nameDetailTextView.setText(child.getName());
                            assignedToView2.setText(staffName);
                            emailTextView.setText(email);
                            dateDetailTextView.setText(dateAdded);
                            //experienceTextView.setText(cat);
                        }
                    }
                });
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("images/" + child.getImg());

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download directly from StorageReference using Glide
                Glide.with(CDetailsActivity.this)
                        .load(uri)
                        .into(childDetailImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CDetailsActivity.this,"Can not load Image", Toast.LENGTH_SHORT).show();
            }
        });
        editChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private int getAge(String dob){
        String[] date = dob.split("-");
        int age=-1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now();

            Date d = new Date();
            try {
                d = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(date[1]);
            }
            catch (Exception e)
            {   String error = e.toString();  }
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int month = cal.get(Calendar.MONTH);
            System.out.println(month == Calendar.FEBRUARY);

            LocalDate birthday = LocalDate.of(Integer.parseInt(date[2]),month, Integer.parseInt(date[0]));  //Birth date
            Period p = Period.between(birthday, today);
            age = p.getYears();
        }

        else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar bdate = Calendar.getInstance();
            try {
                bdate.setTime(sdf.parse(dob));
            }
            catch (ParseException Exception ){
                String error = Exception.toString();
            }
            Calendar todayd = Calendar.getInstance();

            int curYear = todayd.get(Calendar.YEAR);
            int dobYear = bdate.get(Calendar.YEAR);

            age = curYear - dobYear;

            // if dob is month or day is behind today's month or day
            // reduce age by 1
            int curMonth = todayd.get(Calendar.MONTH);
            int dobMonth = bdate.get(Calendar.MONTH);
            if (dobMonth > curMonth) { // this year can't be counted!
                age--;
            } else if (dobMonth == curMonth) { // same month? check for day
                int curDay = todayd.get(Calendar.DAY_OF_MONTH);
                int dobDay = bdate.get(Calendar.DAY_OF_MONTH);
                if (dobDay > curDay) { // this year can't be counted!
                    age--;
                }
            }
        }
        return age;
    }

}
