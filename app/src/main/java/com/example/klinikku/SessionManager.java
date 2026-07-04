package com.example.klinikku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "LOGIN";
    private static final String IS_LOGIN = "isLogin";
    public static final String KEY_ID = "id_user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_ROLE = "role"; // "admin" or "pasien"

    public SessionManager(Context context){
        this.context=context;
        pref=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor=pref.edit();
    }

    public void createSession(String id, String username, String role){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    public boolean isLogin(){
        return pref.getBoolean(IS_LOGIN,false);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "pasien");
    }

    public String getId() {
        return pref.getString(KEY_ID, "");
    }

    public void logout(){
        editor.clear();
        editor.commit();

        Intent i=new Intent(context,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
