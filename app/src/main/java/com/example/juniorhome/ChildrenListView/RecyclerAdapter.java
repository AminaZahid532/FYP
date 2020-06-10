package com.example.juniorhome.ChildrenListView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.example.juniorhome.AdmissionRequest.ChildItemClass;
import com.example.juniorhome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{
    private Context mContext;
    private List<ChildItemClass> children;
    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context, List<ChildItemClass> uploads) {
        mContext = context;
        children = uploads;
    }

    @Override
    @NonNull
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.child_row_model, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        ChildItemClass current = children.get(position);
        int age = getAge(current.getBday());
        if (age < 1)
            holder.childAgeView.setText("");
        else {
            String agestr = "Age: "+ String.valueOf(age);
            holder.childAgeView.setText(agestr);
        }
        holder.childNameView.setText(current.getName());
        holder.dateAdded.setText(current.getDateAdded());

        final FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference();
        StorageReference pathReference = storageRef.child("images/" + current.getImg());

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
            return children.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

            public TextView childNameView,childAgeView,dateAdded;
            public ImageView childImageView;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                childNameView =itemView.findViewById ( R.id.childNameView );
                childAgeView = itemView.findViewById(R.id.childAgeView);
                dateAdded = itemView.findViewById(R.id.dateAdded);
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
