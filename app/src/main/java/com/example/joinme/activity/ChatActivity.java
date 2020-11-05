package com.example.joinme.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joinme.R;
import com.example.joinme.adapter.MessageAdapter;
import com.example.joinme.database.FirebaseAPI;
import com.example.joinme.objects.Conversation;
import com.example.joinme.objects.Message;
import com.example.joinme.objects.Time;
import com.example.joinme.objects.User;
import com.example.joinme.reusableComponent.TitleBar;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int GALLERY_PICK = 1;
    private static final int ACTIVITY_IMAGE_CAPTURE = 3;
    private List<Message> messageList = new ArrayList<>();
    private String friendUid;
    private String friendUsername;
    private String currentUid;
    private String currentUsername;

    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;

    private EditText inputText;
    private ImageButton sendBtn;
    private ImageButton imageButton;
    private ImageButton takePhotoBtn;
    private RecyclerView messageRecyclerView;

    private String currentPhotoPath;
    private FileInputStream is = null;
    private Bitmap bitmap;

    private ChildEventListener loadMsgListener;
    private ValueEventListener chatListener;
    private DatabaseReference messageListRef;
    private TitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        friendUid = getIntent().getStringExtra("friendUid");
        friendUsername = getIntent().getStringExtra("friendUsername");
        currentUsername = getIntent().getStringExtra("currentUsername");
        currentUid = FirebaseAPI.getUser().getUid();

        initView();
        initData();

        // monitor firebase messages
        loadMessages();
        markMessageAsSeen();

        // send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                // clear input text after sending message
                inputText.setText("");
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy ");
        messageListRef.removeEventListener(loadMsgListener);
        messageListRef.removeEventListener(chatListener);
        super.onDestroy();
    }

    private void initView() {
        messageListRef = FirebaseAPI.rootRef.child("Chat").child(currentUid).child(friendUid);
        // set friend username in title bar
        titleBar = findViewById(R.id.chat_title_bar);
        titleBar.setTitle(this.friendUsername);
        titleBar.setOnClickBackListener((v)-> {
            Log.d(TAG, "initView: ");
            messageListRef.removeEventListener(loadMsgListener);
            messageListRef.removeEventListener(chatListener);

            finish();
        });

        messageRecyclerView = findViewById(R.id.chat_message_list);
        inputText = findViewById(R.id.chat_input_message);
        sendBtn = findViewById(R.id.send_message_btn);
        imageButton = findViewById(R.id.message_send_image_btn);
        takePhotoBtn = findViewById(R.id.message_take_photo_btn);
    }

    private void initData() {
        // set up layout manager for message recycler view
        linearLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        //initMessages();

        // set up adapter for message recycler view
        messageAdapter = new MessageAdapter(messageList, this, currentUid, friendUid);
        messageRecyclerView.setAdapter(messageAdapter);
    }

    void add2Conversation(Time time) {

        // update conversation time and name to get correct order
        FirebaseAPI.rootRef.child("ConversationList").child(currentUid).child(friendUid)
                .child("time").setValue(time);
        FirebaseAPI.rootRef.child("ConversationList").child(currentUid).child(friendUid)
                .child("username").setValue(friendUsername);

        FirebaseAPI.rootRef.child("ConversationList").child(friendUid).child(currentUid)
            .child("time").setValue(time);
        FirebaseAPI.rootRef.child("ConversationList").child(friendUid).child(currentUid)
                .child("username").setValue(currentUsername);

//        FirebaseAPI.rootRef.child("ConversationList").child(currentUid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                // if chat user doesn't have any chat with current user, create one and add it to Firebase
////                if (!dataSnapshot.hasChild(friendUid)){
//                Log.d(TAG, "onDataChange: add friend chat branch"+friendUid);
//                Conversation currentConversation = new Conversation(time, friendUsername);
//                Conversation friendConversation = new Conversation(time, currentUsername);
//
//                Map conversations = new HashMap();
//                conversations.put("ConversationList/" + currentUid + "/" +friendUid, currentConversation);
//                conversations.put("ConversationList/" + friendUid + "/" + currentUid, friendConversation);
//
//                FirebaseAPI.updateBatchData(conversations, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                        if (databaseError != null){
//                            Log.d("CREATE_CONVERSATION", databaseError.getMessage().toString());
//                        }
//                    }
//                });
//
//            }
////            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    void loadMessages() {
        loadMsgListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageID = snapshot.getKey();
                Log.d(TAG, "onDataChange: message key => " + messageID);
                Message msg = snapshot.getValue(Message.class);

                Log.d(TAG, "onDataChange: datasnapshot message => " + msg.toString());
                messageList.add(msg);
                // notify adapter to update
                messageAdapter.notifyDataSetChanged();

                // make sure user can see latest message even when typing
                messageRecyclerView.scrollToPosition(messageList.size() - 1);

                // update last message time
                TextView dateTime = findViewById(R.id.chat_time);
                dateTime.setText(msg.getTime().toString());
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
        };
        messageListRef.addChildEventListener(loadMsgListener);
    }

    void markMessageAsSeen(){
        chatListener = new ValueEventListener() {
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
                Log.d(TAG, "onCancelled: cancel listener!");
            }
        };
        messageListRef.addValueEventListener(chatListener);
