package com.example.parsegram.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("User")
@Parcel(analyze={User.class})
public class User extends ParseUser {
    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }
}
