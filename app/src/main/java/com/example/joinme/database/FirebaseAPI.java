package com.example.joinme.database;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates methods for accessing Firebase
 */
public class FirebaseAPI {

    private static final String TAG = "FirebaseAPI";

    // root
    public static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    // User
    public static DatabaseReference userRef = rootRef.child("User");

    // Firebase auth
    public static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static void getFirebaseData(String nodePath, ValueEventListener valueEventListener) {
        DatabaseReference dbRef = rootRef.child(nodePath);
        // Read from the database
        dbRef.addValueEventListener(valueEventListener);
    }
    public static void getFirebaseDataOnce(String nodePath, ValueEventListener valueEventListener) {
        DatabaseReference dbRef = rootRef.child(nodePath);
        // Read from the database
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }
    public static Task<Void> setFirebaseData(String nodePath, String newData) {
        return rootRef.child(nodePath).setValue(newData);
    }

    /**
     * Push to a given firebase node and get unique push id
     * @param nodePath = node path
     * @return unique push id
     */
    public static String pushFirebaseNode(String nodePath) {
        return rootRef.child(nodePath).push().getKey();
    }

    /**
     * Given node paths and data, update batch data at once
     * @param batchData = Map object with nodePath being key
     * @param completionListener = completion logic after updating data
     */
    public static void updateBatchData(Map<String, Object> batchData,
                                       DatabaseReference.CompletionListener completionListener) {
        rootRef.updateChildren(batchData, completionListener);
    }


    public static Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email,password);
    }

    public static Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public static FirebaseUser getUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "getUser UID : " + currentUser.getUid());
        return currentUser;
    }

    public static StorageReference getStorageRef(String nodePath) {
        return storageRef.child(nodePath);
    }



}
