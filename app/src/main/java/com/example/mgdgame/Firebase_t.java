package com.example.mgdgame;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Firebase_t {

    private final FirebaseDatabase FIREBASE_DB = FirebaseDatabase.getInstance();
    private static final String FILE_PATH = "Score";
    private final DatabaseReference DATABASE_REF = FIREBASE_DB.getReference().child(FILE_PATH);
    private static List<Score> mListHighscores = new ArrayList<>();

    public Firebase_t() {

        DATABASE_REF.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            getAll((Map<String, Object>) snapshot.getValue());

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
        });
    }

    public void add(Score argScore) {

        DATABASE_REF.push().setValue(argScore);

    }
    private void getAll(Map<String, Object> argHighscores) {

        List<Score> lsScores = new ArrayList<>();

        for(Map.Entry<String, Object> entry : argHighscores.entrySet()) {

            Map lsUser = (Map)entry.getValue();

            lsScores.add(new Score(Objects.requireNonNull(lsUser.get("username")).toString(),
                    Integer.parseInt(Objects.requireNonNull(lsUser.get("score")).toString())));

        }

        mListHighscores = lsScores;

    }
    public List<Score> getScores() {return mListHighscores;}

}
