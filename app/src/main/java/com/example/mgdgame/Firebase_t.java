package com.example.mgdgame;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Firebase_t {

    private final FirebaseDatabase FIREBASE_DB = FirebaseDatabase.getInstance();
    private static final String FILE_PATH = "Score";
    private final DatabaseReference DATABASE_REF = FIREBASE_DB.getReference();
    private List<Score> mListHighscores = new ArrayList<>();
    private static Firebase_t firebase_instance = null;

    private Firebase_t() {}
    public static Firebase_t getInstance() {

        if(null == firebase_instance) firebase_instance = new Firebase_t();

        return firebase_instance;

    }

    public void add(Score argScore) {

        DATABASE_REF.child(FILE_PATH).push().setValue(argScore);

    }



    public List<Score> getAll() {

        return null;

    }

}
