package com.example.elevator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Application {
    private Elevator elevator;
    private Agenda agenda;

    public void addElevator(int maxWeight, int width, int depth, int[] floors, int currentFloor) {
        this.elevator = new Elevator(IDGenerator.generateElevatorID(), maxWeight, width, depth, floors, currentFloor);
        this.agenda = new Agenda(elevator, new LinkedList<>(), new HashMap<>());
        OutputDevice.printInfo("Elevator created on floor: " + elevator.getCurrentFloor());
    }

    // Add a person to the specified floor queue in Agenda
    public void addPersonToQueue(Person person, int startFloor, int destinationFloor) {
        if (agenda != null) {
            agenda.addPersonToQueue(person, startFloor, destinationFloor);
            OutputDevice.printInfo(person.getClass().getSimpleName() + " added to queue on floor " + startFloor);
        } else {
            OutputDevice.printError("Please add an elevator before adding people.");
        }
    }

    // Start a session, which makes the elevator operate autonomously
    public void startSession() {
        if (agenda == null || elevator == null) {
            OutputDevice.printError("Initialize the elevator and agenda before starting the session.");
            return;
        }

        OutputDevice.print("\n=== Starting Elevator Session ===\n");
        while (true) {
            if (agenda.areAllQueuesEmpty()) {
                clearScreen();
                OutputDevice.printInfo("Session complete. No further destinations.\n");
                break;
            }

            int currentFloor = elevator.getCurrentFloor();
            int nextFloor = agenda.determineNextDestination(elevator);

            while (nextFloor != currentFloor) {
                if (nextFloor > currentFloor) {
                    elevator.moveUp();
                } else {
                    elevator.moveDown();
                }
                currentFloor = elevator.getCurrentFloor();
//                clearScreen();
//                printElevatorState();
            }

            clearScreen();
            OutputDevice.print("=== Elevator Before Handling Passengers ===");
            printElevatorState();
            awaitUserInput();

            agenda.unboardPassengers(elevator);
            agenda.boardPassengers(elevator);

            clearScreen();
            OutputDevice.print("=== Elevator After Handling Passengers ===");
            printElevatorState();
            awaitUserInput();
        }
    }

    private void awaitUserInput() {
        OutputDevice.print("Press Enter to continue...");
        InputDevice.getInput();
    }

    public void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            OutputDevice.printError("Failed to clear screen.");
        }
    }

    private void printElevatorState() {
        int totalFloors = elevator.getFloors().length;
        int elevatorPosition = elevator.getCurrentFloor();
        Map<Integer, Queue<Person>> floorQueues = agenda.getQueuesByFloor();
        Queue<Person> elevatorQueue = agenda.getQueueInside();

        for (int floor = totalFloors; floor >= 1; floor--) {
            StringBuilder floorLine = new StringBuilder();

            // Floor number
            floorLine.append(String.format("%2d |", floor));

            // Elevator
            if (floor == elevatorPosition) {
                floorLine.append("[");
                if (!elevatorQueue.isEmpty()) {
                    for (Person person : elevatorQueue) {
                        floorLine.append(person.getType().charAt(0)+"(d:"+person.getDestinationFloor()+" p:"+person.getEffectivePriority()+") ");
                    }
                } else {
                    floorLine.append(" ");
                }
                floorLine.append("] ");
            } else {
                floorLine.append("    "); // Empty space where the elevator isn't
            }

            // Floor queue
            floorLine.append("Queue: ");
            Queue<Person> queue = floorQueues.getOrDefault(floor, new LinkedList<>());
            if (queue.isEmpty()) {
                floorLine.append("-");
            } else {
                for (Person person : queue) {
                    floorLine.append(person.getType().charAt(0)+"(d:"+person.getDestinationFloor()+" p:"+person.getEffectivePriority()+") "); // First letter of type
                }
            }

            // Print the floor
            OutputDevice.print(floorLine.toString());
        }

        OutputDevice.print("\n--------------------------------------\n");
    }

    private void printQueues() {
        OutputDevice.print("=== Inside Elevator Queue ===");
        if (agenda.getQueueInside().isEmpty()) {
            OutputDevice.print(" - (No passengers inside the elevator)");
        } else {
            for (Person person : agenda.getQueueInside()) {
                OutputDevice.print(" - " + person.getClass().getSimpleName() +
                        " | Dest: " + person.getDestinationFloor() +
                        " | Priority: " + person.getPriorityLevel());
            }
        }

        OutputDevice.print("=== Queues by Floor ===");
        for (Map.Entry<Integer, Queue<Person>> entry : agenda.getQueuesByFloor().entrySet()) {
            int floor = entry.getKey();
            Queue<Person> floorQueue = entry.getValue();
            OutputDevice.print("Floor " + floor + " Queue:");
            if (floorQueue.isEmpty()) {
                OutputDevice.print(" - (No passengers waiting)");
            } else {
                for (Person person : floorQueue) {
                    OutputDevice.print(" - " + person.getClass().getSimpleName() +
                            " | Dest: " + person.getDestinationFloor() +
                            " | Priority: " + person.getPriorityLevel());
                }
            }
        }
    }

    private void printSessionSeparator() {
        OutputDevice.print("\n--------------------------------------");
    }
}
