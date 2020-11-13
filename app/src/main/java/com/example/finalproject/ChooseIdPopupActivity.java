package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseIdPopupActivity extends AppCompatActivity {

    private TextView idEdit;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_id_popup);

        // 타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        idEdit = (TextView) findViewById(R.id.idEdit);

        DatabaseReference myRef = database.getReference("message/test/what");

        // 데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("UId");
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
       return;
    }
}