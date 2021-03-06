package com.example.parsegram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;

// Post model represent a Post object in our Parse database
@ParseClassName("Post")
@Parcel(analyze={Post.class})
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_LIKES = "likes";

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

    public ParseRelation<ParseObject> getComments() {
        return getRelation(KEY_COMMENTS);
    }

    public ParseRelation<ParseUser> getLikes() {
        return getRelation(KEY_LIKES);
    }
}
