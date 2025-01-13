package com.example.elevator;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.DoubleStream;

import static java.lang.Long.sum;

public class Agenda {
    private Elevator elevator;
    private Queue<Person> queueInside;
    private Map<Integer, Queue<Person>> queuesByFloor;

    public Agenda(Elevator elevator, Queue<Person> queueInside, Map<Integer, Queue<Person>> queuesByFloor) {
        //an agenda handles a single elevator, and the respective queues
        this.elevator = elevator;
        this.queueInside = queueInside;
        this.queuesByFloor = queuesByFloor;
    }

    public void addPersonToQueue(Person person, int floor, int destinationFloor) {
        //assign destination
        person.setDestinationFloor(destinationFloor);

        //initialize floor queue if necessary
        queuesByFloor.computeIfAbsent(floor, k -> new LinkedList<>()).add(person);

        //sort queue by priority
        sortAndGroup();
    }

    public Comparator<Person> comparePriority() {
        //compare by priority (bigger priority comes first)
        return Comparator.comparingInt(Person::getEffectivePriority).reversed();
    }

    public Person getNextPerson() {
        // get next person inside elevator by priority
        if (!queueInside.isEmpty()) {
            List<Person> sortedList = new ArrayList<>(queueInside);
            sortedList.sort(comparePriority());
            return sortedList.get(0);
        }
        return null;
    }

    public boolean isFull() {
        return getAvailableSpace() <= 0.5 || getAvailableWeight() <= 80;
    }

    public void boardPassengers(Elevator elevator) {
        int floor = elevator.getCurrentFloor();
        Queue<Person> floorQueue = queuesByFloor.get(floor);
        if (floorQueue == null || floorQueue.isEmpty()) return;

        while (!floorQueue.isEmpty() && !isFull()) {
            Person person = floorQueue.peek();

            assert person != null;
            person.incrementFloorsPassed();
            int personWeight = person.getWeight();
            double personSize = person.getSurface();

            if (getAvailableWeight() >= personWeight && getAvailableSpace() >= personSize) {
                queueInside.add(floorQueue.poll());
            } else {
                break;
            }
        }
        sortAndGroup();
    }

    public boolean areAllQueuesEmpty() {
        if (!queueInside.isEmpty()) {
            return false;
        }
        for (Queue<Person> floorQueue : queuesByFloor.values()) {
            if (!floorQueue.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void unboardPassengers(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        queueInside.removeIf(person -> person.getDestinationFloor() == currentFloor);
    }

    public int determineNextDestination(Elevator elevator) {
        Person topPriorityPerson = null;
        int destination = elevator.getCurrentFloor();

        if (!queueInside.isEmpty()) {
            topPriorityPerson = queueInside.peek();
            destination = topPriorityPerson.getDestinationFloor();

            // Check if the destination floor is accessible
            if (!elevator.canAccessFloor(destination)) {
                OutputDevice.printError("Elevator cannot access floor " + destination + ". Ignoring this request.");
                return elevator.getCurrentFloor(); // Stay on the current floor if the floor is not accessible
            }
        } else {
            for (Map.Entry<Integer, Queue<Person>> entry : queuesByFloor.entrySet()) {
                Queue<Person> floorQueue = entry.getValue();

                if (!floorQueue.isEmpty()) {
                    Person firstPersonInQueue = floorQueue.peek();

                    if (topPriorityPerson == null || firstPersonInQueue.getEffectivePriority() > topPriorityPerson.getEffectivePriority()) {
                        topPriorityPerson = firstPersonInQueue;
                        destination = entry.getKey(); // Set destination to the person's current floor
                    }
                }
            }
        }

        if (topPriorityPerson == null) {
            return elevator.getCurrentFloor();
        }else {
            if (topPriorityPerson.getEffectivePriority() >= 6 || isFull()) {
                return destination;
            } else {
                return getNextIntermediateStop(elevator.getCurrentFloor(), destination);
            }
        }
    }

    public int getNextIntermediateStop(int currentFloor, int destinationFloor) {
        int direction = Integer.compare(destinationFloor, currentFloor);
        int nextStop = destinationFloor;

        for (int floor = currentFloor + direction; floor != destinationFloor; floor += direction) {
            //stop for outside queue
            if (queuesByFloor.containsKey(floor) && !queuesByFloor.get(floor).isEmpty()) {
                return floor;
            }
            //stop for inside people
            for (Person person : queueInside) {
                if (person.getDestinationFloor() == floor) {
                    return floor;
                }
            }
        }
        return nextStop;
    }

    public void sortAndGroup() {
        for (Queue<Person> floorQueue : queuesByFloor.values()) {
            List<Person> sortedList = new ArrayList<>(floorQueue);
            sortedList.sort(comparePriority());
            floorQueue.clear();
            floorQueue.addAll(sortedList);
        }
    }

    public double getAvailableSpace() {
        double usedSpace = queueInside.stream()
                .mapToDouble(person -> person.getSurface())
                .sum();
        return elevator.getSurface() - usedSpace;
    }

    public double getAvailableWeight() {
        double usedWeight = queueInside.stream()
                .mapToInt(person -> person.getWeight())
                .sum();
        return Math.max(0, elevator.getWeight() - usedWeight);
    }

    public boolean evaluateStop(Elevator elevator, int floor) {
        Queue<Person> floorQueue = queuesByFloor.get(floor);

        if (floorQueue == null || floorQueue.isEmpty()) {
            return false;
        }
        // check if the highest priority on outside queue is greater than the one inside
        assert queueInside.peek() != null;
        return floorQueue.peek().getEffectivePriority() > queueInside.peek().getEffectivePriority();
    }

    public Queue<Person> getQueueInside() {
        return queueInside;
    }

    public Map<Integer, Queue<Person>> getQueuesByFloor() {
        return queuesByFloor;
    }

    public void updatePassengerFloorsPassed() {
        for (Person person : queueInside) {
            person.incrementFloorsPassed();
        }
        sortAndGroup();
    }
}