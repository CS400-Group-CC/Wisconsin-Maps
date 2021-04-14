// --== CS400 File Header Information ==--
// Name: Will Langas
// Email: wlangas@wisc.edu
// Team: CC (red)
// Role: Frontend Developer
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * This class controls the frontend of the Wisconsin Maps program. It displays all appropriate
 * menus, and when requested, calls the backend methods and displays the results.
 *
 * @author Will Langas
 */
public class Frontend {

    private Backend backend;
    private Scanner scnr;
    private String mode;

    // Need to read through the data twice so we can filter through the buildings in one list and
    // remove paths
    private String[] files = {"Dataset.csv"};
    private MadisonMapperReader reader = new MadisonMapperReader(files);
    private MadisonMapperReader secondReader = new MadisonMapperReader(files);
    private List<BuildingInterface> allBuildings = reader.getBuildings();
    private List<BuildingInterface> buildings = secondReader.getBuildings();

    /**
     * The main method for this class simply calls Frontend.run(), which controls the flow of the
     * program.
     *
     * @param args No arguments are passed here
     * @throws DataFormatException Passed by the data reader if the csv is not correctly formatted
     * @throws FileNotFoundException This is passed by the data reader if the file is not found
     */
    public static void main(String[] args) throws DataFormatException, FileNotFoundException {
        Frontend frontend = new Frontend();
        frontend.run();
    }

    /**
     * The default constructor for the Frontend, it simply filters out edges from the classes list
     * of buildings.
     *
     * @throws DataFormatException Passed by the data reader if the csv is not correctly formatted
     * @throws FileNotFoundException This is passed by the data reader if the file is not found
     */
    public Frontend() throws DataFormatException, FileNotFoundException {
        this.mode = "base";
        this.buildings.removeIf(building -> building.getTypes().contains("P")); // Filter out edges
    }

    /**
     * The run() method is what controls the overall flow of the program, and when requested, exits
     * the program.
     *
     * @throws DataFormatException Passed by the data reader if the csv is not correctly formatted
     * @throws FileNotFoundException This is passed by the data reader if the file is not found
     */
    public void run() throws DataFormatException, FileNotFoundException {
        boolean noError = true;

        System.out.println("-----------------------------");
        System.out.println("| Welcome to Wisconsin Maps |"); // Welcome screen
        System.out.println("-----------------------------\n");

        try {
            backend = new Backend(reader);
            buildings = backend.getBuildings();
        } catch (DataFormatException d) {
            System.out.println("Data format issue.");
            noError = false;
        }

        if (noError) {
            mode = "base";
            scnr = new Scanner(System.in);
            boolean running = true;
            while (running) {
                if (mode == "base") {
                    baseMode();
                } else if (mode == "end") {
                    System.out.println("Goodbye.");
                    running = false;
                }
            }
        }
    }

    /*
     *       -----------------------------
     *       |         Base Mode         |
     *       -----------------------------
     */

    /**
     * The "home" menu for the program, it houses the three main functionality menus of the program,
     * and helps navigate the user between them.
     */
    private void baseMode() {
        System.out.println("Please select one of the following options and hit enter:");
        System.out.println("-d Distance Screen");
        System.out.println("-b Print the Buildings in the Dataset");
        System.out.println("-s Settings");
        System.out.println("-x Exit Program");

        String command = scnr.nextLine();
        if (command.equals("d")) {
            System.out.println("-----------------------------");
            System.out.println("|           Distance        |");
            System.out.println("-----------------------------\n");
            distance();
        } else if (command.equals("s")) {
            System.out.println("-----------------------------");
            System.out.println("|          Settings         |");
            System.out.println("-----------------------------\n");
            settings();
        } else if (command.equals("b")) {
                System.out.println("-----------------------------");
                System.out.println("|       List Buildings      |");
                System.out.println("-----------------------------\n");
                printBuildings();
        } else if (command.equals("x")) {
            mode = "end";
        } else {
            System.out.println("Unrecognized Option, please try again");
            baseMode();
        }
    }

    /*
     *   -----------------------------
     *   |         Distance          |
     *   -----------------------------
     */

