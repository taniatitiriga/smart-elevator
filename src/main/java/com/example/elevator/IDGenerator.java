package com.example.elevator;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.List;
import java.util.Map;

public class IDGenerator {

    private static int elevatorCount = 1;
    private static int patientCount = 1;
    private static int visitorCount = 1;
    private static int nurseCount = 1;
    private static int doctorCount = 1;

    private static final String ELEVATOR_FILE = "elevators.json";
    private static final String PEOPLE_FILE = "people.json";

    static {
        initializeCountsFromFiles();
    }

    public static String generateElevatorID() {
        return String.format("E%02d", elevatorCount);
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

    private static void initializeCountsFromFiles() {
        // Initialize elevator count from elevators.json
        try (Reader reader = new FileReader(ELEVATOR_FILE)) {
            Gson gson = new Gson();
            List<Map<String, Object>> elevators = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());
            int maxElevatorNum = 0;  // Track the highest number found for elevator IDs
            if (elevators != null) {
                for (Map<String, Object> elevator : elevators) {
                    String id = (String) elevator.get("ID");
                    if (id != null && id.startsWith("E")) {
                        int num = Integer.parseInt(id.substring(1)); // Extract the numeric part of the ID
                        maxElevatorNum = Math.max(maxElevatorNum, num); // Track the highest ID number found
                    }
                }
            }
            elevatorCount = maxElevatorNum + 1; // Set elevatorCount to one more than the highest existing ID
            //System.out.println("[DEBUG] Initialized elevator count to " + elevatorCount);
        } catch (IOException e) {
            System.out.println("[INFO] No existing elevator data found, starting elevator ID from E01.");
            elevatorCount = 1; // Start from E01 if no file or entries are found
        }

        // Initialize people counts from people.json
        try (Reader reader = new FileReader(PEOPLE_FILE)) {
            Gson gson = new Gson();
            List<Map<String, Object>> people = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (people != null) {
                for (Map<String, Object> person : people) {
                    String id = (String) person.get("ID");
                    if (id != null && id.length() > 1) {
                        char typeChar = id.charAt(0);
                        int num = Integer.parseInt(id.substring(1));

                        switch (typeChar) {
                            case 'P':
                                patientCount = Math.max(patientCount, num + 1);
                                break;
                            case 'V':
                                visitorCount = Math.max(visitorCount, num + 1);
                                break;
                            case 'N':
                                nurseCount = Math.max(nurseCount, num + 1);
                                break;
                            case 'D':
                                doctorCount = Math.max(doctorCount, num + 1);
                                break;
                            default:
                                System.out.println("[DEBUG] Unknown ID prefix in people.json: " + id);
                        }
                    }
                }
            }
            //System.out.println("[DEBUG] Initialized counts - Patient: " + patientCount + ", Visitor: " + visitorCount + ", Nurse: " + nurseCount + ", Doctor: " + doctorCount);
        } catch (IOException e) {
            //System.out.println("[INFO] No existing people data found, starting people IDs from defaults.");
        }
    }
}
