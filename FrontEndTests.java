// --== CS400 File Header Information ==--
// Name: Will Langas
// Email: wlangas@wisc.edu
// Team: CC (red)
// Role: Frontend Developer
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: These tests are based on Colby and Jeremy's testing code from P1 & P2

import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.DataFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class includes 5 distinct Junit tests that test the frontend of the Wisconsin Maps program.
 * It ensures that the different screens of the program are displaying the correct information when
 * accessed.
 *
 * @author Will Langas
 */
class FrontEndTests {

    PrintStream standardOut;
    InputStream standardIn;
    Frontend frontend;
    Backend backend;
    String enter;

    /**
     * Setup and resetting of common variables that are used before each test
     */
    @BeforeEach void setup() throws DataFormatException, FileNotFoundException {
        standardOut = System.out;
        standardIn = System.in;
        // Frontend will be instantiated properly once implemented.
        frontend = new Frontend();

        String[] files = {"MapData.csv"}; // Read in the data
        MadisonMapperReader reader = new MadisonMapperReader(files);
        backend = new Backend(reader);

        enter = System.lineSeparator();
    }

    /**
     * Tests that by entering the exit key "x", the program successfully exits
     */
    @Test
    void enterXToExit() {
        try {
            // set the input stream to our input (with an x to test of the program exists)
            String input = "x" + enter;
            InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStreamSimulator);

            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            // set the output to the stream captor to read the output of the front end
            System.setOut(new PrintStream(outputStreamCaptor));

            frontend.run(); // Run the frontend

            // set the output back to standard out for running the test
            System.setOut(standardOut);
            // same for standard in
            System.setIn(standardIn);

            // Make sure the program was exited correctly
        } catch (Exception e) {
            // make sure stdin and stdout are set correctly after we get exception in test
            System.setOut(standardOut);
            System.setIn(standardIn);
            e.printStackTrace();
            // test failed
            fail("Exception caught");
        }
    }

    /**
     * Makes sure the proper home menu appears when the program is started up
     */
    @Test
    void testHome() {
        try {
            String input = "x" + enter;
            InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStreamSimulator);
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            // set the output to the stream captor to read the output of the front end
            System.setOut(new PrintStream(outputStreamCaptor));

            // Running program itself
            frontend.run();

            // set the output back to standard out for running the test
            System.setOut(standardOut);
            // same for standard in
            System.setIn(standardIn);
            String printedToScreen = outputStreamCaptor.toString();

            // Make sure the output contains the correct menu
            boolean contains = printedToScreen
                .contains("Please select one of the following options and hit enter:")
                && printedToScreen.contains("-d Distance Screen") && printedToScreen
                .contains("-b Print the Buildings in the Dataset") && printedToScreen
                .contains("-s Settings") && printedToScreen.contains("-x Exit Program");
            assert (contains);
        } catch (Exception e) {
            // make sure stdin and stdout are set correctly after we get exception in test
            System.setOut(standardOut);
            System.setIn(standardIn);
            e.printStackTrace();
            // test failed
            fail("Exception caught");
        }
    }

    /**
     * Tests that all necessary fields are displayed when the distance menu is opened from the
     * home menu.
     */
    @Test
    void testDistanceMenu() {
        try {
            String input = "d" + enter + "x" + enter + "x" + enter;
            InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStreamSimulator);
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            // set the output to the stream captor to read the output of the front end
            System.setOut(new PrintStream(outputStreamCaptor));

            // Running program itself
            frontend.run();

            // set the output back to standard out for running the test
            System.setOut(standardOut);
            // same for standard in
            System.setIn(standardIn);
            String printedToScreen = outputStreamCaptor.toString();

            // Make sure the output contains the correct menu
            boolean contains = printedToScreen
                .contains("Please select one of the following options then hit enter")
                && printedToScreen.contains("-w Estimate Walking Time Between Two Buildings")
                && printedToScreen.contains("-b List the Buildings Passed on the Path Between Two Buildings")
                && printedToScreen.contains("-c Find the Walking Time to The Closest Building of a Certain Type")
                && printedToScreen.contains("-p Find the Path to The Closest Building of a Certain Type")
                && printedToScreen.contains("-x Go Back to Home Screen");
            assert (contains);
        } catch (Exception e) {
            // make sure stdin and stdout are set correctly after we get exception in test
            System.setOut(standardOut);
            System.setIn(standardIn);
            e.printStackTrace();
            // test failed
            fail("Exception caught");
        }
    }

    /**
     * Tests that when all the buildings in the dataset are printed out, a randomly selected sample
     * of buildings appear in the output. A sample is used due to the large size of the dataset.
     */
    @Test
    void testPrintBuildings() {
        try {
            String input = "b" + enter + "x" + enter;
            InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStreamSimulator);
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            // set the output to the stream captor to read the output of the front end
            System.setOut(new PrintStream(outputStreamCaptor));

            // Running program itself
            frontend.run();

            // set the output back to standard out for running the test
            System.setOut(standardOut);
            // same for standard in
            System.setIn(standardIn);
            String printedToScreen = outputStreamCaptor.toString();

            // Make sure the output contains a sample of building objects
            boolean contains = printedToScreen.contains("Linden & Henry (WB) | 04") &&
                printedToScreen.contains("Observatory & Babcock (WB) | 04") &&
                printedToScreen.contains("Fjällräven | B");
            assert (contains);
        } catch (Exception e) {
            // make sure stdin and stdout are set correctly after we get exception in test
            System.setOut(standardOut);
            System.setIn(standardIn);
            e.printStackTrace();
            // test failed
            fail("Exception caught");
        }
    }

    /**
     * Tests that the settings menu is properly displayed
     */
    @Test
    void testSettings() {
        try {
            String input = "s" + enter + "x" + enter + "x" + enter;
            InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStreamSimulator);
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            // set the output to the stream captor to read the output of the front end
            System.setOut(new PrintStream(outputStreamCaptor));

            // Running program itself
            frontend.run();

            // set the output back to standard out for running the test
            System.setOut(standardOut);
            // same for standard in
            System.setIn(standardIn);
            String printedToScreen = outputStreamCaptor.toString();

            // Make sure the output contains the correct menu
            boolean contains = printedToScreen
                .contains("Please select one of the following options then hit enter")
                && printedToScreen.contains("-ts Set Travel Speed")
                && printedToScreen.contains("-tc Set Travel Conditions");
            assert (contains);
        } catch (Exception e) {
            // make sure stdin and stdout are set correctly after we get exception in test
            System.setOut(standardOut);
            System.setIn(standardIn);
            e.printStackTrace();
            // test failed
            fail("Exception caught");
        }
    }
}