    /**
     * The main distance screen which holds the options for walking time, listing buildings passed,
     * and getting the distance to a category of locations.
     */
    private void distance() {
        System.out.println("Please select one of the following options then hit enter");
        System.out.println("-w Estimate Walking Time Between Two Buildings");
        System.out.println("-b List the Buildings Passed on the Path Between Two Buildings");
        System.out.println("-c Find the Walking Time to The Closest Building of a Certain Type");
        System.out.println("-p Find the Path to The Closest Building of a Certain Type");
        System.out.println("-x Go Back to Home Screen");

        String command = scnr.nextLine();
        if (command.equals("w")) {
            System.out.println("");
            walkingTime();
        } else if (command.equals("b")) {
            System.out.println("");
            listBuildings();
        } else if (command.equals("c")) {
            System.out.println("");
            printIdents();
            System.out.println("");
            category();
        } else if (command.equals("p")) {
            System.out.println("");
            printIdents();
            System.out.println("");
            categoryPath();
        } else if (command.equals("x")) {
            System.out.println("");
            baseMode();
        } else {
            System.out.println("Unrecognized Option, please try again");
            distance();
        }
    }

    /**
     * This method walks the user through the process of finding the walking time between two
     * buildings.
     */
    private void walkingTime() {
        //List<BuildingInterface> buildings = reader.getBuildings();

        System.out.println("Enter the starting building, or x to exit the list screen: ");
        String startBuilding = scnr.nextLine(); // Grab input
        if (startBuilding.equals("x")) distance(); // Exit if needed
        BuildingInterface start = findByName(startBuilding);
        System.out.println("You chose: " + start.getName() + " | " + start.getTypes());
        System.out.println("");

        // Repeat the above but for the end building
        System.out.println("Enter the ending building, or x to exit the list screen: ");
        String endBuilding = scnr.nextLine();
        if (endBuilding.equals("x")) distance();
        BuildingInterface end = findByName(endBuilding);
        assert end != null;
        System.out.println("You chose: " + end.getName() + " | " + end.getTypes());
        System.out.println("");

        // Find and print the walking time in between them
        double time = backend.getPathTime(start, end);
        int timeMins = (int) time;
        int timeSecs = (int) Math.round(60 * (time - timeMins));
        System.out.printf("Expected time for path is %d:%02d", timeMins, timeSecs);
        System.out.println("");
    }

    /**
     * This method walks the user through the process of listing the buildings that would be passed
     * when walking between two buildings.
     */
    private void listBuildings() {
        System.out.println("Enter the starting building, or x to exit the list screen: ");
        String startBuilding = scnr.nextLine();
        if (startBuilding.equals("x")) distance();
        BuildingInterface start = findByName(startBuilding);

        if (start == null) {
            System.out.println("Building not found, please start this step over");
            listBuildings();
        }

        System.out.println("You chose: " + start.getName() + " | " + start.getTypes());
        System.out.println("");

        System.out.println("Enter the ending building, or x to exit the list screen: ");
        String endBuilding = scnr.nextLine();
        if (endBuilding.equals("x")) {
            System.out.println("");
            distance();
        }
        BuildingInterface end = findByName(endBuilding);

        if (end == null) {
            System.out.println("Building not found, please start this step over");
            listBuildings();
        }

        System.out.println("You chose: " + end.getName() + " | " + end.getTypes());
        System.out.println("");

        // Grab the list of buildings passed and print them out
        List<BuildingInterface> results = backend.getAlongPath(start, end);
        System.out.println("When traveling from " + start.getName() + " to " + end.getName() + ", "
            + "you will pass the following buildings: ");
        for (int i = 0; i < results.size(); ++i) {
            System.out.println((i+1) + ". " + results.get(i).getName());
        }
        System.out.println("");
    }

    /**
     * This method walks the user through the process of finding the nearest building of a certain
     * type and displaying how many minutes away that building is.
     */
    private void category() {

        System.out.println("\nEnter your starting building: ");
        String startBuilding = scnr.nextLine().trim();
        BuildingInterface start = findByName(startBuilding);
        if (start == null) {
            System.out.println("Building not found, please start this step over");
            category();
        }

        // Enter the one character identifier
        System.out.println("\nEnter the category identifier: ");
        char ident = scnr.next().charAt(0);
        if (ident == 'x') {
            System.out.println("");
            distance();
        } else {
            try {
                System.out.println("Searching now...");
                double time = backend.getGeneralPathTime(start, ident);
                int timeMins = (int) time;
                int timeSecs = (int) Math.round(60 * (time - timeMins));
                System.out.print(
                    "The closest building of type " + ident + " is " + timeMins + ":" + timeSecs + " away\n");
                System.out.println("");
            } catch (Exception e) {
                System.out.println("Invalid identifier, or no buildings could be found, please"
                    + " try again\n");
                category();
            }
        }
    }

