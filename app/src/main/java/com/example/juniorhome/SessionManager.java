package com.example.juniorhome;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences session;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        session = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        //session = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLoggedin() {
        return session.contains("uid");
    }

    void setUser(String uid, String username, String role) {
        editor = session.edit();
        editor.putString("uid", uid);
        editor.putString("uname", username);
        editor.putString("role", role);
        editor.apply();
    }

    public String getUserId() {
        return session.getString("uid", "null");
    }

    public String getUsername() {
        return session.getString("uname", "null");
    }

    public String getRole() {
        return session.getString("role", "");
    }

    public void clear() {
        editor = session.edit();
        editor.clear();
        editor.apply();
    }
}
