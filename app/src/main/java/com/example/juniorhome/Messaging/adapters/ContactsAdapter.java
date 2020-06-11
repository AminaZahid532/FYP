package com.example.juniorhome.Messaging.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juniorhome.Messaging.activities.ChatActivity;
import com.example.juniorhome.R;
import com.example.juniorhome.SessionManager;
import com.example.juniorhome.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context context;
    private SessionManager session;
    private List<UserModel> contactsList = new ArrayList<>();

    public ContactsAdapter(List<UserModel> contactsList) {
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        session = new SessionManager(context);

        View view = LayoutInflater.from(context).inflate(R.layout.users_display_layout, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, final int position) {
        holder.userName.setText(contactsList.get(position).getUname());
        holder.phoneNo.setText(contactsList.get(position).getPhoneNo());

        String profilePic = contactsList.get(position).getProfilePicUrl();
        if (!profilePic.equals("")) {
            Picasso.get().load(profilePic).into(holder.profileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("visit_user_id", contactsList.get(position).getUid());
                intent.putExtra("visit_user_name", contactsList.get(position).getUname());
                intent.putExtra("visit_image", contactsList.get(position).getProfilePicUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView phoneNo;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            phoneNo = itemView.findViewById(R.id.user_phone_no);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
        }
    }
}
