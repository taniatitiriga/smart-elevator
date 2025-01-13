package com.example.elevator;

public class Doctor extends Person{

    private int emergencyLevel;

    public Doctor(int weight, int height) {
        super(IDGenerator.generateDoctorID(), weight, height);
        this.emergencyLevel = 1;
    }

    @Override
    public int getPriorityLevel() {
        // 4, 7, 9
        if (emergencyLevel==1){
            return 4;
        } else if (emergencyLevel==2) {
            return 7;
        } else {
            return 9;
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

    @Override
    public String getType() {
        return "Doctor";
    }
}