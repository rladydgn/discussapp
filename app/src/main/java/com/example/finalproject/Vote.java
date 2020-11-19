package com.example.finalproject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Vote {
    private int totalVote;
    private int yesVote, noVote, middleVote;

    Vote() {
        totalVote = 0;
        yesVote = 0;
        noVote = 0;
        middleVote = 0;
    }

    Vote(int total, int yes, int middle, int no) {
        totalVote = total;
        yesVote = yes;
        noVote = no;
        middleVote = middle;
    }

    public int getYesVote() {
        return yesVote;
    }

    public int getTotalVote() {
        return totalVote;
    }

    public int getNoVote() {
        return noVote;
    }

    public int getMiddleVote() {
        return middleVote;
    }

}
