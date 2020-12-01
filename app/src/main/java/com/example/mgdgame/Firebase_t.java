package com.example.mgdgame;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Firebase_t {

    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private static final String FILE_PATH = "Highscores";
    private DatabaseReference mDatabaseRef = mFirebaseDB.getReference().child(FILE_PATH);
    private List<Score> mListHighscores = new ArrayList<>();
    private static Firebase_t firebase_instance = null;

    private Firebase_t() {}
    public static Firebase_t getInstance() {

        if(null == firebase_instance) firebase_instance = new Firebase_t();

        return firebase_instance;

    }

    public void add(String argUsername, String argScore) {

        mDatabaseRef.push().setValue(argScore);

    }



    public List<Score> getAll() {

        return null;

    }

}
