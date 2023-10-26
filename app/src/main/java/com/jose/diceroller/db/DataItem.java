package com.jose.diceroller.db;

public class DataItem {
    private String name;
    private int score;

    public DataItem(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}

