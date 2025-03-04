# Smart Elevator for Hospitals

## Project Overview
A Java-based simulation of a smart elevator. The algorithm uses priority queues to determine the best path and handle emergencies specific to medical facilities.

## Getting Started
### Prerequisites
- Java Development Kit (JDK) 11 or later
- Apache Maven
- Git (for cloning the repository)

### Installation
```sh
git clone https://github.com/taniatitiriga/smart-elevator
cd smart-elevator
```

### Compiling
```sh
mvn clean compile
```

## Running the Simulation
#### 1. Start a new elevator:
```sh
mvn exec:java -Dexec.mainClass="com.example.elevator.Main" -Dexec.args="new"
```
#### 2. Load an existing elevator from memory:
```sh
mvn exec:java -Dexec.mainClass="com.example.elevator.Main" -Dexec.args="load"
```

### Preloaded Data
- **Preloaded elevators:** `elevators.json`
- **Preloaded people:** `people.json`

### Elevator Initialization
#### Load an existing elevator
1. Enter the Elevator ID (from `elevators.json`).

#### Create a new elevator
2. Input the following details:
   - Maximum weight (kg)
   - Width (meters)
   - Depth (meters)
   - Starting floor
   - Floors serviced (comma-separated, e.g., `1,2,3,4,5,6,7,8,9`)

### Available Commands
- **`loadpeople`** - Load existing people from memory.
- **`person`** - Create a new person.
- **`start`** - Start the elevator.

## Algorithm Description

1. **Queues** are sorted by priority before people get in the elevator. People with lower priority may wait longer to get in, but once inside the elevator, they gain higher priority.
2. After travelling for a custom amount of floors, the priority of a person is **boosted** with a constant amount to ensure **fairness**.
3. **The minimum route** is precomputed automatically. Unless there is an emergency or the elevator is full, the elevator makes intermediary stops until it reaches the destination.
4. **Useless stops** are avoided by computing the remaining weight and space, with the use of BMI.

### Applicability
To implement this algorithm in real life, one would need a physical badge system, badge scanners and a digital interface for users to choose their destination (instead of the typical up and down arrows). Additionally, a database is required to store details about the facility's employees and anyone who checks in.

Implementing this algorithm on a real infrastructure would help patients get their  urgent care on time, with no delays. It ensures that people with disabilities can always access an elevator and not wait for too long. All of this while being fair to visitors and other patients as well. 
This project is also scalable to any other type of infrastructure where emergencies can happen, like a Fire Department.
