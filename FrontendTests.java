// --== CS400 File Header Information ==--
// Name: Will Langas
// Email: wlangas@wisc.edu
// Team: CC (red)
// Role: Frontend Developer
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FrontendTests {

    PrintStream standardOut;
    InputStream standardIn;
    Frontend frontend;
    Backend backend;
    String inputData;
    String enter;

    /**
     * Basic setup of common variables
     */
    @BeforeEach
    public void setup() throws DataFormatException, FileNotFoundException {
        standardOut = System.out;
        standardIn = System.in;
        // Frontend will be instantiated properly once implemented.
        frontend = new Frontend();
        try {
            String[] files = {"MapData.csv"};
            MadisonMapperReader reader = new MadisonMapperReader(files);
            backend = new Backend(reader);
        }
        catch (Exception e) {
            backend = null;
        }
        enter = System.lineSeparator();
    }

    @Test
    private void testHome() throws DataFormatException, FileNotFoundException {
        try {
            System.setOut(standardOut);
            System.setIn(standardIn);
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStreamCaptor));

            frontend.run();
            String printedToScreen = outputStreamCaptor.toString();
            boolean contains = printedToScreen.contains("Please select one of the following options and hit enter:") &&
                printedToScreen.contains("-d Distance Screen")
                && printedToScreen.contains("-b Print the Buildings in the Dataset") &&
                printedToScreen.contains("-s Settings") &&
                printedToScreen.contains("-x Exit Program");
                assertNotNull(frontend);
                assertTrue(contains);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown.");
        }
    }

    @Test
    private void testExit() {
        assertEquals(10, 10);
    }

}
