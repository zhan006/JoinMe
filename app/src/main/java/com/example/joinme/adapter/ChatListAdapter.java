package com.example.joinme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.R;
import com.example.joinme.activity.ChatActivity;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Conversation;
import com.example.joinme.objects.Time;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ChatListAdapter {

    private static final String TAG = "ChatListAdapter";

    private Context context;
    private String currentUid;
    private DatabaseReference chatRef;
    private DatabaseReference conversationListRef;

    public ChatListAdapter(Context context) {
        this.context = context;
        currentUid = FirebaseAPI.getUser().getUid();
        chatRef = FirebaseAPI.rootRef.child("Chat").child(currentUid);
        chatRef.keepSynced(true);
        conversationListRef = FirebaseAPI.rootRef.child("ConversationList").child(currentUid);
        conversationListRef.keepSynced(true);
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        private TextView friendName;
        private TextView latestMsg;
        private ImageView profilePhoto;
        private ImageView unreadIcon;
        private TextView latestMsgTime;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            latestMsg = itemView.findViewById(R.id.latest_msg);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            unreadIcon = itemView.findViewById(R.id.unread_icon);
            latestMsgTime = itemView.findViewById(R.id.latest_msg_time);
        }

        public TextView getFriendName() {
            return friendName;
        }

        public void setFriendName(String friendName) {
            this.friendName.setText(friendName);
        }

        public TextView getLatestMsg() {
            return latestMsg;
        }

        public void setLatestMsg(String msg, boolean seen) {
            if (msg != null) {
                this.latestMsg.setText(msg);
            } else {
                return;
            }
            Log.d(TAG, "setLatestMsg: seen? "+seen);
            if (seen) {
                unreadIcon.setVisibility(View.GONE);
                latestMsg.setTypeface(latestMsg.getTypeface(), Typeface.NORMAL);
            } else {
                unreadIcon.setVisibility(View.VISIBLE);
                latestMsg.setTypeface(latestMsg.getTypeface(), Typeface.BOLD);
            }
        }

        public ImageView getProfilePhoto() {
            return profilePhoto;
        }

        /**
         * Load profile image from firebase
         */
        public void setProfilePhoto(String userID) {
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

        public ImageView getUnreadIcon() {
            return unreadIcon;
        }

        public void setUnreadIcon(boolean read) {
            if (read) {
                unreadIcon.setVisibility(View.GONE);
            } else {
                unreadIcon.setVisibility(View.VISIBLE);
            }
        }

        public TextView getLatestMsgTime() {
            return latestMsgTime;
        }

        public void setLatestMsgTime(String latestMsgTime) {
            this.latestMsgTime.setText(latestMsgTime);
        }
    }

    public FirebaseRecyclerAdapter<Conversation, ChatListViewHolder> chatListAdaptor() {
        // order all messages by timestamp
        Query chatListQuery = conversationListRef.orderByChild("time/timestamp");
        chatListQuery.keepSynced(true);
        FirebaseRecyclerOptions<Conversation> options =
                new FirebaseRecyclerOptions.Builder<Conversation>()
                .setQuery(chatListQuery, Conversation.class)
                .build();
        return new FirebaseRecyclerAdapter<Conversation, ChatListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatListViewHolder holder, int position, @NonNull Conversation model) {
                // uid of current chatting user
                String chatUserID = getRef(position).getKey();
                // retrieve latest msg
                Query latestMsgQuery = chatRef.child(chatUserID).limitToLast(1);
                latestMsgQuery.keepSynced(true);
                // listen to the last message and update it
                latestMsgQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        if (snapshot.hasChild("time")) {
                            Time time = snapshot.child("time").getValue(Time.class);
                            holder.setLatestMsgTime(time.time());
                        }
                        String msg = snapshot.child("messageContent").getValue().toString();
                        boolean seen = (boolean) snapshot.child("seen").getValue();
                        if (snapshot.hasChild("type") && snapshot.child("type").getValue().toString().equals("image")) {
                            msg = "Image";
                        }
                        Log.d(TAG, "onChildAdded: "+seen);
                        holder.setLatestMsg(msg, seen);
//                        conversationListRef.child(chatUserID).child("seen").setValue(seen);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.hasChild("time")) {
                            Time time = snapshot.child("time").getValue(Time.class);
                            holder.setLatestMsgTime(time.time());
                        }
                        String msg = snapshot.child("messageContent").getValue().toString();
                        boolean seen = (boolean) snapshot.child("seen").getValue();
                        if (snapshot.hasChild("type") && snapshot.child("type").getValue().toString().equals("image")) {
                            msg = "Image";
                        }
                        Log.d(TAG, "onChildAdded: "+seen);
                        holder.setLatestMsg(msg, seen);
//                        conversationListRef.child(chatUserID).child("seen").setValue(seen);

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // retrieve friend
                FirebaseAPI.getFirebaseData("User/" + chatUserID, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String friendUsername = "";
                        if (snapshot.hasChild("username")) {
                            friendUsername = snapshot.child("username").getValue().toString();
                            holder.setFriendName(friendUsername);
                        }
                        if (snapshot.hasChild("profileImage")) {
                            holder.setProfilePhoto(chatUserID);
                        }
                        String finalFriendUsername = friendUsername;
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra("friendUid", chatUserID);
                                chatIntent.putExtra("friendUsername", finalFriendUsername);
                                context.startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
                return new ChatListViewHolder(v);
            }
        };
    }
}

