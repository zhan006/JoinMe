package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.CommentAdapter;
import com.example.joinme.objects.Comment;
import com.example.joinme.reusableComponent.NavBar;

import java.util.ArrayList;
import java.util.List;

public class EventDetailFragment extends Fragment {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar)getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.event_details, container, false);
        initCommentList(v);
        return v;
    }

    public void initCommentList(View v){
        RecyclerView commentRecyclerView = v.findViewById(R.id.event_comments_area);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRecyclerView.setAdapter(new CommentAdapter(initComments()));
    }

    List<Comment> initComments(){
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Zhan", "Wang", R.drawable.host_profile_pic,"That sounds interesting!"));
        comments.add(new Comment("Lixian", "Sun", R.drawable.user_profile_pic_2,"So where shall we meetup?"));
        comments.add(new Comment("Rui", "Wang", R.drawable.user_profile_pic3,"What about Melbourne Central? You can't miss the clock!"));
        comments.add(new Comment("Zhan", "Wang",R.drawable.host_profile_pic, "Can we consider meeting up at Chinatown so we can go to the restaurant directly?"));
        comments.add(new Comment("Lixian", "Sun", R.drawable.user_profile_pic_2,"Agree!"));
        comments.add(new Comment("Rui", "Wang", R.drawable.user_profile_pic3,"I can't wait to have the grill pork! It was so good! Shall we go for milk tea afterwards?"));

        return comments;

    }
}
