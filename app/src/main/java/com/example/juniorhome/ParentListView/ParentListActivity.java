package com.example.juniorhome.ParentListView;

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

import com.example.juniorhome.AdmissionRequest.ParentItemClass;
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

public class ParentListActivity extends AppCompatActivity implements PRecyclerAdapter.OnItemClickListener{

        private FirebaseStorage mStorage;
        private List<ParentItemClass> parentList;

        private void openDetailActivity(ParentItemClass data){
            Intent intent = new Intent(this, PDetailsActivity.class);
            intent.putExtra("PARENT_DATA",data);
            startActivity(intent);
        }

    public RecyclerView mRecyclerView;
    public PRecyclerAdapter mAdapter;
    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_view_parent );

        mRecyclerView = findViewById(R.id.mRecyclerView4);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.myDataLoaderProgressBar4);
        mProgressBar.setVisibility(View.VISIBLE);

        parentList = new ArrayList<> ();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Parents").orderBy("id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    Toast.makeText(ParentListActivity.this, "Connection problem", Toast.LENGTH_SHORT).show();
                                }
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int id = document.getLong("id").intValue();
                                String name = document.getString("name");
                                String address = document.getString("address");
                                String dateAdded = document.getString("dateAdded");
                                String status = document.getString("parentStatus");
                                String occp = document.getString("occp");
                                String cnic = document.getString("cnic");
                                String email = document.getString("email");
                                String phno = document.getString("phno");
                                String qual = document.getString("qual");
                                String telno = document.getString("telno");
                                String userId = document.getString("usrId");

                                ParentItemClass p = new ParentItemClass(id,userId,name,status,address,phno,occp,
                                        qual,cnic,telno,email,dateAdded);
                                parentList.add(p);
                            }
                        }
                        mAdapter = new PRecyclerAdapter (ParentListActivity.this, parentList);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(ParentListActivity.this);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        }

        public void onItemClick(int position) {
            ParentItemClass clicked=parentList.get(position);
            openDetailActivity(clicked);
        }

        @Override
        public void onShowItemClick(int position) {
            ParentItemClass clicked=parentList.get(position);
            openDetailActivity(clicked);
        }

        @Override
        public void onDeleteItemClick(int position) {
            /*StaffItemClass selectedItem = parentList.get(position);
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
