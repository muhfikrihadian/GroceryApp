package com.example.groceryapp.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.groceryapp.Model.Users;


public class DataHelper {
    String key = "GroceryApp";
    Users users;
    Context context;

    public DataHelper(Context context) {
        this.context = context;
    }

    public SharedPreferences getPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return prefs;
    }

    public Boolean isLoggedIn() {
        String user = getPrefs().getString("User", "");
        if (user.equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    public String getUser() {
        String user = getPrefs().getString("User", "");
        return user;
    }

    public void setUser(String user) {
        getPrefs().edit().putString("User", user).apply();
    }

    public String getRole() {
        String role = getPrefs().getString("Role", "");
        return role;
    }

    public void deletePref() {
        context.getSharedPreferences(key, 0).edit().clear().commit();
    }
}
