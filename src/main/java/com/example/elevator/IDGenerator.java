package com.example.elevator;

public class IDGenerator {

    private static int elevatorCount = 1;
    private static int patientCount = 1;
    private static int visitorCount = 1;
    private static int nurseCount = 1;
    private static int doctorCount = 1;

    public static String generateElevatorID() {
        return String.format("E%02d", elevatorCount++);
    }

    public static String generatePatientID() {
        return String.format("P%04d", patientCount++);
    }

    public static String generateVisitorID() {
        return String.format("V%04d", visitorCount++);
    }

    public static String generateNurseID() {
        return String.format("N%04d", nurseCount++);
    }

    public static String generateDoctorID() {
        return String.format("D%04d", doctorCount++);
    }
}
