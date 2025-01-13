package com.example.elevator;

public abstract class Person implements Surface, Weight {

    // Extra space for breathing room
    private static final double BSA_BUFFER_FACTOR = 1.05;

    private String ID;
    private int weight;
    private int height;
    private int destinationFloor;
    private int floorsPassed;
    private boolean temporaryPriorityBoost;

    public Person(String ID, int weight, int height) {
        this.ID = ID; // Corrected to use parameter
        this.weight = weight;
        this.height = height;
        this.floorsPassed = 0;
        this.temporaryPriorityBoost = false;
    }

    public double getSurface() {
        double bsa = 0.007184 * Math.pow(weight, 0.425) * Math.pow(height, 0.725);
        return bsa * BSA_BUFFER_FACTOR;
    }

    public void setDestinationFloor(int floor) {
        this.destinationFloor = floor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public abstract int getPriorityLevel();

    public int getWeight() {
        return this.weight;
    }

    public String getID() {
        return this.ID;
    }

    public int getHeight() {
        return this.height;
    }

    // Increment floors passed
    public void incrementFloorsPassed() {
        this.floorsPassed++;
        if (floorsPassed == 10 && !temporaryPriorityBoost) {
            temporaryPriorityBoost = true;
        }
    }

    // Reset floors passed
    public void resetFloorsPassed() {
        this.floorsPassed = 0;
        this.temporaryPriorityBoost = false;
    }

    // Calculate effective priority
    public int getEffectivePriority() {
        return temporaryPriorityBoost ? getPriorityLevel() + 10 : getPriorityLevel();
    }
}
