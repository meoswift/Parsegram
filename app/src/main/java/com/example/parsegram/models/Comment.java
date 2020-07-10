package com.example.parsegram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_BODY = "body";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_POST = "post";
    public static final String KEY_CREATED_AT = "createdAt";

    public Comment() {}

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setUser(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }
}
