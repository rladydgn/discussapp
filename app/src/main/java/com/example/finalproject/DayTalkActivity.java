package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class DayTalkActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView topic;
    private TextView yesCount, noCount, middleCount;
    private ImageButton yesButton, noButton, middleButton;
    private TextView yesRate, noRate, middleRate;
    private TextView mEdit;
    private LinearLayout textContainer;
    private RadioButton yesCheck, middleCheck, noCheck;
    private String nickname;

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
        setTitle("table talk");
        setContentView(R.layout.activity_day_talk);

        // text
        topic = (TextView) findViewById(R.id.topic);
        yesCount = (TextView) findViewById(R.id.yesCount);
        noCount = (TextView) findViewById(R.id.noCount);
        middleCount = (TextView) findViewById(R.id.middleCount);
        yesRate = (TextView) findViewById(R.id.yesRate);
        noRate = (TextView) findViewById(R.id.noRate);
        middleRate = (TextView) findViewById(R.id.middleRate);
        mEdit = (TextView) findViewById(R.id.mEdit);

        // buttons
        yesButton = (ImageButton) findViewById(R.id.yesButton);
        yesButton.setOnClickListener(this);
        noButton = (ImageButton) findViewById(R.id.noButton);
        noButton.setOnClickListener(this);
        middleButton = (ImageButton) findViewById(R.id.middleButton);
        middleButton.setOnClickListener(this);
        yesCheck = (RadioButton) findViewById(R.id.yesCheck);
        middleCheck = (RadioButton) findViewById(R.id.middleCheck);
        noCheck = (RadioButton) findViewById(R.id.noCheck);

        // layouts
        textContainer = (LinearLayout) findViewById(R.id.textContainer);

        Intent inIntent = getIntent();
        topic.setText(inIntent.getStringExtra("topic"));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // initialize subThread
        BackgroundThread thread = new BackgroundThread();
        thread.start();
        CommentBackgroundThread commentThread = new CommentBackgroundThread();
        commentThread.start();

        // get nickname
        String UId = mAuth.getUid();
        DatabaseReference ref = database.getReference("userdata/users/" + UId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                nickname = user.nickname;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getNickname failed");
            }
        });

        // 키 리스너
        mEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER) {

                        // 다른사람 댓글 썻을 수도 있으니까 댓글창 새로고침
                        CommentBackgroundThread commentThread = new CommentBackgroundThread();
                        commentThread.start();

                        String content = mEdit.getText().toString();
                        TextView text1 = setTextView(content);
                        // 닉네임 저장
                        text1.setText(nickname + "\n" + content);
                        mEdit.setText("");

                        // 텍스트뷰 배경색, 의견에 따라 색갈 변형
                        String vote = null;
                        if(yesCheck.isChecked()) {
                            text1.setBackgroundColor(Color.parseColor("#32a4ff"));
                            vote = "0";
                        }
                        else if(middleCheck.isChecked()) {
                            text1.setBackgroundColor(Color.parseColor("#d3d3d3"));
                            vote = "1";
                        }
                        else if(noCheck.isChecked()) {
                            text1.setBackgroundColor(Color.parseColor("#ffc0cb"));
                            vote = "2";
                        }

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 20, 20, 20);
                        lp.gravity = 5;
                        text1.setLayoutParams(lp);
                        textContainer.addView(text1);

                        // firebase에 저장
                        String UId = mAuth.getUid();
                        Date time = new Date();
                        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss:SS");
                        Comment comment = new Comment(text1.getText().toString(),
                                format1.format(time), UId, vote);
                        DatabaseReference ref = database.getReference("postData/fourteenBoy/comments/" + format1.format(time));
                        DatabaseReference orderRef = database.getReference("postData/fourteenBoy/comments/");
                        orderRef.orderByChild("comment/timeOrder");
                        ref.setValue(comment);

                        return true;
                    }
                }
                return false;
            }
        });
    } // end of onCreate

