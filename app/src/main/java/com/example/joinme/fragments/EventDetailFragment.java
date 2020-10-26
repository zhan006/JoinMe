package com.example.joinme.fragments;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.CommentAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Comment;
import com.example.joinme.reusableComponent.NavBar;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class EventDetailFragment extends Fragment{
    private static final String TAG = "EventDetailFragment";

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
        CheckBox going = (CheckBox) v.findViewById(R.id.event_checkbox_going);

        going.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean going) {
                if(going){
                    FirebaseAPI.rootRef.child("AttendingList").child("dVPWSkIeVHT3SPDfSMYPbAf52Pz2").child("-MKENdIe97l5y0t00Poa").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Congrats! You are going!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    FirebaseAPI.rootRef.child("AttendingList").child("dVPWSkIeVHT3SPDfSMYPbAf52Pz2").child("-MKENdIe97l5y0t00Poa").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "You have successully withdrawn from the attending list", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        initCommentList(v);
        return v;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
