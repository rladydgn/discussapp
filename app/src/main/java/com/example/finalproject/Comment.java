package com.example.finalproject;

// 댓글
public class Comment {
    public String content;
    public String timeOrder;
    public String UId;
    public String vote;


    Comment() {
        content = null;
        timeOrder = null;
        UId = null;
        vote = null;
    }

    Comment(String cont, String time, String U, String vot) {
        content = cont;
        timeOrder = time;
        UId = U;
        vote = vot;
    }
}
