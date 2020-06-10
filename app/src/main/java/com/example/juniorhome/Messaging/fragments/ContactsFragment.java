package com.example.juniorhome.Messaging.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juniorhome.Messaging.adapters.ContactsAdapter;
import com.example.juniorhome.Messaging.models.Contacts;
import com.example.juniorhome.R;
import com.example.juniorhome.SessionManager;
import com.example.juniorhome.UserItemClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View ContactsView;
    private TextView tvEmptyList;
    private ProgressBar progressBar;
    private RecyclerView myContactsList;

    private DatabaseReference ContacsRef, UsersRef;
    private CollectionReference usersCollection;
    private SessionManager session;
    private String currentUserID;
    private String currentUserRole;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        session = new SessionManager(getContext());
        currentUserID = session.getUserId();
        currentUserRole = session.getRole();

        ContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contacts_list);
        progressBar = (ProgressBar) ContactsView.findViewById(R.id.progress_bar);
        tvEmptyList = (TextView) ContactsView.findViewById(R.id.tv_empty_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        usersCollection = FirebaseFirestore.getInstance().collection("users");
        ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentUserRole.equals("parent")) {
            usersCollection.whereEqualTo("role", "staff");
        }else if (currentUserRole.equals("staff")){
            usersCollection.whereEqualTo("role", "parent");
        }

        usersCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful() && task.getResult() != null){
                    List<UserItemClass> usersList = task.getResult().toObjects(UserItemClass.class);
                    if(usersList.size() > 0){
                        Iterator<UserItemClass> iterable = usersList.iterator();
                        while(iterable.hasNext()){
                            if(iterable.next().getUid().equals(session.getUserId())){
                                iterable.remove();
                                break;
                            }
                        }

                        ContactsAdapter adapter = new ContactsAdapter(usersList);
                        tvEmptyList.setVisibility(View.GONE);
                        myContactsList.setAdapter(adapter);
                    }else{
                        tvEmptyList.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


//        FirebaseRecyclerOptions options =
//                new FirebaseRecyclerOptions.Builder<Contacts>()
//                        .setQuery(usersCollection, Contacts.class)
//                        .build();


//        final FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
//                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
//                final String userIDs = getRef(position).getKey();
//
//                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        if (dataSnapshot.exists())
////                        {
////                            if (dataSnapshot.child("userState").hasChild("state"))
////                            {
////                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
////                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
////                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
////
////                                if (state.equals("online"))
////                                {
////                                    holder.onlineIcon.setVisibility(View.VISIBLE);
////                                }
////                                else if (state.equals("offline"))
////                                {
////                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
////                                }
////                            }
////                            else
////                            {
////                                holder.onlineIcon.setVisibility(View.INVISIBLE);
////                            }
//
//
//                        if (dataSnapshot.hasChild("image")) {
//                            String userImage = dataSnapshot.child("image").getValue().toString();
//                            String profileName = dataSnapshot.child("name").getValue().toString();
//
//
//                            holder.userName.setText(profileName);
//
//                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
//                        }
//                        else {
//                            String profileName = dataSnapshot.child("name").getValue().toString();
//
//
//                            holder.userName.setText(profileName);
//
//                        }
//                        // }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
//                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
//                return viewHolder;
//            }
//        };
//
//        myContactsList.setAdapter(adapter);
//        adapter.startListening();
    }



}
