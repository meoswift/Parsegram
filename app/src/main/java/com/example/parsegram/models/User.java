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
    public static final String KEY_PROFILE_PIC = "profilePic";
}
