package com.example.joinme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendAdapter{

    private static final String TAG = "AddFriendAdapter";
    private static final String currentUid = FirebaseAPI.getUser().getUid();
    private Context context;

    public AddFriendAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, about;
        ImageView profilePhoto;
        ImageButton profileBtn;
        ImageButton followBtn;
        ImageButton messageBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            about = itemView.findViewById(R.id.user_about);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            profileBtn = itemView.findViewById(R.id.profile_btn);
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

    public FirebaseRecyclerAdapter<User, ViewHolder> addFriendAdaptor() {
        // TODO: order users based on search condition
        Query userQuery = FirebaseAPI.rootRef.child("User");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(userQuery, User.class)
                .build();
        Log.d(TAG, "addFriendAdaptor: userQuery => " + userQuery.toString());
        return new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                // retrieve user uid
                String userID = getRef(position).getKey();
                Log.d(TAG, "addFriendAdaptor: userID => " + userID);
                FirebaseAPI.getFirebaseData("User/" + userID, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("username")) {
                            ((ViewHolder) holder).setName(snapshot.child("username").getValue());
                        }
                        if (snapshot.hasChild("about")) {
                            ((ViewHolder) holder).setAbout(snapshot.child("about").getValue());
                        }

                        // load profile image
                        holder.setProfile(userID);

                        // TODO: view user's profile
                        ((ViewHolder) holder).profileBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });

                        // follow this user
                        ((ViewHolder) holder).followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map<String, Object> pushFollow = new HashMap<>();
                                pushFollow.put(userID, true);
                                FirebaseAPI.rootRef.child("UserFollowing/"+
                                        currentUid).updateChildren(pushFollow);
                                Log.d(TAG, "FollowOnClick: "+ currentUid + " follow user: " + userID);

                            }
                        });

                        // send message to this user
                        ((ViewHolder) holder).messageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

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
                        Log.d(TAG, "onDataChange: already follow this user! "+snapshot.child(userID));
                        Log.d(TAG, "onDataChange: has user?  "+snapshot.getKey());

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
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
    }
}

