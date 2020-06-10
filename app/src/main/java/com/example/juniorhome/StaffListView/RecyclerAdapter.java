package com.example.juniorhome.StaffListView;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.juniorhome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{
        private Context mContext;
        private List<StaffItemClass> teachers;
        private OnItemClickListener mListener;

        public RecyclerAdapter(Context context, List<StaffItemClass> uploads) {
            mContext = context;
            teachers = uploads;
        }

        @Override
        @NonNull
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.row_model, parent, false);
            return new RecyclerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            StaffItemClass currentTeacher = teachers.get(position);
            String exp = "Experience: " + currentTeacher.getExperience();
            String qualification = "Qualification: " + currentTeacher.getQualification();
            String date = "Date Added: " + currentTeacher.getDateAdded();
            holder.nameTextView.setText(currentTeacher.getSname());
            holder.experienceTextView.setText(exp);
            holder.qualTextView.setText(qualification);
            holder.dateView.setText(date);

            final FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = mStorage.getReference();
            StorageReference pathReference = storageRef.child("staff/images/" + currentTeacher.getImg());

            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Download directly from StorageReference using Glide
                    Glide.with(mContext)
                            .load(uri)
                            .into(holder.teacherImageView);
                    //holder.itemView.findViewById(R.id.myDataLoaderProgressBar).setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(mContext,"Can not load Image", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return teachers.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

            public TextView nameTextView,experienceTextView,dateView,qualTextView;
            public ImageView teacherImageView;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                nameTextView =itemView.findViewById ( R.id.nameTextView );
                experienceTextView = itemView.findViewById(R.id.experienceTextView);
                qualTextView = itemView.findViewById(R.id.qualTextView);
                dateView = itemView.findViewById(R.id.date);
                teacherImageView = itemView.findViewById(R.id.teacherImageView);

                itemView.setOnClickListener(this);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Select Action");
                MenuItem showItem = menu.add( Menu.NONE, 1, 1, "Show");
                MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete");

                showItem.setOnMenuItemClickListener(this);
                deleteItem.setOnMenuItemClickListener(this);
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        switch (item.getItemId()) {
                            case 1:
                                mListener.onShowItemClick(position);
                                return true;
                            case 2:
                                mListener.onDeleteItemClick(position);
                                return true;
                        }
                    }
                }
                return false;
            }
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
            void onShowItemClick(int position);
            void onDeleteItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }
        private String getDateToday(){
            DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
            Date date=new Date();
            String today= dateFormat.format(date);
            return today;
        }
    }
