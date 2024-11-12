package com.example.elevator;

public class Patient extends Person{

    private static final double WHEELCHAIR_SURFACE = 1.0;// in m^2, including extra space
    private static final double FRAME_EXTRA_SURFACE = 0.3;

    private boolean hasWalkingAid;
    private WalkingAid walkingAid = WalkingAid.None;

    public Patient(int weight, int height) {
        super(IDGenerator.generatePatientID(), weight, height);
        this.hasWalkingAid = false;
    }

    @Override
    public int getPriorityLevel() {
        //prioritize over non-emergency doctors and nurses if disabled
        return hasWalkingAid ? 5 : 2;
    }

    @Override
    public int getWeight() {
        int walkingAidWeight = 0;

        switch (walkingAid) {
            case Wheelchair:
                walkingAidWeight = 30;
                break;
            case Frame:
                walkingAidWeight = 15;
                break;
            case Crutches:
                walkingAidWeight = 2;
                break;
            case None:
                walkingAidWeight = 0;
                break;
        }
        return super.getWeight() + walkingAidWeight;
    }

    @Override
    public double getSurface() {
        // default person surface
        double bsa = super.getSurface();

        switch (walkingAid) {
            case Wheelchair:
                //wheelchair standard size
                return WHEELCHAIR_SURFACE;

            case Frame:
                //add frame standard size to the variable person size
                return bsa + FRAME_EXTRA_SURFACE;

            //no extra space required
            case Crutches:
            case None:
                return bsa;
        }
        return bsa;
    }

    public void setWalkingAid(WalkingAid aid) {
        this.walkingAid = aid;
        this.hasWalkingAid = aid != WalkingAid.None;
    }

    public WalkingAid getWalkingAid() {
        return walkingAid;
    }
}