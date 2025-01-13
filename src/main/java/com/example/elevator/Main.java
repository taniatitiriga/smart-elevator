package com.example.elevator;

import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.example.elevator.CustomExceptions.InvalidCommandException;
import com.example.elevator.CustomExceptions.InvalidNumberFormatException;

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
                    OutputDevice.printInfo("Exiting the application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    OutputDevice.printError("Invalid option. Try one of the following options:\n- demo: Run an instant demo\n- load: Load elevator from memory\n- new: New elevator");
                    break;
            }
        } else {
            OutputDevice.printError("No option selected. Try one of the following options:\n- demo: Run an instant demo\n- load: Load elevator from memory\n- new: New elevator");
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
                OutputDevice.printInfo("Exiting demo mode.");
                break;
            }

            String[] inputParts = input.split(" ");
            String command = inputParts[0];

            try {
                switch (command) {
                    case "elevator":
                        if (inputParts.length >= 5) {
                            try {
                                int maxWeight = parseInt(inputParts[1]);
                                int width = parseInt(inputParts[2]);
                                int depth = parseInt(inputParts[3]);
                                int currentFloor = parseInt(inputParts[4]);
                                int[] floors = {0, 1, 2, 3, 4, 5, 6, 7};  // Example floors
                                app.addElevator(maxWeight, width, depth, floors, currentFloor);
                            } catch (InvalidNumberFormatException e) {
                                OutputDevice.printError(e.getMessage());
                            }
                        } else {
                            throw new InvalidCommandException("Incorrect format for elevator command. Usage: elevator max_weight width depth current_floor");
                        }
                        break;

                    case "person":
                        if (inputParts.length >= 6) {
                            String type = inputParts[1];
                            try {
                                int weight = parseInt(inputParts[2]);
                                int height = parseInt(inputParts[3]);
                                int startFloor = parseInt(inputParts[4]);
                                int destinationFloor = parseInt(inputParts[5]);

                                Person person = createPerson(type, weight, height, inputParts);
                                if (person != null) {
                                    app.addPersonToQueue(person, startFloor, destinationFloor);
                                }
                            } catch (InvalidNumberFormatException e) {
                                OutputDevice.printError(e.getMessage());
                            }
                        } else {
                            throw new InvalidCommandException("Incorrect format for person command. Usage: person type weight height start_floor destination_floor [extra]");
                        }
                        break;

                    case "start":
                        app.startSession();
                        break;

                    default:
                        throw new InvalidCommandException("Unknown command: '" + command + "'. Try 'elevator', 'person', or 'start'.");
                }
            } catch (InvalidCommandException e) {
                OutputDevice.printError(e.getMessage());
            }
        }
    }

    private static int parseInt(String value) throws InvalidNumberFormatException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException("Invalid number format: '" + value + "'. Please enter a valid integer.");
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
                    OutputDevice.printError("Invalid person type. Use 'visitor', 'patient', 'nurse', or 'doctor'.");
                    return null;
            }
        } catch (IllegalArgumentException e) {
            OutputDevice.printError("Invalid argument: " + e.getMessage());
            return null;
        }
    }

    private static void loadElevator(Application app) {
        OutputDevice.printInfo("Loading elevator from memory...");
        app.clearScreen();

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
                OutputDevice.printError("Elevator with ID " + elevatorId + " not found.");
            }
        } catch (IOException e) {
            OutputDevice.printError("Failed to load elevator: " + e.getMessage());
        }
    }

    private static void newElevator(Application app) {
        OutputDevice.printInfo("Creating a new elevator...");
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
        Gson gson = new Gson();
        List<Elevator> elevators = new ArrayList<>();

        // Check if elevators.json exists and read existing elevators
        File file = new File(ELEVATOR_FILE);
        if (file.exists()) {
            // Read existing elevators if file exists
            try (Reader reader = new FileReader(file)) {
                elevators = gson.fromJson(reader, new TypeToken<List<Elevator>>(){}.getType());
                if (elevators == null) elevators = new ArrayList<>();
            } catch (IOException e) {
                OutputDevice.printError("Failed to read elevator file: " + e.getMessage());
            }
        } else {
            // Create the file and initialize it with an empty JSON array if it doesn't exist
            try {
                file.createNewFile();
                try (Writer writer = new FileWriter(file)) {
                    writer.write("[]"); // Initialize with empty JSON array
                }
            } catch (IOException e) {
                OutputDevice.printError("Failed to create elevator file: " + e.getMessage());
                return;
            }
        }

        // Add the new elevator to the list
        Elevator newElevator = new Elevator(IDGenerator.generateElevatorID(), maxWeight, width, depth, floors, currentFloor);
        elevators.add(newElevator);

        // Write updated elevator list back to file
        try (Writer writer = new FileWriter(ELEVATOR_FILE)) {
            gson.toJson(elevators, writer);
            OutputDevice.printInfo("New elevator saved to memory.");
        } catch (IOException e) {
            OutputDevice.printError("Failed to save elevator: " + e.getMessage());
        }
    }

    private static void runSession(Application app) {
        app.clearScreen();
        OutputDevice.print("=== Managing passengers ===\n");
        OutputDevice.print("Available commands:\nloadpeople - load existing people from memory\nperson - create a new person\nstart - start the elevator\n");

        while (true) {
            OutputDevice.print("Enter a command (or type 'exit' to quit):");
            String input = InputDevice.getInput().trim();

            if (input.equalsIgnoreCase("exit")) {
                OutputDevice.printInfo("Exiting session mode.");
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

                                // Prepare optional fields based on person type
                                Map<String, Object> optionalFields = new HashMap<>();
                                if (person instanceof Doctor) {
                                    Doctor doctor = (Doctor) person;
                                    optionalFields.put("emergencyLevel", doctor.getEmergencyLevel());
                                } else if (person instanceof Patient) {
                                    Patient patient = (Patient) person;
                                    optionalFields.put("walkingAid", patient.getWalkingAid().toString());
                                }

                                // Pass individual fields and optional fields map to savePersonToMemory
                                savePersonToMemory(type, person.getID(), weight, height, startFloor, destinationFloor, optionalFields);
                            }
                        } catch (NumberFormatException e) {
                            OutputDevice.printError("Invalid number format. Ensure weight, height, start floor, and destination floor are integers.");
                        }
                    } else {
                        OutputDevice.printUsage("person type weight height start_floor destination_floor [extra: emergency level for staff or walking aid for patients]");
                    }
                    break;

                case "loadpeople":
                    loadPeopleFromMemory(app);
                    break;

                case "start":
                    app.startSession();
                    break;

                default:
                    OutputDevice.printError("Unknown command: '" + command + "'. Try 'person', 'loadpeople', and 'start'.");
                    break;
            }
        }
    }

    private static void loadPeopleFromMemory(Application app) {
        OutputDevice.printInfo(" Loading people from memory...");
        File file = new File(PEOPLE_FILE);

        if (!file.exists()) {
            OutputDevice.printInfo("No people data found. No people were saved previously.");
            return;
        }

        try (Reader reader = new FileReader(PEOPLE_FILE)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Person.class, new PersonDeserializer())
                    .create();

            List<Map<String, Object>> people = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());

            if (people == null || people.isEmpty()) {
                OutputDevice.printInfo("No people data found. No people were saved previously.");
                return;
            }

            while (true) {
                OutputDevice.print("Enter person ID to load (leave blank to finish):");
                String personId = InputDevice.getInput().trim();

                if (personId.isEmpty()) {
                    OutputDevice.printInfo("Finished loading people.");
                    break;
                }

                boolean personFound = false;
                for (Map<String, Object> personData : people) {
                    if (personData.containsKey("ID") && personId.equals(personData.get("ID"))) {
                        personFound = true;
                        try {
                            String type = (String) personData.get("type");
                            int weight = ((Double) personData.get("weight")).intValue();
                            int height = ((Double) personData.get("height")).intValue();
                            int startFloor = ((Double) personData.get("startFloor")).intValue();
                            int destinationFloor = ((Double) personData.get("destinationFloor")).intValue();

                            Person person = createPerson(type, weight, height, new String[]{});
                            if (person != null) {
                                app.addPersonToQueue(person, startFloor, destinationFloor);
//                                OutputDevice.printInfo( person.getClass().getSimpleName() + " loaded and added to queue on floor " + startFloor);
                            }
                        } catch (Exception e) {
                            OutputDevice.printError("Invalid data format for person ID " + personId + ". Skipping entry.");
                        }
                    }
                }

                if (!personFound) {
                    OutputDevice.printInfo("Person with ID " + personId + " not found in memory.");
                }
            }
        } catch (IOException e) {
            OutputDevice.printError("Failed to load people: " + e.getMessage());
        }
    }

    private static void savePersonToMemory(String type, String id, int weight, int height, int startFloor, int destinationFloor, Map<String, Object> optionalFields) {
        if (id == null || weight <= 0 || height <= 0 || destinationFloor < 0) {
            OutputDevice.printError("Invalid person data. Person not saved to memory.");
            return;
        }

        File file = new File(PEOPLE_FILE);
        Gson gson = new Gson();
        List<Map<String, Object>> people = new ArrayList<>();

        // Check if people.json exists and read existing people
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                people = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>(){}.getType());
                if (people == null) people = new ArrayList<>();
            } catch (IOException e) {
                OutputDevice.printError("Failed to read people file: " + e.getMessage());
            }
        } else {
            try {
                file.createNewFile();
                try (Writer writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                OutputDevice.printError("Failed to create people file: " + e.getMessage());
                return;
            }
        }

        // Create a new person data map with core fields
        Map<String, Object> personData = new HashMap<>();
        personData.put("type", type.toLowerCase());
        personData.put("ID", id);
        personData.put("weight", weight);
        personData.put("height", height);
        personData.put("startFloor", startFloor);
        personData.put("destinationFloor", destinationFloor);

        // Add optional fields if they are provided
        if (optionalFields != null) {
            personData.putAll(optionalFields);
        }

        people.add(personData);

        try (Writer writer = new FileWriter(PEOPLE_FILE)) {
            gson.toJson(people, writer);
            OutputDevice.printInfo("New person saved to memory.");
        } catch (IOException e) {
            OutputDevice.printError("Failed to save person: " + e.getMessage());
        }
    }

}
