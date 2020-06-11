package com.example.juniorhome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.juniorhome.StaffListView.ItemsActivity;

public class help_center extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
    }
    public void phoneAndEmail( View view )
    {
        Intent intent = new Intent(help_center.this, phoneAndEmail.class);
        startActivity(intent);
    }

    public void buisnessHours( View view )
    {
        Intent intent = new Intent(help_center.this, buisness_hour.class);
        startActivity(intent);
    }
    public void ChildyHour( View view )
    {
        Intent intent = new Intent(help_center.this, childyHour.class);
        startActivity(intent);
    }
    public void postalAddress( View view )
    {
        Intent intent = new Intent(help_center.this, postalAddress.class);
        startActivity(intent);
    }
}
