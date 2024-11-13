package com.example.elevator;

public class Nurse extends Person{

    private int emergencyLevel;

    public Nurse(int weight, int height) {
        super(IDGenerator.generateNurseID(), weight, height);
        this.emergencyLevel = 1;//default no emergency
    }

    @Override
    public int getPriorityLevel() {
        // 3, 6, 8
        if (emergencyLevel==1){
            return 3;
        } else if (emergencyLevel==2) {
            return 6;
        } else {
            return 8;
        }
    }

    public void setEmergencyLevel(int level) {
        if (level < 1 || level > 3) {
            throw new IllegalArgumentException("Emergency level must be between 1 and 3.");
        }
        this.emergencyLevel = level;
    }

    public int getEmergencyLevel() {
        return emergencyLevel;
    }

}