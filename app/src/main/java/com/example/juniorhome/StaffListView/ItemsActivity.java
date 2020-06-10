package com.example.juniorhome.StaffListView;

import android.content.Intent;
import android.os.Build;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.juniorhome.R;

public class ItemsActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener{

        private FirebaseStorage mStorage;
        private List<StaffItemClass> mTeachers;

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("NAME_KEY",data[0]);
        intent.putExtra("EXPERIENCE_KEY",data[1]);
        intent.putExtra("IMAGE_KEY",data[2]);
        intent.putExtra("QUALIFICATION_KEY",data[3]);
        intent.putExtra("ADDRESS_KEY",data[4]);
        intent.putExtra("DATE_KEY",data[5]);
        intent.putExtra("ASSIGNED_KEY",data[6]);
        intent.putExtra("SID_KEY",data[7]);
        intent.putExtra("UID_KEY",data[8]);
        startActivity(intent);
    }

    public RecyclerView mRecyclerView;
    public RecyclerAdapter mAdapter;
    public ProgressBar mProgressBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_view_staff );

            mRecyclerView = findViewById(R.id.mRecyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressBar = findViewById(R.id.myDataLoaderProgressBar);
            mProgressBar.setVisibility(View.VISIBLE);

            mTeachers = new ArrayList<> ();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Staff").orderBy("sname").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    Toast.makeText(ItemsActivity.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    int sid = document.getLong("sid").intValue();
                                    String uid = document.getString("uid");
                                    String imgPath = document.getString("img");
                                    String name = document.getString("sname");
                                    String exp = document.getString("experience");
                                    String qual = document.getString("qualification");
                                    String address = document.getString("address");
                                    String date = document.getString("dateAdded");
                                    int assignedNum = document.getLong("assignedNum").intValue();

                                    StaffItemClass s = new StaffItemClass(name,sid,uid,date,imgPath,exp,qual,address,assignedNum);
                                    mTeachers.add(s);
                                }
                            }
                        }
                        mAdapter = new RecyclerAdapter (ItemsActivity.this, mTeachers);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(ItemsActivity.this);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        }

        public void onItemClick(int position) {
            StaffItemClass clickedTeacher=mTeachers.get(position);
            String[] teacherData={clickedTeacher.getSname(),clickedTeacher.getExperience(),clickedTeacher.getImg(),
                    clickedTeacher.getQualification(),clickedTeacher.getAddress(),clickedTeacher.getDateAdded(),
                    String.valueOf(clickedTeacher.getAssignedNum()),String.valueOf(clickedTeacher.getSid()),clickedTeacher.getUid()};
            openDetailActivity(teacherData);
        }

        @Override
        public void onShowItemClick(int position) {
            StaffItemClass clickedTeacher=mTeachers.get(position);
            String[] teacherData={clickedTeacher.getSname(),clickedTeacher.getExperience(),clickedTeacher.getImg(),
                    clickedTeacher.getQualification(),clickedTeacher.getAddress(),clickedTeacher.getDateAdded(),
                    String.valueOf(clickedTeacher.getAssignedNum()),String.valueOf(clickedTeacher.getSid()),clickedTeacher.getUid()};
            openDetailActivity(teacherData);
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
    }
