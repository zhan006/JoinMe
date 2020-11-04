package com.example.joinme.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Comment;
import com.example.joinme.objects.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter{

    private List<Comment> commentList;
    private BottomSheetDialog dialog;
    private String currentUid, eventID;
    private Context context;
    private static String TAG = "CommentAdapter";
    private User user;




    public CommentAdapter(Context context, String eventID,List<Comment> commentList){
        this.commentList = commentList;
        this.context = context;
        this.eventID = eventID;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView firstName,  commentContent, replyButton, createdDateTime;
        ImageView profilePhoto;

        public ViewHolder(View itemView){
            super(itemView);
            firstName = itemView.findViewById(R.id.comment_user_firstName);
            profilePhoto = itemView.findViewById(R.id.comment_user_photo);
            commentContent = itemView.findViewById(R.id.event_comments_content);
            replyButton = itemView.findViewById(R.id.comment_reply_btn);
            createdDateTime = itemView.findViewById(R.id.comment_createdDateTime);

        }



        public TextView getFirstName(){
            return firstName;
        }

        public void setFirstName(Object firstName) {
            if (firstName == null){
                firstName = "";
            }this.firstName.setText(firstName.toString());
        }

        public TextView getCommentContent(){
            return commentContent;
        }

        public void setCommentContent(Object commentContent){
            if (commentContent == null){
                commentContent = "";
            }this.commentContent.setText(commentContent.toString());
        }

        public TextView getCreatedDateTime(){
            return createdDateTime;
        }

        public void setCreatedDateTime(Object createdDateTime){
            if (createdDateTime == null){
                createdDateTime = "";
            }this.createdDateTime.setText(createdDateTime.toString());
        }


        public void setProfile(String profileLink){
            Glide.with(context).load(profileLink).into(profilePhoto);
        }

    }





    public void addNewComment(Comment newComment){
        if(newComment != null){
            commentList.add(newComment);
            notifyDataSetChanged();
        }else{
            throw new IllegalArgumentException("Empty Comment!");
        }
    }

    public void addNewReply(Comment newReply, int position){

        pushReply(newReply);

        if(newReply != null){
            Log.d(TAG, "Refresh required! ");
            commentList.add(position+1,newReply);
            notifyDataSetChanged();
        }else{
            throw new IllegalArgumentException("Empty reply!");
        }
    }





//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
//        return new ViewHolder(view);
//
//    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        String replyTo = "@" + comment.getFirstName() + " ";
        ((ViewHolder) holder).firstName.setText(comment.getFirstName());
        ((ViewHolder) holder).commentContent.setText(comment.getCommentContent());
        ((ViewHolder) holder).createdDateTime.setText(comment.getDateTime());
        ((ViewHolder) holder).replyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "You clicked Reply!", Toast.LENGTH_SHORT).show();
                View commentView = LayoutInflater.from(view.getContext()).inflate(R.layout.event_comment_dialog, null);
                dialog = new BottomSheetDialog(commentView.getContext());
                EditText commentText = (EditText) commentView.findViewById(R.id.comment_dialog_textArea);
                Button comment_btn = (Button) commentView.findViewById(R.id.comment_dialog_reply_btn);
                commentText.setHint(replyTo);
                dialog.setContentView(commentView);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String replyContent = commentText.getText().toString().trim();
                        if (!TextUtils.isEmpty((replyContent))) {
                            dialog.dismiss();
                            currentUid = FirebaseAPI.getUser().getUid();

                            FirebaseAPI.getFirebaseDataOnce("User/" + currentUid, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    user = ((MainActivity) context).getUser();
                                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!"+user.getEmail());
                                    Comment newReply = new Comment(user, eventID,replyTo+replyContent);
                                    addNewReply(newReply, position);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }else{
                            Toast.makeText(view.getContext(), "Cannot reply nothing", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();

            }
        });
    }


    public void pushReply(Comment comment){
        String key = FirebaseAPI.pushFirebaseNode("EventCommentList/" + eventID);
        comment.setCommentID(key);
        comment.setCommentContent(comment.getCommentContent());
        comment.setFirstName(comment.getFirstName());
        comment.setDateTime(comment.getDateTime());
        comment.setProfileImgID(comment.getProfileImageId());
        comment.setUserID(comment.getUserID());

        FirebaseAPI.addComment(comment, key, eventID, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null){
//                    Snackbar.make(getView(),"Success!",Snackbar.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: The comment is pushed online");

                }
                else{
//                    Snackbar.make(getView(),error.getDetails(),Snackbar.LENGTH_SHORT).show();
                    Log.d(TAG, "mission failed!!!!!!");
                }


            }
        });


    }


//    Ideally this showCommentDialog should be a separate function, but I'm having trouble to pass the view.

//    public void showCommentDialog(View view){
//        View commentView = LayoutInflater.from(view.getContext()).inflate(R.layout.event_comment_dialog, null);
//        dialog = new BottomSheetDialog(commentView.getContext());
//        EditText commentText = (EditText) commentView.findViewById(R.id.comment_dialog_textArea);
//        Button comment_btn = (Button) commentView.findViewById(R.id.comment_dialog_reply_btn);
//        dialog.setContentView(commentView);
//        comment_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "You clicked Reply!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }

//    public FirebaseRecyclerAdapter<Comment, ViewHolder> commentAdapter(){
//        Query commentQuery = FirebaseAPI.rootRef.child("EventCommentList" + eventID);
//        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
//                .setQuery(commentQuery, Comment.class).build();
//
//        return new FirebaseRecyclerAdapter<Comment, ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment model) {
//                String commentID = getRef(position).getKey();
//                FirebaseAPI.getFirebaseData("EventCommentList/" + eventID + "/" + commentID, new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.hasChild("firstName")){
//                            holder.setFirstName(snapshot.child("firstName"));
//                        }
//                        if (snapshot.hasChild("commentContent")){
//                            holder.setCommentContent(snapshot.child("commentContent"));
//                        }
//                        if (snapshot.hasChild("profileImageID")){
//                            holder.setProfile(snapshot.child("profileImageID").toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };
//
//    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}


