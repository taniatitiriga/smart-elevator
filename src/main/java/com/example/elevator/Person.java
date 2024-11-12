package com.example.elevator;

public abstract class Person implements Surface, Weight {

    // extra space for breathing room
    private static final double BSA_BUFFER_FACTOR = 1.05;

    private String ID;
    private int weight;
    private int height;
    private int destinationFloor;

    public Person(String ID,int weight, int height) {
        this.ID = "0";
        this.weight = weight;
        this.height = height;
    }

    public double getSurface() {
        // approx. body surface area (BSA)
        double bsa = 0.007184 * Math.pow(weight, 0.425) * Math.pow(height, 0.725);
        // add personal space
        return bsa * BSA_BUFFER_FACTOR;
    }

    public void setDestinationFloor(int floor) {
        this.destinationFloor = floor;
    }
    public int getDestinationFloor() {
        return destinationFloor;
    }

    public abstract int getPriorityLevel();
    public int getWeight(){ return this.weight; };
}
