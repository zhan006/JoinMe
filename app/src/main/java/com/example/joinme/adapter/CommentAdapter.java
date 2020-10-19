package com.example.joinme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.objects.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList){
        this.commentList = commentList;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView firstName, lastName, textContent;
        ImageView profilePhoto;
        public ViewHolder(View itemView){
            super(itemView);
            firstName = itemView.findViewById(R.id.comment_user_firstName);
            lastName = itemView.findViewById(R.id.comment_user_lastName);
            profilePhoto = itemView.findViewById(R.id.comment_user_photo);
            textContent = itemView.findViewById(R.id.event_comments_content);
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
        ((ViewHolder) holder).profilePhoto.setImageResource(comment.getProfileImageId());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}


