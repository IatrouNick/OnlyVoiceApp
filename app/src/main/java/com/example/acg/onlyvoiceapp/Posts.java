package com.example.acg.onlyvoiceapp;

import android.widget.Button;

public class Posts {
    private String userKey;
    private String postTitle; //User's name
    private String postBody;
    private long timestamp;

    public Posts() {
        //Retrieve posts objects from DB DataSnapshot.getValue(Post.class)
    }
    public Posts(String userKey, String postTitle, String postBody, long timestamp) {
        this.userKey = userKey;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.timestamp = timestamp;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