    /**
     * This method walks the user through the process of finding the nearest building of a certain
     * type and displaying which buildings are passed on the way to that nearest building.
     */
    private void categoryPath() {

        System.out.println("\nEnter your starting building: ");
        String startBuilding = scnr.nextLine().trim();
        BuildingInterface start = findByName(startBuilding);
        if (start == null) {
            System.out.println("Building not found, please start this step over");
            categoryPath();
        }

        System.out.println("\nEnter the category identifier: ");
        char ident = scnr.next().charAt(0);
        if (ident == 'x') {
            System.out.println("");
            distance();
        } else {
            if (ident == start.getTypes().charAt(0)) {
                System.out.println("Start building identifier and selected identifier can not be "
                    + "the same. Please restart this step");
                categoryPath();
            }
            try {
               System.out.println("Searching now...");
               List<BuildingInterface> resultPath = backend.getAlongGeneralPath(start, ident);

               System.out.println("The shortest path from " + startBuilding + " to a building of type"
                   + " " + ident + " passes the following buildings: ");
                for (int i = 0; i < resultPath.size(); ++i) {
                    System.out.println((i+1) + ". " + resultPath.get(i).getName());
                }
                System.out.println("");
                scnr.nextLine();
                distance();
            } catch (Exception e) {
                System.out.println("Invalid identifier, or no buildings could be found, please"
                    + " try again\n");
                categoryPath();
            }
        }
    }

    /**
     * This method prints all the building type identifiers
     */
    private void printIdents() {
        // Print the category identifiers
        String[] identifiers = {"B - Building","K - Parking","L - Library","D - Dorm","H - Housing",
            "F - Food hall","R - Restaurant","E - Education","S - Shopping","A - Athletics","O - Outdoor",
            "M - Museum","X - Infrastructure/Service/Admin","T - Religious","0 - Bus stop",
            "1 - Northbound bus stop","2 - Southbound bus stop","3 - Eastbound bus stop",
            "4 - Westbound bus stop"};

        System.out.println("The building identifiers are as follows: ");
        for (String identifier : identifiers) {
            System.out.println(identifier);
        }
    }

    /**
     * This method finds and returns a building using it's String name to find it.
     *
     * @param name A string representation of a building
     * @return building A BuildingInterface object of that building
     */
    private BuildingInterface findByName(String name) {
        for (BuildingInterface building : this.buildings) {
            if (building.getName().equals(name)) { // Search through buildings for a match
                return building;
            }
        }
        return null;
    }

    /*
     *       -----------------------------
     *       |          Settings         |
     *       -----------------------------
     */

    /**
     * Allows the user to change the travel speed or travel conditions that might affect the speed
     */
    private void settings() {
        System.out.println("Please select one of the following options then hit enter");
        System.out.println("-ts Set Travel Speed");
        System.out.println("-tc Set Travel Conditions");

        String command = scnr.nextLine();
        if (command.equals("ts")) {
            System.out.println("Enter your travel speed (In Feet Per Second): ");
            double speed = scnr.nextDouble();
            backend.setTravelSpeed(speed); // Set the speed
            System.out.println("Travel Speed set successfully\n");
            scnr.nextLine();
            baseMode();
        } else if (command.equals("tc")) {
            System.out.println("Enter a multiplier based on the conditions (Ex: Snow might make a"
                + "path take 2x as long, so enter 2.0): ");
            double multiplier = scnr.nextDouble();
            backend.setTravelConditions(multiplier); // Set the conditions
            System.out.println("Travel Conditions set successfully\n");
            scnr.nextLine();
            baseMode();
        } else if (command.equals("x")) {
            System.out.println("");
            baseMode();
        } else {
            System.out.println("Unrecognized Option, please try again");
            System.out.println("");
            settings();
        }
    }

    /*
     *       -----------------------------
     *       |       Print Buildings     |
     *       -----------------------------
     */

    /**
     * Print's all the buildings and their types in the Data Set to the console.
     */
    private void printBuildings() {
        System.out.println("The List of Buildings at UW-Madison: ");
        for (BuildingInterface building : buildings) {
            if (!building.getTypes().contains("P") && !building.getTypes().contains("0")) {
                System.out.println(building.getName() + " | " + building.getTypes());
            }
        }
        System.out.println("");
    }
}
