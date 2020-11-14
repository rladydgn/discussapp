package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChooseIdPopupActivity extends Activity {

    // auth
    private FirebaseAuth mAuth;

    //logcat
    private static final String TAG = "ChooseIdPopupActivity";

    private TextView idEdit;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_id_popup);

        idEdit = (TextView) findViewById(R.id.idEdit);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // (UId)데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("UId");

        // firebase realtime database에 uid와 id 매칭하기 위한 path
        DatabaseReference myRef = database.getReference("UID/" + data + "/");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 레이어 클릭해도 안닫힘
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼 눌러도 안닫힘힘
        Toast.makeText(ChooseIdPopupActivity.this, "뒤로갈 수 없습니다.",
                Toast.LENGTH_SHORT).show();
       return;
    }

    private boolean validateForm() {
        boolean valid = true;

        String mIdEdit = idEdit.getText().toString();
        Log.d(TAG, mIdEdit.length() + "yy");
        if(TextUtils.isEmpty(mIdEdit)) {
            idEdit.setError("아이디를 입력하세요");
            Toast.makeText(ChooseIdPopupActivity.this, "아이디를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(mIdEdit.length() < 2 || mIdEdit.length() > 10) {
            idEdit.setError("2~10자");
            Toast.makeText(ChooseIdPopupActivity.this, "아이디는 2~10자 사이 이어야 합니다.",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {
            idEdit.setError(null);
        }
        return valid;
    }

    public void onIdButtonClicked(View v) {
        Log.d(TAG, "TTTT");
        if(!validateForm()) {
            Log.d(TAG, "PASS");
            return;
        }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UId = currentUser.getUid();

        // 유저정보를 firebase realtime database 에 저장
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("userdata");
        DatabaseReference usersRef = ref.child("users/" + UId);


        // uid와 nickname 저장
        User user = new User(idEdit.getText().toString());
        usersRef.setValue(user);
        /*Map<String, User> users = new HashMap<>();
        users.put(data, new User(idEdit.getText().toString()));
        usersRef.setValue(users);*/

        // MainActivity 로 이동
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("nickname", idEdit.getText().toString());
        startActivity(mainIntent);

        // 실행중인 다른 액티비티 종료
        /*Login loginActivity = (Login) Login.loginActivity;
        loginActivity.finish();
        SignUpActivity signUpActivity = (SignUpActivity) SignUpActivity.signUpActivity;
        signUpActivity.finish();*/
        finish();
    }

}