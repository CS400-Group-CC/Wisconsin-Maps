import java.util.ArrayList;
import java.util.List;

//--== CS400 File Header Information ==--
//Name: Colby Brown
//Email: csbrown7@wisc.edu
//Team: CC - Red
//Role: DW
//TA: Xi Chen
//Lecturer: Gary Dahl
//Notes to Grader:

public class Building implements BuildingInterface {

  private String name;
  private String types;
  private Double latitude;
  private Double longitude;
  private List<BuildingInterface> connectedNodes;
  private String[] connectedNodeNames;
  
  /**
   * Building parameter constructor
   * 
   * @param name - name of building
   * @param types - Types of building categories
   * @param latitude - Latitude of building
   * @param longitude - Longitude of building
   * @param connectedNodeNames - Connected Node Names
   */
  public Building(String name, String types, Double latitude, Double longitude, String[] connectedNodeNames) {
    this.name = name;
    this.types = types;
    this.latitude = latitude;
    this.longitude = longitude;
    this.connectedNodeNames = connectedNodeNames;
    this.connectedNodes = new ArrayList<BuildingInterface>();
    
  }
  
  /**
   * Returns name of building
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name;
  }

  /**
   * Returns types of buildings
   */
  @Override
  public String getTypes() {
    // TODO Auto-generated method stub
    return types;
  }

  /**
   * Returns latitude
   */
  @Override
  public double getLat() {
    // TODO Auto-generated method stub
    return latitude;
  }

  /**
   * Returns Longitude
   */
  @Override
  public double getLon() {
    // TODO Auto-generated method stub
    return longitude;
  }

  /**
   * Returns list of connected buildings
   */
  @Override
  public List<BuildingInterface> getConnectedNodes() {
    // TODO Auto-generated method stub
    return connectedNodes;
  }
  
  /**
   * Checks if this building is equal to object o
   */
  @Override
  public boolean equals(Object o) {
    if (o.getClass() == this.getClass()) {
      BuildingInterface buildingObject = (Building)o;
      if (buildingObject.getName().equals(name))
        return true;
      else
        return false;
    } else {
      return false;
    }
  }
  
  /**
   * Loads connected nodes into list
   * 
   * @param buildings - list of all buildings
   */
  public void loadConnectedNodes(List<BuildingInterface> buildings) {

    for (String nodeName : connectedNodeNames) {
      BuildingInterface buildingFound = findBuilding(nodeName, buildings);

      if (buildingFound != null) {
        connectedNodes.add(buildingFound);
      }
    }
  }
  
  /**
   * Helper method to find a building
   * 
   * @param nodeName
   * @param buildings
   * @return foundBuilding
   */
  private BuildingInterface findBuilding(String nodeName, List<BuildingInterface> buildings) {
    for (int i = 0; i < buildings.size(); i++) {
      BuildingInterface currentBuilding = buildings.get(i);

      if (currentBuilding.getName().equals(nodeName)) {
        return currentBuilding;
      }
    }
    
    return null;
  }

}
