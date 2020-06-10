package com.example.juniorhome.Messaging.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juniorhome.Messaging.activities.ChatActivity;
import com.example.juniorhome.Messaging.models.Contacts;
import com.example.juniorhome.Messaging.models.Messages;
import com.example.juniorhome.R;
import com.example.juniorhome.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View PrivateChatsView;
    private TextView tvEmptyList;
    private RecyclerView chatsList;
    private ProgressBar progressBar;

    private String currentUserID = "";
    private SessionManager session;
    private DatabaseReference ChatsRef, UsersRef;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);


        session = new SessionManager(getContext());
        currentUserID = session.getUserId();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        tvEmptyList = (TextView) PrivateChatsView.findViewById(R.id.tv_empty_list);
        progressBar = (ProgressBar) PrivateChatsView.findViewById(R.id.progress_bar);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatsView;
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(ChatsRef, Messages.class)
                        .build();

        FirebaseRecyclerAdapter<Messages, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Messages, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull final Messages model) {

                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        holder.userName.setText(model.getTo());
                        holder.lastMessage.setText(model.getMessage());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", usersIDs);
                                chatIntent.putExtra("visit_user_name", model.getTo());
//                                chatIntent.putExtra("visit_image", retImage[0]);
                                startActivity(chatIntent);
                            }
                        });
//                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    if (dataSnapshot.hasChild("image")) {
//                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
//                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
//                                    }
//
//                                    final String retName = dataSnapshot.child("name").getValue().toString();
//                                    holder.userName.setText(retName);
//
////
////                                    if (dataSnapshot.child("userState").hasChild("state"))
////                                    {
////                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
////                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
////                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();
////
////                                        if (state.equals("online"))
////                                        {
////                                            holder.userStatus.setText("online");
////                                        }
////                                        else if (state.equals("offline"))
////                                        {
////                                            holder.userStatus.setText("Last Seen: " + date + " " + time);
////                                        }
////                                    }
////                                    else
////                                    {
////                                        holder.userStatus.setText("offline");
////                                    }
////
//
//                                }
//                            }
//
//
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                    }

                    @NonNull
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        progressBar.setVisibility(View.GONE);
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }

                    @Override
                    public int getItemCount() {
                        if(super.getItemCount() > 0){
                            tvEmptyList.setVisibility(View.GONE);
                        }else{
                            tvEmptyList.setVisibility(View.VISIBLE);
                        }
                        return super.getItemCount();
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView lastMessage, userName;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            lastMessage = itemView.findViewById(R.id.user_phone_no);
        }
    }
}
