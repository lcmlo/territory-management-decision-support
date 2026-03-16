# Territory Management Decision Support

## Project Overview
This project provides a decision-support application aimed at promoting a more balanced and sustainable management of the Portuguese territory. Developed for the Software Engineering (Engenharia de Software) course during the 3rd year of the BSc degree, the system tackles the severe issue of rustic property fragmentation in the country's interior. By analyzing spatial data and ownership structures, the software calculates contiguous areas and suggests strategic property swaps to ensure viability for agricultural and forestry exploitation.

## Team Members
* Afonso Valente (GitHub: abcvalente)
* Luis Lourenco (GitHub: lcmlo)
* Pedro Li (GitHub: Pclin0620)

## Technologies Used
* Language: Java 22
* Build Tool: Apache Maven
* Graph Data Structures: JGraphT
* Graph Visualization: JGraphX
* Spatial Analysis: JTS Topology Suite
* Data Parsing: OpenCSV
* Testing: JUnit 5

## Software Quality and Testing
To ensure high maintainability and reliability, the project was subjected to rigorous code quality checks and testing. Detailed reports and documentation are available in their respective directories within this repository:
* **Cyclomatic Complexity**: Analysis and metrics to evaluate code complexity are located in the `Complexidade ciclomatica` folder.
* **Test Coverage**: The complete testing coverage report is available in the `Coverage report` folder.
* **Code Documentation**: The generated API documentation can be found in the `Javadoc` folder.

## Agile Project Management
The development lifecycle strictly followed the Scrum framework. Task allocation, sprint planning, and progress tracking were managed via Trello to ensure iterative delivery and team synchronization. The management boards can be consulted below:
* Product Backlog: https://trello.com/b/5d7crjAy/product-backlog
* Sprint 1: https://trello.com/b/bINp1L1F/sprint-1
* Sprint 2: https://trello.com/b/hfWeRRa2/sprint-2

## Key Features
* Geographic Filtering & Area Calculation: Calculates the average area of properties within specific geographic zones (parish, municipality, or island) while intelligently grouping adjacent properties belonging to the same owner.
* Property & Ownership Graphs: Builds adjacency graphs for physical properties and a secondary graph linking neighboring property owners.
* Swap Suggestions: Implements an algorithm to suggest property exchanges between neighbors. It evaluates continuous area improvements, area similarity, compactness index, and distance to major municipalities.
* Spatial Geometry Processing: Reads WKT (Well-Known Text) geometries to compute centroids, perimeters, areas, and exact distances using the Haversine formula.
* Graph Visualization: Provides a graphical user interface to visualize the generated networks of properties and owners.

## How to Run
1. Ensure you have Java JDK 22 and Apache Maven installed.
2. Clone this repository to your local machine.
3. Place the required dataset file (e.g., `111.csv` or `Madeira-Moodle-1.1.csv`) in the project's root directory.
4. Open a terminal in the project's root directory and build the project:
   `mvn clean install`
5. Run the main application:
   `mvn exec:java -Dexec.mainClass="iscteiul.ista.Main"`