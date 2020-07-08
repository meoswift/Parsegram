package com.example.parsegram.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Post model represent a Post object in our Parse database
@ParseClassName("Post")
@Parcel(analyze={Post.class})
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";

    public Post() {}

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public String getFormatedTime() {

        long dateMillis = getCreatedAt().getTime();
        String ago = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return ago;
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setImage(ParseFile file) {
        put(KEY_IMAGE, file);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

}
