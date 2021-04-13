import static org.junit.jupiter.api.Assertions.*;
import java.awt.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//--== CS400 File Header Information ==--
//Name: Colby Brown
//Email: csbrown7@wisc.edu
//Team: CC - Red
//Role: DW
//TA: Xi Chen
//Lecturer: Gary Dahl
//Notes to Grader:

class DataWranglerTests {

  MadisonMapperReader mapperReader;
  
  
  /**
   * Sets up the mapperReader object before each test
   */
  @BeforeEach
  public void setUp() {
    String csv = "Building Name,Building Type (R-Residential | E-Educational | F-Fitness | D-Dining | T-Shopping  | S-Student Life | L-Library | A-Athletics | M-Entertainment | P-Path | K-Parking),Latitude,Longitude,Connected Nodes\n" + 
        "Witte Residence Hall,R,43.071712,-89.3992113,Dept. of Computer Science;Memorial Union Terrace\n" + 
        "Camp Randall Stadium,F,43.0713044,-89.4144677,Trader Joe's;Dept. of Computer Science\n" + 
        "Memorial Union Terrace,L,43.0762566,-89.4020182,Dept. of Computer Science;Witte Residence Hall\n" + 
        "Dept. of Computer Science,E,43.0713714,-89.4078986,Witte Residence Hall;Camp Randall Stadium;Memorial Union Terrace\n" + 
        "Trader Joe's,S,43.0647427,-89.4264225,Camp Randall Stadium";
    
    Scanner scanner = new Scanner(csv);
    try {
      mapperReader = new MadisonMapperReader(scanner);
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Tests the constructor of the MadisonMapperReader class to ensure that buildings are loaded properly
   * and the file is read correctly
   */
  @Test
  public void testConstructor() {
    ArrayList<BuildingInterface> buildings = (ArrayList<BuildingInterface>)mapperReader.getBuildings();
    assertEquals(buildings.size(), 5);
  }
  
  /**
   * Tests that the mapperReader returns the correct set of buildings as matching the dataset
   */
  @Test
  public void testGetBuildings() {
    ArrayList<BuildingInterface> buildings = (ArrayList<BuildingInterface>)mapperReader.getBuildings();
    assertEquals(buildings.get(0).getName(), "Witte Residence Hall");
  }
  
  /**
   * Tests that the mapperReader getTypes() method returns the correct 2 dimensional array of types
   */
  @Test
  public void testGetTypes() {
    String[][] types = mapperReader.getBuildingSpecifiers();
    assertEquals(types[0][0], "Residential");
  }
  
  /**
   * Tests that the building class returns the correct set of connected buildings
   */
  @Test
  public void testGetConnectedNodes() {
    ArrayList<BuildingInterface> buildings = (ArrayList<BuildingInterface>)mapperReader.getBuildings();
    BuildingInterface building = buildings.get(0);
    String[] connections = {"Dept. of Computer Science", "Memorial Union Terrace"};
    int counter = 0;
    for (BuildingInterface connection : building.getConnectedNodes()) {
      assertEquals(connection.getName(), connections[counter]);
      counter++;
    }
  }
  
  /**
   * Tests that the building class returns the correct types and specifiers
   */
  @Test
  public void testGetBuildingTypes() {
    ArrayList<BuildingInterface> buildings = (ArrayList<BuildingInterface>)mapperReader.getBuildings();
    BuildingInterface building = buildings.get(0);
    assertEquals(building.getTypes(), "R");
  }
  

}
