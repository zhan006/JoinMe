package com.example.joinme.objects;

public class Comment {
    private String firstName;
    private String lastName;
    private int profileImageId;
    private String commentContent;
    private int upvoteCount;
    private String upvoteIcon;
    private String commentAge;
//    private String replyToUsername;


    public Comment(String firstName, String lastName, int profileImageId,String commentContent){
        this.firstName = firstName;
        this.lastName = lastName;
        this.commentContent = commentContent;
        this.profileImageId = profileImageId;
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

}
