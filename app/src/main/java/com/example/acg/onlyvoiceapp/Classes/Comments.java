package com.example.acg.onlyvoiceapp.Classes;

public class Comments {
        private String userKey;
        private String postKey;
        private String commentKey;
        private String userName;
        private String commentBody;
        private long timestamp;

    public Comments(String userKey, String postKey, String commentKey, String userName, String commentBody, long timestamp) {
        this.userKey = userKey;
        this.postKey = postKey;
        this.commentKey = commentKey;
        this.userName = userName;
        this.commentBody = commentBody;
        this.timestamp = timestamp;
    }
    public Comments(){

    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getPostKey() {
        return postKey;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
