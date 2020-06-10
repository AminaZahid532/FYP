package com.example.juniorhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    private Intent intent;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
        //String user = session.getUsername();
        if (session.isLoggedin()) {
            String role = session.getRole();
            switch (role) {
                case "admin":
                    intent = new Intent(Login.this, Admin.class);
                    break;
                case "parent":
                    intent = new Intent(Login.this, Parent.class);
                    break;
                case "staff":
                    intent = new Intent(Login.this, Staff.class);
                    break;
            }
            startActivity(intent);
            finish();
        }
    }

    /**
     * Called when the user taps the login button
     */
    public void login(final View view) {
        //disabled to avoid multi click
        view.setEnabled(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //final SharedPreferences session;
        //session = getSharedPreferences("user_details",MODE_PRIVATE);
        EditText id = findViewById(R.id.usrid);
        EditText pass = findViewById(R.id.passwrd);
        final String userId = id.getText().toString();
        final String password = pass.getText().toString();

        db.collection("users")
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        view.setEnabled(false);
                        if (task.isSuccessful()) {
                            if (task.getResult() == null) {
                                Toast.makeText(Login.this, "Incorrect UserID", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String role = document.getString("role");
                                    String username = document.getString("uname");
                                    if (document.get("password").toString().equals(password)) {
                                        if (role.equals("admin")) {
                                            intent = new Intent(Login.this, Admin.class);
                                        }
                                        else if (role.equals("staff")) {
                                            intent = new Intent(Login.this, Staff.class);
                                        }
                                        else if (role.equals("parent")) {
                                            intent = new Intent(Login.this, Parent.class);
                                        }

                                        session.setUser(userId, username, role);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(Login.this, "Error getting documents." + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}