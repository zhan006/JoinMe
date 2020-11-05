package com.example.joinme.objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private static final String TAG = "Comment";
    private String commentID, userID, profileImgID, firstName, commentContent, dateTime, eventID;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Comment(String firstName, String lastName, String profileImgID, String commentContent){
        this.firstName = firstName;
        this.commentContent = commentContent;
        this.profileImgID = profileImgID;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.dateTime = dateFormat.format(new Date());

    }

    public Comment(User user, String userID, String eventID, String commentContent){
        this.userID = userID;
        this.firstName = user.getFirstName();
        this.profileImgID = user.getProfileImage();
        this.commentContent = commentContent;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.dateTime = dateFormat.format(new Date());
        this.eventID = eventID;
    }


    public Comment(){

    };

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getProfileImageId(){
        return profileImgID;
    }

    public void setProfileImgID(String profileImgID) {
        this.profileImgID = profileImgID;
    }

    public String getCommentContent(){
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
    

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID){
        this.commentID = commentID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String toString(){
        return this.getFirstName()+this.commentContent+this.dateTime;
    }
}
