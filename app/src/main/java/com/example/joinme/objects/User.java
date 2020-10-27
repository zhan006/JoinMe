package com.example.joinme.objects;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.joinme.database.FirebaseAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;
import android.content.Context;

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String profileImage;
    public String username;
    public String about;
    public String gender;
    public String email,phone;
    public List<String> album;
    public location location;

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
    public void setLocation(location l){location = l;}
    public location getLocation(){return location;}
    public String getGender(){return gender;};
    public void setGender(String g){this.gender= g;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone = phone;}
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

    /**
     * Load profile image from firebase
     */
    public void loadProfileImage(Context context, String uid, ImageView imageView) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("profileImage")) {
                    String image = snapshot.child("profileImage").getValue().toString();
                    if (!image.equals("null")) {
                        //display image from the url in real time database for user profile image
                        Glide.with(context).load(image).into(imageView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("User/"+uid, valueEventListener);

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
