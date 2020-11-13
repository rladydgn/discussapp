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

public class Login extends AppCompatActivity {

    // auth
    private FirebaseAuth mAuth;
    private TextView mEmail, mPassword;

    // logcat
    private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextView) findViewById(R.id.mEmail);
        mPassword = (TextView) findViewById(R.id.mPassword);
    }

    // 사용자가 로그인 되어 있는지 확인
    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }


        // [START sign_in_with_email]
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
        // [END sign_in_with_email]
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

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Toast.makeText(Login.this, "update UI",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 로그인 버튼 클릭
    public void onSignInButtonClicked(View v) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        signIn(email, password);
    }

    public void onSignUpButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}