// --== CS400 File Header Information ==--
// Name: Will Langas
// Email: wlangas@wisc.edu
// Team: CC (red)
// Role: Frontend Developer
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Frontend {

    private Backend backend;
    private Scanner scnr;
    private String mode;

    private String[] files = {"data.csv"};
    private MadisonMapperReader reader = new MadisonMapperReader(files);
    private List<BuildingInterface> buildings = reader.getBuildings();

    public static void main(String[] args) throws DataFormatException, FileNotFoundException {
        Frontend frontend = new Frontend();
        frontend.run();
    }

    public Frontend() throws DataFormatException, FileNotFoundException {
        this.mode = "base";
    }

    public void run() throws DataFormatException, FileNotFoundException {
        boolean noError = true;

        System.out.println("-----------------------------");
        System.out.println("| Welcome to Wisconsin Maps |");
        System.out.println("-----------------------------\n");

        try {
            backend = new Backend(reader);

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
        } else if (command.equals("s")) {
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
     * and getting the distance to a category of locations
     */
    private void distance() {
        System.out.println("Please select one of the following options then hit enter");
        System.out.println("-w Estimate Walking Time Between Two Buildings");
        System.out.println("-b List the Buildings Passed on the Path Between Two Buildings");
        System.out.println("-c Get the Distance to a Category of Locations");
        System.out.println("-x Go Back to Home Screen");

        String command = scnr.nextLine();
        if (command.equals("w")) {
            walkingTime();
        } else if (command.equals("b")) {
            listBuildings();
        } else if (command.equals("c")) {
            category();
        } else if (command.equals("x")) {
            baseMode();
        } else {
            System.out.println("Unrecognized Option, please try again");
            distance();
        }
    }

    /**
     * Find the walking time between two buildings
     */
    private void walkingTime() {
        List<BuildingInterface> buildings = reader.getBuildings();

        System.out.println("Enter the starting building, or x to exit the list screen: ");
        String startBuilding = scnr.nextLine();
        if (startBuilding.equals("x")) distance();

        System.out.println("Enter the ending building, or x to exit the list screen: ");
        String endBuilding = scnr.nextLine();
        if (endBuilding.equals("x")) distance();

        // Find and print the walking time in between them
    }

    /**
     * List the buildings that would be passed on a certain path
     */
    private void listBuildings() {
        System.out.println("Enter the starting building, or x to exit the list screen: ");
        String startBuilding = scnr.nextLine();
        if (startBuilding.equals("x")) distance();

        // TODO: FIND THE BUILDING OBJECT

        System.out.println("Enter the ending building, or x to exit the list screen: ");
        String endBuilding = scnr.nextLine();
        if (endBuilding.equals("x")) distance();

        // TODO: FIND THE BUILDING OBJECT

        // TODO: FIND THE LIST OF BUILDINGS ON THE PATH AND PRINT THEM

    }

    /**
     * Find distance to a category of locations using a category identifier
     */
    private void category() {
        // Print the category identifiers
        String[] identifiers = {"R - Residential", "E - Educational", "F - Fitness", "D - Dining",
            "T - Shopping", "S -Student Life", "L - Library", "A - Athletics", "M - Entertainment",
            "P - Path", "K - Parking"};

        System.out.println("The building identifiers are as follows: ");
        for (int i = 0; i < identifiers.length; ++i) {
            System.out.println(identifiers[i]);
        }

        System.out.println("\nEnter an identifier, or x to exit the category function: ");
        String ident = scnr.nextLine().trim().toLowerCase(); // TODO: Grab the identifier

        if (ident.equals("x")) {
            distance();
        } else {
            // TODO: PRINT ALL THE BUILDINGS WITH THE IDENTIFIER
        }
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
            backend.setTravelSpeed(speed);
        } else if (command.equals("tc")) {
            System.out.println("Enter a multiplier based on the conditions (Ex: Snow might make a"
                + "path take 2x as long, so enter 2.0): ");
            double multiplier = scnr.nextDouble();
            backend.setTravelConditions(multiplier);
        } else {
            System.out.println("Unrecognized Option, please try again");
            settings();
        }
    }

    /*
     *       -----------------------------
     *       |       Print Buildings     |
     *       -----------------------------
     */

    /**
     * Print's all the buildings and their types in the Data Set to the console
     */
    private void printBuildings() {
        System.out.println("The List of Buildings at UW-Madison: ");
        for (BuildingInterface building : buildings) {
            System.out.println(building.getName() + " | " + Arrays.toString(building.getTypes()));
        }
    }
}
