# Avant Parking Lot System

This project implements a parking lot management system as part of the Avant take-home assignment.  
It demonstrates object-oriented design, vehicle allocation logic, summary reporting, and automated tests.

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
mvn exec:java -Dexec.mainClass="ParkingLotApplication"
```
This will execute a demo scenario showcasing parking lot operations.

### Continuous Integration
The project is set up with GitHub Actions to run tests on each push and pull request.

### Project Structure
- `src/main/java` - Main application code
- `src/test/java` - Unit and integration tests
- `pom.xml` - Maven build configuration

---