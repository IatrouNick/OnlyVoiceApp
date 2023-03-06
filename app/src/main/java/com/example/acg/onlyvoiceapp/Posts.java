package com.example.acg.onlyvoiceapp;

import android.widget.Button;

public class Posts {
    private String userKey;
    private String userName;
    private String postBody;
    private long timestamp;

    public Posts() {
        //Retrieve posts objects from DB DataSnapshot.getValue(Post.class)
    }
    public Posts(String userKey, String userName, String postBody, long timestamp) {
        this.userKey = userKey;
        this.userName = userName;
        this.postBody = postBody;
        this.timestamp = timestamp;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String postTitle) {
        this.userName = postTitle;
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
