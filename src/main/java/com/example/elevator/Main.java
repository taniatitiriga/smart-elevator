package com.example.elevator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Application app = new Application();
        Scanner scanner = new Scanner(System.in);
        if (args.length == 1) {
            String option = args[0].toLowerCase();

            switch (option) {
                case "demo":
                    runDemo(app);
                    break;
                case "load":
                    OutputDevice.print("[INFO] 'Load' option is not available yet.");
                    break;
                case "new":
                    OutputDevice.print("[INFO] 'New elevator' option is not available yet.");
                    break;
                case "exit":
                    OutputDevice.print("[INFO] Exiting the application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    OutputDevice.print("[ERROR] Invalid option. Try one of the following options:\n- demo: Run an instant demo\n- load: Load elevator from memory\n- new: New elevator");
                    break;
            }
        } else {
            OutputDevice.print("[ERROR] No option selected. Try one of the following options:\n- demo: Run an instant demo\n- load: Load elevator from memory\n- new: New elevator");
        }
    }

    private static void runDemo(Application app) {
        OutputDevice.print("\n=== Demo Mode ===");
        OutputDevice.print("Available commands:");
        OutputDevice.print(" - Add Elevator: elevator maximum_weight width depth current_floor");
        OutputDevice.print(" - Add Person: person type weight height current_floor destination_floor [extra: emergency level for staff or walking aid for patients]");
        OutputDevice.print(" - Start Session: start");
        OutputDevice.print("Enter a command (or type 'exit' to quit demo mode):");

        while (true) {
            String input = InputDevice.getInput().trim();

            if (input.equalsIgnoreCase("exit")) {
                OutputDevice.print("[INFO] Exiting demo mode.");
                break;
            }

            String[] inputParts = input.split(" ");
            String command = inputParts[0];

            switch (command) {
                case "elevator":
                    if (inputParts.length >= 5) {
                        try {
                            int maxWeight = Integer.parseInt(inputParts[1]);
                            int width = Integer.parseInt(inputParts[2]);
                            int depth = Integer.parseInt(inputParts[3]);
                            int currentFloor = Integer.parseInt(inputParts[4]);
                            int[] floors = {0, 1, 2, 3, 4, 5, 6, 7};  // Example floors
                            app.addElevator(maxWeight, width, depth, floors, currentFloor);
                        } catch (NumberFormatException e) {
                            OutputDevice.print("[ERROR] Invalid number format. Ensure maximum weight, width, depth, and current floor are integers.");
                        }
                    } else {
                        OutputDevice.print("[USAGE] elevator maximum_weight width depth current_floor");
                    }
                    break;

                case "person":
                    if (inputParts.length >= 6) {
                        String type = inputParts[1];
                        try {
                            int weight = Integer.parseInt(inputParts[2]);
                            int height = Integer.parseInt(inputParts[3]);
                            int startFloor = Integer.parseInt(inputParts[4]);
                            int destinationFloor = Integer.parseInt(inputParts[5]);

                            Person person = createPerson(type, weight, height, inputParts);
                            if (person != null) {
                                app.addPersonToQueue(person, startFloor, destinationFloor);
                            }
                        } catch (NumberFormatException e) {
                            OutputDevice.print("[ERROR] Invalid number format. Please check that weight, height, current floor, and destination floor are integers.");
                        }
                    } else {
                        OutputDevice.print("[USAGE] person type weight height current_floor destination_floor [extra: emergency level for staff or walking aid for patients]");
                    }
                    break;

                case "start":
                    app.startSession();
                    break;

                default:
                    OutputDevice.print("[ERROR] Unknown command: '" + command + "'. Try 'elevator', 'person', and 'start'.");
                    break;
            }
        }
    }

    private static Person createPerson(String type, int weight, int height, String[] inputParts) {
        try {
            type.toLowerCase();
            switch (type) {
                case "visitor":
                    return new Visitor(weight, height);
                case "patient":
                    Patient patient = new Patient(weight, height);
                    if (inputParts.length >= 7) {
                        String walkingAid = inputParts[6];
                        patient.setWalkingAid(WalkingAid.valueOf(walkingAid));
                    }
                    return patient;
                case "nurse":
                    Nurse nurse = new Nurse(weight, height);
                    if (inputParts.length >= 7) {
                        int emergencyLevel = Integer.parseInt(inputParts[6]);
                        nurse.setEmergencyLevel(emergencyLevel);
                    }
                    return nurse;
                case "doctor":
                    Doctor doctor = new Doctor(weight, height);
                    if (inputParts.length >= 7) {
                        int emergencyLevel = Integer.parseInt(inputParts[6]);
                        doctor.setEmergencyLevel(emergencyLevel);
                    }
                    return doctor;
                default:
                    OutputDevice.print("[ERROR] Invalid person type. Use 'visitor', 'patient', 'nurse', or 'doctor'.");
                    return null;
            }
        } catch (IllegalArgumentException e) {
            OutputDevice.print("[ERROR] Invalid argument: " + e.getMessage());
            return null;
        }
    }
}
