# Avant Parking Lot System

This project implements a parking lot management system as part of the Avant take-home assignment.

---
## Design Overview
- The parking lot application is designed around a couple of core principles to allow for easy extension, testing and maintainability:
  - **Single Responsibility Principle**: Each class has a clear, focused responsibility.
  - **Encapsulation**: Internal states are hidden behind clear methods.
  - **Immutability**: To ensure the generated summaries are read only snapshots of internal states.
  - **Abstraction**: To allow for introduction of different lot strategies and different lot types.
- Different OOP design patterns are used to achieve the above principles:
  - **Factory Pattern**: To create different types of vehicles and parking spots.
  - **Strategy Pattern**: To encapsulate different parking strategies.
  - **Facade Pattern**: To provide a simplified interface for parking lot operations while hiding implementation mechanics.
---

## Features
- Domain model for **Vehicle**, **ParkingSpot**, **ParkingLot**, and **ParkingLotSummary**
- Allocation rules:
    - **Motorcycle** → prefers compact, falls back to regular
    - **Car** → prefers regular, falls back to compact
    - **Van** → requires two adjacent regular spots
- Remove vehicles by identifier
- Generate parking lot summaries:
    - Totals (overall and by type)
    - Available vs. occupied
    - Full/empty flags
    - Van spot counts
    - Per-row summary
- Unit and integration tests with **JUnit 5**
- CLI demo application for live demonstration of features (program arguments for running included in repo. See "Run Application" section below)

## Assumptions
- The parking lot has a fixed number of rows and spots per row, the product of which would never exceed long maximum.
- All data needed can be stored in memory.
- Spot allocation is done on a first-come, first-served basis.
- The system does not handle payments or time tracking.
- There are only three vehicle types supported by the parking lot: Motorcycle, Car, and Van.
- There are only two spot types: Compact and Regular.
- Vehicles have unique identifiers.
- The parking lot does not support reservations or advanced booking
- The parking lot size cannot be changed at runtime.
- The parking lot only has one access point for entry and exit.

## Possible Improvements
- Implement ParkingLot as a callable REST API.
- Add support for reservations and advanced bookings.
- Implement a more sophisticated allocation strategy (e.g., nearest spot to entrance).
- Add support for different vehicle and spot types (e.g., electric vehicles, handicapped spots).
- Implement ticketing and advance reservation features.
- Implement concurrency handling for multithreaded environments to simulate multiple entry points to parking spot.
- Implement different parking lot admin types.
- Add a GUI for better user interaction instead of CLI.

---

## Build & Run

### Prerequisites
- Java 21
- Maven 3.9 or later

### Compile
```bash
mvn clean compile
```
### Run Tests
```bash
mvn test
```

### Run Application
The project includes a simple CLI demo (ParkingLotApplication).
From the project root, run:

```bash
mvn exec:java -Dexec.mainClass="ParkingLotApplication" <numOfRows> <rowSequence> COMPACT_REGULAR LIVE
```
This will execute a demo scenario showcasing parking lot operations.

### Continuous Integration
The project is set up with GitHub Actions to run tests on each push and pull request.

### Project Structure
- `src/main/java` - Main application code
- `src/test/java` - Unit and integration tests
- `pom.xml` - Maven build configuration

---