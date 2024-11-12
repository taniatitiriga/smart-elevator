package com.example.elevator;

public class Visitor extends Person{

    public Visitor(int weight, int height) {
        super(IDGenerator.generateVisitorID(), weight, height);
    }

    @Override
    public int getPriorityLevel() {
        return 1;
    }

}