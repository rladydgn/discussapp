package com.example.finalproject;

// realtime firebase 에 저장할 유저 정보 양식
public class User {

    public String nickname;

    public User() {
        nickname = null;
    }
    public User(String nickname) {
        this.nickname = nickname;
    }

    String getNickname() {
        return nickname;
    }

}
