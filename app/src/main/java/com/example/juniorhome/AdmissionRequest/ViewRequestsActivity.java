package com.example.juniorhome.AdmissionRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.juniorhome.R;

public class ViewRequestsActivity extends AppCompatActivity implements AdmissionRecyclerAdapter.OnItemClickListener{

        private List<AdmissionFormItemClass> requests;

        private void openDetailActivity(AdmissionFormItemClass data){
            Intent intent = new Intent(this, ViewFormActivity.class);
            intent.putExtra("FORM_DATA",data);
            startActivity(intent);
        }

    public RecyclerView mRecyclerView;
    public AdmissionRecyclerAdapter mAdapter;
    public ProgressBar mProgressBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_view_requests );

            mRecyclerView = findViewById(R.id.mRecyclerView2);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressBar = findViewById(R.id.myDataLoaderProgressBar2);
            mProgressBar.setVisibility(View.VISIBLE);

            requests = new ArrayList<> ();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Form").orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(ViewRequestsActivity.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                }
                            for (QueryDocumentSnapshot document : (task.getResult())) {
                                    String imgPath = document.getString("img");
                                    String name = document.getString("name");
                                    String dob = document.getString("bday");
                                    String gender = document.getString("gender");
                                    Date dateSubmit = document.getDate("timestamp");
                                    String address = document.getString("address");
                                    String bday =document.getString("bday");
                                    String fname=document.getString("fname");
                                    String fphno=document.getString("fphno");
                                    String foccp=document.getString("foccp");
                                    String fqual=document.getString("fqual");
                                    String fcnic=document.getString("fcnic");
                                    String mname=document.getString("mname");
                                    String mphno=document.getString("mphno");
                                    String moccp=document.getString("moccp");
                                    String mqual=document.getString("mqual");
                                    String mcnic=document.getString("mcnic");
                                    String info=document.getString("infor");
                                    String pay=document.getString("pay");
                                    String prog=document.getString("prog");
                                    String telno=document.getString("telno");
                                    String email=document.getString("email");
                                    String dname=document.getString("dname");
                                    String dphno=document.getString("dphno");
                                    String gname=document.getString("gname");
                                    String gphno=document.getString("gphno");

                                    int age = getAge(dob);
                                    //Toast.makeText(ViewRequestsActivity.this, String.valueOf(age),Toast.LENGTH_LONG).show();
                                    String date = dateSubmit.toString();

                                    AdmissionFormItemClass form = new AdmissionFormItemClass(name,age,gender,address,bday,fname,fphno,
                                            foccp,fqual,fcnic,mname,mphno,moccp,mqual,mcnic,info,imgPath,pay,prog,telno,email,dname
                                            ,dphno,gname,gphno,date);
                                    //AdmissionFormItemClass form = new AdmissionFormItemClass(name,imgPath,age,date);
                                    requests.add(form);
                                }
                        }
                        mAdapter = new AdmissionRecyclerAdapter (ViewRequestsActivity.this, requests);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(ViewRequestsActivity.this);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        }

        public void onItemClick(int position) {
            AdmissionFormItemClass clickedForm=requests.get(position);
            //String[] formData={clickedForm.getName(),clickedForm.getImg(),String.valueOf(clickedForm.getAge()),clickedForm.getDateSubmit()};
            openDetailActivity(clickedForm);
        }

        @Override
        public void onShowItemClick(int position) {
            AdmissionFormItemClass clickedForm=requests.get(position);
            //String[] formData={clickedForm.getName(),clickedForm.getImg(),String.valueOf(clickedForm.getAge()),clickedForm.getDateSubmit()};
            openDetailActivity(clickedForm);
        }

        @Override
        public void onDeleteItemClick(int position) {
            /*StaffItemClass selectedItem = mTeachers.get(position);
            final String selectedKey = selectedItem.getKey();

            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void> () {
                @Override
                public void onSuccess(Void aVoid) {
                    //final FirebaseStorage mStorage = FirebaseStorage.getInstance();
                    //FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //mDatabaseRef.child(selectedKey).removeValue();
                    Toast.makeText(ItemsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });*/
        }
        protected void onDestroy() {
            super.onDestroy();
        }

    private int getAge(String dob){
        String[] date = dob.split("-");
        int age=-1;
        LocalDate today = null;                          //Today's date
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();

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