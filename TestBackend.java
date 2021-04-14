// --== CS400 File Header Information ==--
// Name: Jeremy Peplinski
// Email: japeplinski@wisc.edu
// Team: Red
// Group: CC
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None
///////////////////////////////////////////////////////////////////////////////

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A class of JUnit tests designed to test the Backend class. JUnit tests essentially test to ensure
 * that no catastrophic errors occur, with the rest of the analysis done through printouts and other
 * manual tests.
 * 
 * @author Jeremy Peplinski
 */
class TestBackend {

  MadisonMapperReader dataReader;
  String args[] = {"Dataset.csv"};

  /**
   * A method to set up a data reader object before each test, leaving it null if some exception is
   * thrown.
   */
  @BeforeEach
  public void setup() {
    try {
      dataReader = new MadisonMapperReader(args);
    } catch (DataFormatException format) {
      format.printStackTrace();
    } catch (FileNotFoundException file) {
      file.printStackTrace();
    }
  }

  /**
   * A method to test the Backend constructor, currently testing the command line argument version.
   * This simply ensures that the constructor does not throw exceptions.
   */
  @Test
  void testConstructor() {
    if (dataReader == null) {
      fail("MadisonMapperReader constructor failed.");
    }
    Backend backend;
    try {
      backend = new Backend(args);
    } catch (DataFormatException e) {
      e.printStackTrace();
      fail("Constructor threw unexpected format exception.");
    } catch (Exception general) {
      general.printStackTrace();
      fail("Constructor threw unexpected exception type.");
    }
  }

  /**
   * A method to test the getGeneralPath method. The JUnit test itself only fails if exceptions are
   * thrown, with printouts being used to debug.
   */
  @Test
  void testGeneralPath() {
    if (dataReader == null) {
      fail("MadisonMapperReader constructor failed.");
    }
    Backend backend;
    try {
      backend = new Backend(args);
      BuildingInterface base = backend.getBuildings().get(0);

      // Search for the shortest general path to a building (specifier: B)
      List<BuildingInterface> returned = backend.shortestGeneralPath(base, 'B');

      System.out.println("Returned path:");
      System.out.println("Types\tLatitude\tLongitude\tName");
      BuildingInterface current;
      for (int i = 0; i < returned.size(); i++) {
        current = returned.get(i);
        System.out.println(current.getTypes() + "\t" + current.getLat() + "\t" + current.getLon()
            + "\t" + current.getName());
      }
    } catch (DataFormatException e) {
      e.printStackTrace();
      fail("Constructor threw unexpected format exception.");
    } catch (NoSuchElementException notFound) {
      notFound.printStackTrace();
      fail("getGeneralPath threw unexpected notFound exception.");
    } catch (Exception general) {
      general.printStackTrace();
      fail("Constructor threw unexpected exception type.");
    }
  }

  /**
   * A method to test the getAlongPath method, printing out the path taken and the returned
   * buildings along it for manual review, and failing the JUnit test if an exception is thrown.
   */
  @Test
  void testAlongPath() {
    if (dataReader == null) {
      fail("MadisonMapperReader constructor failed.");
    }
    Backend backend;
    try {
      backend = new Backend(args);
      BuildingInterface base = backend.getBuildings().get(0);
      BuildingInterface dest = backend.getBuildings().get(92);

      // Get path itself for reference
      List<BuildingInterface> path = backend.shortestPath(base, dest);

      System.out.println("Returned path:");
      System.out.println("Types\tLatitude\tLongitude\tName");
      BuildingInterface current;
      for (int i = 0; i < path.size(); i++) {
        current = path.get(i);
        System.out.println(current.getTypes() + "\t" + current.getLat() + "\t" + current.getLon()
            + "\t" + current.getName());
      }

      // Search for the buildings along a given path
      List<BuildingInterface> returned = backend.getAlongPath(base, dest);

      System.out.println("Returned buildings along path:");
      System.out.println("Types\tLatitude\tLongitude\tName");
      for (int i = 0; i < returned.size(); i++) {
        current = returned.get(i);
        System.out.println(current.getTypes() + "\t" + current.getLat() + "\t" + current.getLon()
            + "\t" + current.getName());
      }
    } catch (DataFormatException e) {
      e.printStackTrace();
      fail("Constructor threw unexpected format exception.");
    } catch (NoSuchElementException notFound) {
      notFound.printStackTrace();
      fail("getAlongPath threw unexpected notFound exception.");
    } catch (Exception general) {
      general.printStackTrace();
      fail("Constructor threw unexpected exception type.");
    }
  }

