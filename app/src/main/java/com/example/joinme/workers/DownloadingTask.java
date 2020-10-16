package com.example.joinme.workers;

import android.os.Handler;

import com.example.joinme.database.FirebaseAPI;
import com.google.firebase.database.ValueEventListener;
public class DownloadingTask implements Runnable{
    private Handler handler;
    String path;
    ValueEventListener listener;
    public DownloadingTask(Handler handler, String path, ValueEventListener listener){
        this.handler = handler;
        this.path = path;
        this.listener = listener;
    }

    @Override
    public void run() {
        FirebaseAPI.getFirebaseData(this.path,this.listener);
    }
}
