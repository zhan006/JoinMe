package com.example.joinme.objects;

import com.example.joinme.R;

public class Comment {
    private String firstName;
    private String lastName;
    private int profileImageId;
    private String commentContent;
    private int upvoteCount;
    private int upvoteIcon;
    private String timeAgo;
    private String timeCreated;

//    private String replyToUsername;


    public Comment(String firstName, String lastName, int profileImageId,String commentContent){
        this.firstName = firstName;
        this.lastName = lastName;
        this.commentContent = commentContent;
        this.profileImageId = profileImageId;
        this.upvoteIcon = R.drawable.event_upvote_icon;
        this.upvoteCount = 0;
        this.timeCreated = "" + System.currentTimeMillis();

    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }


    public int getProfileImageId(){
        return profileImageId;
    }

    public String getCommentContent(){
        return commentContent;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }


    public String getTimeAgo() {
        return timeAgo;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

}
