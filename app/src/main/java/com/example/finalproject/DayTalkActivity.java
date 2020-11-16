package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DayTalkActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView topic;
    private TextView yesCount, noCount, middleCount;
    private ImageButton yesButton, noButton, middleButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // 서브스레드에서 메인스레드로 UI변경 전달
    Handler handler = new Handler();

    // logcat
    private static final String TAG = "DayTalkActivity";

    // auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_talk);

        // text
        topic = (TextView) findViewById(R.id.topic);
        yesCount = (TextView) findViewById(R.id.yesCount);
        noCount = (TextView) findViewById(R.id.noCount);
        middleCount = (TextView) findViewById(R.id.middleCount);

        // buttons
        yesButton = (ImageButton) findViewById(R.id.yesButton);
        yesButton.setOnClickListener(this);
        noButton = (ImageButton) findViewById(R.id.noButton);
        noButton.setOnClickListener(this);
        middleButton = (ImageButton) findViewById(R.id.middleButton);
        middleButton.setOnClickListener(this);

        Intent inIntent = getIntent();
        topic.setText(inIntent.getStringExtra("topic"));

        // initialize subThread
        BackgroundThread thread = new BackgroundThread();
        thread.start();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        DatabaseReference ref = database.getReference("postData/fourteenBoy/vote");

        // 버튼 비활성화
        yesButton.setEnabled(false);
        middleButton.setEnabled(false);
        noButton.setEnabled(false);

        // 저장된 값 불러오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UId = mAuth.getUid();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // firebase에 저장된 Vote class 불러오기
                Vote vote = snapshot.getValue(Vote.class);
                if(vote == null) {
                    ArrayList<String> UIdList = new ArrayList<String>();
                    UIdList.add(UId);
                    Vote newVote = new Vote(0, 0, 0, 0, UIdList);

                    if(id == R.id.yesButton)
                        newVote.plusYesVote();
                    else if (id == R.id.middleButton)
                        newVote.plusMiddleVote();
                    else if (id == R.id.noButton)
                        newVote.plusNoVote();
                    newVote.plusTotalVote();

                    // 새로운 객체 firebase에 저장
                    ref.setValue(newVote);
                }
                else {
                    int totalVote = vote.getTotalVote();
                    int yesVote = vote.getYesVote();
                    int middleVote = vote.getMiddleVote();
                    int noVote = vote.getNoVote();
                    ArrayList<String> UIdList = vote.getUidList();
                    UIdList.add(UId);
                    Log.d(TAG, UIdList.get(0));

                    // 새로운 vote 클래스 객체 생성
                    Vote newVote = new Vote(totalVote, yesVote, middleVote, noVote, UIdList);
                    if (id == R.id.yesButton)
                        newVote.plusYesVote();
                    else if (id == R.id.middleButton)
                        newVote.plusMiddleVote();
                    else if (id == R.id.noButton)
                        newVote.plusNoVote();
                    newVote.plusTotalVote();

                    // 새로운 객체 firebase에 저장
                    ref.setValue(newVote);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "in onClick call canceled");
            }
        });

        // 새로 저장한 후 저장한 값 불러오기
        BackgroundThread thread = new BackgroundThread();
        thread.start();
    }

    private void updateUI(FirebaseUser user) {

    }

    // 댓글, 투표수 등 백그라운드로 불러옴
    class BackgroundThread extends Thread {

        public void run() {
            DatabaseReference ref = database.getReference("postData/fourteenBoy/vote");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Vote vote = snapshot.getValue(Vote.class);
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String UId = mAuth.getUid();
                    if(vote != null) {
                        yesCount.setText(Integer.toString(vote.getYesVote()));
                        middleCount.setText(Integer.toString(vote.getMiddleVote()));
                        noCount.setText(Integer.toString(vote.getNoVote()));
                        // 버튼을 클릭한 이력이 있을 시 버튼 비활성화
                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        ArrayList<String> UIdList = snapshot.child("uidList").getValue(t);
                        //Log.e(TAG, UIdList.get(0) + " @@ " + UId);
                        if(UIdList.contains(UId)){
                            // 버튼 비활성화
                            yesButton.setEnabled(false);
                            middleButton.setEnabled(false);
                            noButton.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "in thread, read failed :" + error.getCode());
                }
            });
        }
    }
}