package com.example.juniorhome.ChildrenListView;

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

import com.example.juniorhome.AdmissionRequest.ChildItemClass;
import com.example.juniorhome.ParentListView.ParentListActivity;
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

public class ChildrenListActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener{

        private FirebaseStorage mStorage;
        private List<ChildItemClass> childList;

        private void openDetailActivity(ChildItemClass data){
            Intent intent = new Intent(this, CDetailsActivity.class);
            intent.putExtra("CHILD_DATA",data);
            startActivity(intent);
        }

    public RecyclerView mRecyclerView;
    public RecyclerAdapter mAdapter;
    public ProgressBar mProgressBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_view_child );

            mRecyclerView = findViewById(R.id.mRecyclerView3);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressBar = findViewById(R.id.myDataLoaderProgressBar3);
            mProgressBar.setVisibility(View.VISIBLE);

        childList = new ArrayList<> ();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Children").orderBy("id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    Toast.makeText(ChildrenListActivity.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                }
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int id = document.getLong("id").intValue();
                                int staffId = document.getLong("assignedTo").intValue();
                                String imgPath = document.getString("img");
                                String name = document.getString("name");
                                String address = document.getString("address");
                                String dateAdded = document.getString("dateAdded");
                                String bday = document.getString("bday");
                                String dname = document.getString("dname");
                                String dphno = document.getString("dphno");
                                String email = document.getString("email");
                                String fcnic = document.getString("fcnic");
                                String fname = document.getString("fname");
                                String foccp = document.getString("foccp");
                                String fqual = document.getString("fqual");
                                String fphno = document.getString("fphno");
                                String mphno = document.getString("mphno");
                                String mcnic = document.getString("mcnic");
                                String mname = document.getString("mname");
                                String moccp = document.getString("moccp");
                                String mqual = document.getString("mqual");
                                String gender = document.getString("gender");
                                String gname = document.getString("gname");
                                String gphno = document.getString("gphno");
                                String info = document.getString("info");
                                String parentUID = document.getString("parentUID");
                                String pay = document.getString("pay");
                                String prog = document.getString("prog");
                                String telno = document.getString("telno");

                                ChildItemClass c = new ChildItemClass(id,parentUID,name,gender,staffId,
                                        address,bday,fname,fphno,foccp,fqual,fcnic,mname,mphno,
                                        moccp,mqual,mcnic,info,imgPath,pay,prog,telno,email,dname,dphno,gname,gphno,dateAdded);
                                childList.add(c);
                            }
                        }
                        mAdapter = new RecyclerAdapter (ChildrenListActivity.this, childList);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(ChildrenListActivity.this);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        }

        public void viewParent(View view)
        {
            Intent i = new Intent(ChildrenListActivity.this, ParentListActivity.class);
            startActivity(i);
        }

        public void onItemClick(int position) {
            ChildItemClass clicked=childList.get(position);
            openDetailActivity(clicked);
        }

        @Override
        public void onShowItemClick(int position) {
            ChildItemClass clicked=childList.get(position);
            openDetailActivity(clicked);
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
