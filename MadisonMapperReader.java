import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

//--== CS400 File Header Information ==--
//Name: Colby Brown
//Email: csbrown7@wisc.edu
//Team: CC - Red
//Role: DW
//TA: Xi Chen
//Lecturer: Gary Dahl
//Notes to Grader:

public class MadisonMapperReader implements MapperReaderInterface {

  
  private List<BuildingInterface> buildings = new ArrayList<BuildingInterface>();
  
  /**
   * Constructor 
   * 
   * @param input scanner for file
   * @throws DataFormatException
   */
  public MadisonMapperReader(Scanner input) throws DataFormatException {
    if (input == null)
      throw new DataFormatException("Input is empty");
    readDataSet(input);
  }
  
  /**
   * Constructor
   * 
   * @param args for file
   * @throws DataFormatException
   * @throws FileNotFoundException
   */
  public MadisonMapperReader(String[] args) throws DataFormatException, FileNotFoundException {
    if (args == null || args[0] == null || args[0].length() == 0)
      throw new FileNotFoundException("Input is empty");
    
    File file = new File(args[0]);
    Scanner input = new Scanner(file);
    
    readDataSet(input);
  }
  
  /**
   * Generates a list of BuildingInterfaces from the dataset that 
   * is passed in
   */
  @Override
  public void readDataSet(Scanner inputScanner) throws DataFormatException {
    inputScanner.useDelimiter(",");
    
    if (!inputScanner.hasNext()) {
      throw new DataFormatException("Empty Data Set Found");
    }
    
    inputScanner.nextLine();
    
    
    while (inputScanner.hasNextLine()) {
      String temp = null;
      
      try {
        temp = inputScanner.nextLine();
      } catch (Exception e) {
        temp = null;
      }
      
      if (temp == null)
        throw new DataFormatException("Error Reading File");
      
      Scanner tempScanner = new Scanner(temp);
      tempScanner.useDelimiter(",");
      
      try {
        String name = tempScanner.next();
        String buildingTypes = tempScanner.next();
        Double latitude = tempScanner.nextDouble();
        Double longitude = tempScanner.nextDouble();
        String connectedNodes = tempScanner.next();
        
        String[] buildingTypeList = buildingTypes.split(";");
        String typeList = "";
        for (String type : buildingTypeList) {
          typeList += type;
        }
        String[] connectedNodeNames = connectedNodes.split(";");
        Building building = new Building(name, typeList, latitude, longitude, connectedNodeNames);
        
        tempScanner.close();
        
        buildings.add(building);
        
        
        
      } catch (Exception e) {
        throw new DataFormatException("Error Reading File");
      }
    }
    
    try {
      
      for (BuildingInterface buildingInterface : buildings) {
        Building building = (Building)buildingInterface;
        building.loadConnectedNodes(buildings);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  /**
   * Returns a list of all of the BuildingInterfaces found in this dataset
   */
  @Override
  public List<BuildingInterface> getBuildings() {
    return buildings;
  }

  /**
   * Returns the different specifiers that can be used for each 
   * building in this dataset. One "column" contains the full 
   * specifier text, while the other contains the "char" that 
   * represents this full text specifier.
   */
  @Override
  public String[][] getBuildingSpecifiers() {
    String[] full = {"Residential", "Educational", "Fitness", "Dining", "Shopping", "Student Life", "Library", "Athletics", "Entertainment", "Path", "Parking"};
    String[] characters = {"R", "E", "F", "D", "T", "S", "L", "A", "M", "P", "K"};
    String[][] combined = new String[11][2];
    
    for (int i = 0; i < full.length; i++) {
      String fullName = full[i];
      String character = characters[i];
      
      combined[i][0] = fullName;
      combined[i][1] = character;
    }
    
    return combined;
  }
  
}
