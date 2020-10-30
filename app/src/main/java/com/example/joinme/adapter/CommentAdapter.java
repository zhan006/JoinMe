package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.objects.Comment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter{

    private List<Comment> commentList;
    private BottomSheetDialog dialog;

    public CommentAdapter(List<Comment> commentList){
        this.commentList = commentList;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView firstName, lastName, textContent, upvoteCount, replyButton, createdDateTime;
        ImageView profilePhoto;
        ImageButton upvoteButton;
        public ViewHolder(View itemView){
            super(itemView);
            firstName = itemView.findViewById(R.id.comment_user_firstName);
            lastName = itemView.findViewById(R.id.comment_user_lastName);
            profilePhoto = itemView.findViewById(R.id.comment_user_photo);
            textContent = itemView.findViewById(R.id.event_comments_content);
            upvoteCount = itemView.findViewById(R.id.event_comments_upvote_count);
            upvoteButton = itemView.findViewById(R.id.event_comments_upvote_icon);
            replyButton = itemView.findViewById(R.id.comment_reply_btn);
            createdDateTime = itemView.findViewById(R.id.comment_createdDateTime);



        }



    }

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
        ((ViewHolder) holder).firstName.setText(comment.getFirstName());
        ((ViewHolder) holder).lastName.setText(comment.getLastName());
        ((ViewHolder) holder).textContent.setText(comment.getCommentContent());
        ((ViewHolder) holder).upvoteCount.setText(String.valueOf(comment.getUpvoteCount()));
        ((ViewHolder) holder).createdDateTime.setText(comment.getTimeCreated());
        ((ViewHolder) holder).upvoteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                comment.setUpvoteCount(comment.getUpvoteCount()+1);
                ((ViewHolder) holder).upvoteCount.setText(String.valueOf(comment.getUpvoteCount()));
            }
        });

        ((ViewHolder) holder).replyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "You clicked Reply!", Toast.LENGTH_SHORT).show();
                View commentView = LayoutInflater.from(view.getContext()).inflate(R.layout.event_comment_dialog, null);
                dialog = new BottomSheetDialog(commentView.getContext());
                EditText commentText = (EditText) commentView.findViewById(R.id.comment_dialog_textArea);
                Button comment_btn = (Button) commentView.findViewById(R.id.comment_dialog_reply_btn);
                dialog.setContentView(commentView);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//                comment_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String replyContent = commentText.getText().toString().trim();
//                        if (!TextUtils.isEmpty((replyContent))) {
//                            dialog.dismiss();
//                            Comment commentReply = new Comment();
//
//
//
//                        }else{
//                            Toast.makeText(view.getContext(), "Cannot reply nothing", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                });






                dialog.show();



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

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}


