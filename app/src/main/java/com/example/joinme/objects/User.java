package com.example.joinme.objects;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String profileImage;
    public String username;
    public String about;
    public List<String> album;

    public User() {
    }

    public User(String firstName, String lastName, String profileImage,
                String username, String about, List<String> album) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.username = username;
        this.about = about;
        this.album = album;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }
    public String toString(){
        return firstName+" "+lastName;
    }
}
