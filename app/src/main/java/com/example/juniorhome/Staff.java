package com.example.juniorhome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.juniorhome.Messaging.activities.MessagingActivity;

public class Staff extends AppCompatActivity {

    Button messageBtn;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        //SessionManager session = new SessionManager(getApplicationContext());
        //String nm = session.getUsername();
        initXml();
        setListeners();
    }

    private void initXml(){
        messageBtn = findViewById(R.id.button12);
    }

    private void setListeners(){
        messageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Staff.this, MessagingActivity.class));
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        View v = findViewById(R.id.action_logout);

        switch (item.getItemId()) {
            case R.id.action_settings :
                // User chose the "Settings" item, show the app settings UI...
                Intent i = new Intent(Staff.this,UserProfile.class);
                startActivity(i);
                return true;

            case R.id.action_logout:
                builder = new AlertDialog.Builder(this);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Uncomment the below code to Set the message and title from the strings.xml file
                        //builder.setMessage("Do you want to Logout?") .setTitle("Logout");

                        //Setting message manually and performing action on button click
                        builder.setMessage("Do you want to Logout?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SessionManager session = new SessionManager(getApplicationContext());
                                        session.clear();
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Logout");
                        alert.show();
                    }
                });

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}