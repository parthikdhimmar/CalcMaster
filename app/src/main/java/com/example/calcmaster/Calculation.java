package com.example.calcmaster;

public class Calculation {
    private int id;
    private String input;
    private String result;

    public Calculation(int id, String input, String result) {
        this.id = id;
        this.input = input;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getResult() {
        return result;
    }
}