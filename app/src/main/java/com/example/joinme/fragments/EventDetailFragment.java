package com.example.joinme.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joinme.MainActivity;
import com.example.joinme.R;
import com.example.joinme.activity.ChatActivity;
import com.example.joinme.adapter.CommentAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Comment;
import com.example.joinme.objects.Event;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.NavBar;
import com.example.joinme.reusableComponent.TitleBar;
import com.example.joinme.utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EventDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EventDetailFragment";
    private String uid, eventOrganizerUID, currentEventID, hostName;
    private User user;
    private CheckBox going;
    private ImageButton hostProfileBtn, followOrganiser, messageOrganiser;
    private ImageView hostProfile;
    private TextView eventDate, eventName, eventLocation, eventDateTime, eventGroupSize,
            eventCurrentParticipants, eventHost, eventDetails, eventHostName, eventHostAbout;
    private long count;
    private Button makeComment;
    private ArrayList<Comment> commentList = new ArrayList<>();
    private BottomSheetDialog dialog;
    private RecyclerView commentRecyclerView;
    private CommentAdapter addCommentAdapter;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int prev = ((NavBar) getActivity().findViewById(R.id.navbar)).getPrevSelected();
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_in));
        setExitTransition(inflater.inflateTransition(R.transition.slide_out));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.event_details, container, false);
        uid = ((MainActivity) getActivity()).getUid();


        TitleBar bar = v.findViewById(R.id.event_title_bar);
        bar.setOnClickBackListener((view)->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        setButtons(v);
        initEventDetail(v);
        initCommentList(v);


        going.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean going) {
                if (going) {

                    FirebaseAPI.rootRef.child("AttendingList").child(uid).child(currentEventID).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "AttendingList is updated", Toast.LENGTH_SHORT).show();

                        }
                    });

                    FirebaseAPI.rootRef.child("EventMember").child(currentEventID).child(uid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "EventMember list is updated!", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    FirebaseAPI.rootRef.child("AttendingList").child(uid).child(currentEventID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Removed from AttendingList!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    FirebaseAPI.rootRef.child("EventMember").child(currentEventID).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Removed from EventMember List!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        return v;
    }


    public void setButtons(View v){
        eventDate = (TextView) v.findViewById(R.id.event_date);
        eventName = (TextView) v.findViewById(R.id.event_name);
        going = (CheckBox) v.findViewById(R.id.event_checkbox_going);
        eventLocation = (TextView) v.findViewById(R.id.event_location_text);
        eventDateTime = (TextView) v.findViewById(R.id.event_datetime_text);
        eventGroupSize = (TextView) v.findViewById(R.id.event_groupsize_text);
        eventCurrentParticipants = (TextView) v.findViewById(R.id.event_participants_number);
        eventHost = (TextView) v.findViewById(R.id.event_host_text);

        hostProfileBtn = (ImageButton) v.findViewById(R.id.event_organizer_profile_btn);
//        organiserProfile.setOnClickListener(this);

        followOrganiser = (ImageButton) v.findViewById(R.id.event_follow_btn);
        followOrganiser.setOnClickListener(this);

        messageOrganiser = (ImageButton) v.findViewById(R.id.event_message_icon);
        messageOrganiser.setOnClickListener(this);

        eventDetails = (TextView) v.findViewById(R.id.event_details);

        eventHostName = (TextView) v.findViewById(R.id.event_host_name);
        eventHostAbout = (TextView) v.findViewById(R.id.event_host_about);
        hostProfile = (ImageView) v.findViewById(R.id.host_profile_photo);


        makeComment = (Button) v.findViewById(R.id.event_details_comment_btn);
        makeComment.setOnClickListener(this);


        commentRecyclerView = v.findViewById(R.id.event_comments_area);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.event_follow_btn:

                followOrganiser.setImageResource(R.drawable.tick_icon);
                FirebaseAPI.rootRef.child("UserFollowing").child(uid).child(eventOrganizerUID).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Congrats! You have followed user " + eventOrganizerUID, Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseAPI.rootRef.child("FollowingUser").child(eventOrganizerUID).child(uid).setValue(true);

                break;

            case R.id.event_organizer_profile_btn:
//                ProfileFragment f = new ProfileFragment();
//                Bundle bd = new Bundle();
//                bd.putSerializable("user", getOrganiser());
//                f.setArguments(bd);
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                utils.replaceFragment(fm, f, "organiser_profile");
                break;

            case R.id.event_message_icon:
                // jump to chat activity
                Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
                chatActivity.putExtra("friendUid", eventOrganizerUID);
                chatActivity.putExtra("friendUsername", hostName);
                startActivity(chatActivity);
                break;


            case R.id.event_details_comment_btn:

                View commentView = LayoutInflater.from(view.getContext()).inflate(R.layout.event_comment_dialog, null);
                dialog = new BottomSheetDialog(commentView.getContext());
                EditText commentText = (EditText) commentView.findViewById(R.id.comment_dialog_textArea);
                Button comment_btn = (Button) commentView.findViewById(R.id.comment_dialog_reply_btn);

                dialog.setContentView(commentView);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                dialog.show();
                comment_btn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        String replyContent = commentText.getText().toString().trim();
                        if (!TextUtils.isEmpty((replyContent))) {
                            dialog.dismiss();
                            user = ((MainActivity) getActivity()).getUser();
                            Comment newComment = new Comment(user, uid,currentEventID,replyContent);
//                            addCommentAdapter.addNewComment(newComment);
                            createComment(newComment);
                            Toast.makeText(getActivity(), "Comment successfully!",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(view.getContext(), "Cannot reply nothing", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            default:
                break;
        }
    }



    public void createComment(Comment comment){
        String key = FirebaseAPI.pushFirebaseNode("EventCommentList/"+currentEventID);
        comment.setCommentID(key);
        comment.setCommentContent(comment.getCommentContent());
        comment.setFirstName(comment.getFirstName());
        comment.setDateTime(comment.getDateTime());
        comment.setProfileImgID(comment.getProfileImageId());
        comment.setUserID(comment.getUserID());
        FirebaseAPI.addComment(comment, key, currentEventID, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null){
                    Snackbar.make(getView(),"Success!",Snackbar.LENGTH_SHORT).show();

                }
                else{
                    Snackbar.make(getView(),error.getDetails(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initCommentList(View v) {
        String path = "EventCommentList/" + currentEventID;
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAPI.rootRef.child(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comment comment = snapshot.getValue(Comment.class);
                if (comment != null){
                    commentList.add(comment);
                    addCommentAdapter = new CommentAdapter(getActivity(), currentEventID, commentList);
                    commentRecyclerView.setAdapter(addCommentAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    }


    public void initEventDetail(View view) {
        Event currentEvent = (Event) getArguments().getSerializable("current_event");
        currentEventID = currentEvent.getId();
        eventOrganizerUID = currentEvent.getOrganizerid();
        eventName.setText(currentEvent.getEventName());
        eventDate.setText(currentEvent.getDatetime().getDate());
        eventLocation.setText(currentEvent.getLocation().getAddress());
        eventDateTime.setText(currentEvent.getDatetime().toString());
        eventGroupSize.setText("Group size " + currentEvent.getMin() + "-" + currentEvent.getMax() + " ppl");
        eventDetails.setText(currentEvent.getDescription());
        setGoingButton();
        setEventCurrentParticipants();
        setHostName();
        setHostSection();
    }

    public void setGoingButton(){
        FirebaseAPI.rootRef.child("EventMember").child(currentEventID).addValueEventListener(
                new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String,Boolean> map = (HashMap)snapshot.getValue();
                        if(map!=null && map.getOrDefault(uid,false)){
                            going.setChecked(true);
                        }
                        Log.d("event detail",snapshot.toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    public void setEventCurrentParticipants(){
        FirebaseAPI.getFirebaseData("EventMember/" + currentEventID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                eventCurrentParticipants.setText("Current Participants: " + count + " ppl");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setHostName(){
        FirebaseAPI.getFirebaseData("User/" + eventOrganizerUID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hostName = snapshot.child("username").getValue().toString();
                eventHost.setText("Hosted by "+ hostName);
                eventHostName.setText(hostName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setHostSection(){
        FirebaseAPI.getFirebaseData("User/" + eventOrganizerUID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User host = snapshot.getValue(User.class);
                String about = host.getAbout();
                String image = snapshot.child("profileImage").getValue().toString();
                if (!about.equals("null")){
                    eventHostAbout.setText(host.getAbout());
                }
                if (!image.equals("null")){
                    Glide.with(getContext()).load(image).into(hostProfile);
                }

                hostProfileBtn.setOnClickListener(view -> {
                    visitorProfileFragment f = new visitorProfileFragment(eventOrganizerUID);
                    Bundle bd = new Bundle();
                    bd.putSerializable("host", host);
                    f.setArguments(bd);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    utils.replaceFragment(fm, f, "null");
                    Log.d(TAG, "I'm passing an User object" + host.getEmail());

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseAPI.getFirebaseData("UserFollowing/" + uid, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean followingHost = snapshot.hasChild(eventOrganizerUID);

                if(followingHost){
                    followOrganiser.setImageResource(R.drawable.tick_icon);
                    followOrganiser.setVisibility(View.GONE);
                }else{
                    followOrganiser.setImageResource(R.drawable.add_icon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