  /**
   * A method to test the getGeneraPathTime method, printing out the path taken and the expected
   * times at various speeds and in various conditions for manual review, and failing the JUnit test
   * if an exception is thrown.
   */
  @Test
  void testGeneralPathTime() {
    if (dataReader == null) {
      fail("MadisonMapperReader constructor failed.");
    }
    Backend backend;
    try {
      backend = new Backend(args);
      BuildingInterface curr = backend.getBuildings().get(0);

      // Find shortest path from Camp Randall to a library and print out stuff for debugging

      List<BuildingInterface> path = backend.shortestGeneralPath(curr, 'L');

      System.out
          .println("Shortest path was from " + path.get(0) + " to " + path.get(path.size() - 1));

      double time = backend.getGeneralPathTime(curr, 'L');
      int timeMins = (int) time;
      int timeSecs = (int) Math.round(60 * (time - timeMins));
      System.out.println("Expected time for path is " + timeMins + ":" + timeSecs);

      backend.setTravelConditions(2.0);

      time = backend.getGeneralPathTime(curr, 'L');
      timeMins = (int) time;
      timeSecs = (int) Math.round(60 * (time - timeMins));
      System.out
          .println("Expected time for path in horrible conditions is " + timeMins + ":" + timeSecs);

      backend.setTravelConditions(1.0);
      backend.setTravelSpeed(5.0);

      time = backend.getGeneralPathTime(curr, 'L');
      timeMins = (int) time;
      timeSecs = (int) Math.round(60 * (time - timeMins));
      System.out
          .println("Expected time for path while speedwalking is " + timeMins + ":" + timeSecs);
    } catch (DataFormatException e) {
      e.printStackTrace();
      fail("Constructor threw unexpected format exception.");
    } catch (NoSuchElementException notFound) {
      notFound.printStackTrace();
      fail("getGeneralPathTime threw unexpected notFound exception.");
    } catch (Exception general) {
      general.printStackTrace();
      fail("Constructor threw unexpected exception type.");
    }
  }

  /**
   * A method to test the getPathKML methods, which are largely for internal testing and
   * demonstration purposes (though could easily be integrated into the UI). This generates KML
   * files for between two arbitrary points and from one of these points to any other building,
   * providing an effective stress test of the KML-writing system. JUnit tests only fail if
   * exceptions are thrown.
   */
  @Test
  void testGetPathKML() {
    if (dataReader == null) {
      fail("MadisonMapperReader constructor failed.");
    }
    Backend backend;
    try {
      backend = new Backend(args);
      BuildingInterface curr = backend.getBuildings().get(143);
      BuildingInterface dest = backend.getBuildings().get(62);

      // Testing both the single-path and general-path kml outputs for completeness
      List<BuildingInterface> path = backend.shortestPath(curr, dest);
      System.out.println(path);
      System.out.println("File written to " + backend.getPathKML(path));
      System.out.println("Testing general kml writer with shortestGeneralPath and genKMLs true.");
      backend.setKMLGen(true);
      backend.shortestGeneralPath(curr, 'B');
    } catch (DataFormatException e) {
      e.printStackTrace();
      fail("Constructor threw unexpected format exception.");
    } catch (NoSuchElementException notFound) {
      notFound.printStackTrace();
      fail("getGeneralPath threw unexpected notFound exception.");
    } catch (IOException file) {
      file.printStackTrace();
      fail("getPathKML threw a file-writing exception.");
    } catch (Exception general) {
      general.printStackTrace();
      fail("Constructor threw unexpected exception type.");
    }
  }
}
