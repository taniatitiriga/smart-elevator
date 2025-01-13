package com.example.elevator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Application {
    private Elevator elevator;
    private Agenda agenda;

    // Initialize the application and create an elevator
    public void addElevator(int maxWeight, int width, int depth, int[] floors, int currentFloor) {
        this.elevator = new Elevator(IDGenerator.generateElevatorID(), maxWeight, width, depth, floors, currentFloor);
        this.agenda = new Agenda(elevator, new LinkedList<>(), new HashMap<>());
        OutputDevice.print("[INFO] Elevator created on floor: " + elevator.getCurrentFloor());
    }

    // Add a person to the specified floor queue in Agenda
    public void addPersonToQueue(Person person, int startFloor, int destinationFloor) {
        if (agenda != null) {
            agenda.addPersonToQueue(person, startFloor, destinationFloor);
            OutputDevice.print("[INFO] " + person.getClass().getSimpleName() + " added to queue on floor " + startFloor);
        } else {
            OutputDevice.print("[ERROR] Please add an elevator before adding people.");
        }
    }

    // Start a session, which makes the elevator operate autonomously
    public void startSession() {
        if (agenda == null || elevator == null) {
            OutputDevice.print("[ERROR] Initialize the elevator and agenda before starting the session.");
            return;
        }

        OutputDevice.print("\n=== Starting Elevator Session ===\n");
        while (true) {
            if (agenda.areAllQueuesEmpty()) {
                OutputDevice.print("\n[INFO] Session complete. No further destinations.\n");
                break;
            }

            printSessionSeparator();
            int currentFloor = elevator.getCurrentFloor();
            OutputDevice.print("[INFO] Elevator at floor: " + currentFloor);
            printQueues();

            // Determine the next floor and move the elevator accordingly
            int nextFloor = agenda.determineNextDestination(elevator);
            while (nextFloor != currentFloor) {
                if (nextFloor > currentFloor) {
                    elevator.moveUp();
                } else {
                    elevator.moveDown();
                }
                currentFloor = elevator.getCurrentFloor();
                OutputDevice.print("[INFO] Elevator at floor: " + currentFloor);
                agenda.updatePassengerFloorsPassed();
            }

            // Handle boarding and unboarding at the current floor
            OutputDevice.print("\n[INFO] Handling passengers at floor: " + currentFloor);
            agenda.unboardPassengers(elevator);
            agenda.boardPassengers(elevator);
        }
    }

    // Display the state of all queues (both floor queues and inside the elevator)
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

    // Print a separator for session steps
    private void printSessionSeparator() {
        OutputDevice.print("\n--------------------------------------");
    }
}
