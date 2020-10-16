package com.example.joinme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MessageAdapter";
    private List<Message> messageList;
    private String currentUid;
    private String friendUid;
    private Context context;

    public MessageAdapter(List<Message> messageList, Context context,
                          String currentUid, String friendUid) {
        this.messageList = messageList;
        this.currentUid = currentUid;
        this.context = context;
        this.friendUid = friendUid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // render different view holder based on message type
        if (viewType == Message.LEFT_MSG_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
            return (new LeftMessageViewHolder(view));
        } else if (viewType == Message.RIGHT_MSG_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
            return (new RightMessageViewHolder(view));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        String chatWith = message.getFrom();
        if (chatWith == null) {
            return;
        }


        // set right hand side view holder
        if (chatWith.equals(this.currentUid)) {
            // set current user's profile image
            ((RightMessageViewHolder) holder).setProfile();

            // render message content
            if (message.getType().equals("text")) {
                ((RightMessageViewHolder) holder).setMessage(message.getMessageContent());
                // hide image component
                ((RightMessageViewHolder) holder).getMessageImage().setVisibility(View.GONE);
            } else if (message.getType().equals("image")) {
                // display image and hide message text box
                ((RightMessageViewHolder) holder).getMessageImage().setVisibility(View.VISIBLE);
                ((RightMessageViewHolder) holder).getMessage().setVisibility(View.GONE);
                ((RightMessageViewHolder) holder).setMessageImage(message.getMessageContent());
            }

        }
        // set left hand side view holder
        else {
            // set friend's profile image
            ((LeftMessageViewHolder) holder).setProfile();

            // render message content
            if (message.getType().equals("text")) {
                ((LeftMessageViewHolder) holder).setMessage(message.getMessageContent());
                // hide image component
                ((LeftMessageViewHolder) holder).getMessageImage().setVisibility(View.GONE);
            } else if (message.getType().equals("image")) {
                // display image and hide message text box
                ((LeftMessageViewHolder) holder).getMessageImage().setVisibility(View.VISIBLE);
                ((LeftMessageViewHolder) holder).getMessage().setVisibility(View.GONE);
                ((LeftMessageViewHolder) holder).setMessageImage(message.getMessageContent());
            }
        }

        // after load message & image, update all messages as seen for current user
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // update all messages under this chat to be seen
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.hasChild("seen")) {
                        Log.d(TAG, "onDataChange: child seen => "+
                                dataSnapshot.child("seen").getValue().toString());
                        dataSnapshot.child("seen").getRef().setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseAPI.getFirebaseData("Chat/"+this.currentUid+"/"+
                this.friendUid, valueEventListener );
    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (this.currentUid == null || message.getFrom() == null) {
            return -1;
        }
        // check who send this message
        if (message.getFrom().equals(this.currentUid)) {
            return Message.RIGHT_MSG_TYPE;
        } else {
            return Message.LEFT_MSG_TYPE;
        }
    }

    /**
     * View Holder for left message
     */
     class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView message;
        ImageView messageImage;

        public LeftMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.chat_message_left_profile);
            message = itemView.findViewById(R.id.left_message_text);
            messageImage = itemView.findViewById(R.id.left_message_image);
        }

        /**
         * Load profile image from firebase
         */
        public void setProfile() {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("profileImage")) {
                        String image = snapshot.child("profileImage").getValue().toString();
                        if (!image.equals("null")) {
                            //display image from the url in real time database for user profile image
                            Glide.with(context).load(image).into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseAPI.getFirebaseData("User/"+friendUid, valueEventListener);
        }

        public void setMessage(String messageText) {
            this.message.setText(messageText);
        }

        public void setMessageImage(String messageImageUrl) {
            if (messageImageUrl != null && !messageImageUrl.equals("null")) {
                // display image from the url in real time database
                Glide.with(context).load(messageImageUrl).into(messageImage);
            }
        }

        public ImageView getProfileImage() {
            return profileImage;
        }

        public TextView getMessage() {
            return message;
        }

        public ImageView getMessageImage() {
            return messageImage;
        }
    }

    /**
     * View Holder for Right message
     */
    class RightMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView message;
        ImageView messageImage;

        public RightMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.chat_message_right_profile);
            message = itemView.findViewById(R.id.right_message_text);
            messageImage = itemView.findViewById(R.id.right_message_image);
        }

        /**
         * Load profile image from firebase
         */
        public void setProfile() {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("profileImage")) {
                        String image = snapshot.child("profileImage").getValue().toString();
                        if (!image.equals("null")) {
                            //display image from the url in real time database for user profile image
                            Glide.with(context).load(image).into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseAPI.getFirebaseData("User/"+currentUid, valueEventListener);
        }

        public void setMessage(String messageText) {
            this.message.setText(messageText);
        }

        public void setMessageImage(String messageImageUrl) {
            if (messageImageUrl != null && !messageImageUrl.equals("null")) {
                // display image from the url in real time database
                Glide.with(context).load(messageImageUrl).into(messageImage);
            }
        }

        public ImageView getProfileImage() {
            return profileImage;
        }

        public TextView getMessage() {
            return message;
        }

        public ImageView getMessageImage() {
            return messageImage;
        }
    }
}
