package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 투표율
    private TextView dayYes, dayMiddle, dayNo, topic;
    private TextView nickText;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // logcat
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Table Talk");
        setContentView(R.layout.activity_main);

        // 다른 액티비티 종료
        /*Login loginActivity = (Login) Login.loginActivity;
        loginActivity.finish();
        SignUpActivity signUpActivity = (SignUpActivity) SignUpActivity.signUpActivity;
        signUpActivity.finish();*/

        // textView
        topic = (TextView) findViewById(R.id.topic);
        // 투표율 100dp 기준 3등분 배분
        dayYes = (TextView) findViewById(R.id.dayYes);
        dayMiddle = (TextView) findViewById(R.id.dayMiddle);
        dayNo = (TextView) findViewById(R.id.dayNo);
        nickText = (TextView) findViewById(R.id.nickText);

        Intent intent = getIntent();

        String nickname = intent.getStringExtra("nickname");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        nickText.setText(nickname);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signOut() {
        // 로그아웃 정보 전달
        mAuth.signOut();
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("logout", false);
        startActivity(intent);
        finish();
    }

    // 로그아웃
    public void onLogoutButtonClicked(View v) {
        signOut();
    }

    private void updateUI(FirebaseUser user) {

        DatabaseReference ref = database.getReference("postData/fourteenBoy");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String UId = mAuth.getUid();


                // 투표수 불러오기
                Vote vote = snapshot.child("voteRate").getValue(Vote.class);
                if (vote != null) {

                    int yes = vote.getYesVote();
                    int middle = vote.getMiddleVote();
                    int no = vote.getNoVote();

                    // 비율 조정
                    int totalDp = 200;
                    int totalPx = dpToPx(totalDp);
                    double totalVote = yes + no + middle;
                    dayYes.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx * (yes / totalVote)))));
                    dayMiddle.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx * (middle / totalVote)))));
                    dayNo.setWidth(Integer.parseInt(String.valueOf(Math.round(totalPx * (no / totalVote)))));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "call vote failed");
            }
        });
    }

    private int dpToPx(int dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public void onDayButtonClicked(View v) {
        Intent intent = new Intent(this, DayTalkActivity.class);
        intent.putExtra("topic", topic.getText().toString());
        startActivity(intent);
    }
}