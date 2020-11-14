package com.example.finalproject;

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

    private TextView textEdit;
    // 투표율
    private TextView dayYes, dayMiddle, dayNo;

    private FirebaseAuth mAuth;

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

        textEdit = (TextView) findViewById(R.id.textEdit);
        // 투표율 100dp 기준 3등분 배분
        dayYes = (TextView) findViewById(R.id.dayYes);
        dayMiddle = (TextView) findViewById(R.id.dayMiddle);
        dayNo = (TextView) findViewById(R.id.dayNo);

        Intent intent = getIntent();

        String nickname = intent.getStringExtra("nickname");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textEdit.setText(nickname + " " + currentUser.getUid());

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
        int totalPx = dpToPx(200);
        dayYes.setWidth(totalPx/3);
        dayMiddle.setWidth(totalPx/3);
        dayNo.setWidth(totalPx/3);
        Log.d(TAG, Math.round(totalPx/3) + " " + totalPx);
    }

    private int dpToPx(int dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}