/*    @Override
    protected void onStart() {
        super.onStart();

        updateUI();
    }*/

    // 댓글 양식
    private TextView setTextView(String text) {

        // 엔터 누르면 텍스트 하나 생성하고 댓글창에 텍스트 추가, firebase에 저장
        Log.d(TAG,  "test success");
        // 텍스트뷰 생성
        TextView text1 = new TextView(getApplicationContext());
        int sp = spToPx(8);
        Log.d(TAG, sp + "");
        text1.setTextSize(sp);
        text1.setTextColor(Color.BLACK);
        text1.setMaxEms(2000);

        text1.setText(text);

        return text1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int yes = Integer.parseInt(yesCount.getText().toString());
        int no = Integer.parseInt(noCount.getText().toString());
        int middle = Integer.parseInt(middleCount.getText().toString());

        // 현재 앱 이용중인 유저
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UId = mAuth.getUid();

        DatabaseReference ref = database.getReference("postData/fourteenBoy/voteRate");
        DatabaseReference URef = database.getReference("postData/fourteenBoy/" + UId);

        if(id == R.id.yesButton) {
            yes += 1;
        }
        else if(id == R.id.middleButton) {
            middle += 1;
        }
        else if(id == R.id.noButton) {
            no += 1;
        }
        // 버튼 비활성화
        yesButton.setEnabled(false);
        middleButton.setEnabled(false);
        noButton.setEnabled(false);

        // 저장할 객체
        Vote vote = new Vote(yes+middle+no, yes, middle, no);

        // 변경값 저장
        ref.setValue(vote);
        URef.setValue(UId);

        // 변화값 불러오기
        BackgroundThread thread = new BackgroundThread();
        thread.start();
    }


    /*private void updateUI() {
    }*/

    private int dpToPx(int dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private int spToPx(int sp) {
        Resources resources = this.getResources();
        float metrics = resources.getDisplayMetrics().scaledDensity;
        int px = Math.round(sp * metrics);
        return px;
    }

    // 댓글 불러오기
    class CommentBackgroundThread extends Thread {
        public void run() {
            DatabaseReference ref = database.getReference("postData/fourteenBoy/comments");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 댓글 불러오기
                    textContainer.removeAllViews();
                    for(DataSnapshot data : snapshot.getChildren()) {
                        Comment comment = data.getValue(Comment.class);
                        if(comment != null) {
                            TextView text1 = setTextView(comment.content);

                            // 텍스트뷰 배경색, 의견에 따라 색갈 변형
                            String vote = null;
                            if(comment.vote.equals("0"))
                                text1.setBackgroundColor(Color.parseColor("#32a4ff"));
                            else if(comment.vote.equals("1"))
                                text1.setBackgroundColor(Color.parseColor("#d3d3d3"));
                            else if(comment.vote.equals("2"))
                                text1.setBackgroundColor(Color.parseColor("#ffc0cb"));

                            String UId = mAuth.getUid();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(20, 20, 20, 20);
                            if(UId.equals(comment.UId))
                                lp.gravity = 5;
                            text1.setLayoutParams(lp);
                            textContainer.addView(text1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "comments call failed");
                }
            });
        }
    }

    // 투표수 백그라운드로 불러옴
    class BackgroundThread extends Thread {

        public void run() {
            // firebase 에서 데이터 불러오기
            DatabaseReference ref = database.getReference("postData/fourteenBoy");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String UId = mAuth.getUid();


                    // 투표수 불러오기
                    Vote vote = snapshot.child("voteRate").getValue(Vote.class);
                    if(vote != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                yesCount.setText(Integer.toString(vote.getYesVote()));
                                middleCount.setText(Integer.toString(vote.getMiddleVote()));
                                noCount.setText(Integer.toString(vote.getNoVote()));

                                int yes = vote.getYesVote();
                                int middle = vote.getMiddleVote();
                                int no = vote.getNoVote();

                                // 비율 조정
                                int totalDp = 360;
                                int totalPx = dpToPx(totalDp);
                                double totalVote = yes+no+middle;
                                yesRate.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx*(yes/totalVote)))));
                                middleRate.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx*(middle/totalVote)))));
                                noRate.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx*(no/totalVote)))));
                            }
                        });
                    }

                    // UId 폴더가 존재할 경우 이미 투표 했으므로 재투표 금지.
                    String fireUId = snapshot.child(UId).getValue(String.class);
                    Log.d(TAG, "UId test" + fireUId);
                    if(fireUId != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                yesButton.setEnabled(false);
                                middleButton.setEnabled(false);
                                noButton.setEnabled(false);
                            }
                        });
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