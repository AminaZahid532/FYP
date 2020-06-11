package com.example.juniorhome.Messaging.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juniorhome.Messaging.adapters.MessageAdapter;
import com.example.juniorhome.Messaging.models.Messages;
import com.example.juniorhome.R;
import com.example.juniorhome.SessionManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private SessionManager session;
    private DatabaseReference RootRef;
    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private String saveCurrentTime, saveCurrentDate, checker = "", myUrl = "";
    private Uri fileUri;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(this);
        messageSenderID = session.getUserId();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getStringExtra("visit_user_id").toString();
        messageReceiverName = getIntent().getStringExtra("visit_user_name").toString();
        if (getIntent().hasExtra("visit_image")) {
            messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
        }

        IntializeControllers();

        userName.setText(messageReceiverName);
        if (!messageReceiverImage.equals("")) {
            Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
        }

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",

                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        }

                    }
                });
                builder.show();
            }
        });

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//        mAuth = FirebaseAuth.getInstance();
//        messageSenderID = mAuth.getCurrentUser().getUid();
//        RootRef = FirebaseDatabase.getInstance().getReference();
//
//
//        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
//        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
//        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
//      //  Toast.makeText(ChatActivity.this ,messageReceiverID,Toast.LENGTH_SHORT).show();
//        //Toast.makeText(ChatActivity.this ,messageReceiverName,Toast.LENGTH_SHORT).show();
//        IntializeControllers();
//
//        userName.setText(messageReceiverName);
//        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
//
//        SendMessageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                SendMessage();
//            }
//        });
//        SendFilesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                CharSequence options[]=new CharSequence[]
//                        {
//                                "images"
//
//                        };
//                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
//                builder.setTitle("Select file");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if(i==0){
//                            checker="image";
//                            Intent intent=new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            intent.setType("image/*");
//                            startActivityForResult(Intent.createChooser(intent,"Select Image"),438);
//                        }
//                    }
//                });
//                builder.show();
//            }
//        });
//    }//end func

    private void IntializeControllers() {

        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        loadingBar = new ProgressDialog(this);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        userImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
                intent.putExtra("visit_user_id", messageReceiverID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            loadingBar.setTitle("Sending");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            fileUri = data.getData();
            if (!checker.equals("image")) {


            }
            else if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image Files");
                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();
                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {

                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });
                        }
                    }
                });

            }
            else {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected ,Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendMessage() {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Firstly, Write  Message", Toast.LENGTH_SHORT).show();
        }
        else {

            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
            String inboxSenderRef = "" + messageSenderID + "/" + messageReceiverID;
            String inboxReceiverRef = "" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("picUrl", messageReceiverImage);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
            Map inboxBodyDetails = new HashMap();
            inboxBodyDetails.put(inboxSenderRef, messageTextBody);
            inboxBodyDetails.put(inboxReceiverRef, messageTextBody);

            RootRef.child("Contacts").updateChildren(inboxBodyDetails);
            RootRef.child("MessageNotifications").child(messageReceiverID).push().setValue(messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }

    }

    // @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
//                    {
//                        Messages messages = dataSnapshot.getValue(Messages.class);
//
//                        messagesList.add(messages);
//
//                        messageAdapter.notifyDataSetChanged();
//
//                       userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
//                    }
//
//
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }

}
