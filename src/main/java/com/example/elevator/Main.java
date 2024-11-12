package com.example.elevator;

import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;


public class Main {
    private static final String ELEVATOR_FILE = "elevators.json";
    private static final String PEOPLE_FILE = "people.json";

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
                    loadElevator(app);
                    break;
                case "new":
                    newElevator(app);
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

    private static void loadElevator(Application app) {
        OutputDevice.print("[INFO] Loading elevator from memory...");

        try (Reader reader = new FileReader(ELEVATOR_FILE)) {
            Gson gson = new Gson();
            List<Elevator> elevators = gson.fromJson(reader, new TypeToken<List<Elevator>>(){}.getType());

            Scanner scanner = new Scanner(System.in);
            OutputDevice.print("Enter elevator ID to load:");
            String elevatorId = scanner.nextLine();

            // Find the elevator by ID using getId()
            Optional<Elevator> elevatorOpt = elevators.stream()
                    .filter(e -> e.getId().equals(elevatorId))
                    .findFirst();

            if (elevatorOpt.isPresent()) {
                Elevator loadedElevator = elevatorOpt.get();
                app.addElevator(loadedElevator.getMaxWeight(), loadedElevator.getWidth(), loadedElevator.getDepth(), loadedElevator.getFloors(), loadedElevator.getCurrentFloor());
                runSession(app); // Call session with loaded elevator
            } else {
                OutputDevice.print("[ERROR] Elevator with ID " + elevatorId + " not found.");
            }
        } catch (IOException e) {
            OutputDevice.print("[ERROR] Failed to load elevator: " + e.getMessage());
        }
    }

    private static void newElevator(Application app) {
        OutputDevice.print("[INFO] Creating a new elevator...");
        Scanner scanner = new Scanner(System.in);

        OutputDevice.print("Enter maximum weight:");
        int maxWeight = scanner.nextInt();
        OutputDevice.print("Enter width:");
        int width = scanner.nextInt();
        OutputDevice.print("Enter depth:");
        int depth = scanner.nextInt();
        OutputDevice.print("Enter starting floor:");
        int currentFloor = scanner.nextInt();

        OutputDevice.print("Enter floors serviced by this elevator (comma-separated, e.g., 0,1,2,3,4):");
        scanner.nextLine(); // Consume the newline
        String floorInput = scanner.nextLine();
        int[] floors = Arrays.stream(floorInput.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        app.addElevator(maxWeight, width, depth, floors, currentFloor);

        // Save the new elevator to memory
        saveElevatorToMemory(maxWeight, width, depth, currentFloor, floors);
        runSession(app); // Call session with the new elevator
    }

    private static void saveElevatorToMemory(int maxWeight, int width, int depth, int currentFloor, int[] floors) {
        try (Reader reader = new FileReader(ELEVATOR_FILE)) {
            Gson gson = new Gson();
            List<Map<String, Object>> elevators = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (elevators == null) elevators = new ArrayList<>();

            Map<String, Object> newElevatorData = new HashMap<>();
            newElevatorData.put("id", IDGenerator.generateElevatorID());
            newElevatorData.put("maxWeight", maxWeight);
            newElevatorData.put("width", width);
            newElevatorData.put("depth", depth);
            newElevatorData.put("currentFloor", currentFloor);
            newElevatorData.put("floors", Arrays.stream(floors).boxed().toArray());

            elevators.add(newElevatorData);

            try (Writer writer = new FileWriter(ELEVATOR_FILE)) {
                gson.toJson(elevators, writer);
                OutputDevice.print("[INFO] New elevator saved to memory.");
            }
        } catch (IOException e) {
            OutputDevice.print("[ERROR] Failed to save elevator: " + e.getMessage());
        }
    }

    private static void runSession(Application app) {
        OutputDevice.print("\n=== Session Mode ===");
        OutputDevice.print("This session behaves like the demo but includes loading people from memory.");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            OutputDevice.print("Enter a command (or type 'exit' to quit):");
            String input = InputDevice.getInput().trim();

            if (input.equalsIgnoreCase("exit")) {
                OutputDevice.print("[INFO] Exiting session mode.");
                break;
            }

            String[] inputParts = input.split(" ");
            String command = inputParts[0];

            switch (command) {
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
                            OutputDevice.print("[ERROR] Invalid number format. Ensure weight, height, start floor, and destination floor are integers.");
                        }
                    } else {
                        OutputDevice.print("[USAGE] person type weight height start_floor destination_floor [extra: emergency level for staff or walking aid for patients]");
                    }
                    break;

                case "loadpeople":
                    loadPeopleFromMemory(app);
                    break;

                case "start":
                    app.startSession();
                    break;

                default:
                    OutputDevice.print("[ERROR] Unknown command: '" + command + "'. Try 'person', 'loadpeople', and 'start'.");
                    break;
            }
        }
    }
    
    private static void loadPeopleFromMemory(Application app) {
        OutputDevice.print("[INFO] Loading people from memory...");
        try (Reader reader = new FileReader(PEOPLE_FILE)) {
            Gson gson = new Gson();
            List<Map<String, Object>> people = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());

            if (people != null) {
                for (Map<String, Object> personData : people) {
                    String type = (String) personData.get("type");
                    int weight = ((Double) personData.get("weight")).intValue();
                    int height = ((Double) personData.get("height")).intValue();
                    int startFloor = ((Double) personData.get("startFloor")).intValue();
                    int destinationFloor = ((Double) personData.get("destinationFloor")).intValue();

                    Person person = createPerson(type, weight, height, new String[]{}); // adjust params if necessary
                    if (person != null) {
                        app.addPersonToQueue(person, startFloor, destinationFloor);
                    }
                }
                OutputDevice.print("[INFO] People loaded from memory.");
            }
        } catch (IOException e) {
            OutputDevice.print("[ERROR] Failed to load people: " + e.getMessage());
        }
    }

}
