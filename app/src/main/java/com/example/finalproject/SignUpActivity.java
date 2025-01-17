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

public class SignUpActivity extends AppCompatActivity {

    // 다른 액티비티에서 이 액티비티 종료 시키기 위해 사용
    public static SignUpActivity signUpActivity;

    // auth
    private FirebaseAuth mAuth;

    private TextView mEmail, mPassword, mCheckPassword;

    //logcat
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Table Talk");
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextView) findViewById(R.id.email);
        mPassword = (TextView) findViewById(R.id.password);
        mCheckPassword = (TextView) findViewById(R.id.checkPassword);

        signUpActivity = SignUpActivity.this;
    }

    // email이 비었는지, password가 비었는지, password와 checkPassword가 다른지 확인
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        }
        else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        String checkPassword = mCheckPassword.getText().toString();

        if(TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        }
        else if(!password.equals(checkPassword)) {
            mPassword.setError("different password");
            mCheckPassword.setError("different password");
            valid = false;
        }
        else {
            mPassword.setError(null);
            mCheckPassword.setError(null);
        }
        return valid;
    }

    // firebase 에 계정 추가하기
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "이미 존재하는 이메일입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    // 회원가입 버튼 클릭
    public void onSignUpButtonClicked(View v) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        createAccount(email, password);
    }

    // 회원가입 완료시 아이디 생성 팝업
    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Toast.makeText(SignUpActivity.this, "updateUI @@",
                    Toast.LENGTH_SHORT).show();

            // id 생성 popup 창 이동
            String UId = user.getUid();
            Intent intent = new Intent(this, ChooseIdPopupActivity.class);
            intent.putExtra("UId", UId);
            startActivity(intent);
        }
    }
}