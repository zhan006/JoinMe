package com.example.joinme.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.activity.ChatActivity;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.fragments.visitorProfileFragment;
import com.example.joinme.objects.User;
import com.example.joinme.utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddFriendAdapter{

    private static final String TAG = "AddFriendAdapter";
    private static final String currentUid = FirebaseAPI.getUser().getUid();
    private Context context;
    private User user;


    public AddFriendAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, about;
        ImageView profilePhoto;
        Button followBtn;
        Button messageBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            about = itemView.findViewById(R.id.user_about);
            profilePhoto = itemView.findViewById(R.id.profile_photo);

            followBtn = itemView.findViewById(R.id.follow_btn);
            messageBtn = itemView.findViewById(R.id.message_icon);
        }

        public TextView getName() {
            return name;
        }

        public void setName(Object name) {
            if (name == null) {
                name = "";
            }
            this.name.setText(name.toString());
        }
        public void setItemViewListener(View.OnClickListener listener){
            this.itemView.setOnClickListener(listener);
        }

        public TextView getAbout() {
            return about;
        }

        public void setAbout(Object about) {
            if (about == null) {
                about = "";
            }
            this.about.setText(about.toString());
        }

        /**
         * Load profile image from firebase
         */
        public void setProfile(String userID) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("profileImage")) {
                        String image = snapshot.child("profileImage").getValue().toString();
                        if (!image.equals("null")) {
                            //display image from the url in real time database for user profile image
                            Glide.with(context).load(image).into(profilePhoto);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseAPI.getFirebaseData("User/"+userID, valueEventListener);
        }
    }

    public FirebaseRecyclerAdapter<User, ViewHolder> addFriendAdaptor(Query userQuery) {

//        Query test = FirebaseAPI.rootRef.child("User").child("gender").equalTo("Female");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(userQuery, User.class)
                .build();

        return new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                // retrieve user uid
                String userID = getRef(position).getKey();

                FirebaseAPI.getFirebaseData("User/" + userID, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("username")) {
                            holder.setName(snapshot.child("username").getValue());
                        }
                        if (snapshot.hasChild("about")) {
                            holder.setAbout(snapshot.child("about").getValue());
                        }

                        // load profile image
                        holder.setProfile(userID);
                        holder.profilePhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewUserProfile(userID);
                            }
                        });


                        // view user's profile
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewUserProfile(userID);
                            }
                        });

                        // follow this user
                        holder.followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                followUser(userID);
                            }
                        });

                        // send message to this user
                        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chat(userID, snapshot.child("username").getValue().toString(), v);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // hide follow btn
                FirebaseAPI.getFirebaseData("UserFollowing/" +  currentUid, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        Log.d(TAG, "onDataChange: already follow this user! "+snapshot.child(userID));
//                        Log.d(TAG, "onDataChange: has user?  "+snapshot.getKey());

                        // already follow this user, hide follow btn
                        if (snapshot.hasChild(userID) || userID.equals(currentUid)) {
//                            Log.d(TAG, "onDataChange: already follow this user! "+snapshot.child(userID));
                            holder.followBtn.setVisibility(View.GONE);
                        } else {
                            holder.followBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.add_friend_item, parent, false);
                return new ViewHolder(view);
            }
        };
    }

    private void viewUserProfile(String userID) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        utils.replaceFragment(fm, new visitorProfileFragment(userID), null);
    }

    private void followUser(String userID) {
        Map<String, Object> pushFollow = new HashMap<>();
        pushFollow.put(userID, true);
        FirebaseAPI.rootRef.child("UserFollowing/"+
                currentUid).updateChildren(pushFollow);
        Log.d(TAG, "FollowOnClick: "+ currentUid + " follow user: " + userID);
    }

    private void chat(String userID, String username, View v) {
        Intent chatActivity = new Intent(v.getContext(), ChatActivity.class);
        chatActivity.putExtra("friendUid", userID);
        chatActivity.putExtra("friendUsername",
                username);
        chatActivity.putExtra("currentUsername", user.getUsername());
        context.startActivity(chatActivity);
    }
}

