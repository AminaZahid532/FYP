package com.example.juniorhome.AdmissionRequest;

import android.content.Context;
import android.content.Intent;
import android.icu.util.DateInterval;
import android.net.Uri;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.juniorhome.AdmissionRequest.AdmissionFormItemClass;

import com.bumptech.glide.Glide;
import com.example.juniorhome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static java.text.DateFormat.getDateInstance;

public  class AdmissionRecyclerAdapter extends RecyclerView.Adapter<AdmissionRecyclerAdapter.RecyclerViewHolder>{
        private Context mContext;
        private List<AdmissionFormItemClass> formList;
        private OnItemClickListener mListener;

        public AdmissionRecyclerAdapter(Context context, List<AdmissionFormItemClass> forms) {
            mContext = context;
            formList = forms;
        }

        @Override
        @NonNull
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.request_row_model, parent, false);
            return new RecyclerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            AdmissionFormItemClass currentForm = formList.get(position);
            int age = currentForm.getAge();
            if (age < 1)
                holder.ageTextView.setText("");
            else {
                String agestr = "Age: "+ String.valueOf(age);
                holder.ageTextView.setText(agestr);
            }
            String dateSubmit = currentForm.getDateSubmit();
            holder.nameTextView.setText(currentForm.getName());
            holder.dateTextView.setText(dateSubmit);

            final FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = mStorage.getReference();
            StorageReference pathReference = storageRef.child("images/" + currentForm.getImg());

            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Download directly from StorageReference using Glide
                    Glide.with(mContext)
                            .load(uri)
                            .into(holder.childImageView);
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
            return formList.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

            public TextView nameTextView,ageTextView,dateTextView;
            public ImageView childImageView;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                nameTextView =itemView.findViewById ( R.id.cnameTextView );
                ageTextView = itemView.findViewById(R.id.ageTextView);
                dateTextView = itemView.findViewById(R.id.dateSubmittedTextView);
                childImageView = itemView.findViewById(R.id.childImageView);

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
    }
