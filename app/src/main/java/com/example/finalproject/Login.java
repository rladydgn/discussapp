package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    // 다른 액티비티에서 이 액티비티 종료 시키기 위해 사용
    public static Login loginActivity;

    // auth
    private FirebaseAuth mAuth;
    private TextView mEmail, mPassword;

    // logcat
    private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Table Talk");
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextView) findViewById(R.id.mEmail);
        mPassword = (TextView) findViewById(R.id.mPassword);

        loginActivity = Login.this;
    }

    // 사용자가 로그인 되어 있는지 확인
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        boolean logout = intent.getBooleanExtra("logout", true);

        if(!logout)
            return;

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // 로그인
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        // 로그인 형식 확인
        if (!validateForm()) {
            return;
        }


        // firebase에 로그인 정보 전달
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "아이디 또는 비밀번호가 틀렸습니다.", task.getException());
                            Toast.makeText(Login.this, "아이디 또는 비밀번호가 틀렸습니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        // email 이 비어있을경우
        String email = mEmail.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        }
        else {
            mEmail.setError(null);
        }

        // password 가 비어있을 경우
        String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        }
        else {
            mPassword.setError(null);
        }

        return valid;
    }

    // 로그인 성공했을때 호출되거나 맨 처음 호출됨
    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Toast.makeText(Login.this, "update UI",
                    Toast.LENGTH_SHORT).show();

            // realtime database 에 UId 폴더 까지 경로
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("userdata");
            String UId = user.getUid();
            DatabaseReference usersRef = ref.child("users/" + UId);

            Intent idintent = new Intent(this, ChooseIdPopupActivity.class);
            Intent mainintent = new Intent(this, MainActivity.class);

            // 아이디를 만들고(아이디가 있으면) MainActivity 호출
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // nickname이 database 에 존재 할 경우(회원가입 완료 + nickname 존재)
                    User nickname = dataSnapshot.getValue(User.class);
                    if(nickname != null) {
                        mainintent.putExtra("nickname", nickname.nickname);
                        Log.d(TAG, "nickname이 firebase에 있음");
                        startActivity(mainintent);
                        finish();
                    }
                    // nickname 이 database 에 없을경우(회원가입만 하고 nickname 미생성)
                    // 자동으로 id입력이 뜨는걸 방지하기 위해 mEamil 이 비어 있지 않아야 함.
                    else {
                        if(!TextUtils.isEmpty(mEmail.getText().toString())) {
                            Log.d(TAG, "nickname이 firebase에 없음");
                            startActivity(idintent);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    // 로그인 버튼 클릭
    public void onSignInButtonClicked(View v) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        signIn(email, password);
    }

    // 회원가입 버튼 클릭
    public void onSignUpButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}