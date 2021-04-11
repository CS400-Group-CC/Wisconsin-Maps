import java.util.List;

public class Building implements BuildingInterface {

  private String name;
  private String[] types;
  private Double latitude;
  private Double longitude;
  private List<BuildingInterface> connectedNodes;
  private String[] connectedNodeNames;
  
  public Building(String name, String[] types, Double latitude, Double longitude, String[] connectedNodeNames) {
    this.name = name;
    this.types = types;
    this.latitude = latitude;
    this.longitude = longitude;
    this.connectedNodeNames = connectedNodeNames;
    
  }
  
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public String[] getTypes() {
    // TODO Auto-generated method stub
    return types;
  }

  @Override
  public double getLat() {
    // TODO Auto-generated method stub
    return latitude;
  }

  @Override
  public double getLon() {
    // TODO Auto-generated method stub
    return longitude;
  }

  @Override
  public List<BuildingInterface> getConnectedNodes() {
    // TODO Auto-generated method stub
    return connectedNodes;
  }
  
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

}
