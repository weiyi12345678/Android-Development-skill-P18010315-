package android.bignerdranch.gameintro;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content, uid, username,timestamp;

    public Comment() {
    }


    public Comment(String content, String uid, String username, String timestamp) {
        this.content = content;
        this.uid = uid;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
