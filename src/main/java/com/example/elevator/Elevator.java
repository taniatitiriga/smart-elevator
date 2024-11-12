package com.example.elevator;

import java.util.LinkedList;

public class Elevator implements Surface, Weight{
    private String ID;

    private int maxWeight;
    private int width;
    private int depth;

    private int[] floors;
    private int currentFloor;

    public Elevator(String ID, int maxWeight, int width, int depth, int floors[], int currentFloor) {
        this.ID = IDGenerator.generateElevatorID();
        this.maxWeight = maxWeight;
        this.width = width;
        this.depth = depth;
        this.floors = floors;
        this.currentFloor = currentFloor;
    }

    public double getSurface() {
        return width * depth;
    }

    public int getWeight() {
        return maxWeight;
    }

    public void moveUp() {
        if (currentFloor < floors.length - 1) {
            currentFloor++;
        }
    }

    public void moveDown() {
        if (currentFloor > 0) {
            currentFloor--;
        }
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public String getId() {
        return this.ID;
    }
}