//        FirebaseAPI.getFirebaseData("Chat/" + this.currentUid + "/" +
//                this.friendUid, chatListener);
//        FirebaseAPI.rootRef.child("ConversationList").child(currentUid).child(friendUid).child("seen").setValue(true);
    }

    void sendMessage() {
        String sendText = inputText.getText().toString();

        // empty input cannot be send
        if (TextUtils.isEmpty(sendText)) {
            Toast.makeText(this, "Please enter text first...", Toast.LENGTH_SHORT).show();
        } else {
            String currentUserChatPath = "Chat/" + currentUid + "/" + friendUid;
            String friendChatPath = "Chat/" + friendUid + "/" + currentUid;

            // get unique message ID for current message
            String messageID = FirebaseAPI.pushFirebaseNode(currentUserChatPath);
            Time time = new Time();

            // create current message
            Message message = new Message(sendText, "text", currentUid, time, false);
            Log.d(TAG, "sendMessage: new time => "+ time.toString());

            // Push current message to both current user's chat and friend's chat path
            Map<String, Object> messagePush = new HashMap<>();
            Log.d(TAG, "sendMessage: message sent => "+message);
            messagePush.put(currentUserChatPath + "/" + messageID, message);
            messagePush.put(friendChatPath + "/" + messageID, message);

            DatabaseReference.CompletionListener batchCompletionListener = (error, ref) -> {
                if (error != null){
                    Log.d("SEND_CHAT_MESSAGE_ERROR", error.getMessage().toString());
                }
            };
            FirebaseAPI.updateBatchData(messagePush, batchCompletionListener);

            // and current chat to current user's conversation list
            add2Conversation(time);
        }
    }

    void sendImage() {

        // start an intent to select image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), GALLERY_PICK);
    }

    private void takePhoto() {
        /*
        functions when you click the take a photo button
        this function contains two version
        one for reduced-size picture
        another for the normal size picture
         */

        // this version is only for small picture
        // use it when you want to reduce picture size
//        Intent mIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//        startActivityForResult(mIntent, ACTIVITY_IMAGE_CAPTURE);

        // this version for the normal size picture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.w("False", "Create image file false at takePhoto()");
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {

            // get uri of the file
            // remember to add the permission
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.joinme.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            // start the camera of the phone
            startActivityForResult(takePictureIntent, ACTIVITY_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        /*
        create image file in the external space of the APP
         */

        // Create an image file name
        String imageFileName = "takePhoto";

        // get the save path for the image
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // create the image file
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //open image gallery in the phone and get crop image
        Uri imageUri=null;
        if(data!=null) imageUri = data.getData();
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        // after take a picture useing the camrea of the phone
        if (requestCode == ACTIVITY_IMAGE_CAPTURE && resultCode == RESULT_OK){

            // get the photo in bitmap type
            Log.d(TAG, "take a picture from phone camera");
            bitmap = BitmapFactory.decodeFile(currentPhotoPath);

            // double check if we can get the uri from the intent
            if (data.getData()!=null){
                imageUri =data.getData();
            }else {

                // get the image URI from the bitmap
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null,null));
            }

            // crop the image into appropriate size
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);


            // this is for the version 1 when you wanna get reduced size picture
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");
//            if (data.getData()!=null){
//                imageUri =data.getData();
//            }else {
//                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null,null));
//            }
//
//            CropImage.activity(imageUri)
//                    .setAspectRatio(1, 1)
//                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                Calendar now = Calendar.getInstance();

                final StorageReference filepath = FirebaseAPI.getStorageRef(
                        "chat_images/" + currentUid+"/"+String.valueOf(now.getTimeInMillis()));

                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] bytes = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(bytes);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: image upload Task");
//                        Toast.makeText(ChatActivity.this, "worked", Toast.LENGTH_SHORT).show();
                    }
                });

                // save storage url to realtime database
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUri = task.getResult().toString();

                            // under Chat node create current user node with chat uid and vise versa
                            String currentUserChatPath = "Chat/" + currentUid + "/" + friendUid;
                            String friendChatPath = "Chat/" + friendUid + "/" + currentUid;

                            // get unique message ID for current message
                            String messageID = FirebaseAPI.pushFirebaseNode(currentUserChatPath);
                            Time time = new Time();

                            // create current message
                            Message imgMessage = new Message(downloadUri, "image", currentUid, time, false);
                            Log.d(TAG, "sendMessage: new time => "+ time.toString());

                            // Push current message to both current user's chat and friend's chat path
                            Map<String, Object> messagePush = new HashMap<>();
                            messagePush.put(currentUserChatPath + "/" + messageID, imgMessage);
                            messagePush.put(friendChatPath + "/" + messageID, imgMessage);

                            DatabaseReference.CompletionListener batchCompletionListener = (error, ref) -> {
                                if (error != null){
                                    Log.d("SEND_IMAGE_MSG_ERROR", error.getMessage().toString());
                                }
                            };
                            FirebaseAPI.updateBatchData(messagePush, batchCompletionListener);

                            // and current chat to current user's conversation list
                            add2Conversation(time);
                        } else {
                            Toast.makeText(ChatActivity.this, "Please write messages First... ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }
    }


}