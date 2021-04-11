// --== CS400 File Header Information ==--
// Name: Jeremy Peplinski
// Email: japeplinski@wisc.edu
// Team: Red
// Group: CC
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: Code originally written by Colby for previous project for calculating distance 
// between points used as a basis for a version applicable here.
///////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

/**
 * A class to organize Building objects into a GraphADT representation of campus, providing methods 
 * of interaction with this data for pathfinding.
 */
public class Backend implements BackendInterface {
  
  CS400Graph<BuildingInterface> graph;
  MapperReader dataReader;
  double travelSpeed, conditionMultiplier;
  
  /**
   * A single-argument constructor for the Backend, taking an existing dataReader as input.  This
   * constructs a CS400Graph object from the Building objects and sets default travel parameters.
   * @param dataReader the MapperReader object that has read the dataset
   * @throws DataFormatException if some aspect of the data cannot be placed into the graph
   */
  public Backend (MapperReader dataReader) throws DataFormatException {
    this.dataReader = dataReader;
    
    List<BuildingInterface> buildings = dataReader.getBuildings();
    // Iterate through all buildings, adding vertices
    for (int i = 0; i < buildings.size(); i++) {
      try {
        graph.insertVertex(buildings.get(i));
      }
      catch (Exception e) {
        throw new DataFormatException("Invalid building vertex encountered");
      }
    }
    // Once vertices added, add edges
    for (int i = 0; i < buildings.size(); i++) {
      BuildingInterface current = buildings.get(i);
      for (int j = 0; j < buildings.get(i).getConnectedNodes().size(); j++) {
        BuildingInterface target = current.getConnectedNodes().get(j);
        try {
          int weight = getDistance(current, target);
          graph.insertEdge(current, target, weight);
        }
        catch (Exception e) {
          throw new DataFormatException("Invalid path encountered.");
        }
      }
    }
    // Set default travel speed and condition multiplier
    // I've chosen 4fps based on 8 minutes from Lucky to Birge (4.25) being slightly fast
    travelSpeed = 4.0;
    conditionMultiplier = 1.0;
  }

  @Override
  public double getPathTime(BuildingInterface currentVertex, BuildingInterface destinationVertex)
      throws NoSuchElementException {
    // First level, ensure that neither vertex is null (to avoid exceptions)
    if (currentVertex == null || destinationVertex == null) {
      throw new NoSuchElementException("Cannot find path time to or from a null building.");
    }
    // Basic test of whether the graph contains these vertices
    if (!graph.containsVertex(currentVertex) || !graph.containsVertex(destinationVertex)) {
      throw new NoSuchElementException("One or both provided buildings were not in the graph.");
    }
    // Try to find path cost between vertices, which will throw an exception if this fails
    try {
      int pathCost = graph.getPathCost(currentVertex, destinationVertex);
      return pathCost * conditionMultiplier / travelSpeed;
    }
    catch (NoSuchElementException notFound) {
      throw new NoSuchElementException("No path was found between buildings.");
    }
  }

  @Override
  public List<BuildingInterface> shortestGeneralPath(BuildingInterface currentVertex, char destinationType)
      throws NoSuchElementException {
    // Create a stream of buildings to filter by type
    Stream<BuildingInterface> buildingStream = dataReader.getBuildings().stream();
    
    // Modify stream into an array of path options
    ArrayList<BuildingInterface> options[] = buildingStream
      // Remove current node from consideration
      .filter(b -> b != currentVertex)
      // Filter by type
      .filter(b -> b.getTypes().contains(Character.toString(destinationType)))
      // Map to shortest paths, setting nonexistent ones to null
      .map(b -> {
        try {
          return graph.shortestPath(currentVertex, b);
        }
        catch (Exception e){
          return null;
        }
      })
      // Remove null paths
      .filter(b -> b != null)
      // Convert to array
      .toArray(size -> new ArrayList[size]);
    
    // If there are no options, throw an exception
    if (options.length == 0) {
      throw new NoSuchElementException("No paths were found.");
    }
    // Else, search through and return the shortest path
    ArrayList<BuildingInterface> returnVal = options[0];
    int minCost = graph.getPathCost(currentVertex, options[0].get(options[0].size() - 1));
    for (int i = 1; i < options.length; i++) {
      if (graph.getPathCost(currentVertex, options[i].get(options[i].size() - 1)) < minCost) {
        returnVal = options[i];
      }
    }
    
    return returnVal;
  }

  @Override
  public int getGeneralPathCost(BuildingInterface currentVertex, char destinationType)
      throws NoSuchElementException {
    // Create a stream of buildings to filter by type
    Stream<BuildingInterface> buildingStream = dataReader.getBuildings().stream();
    
    // Modify stream
    int returnVal = buildingStream
      // Remove current node from consideration
      .filter(b -> b != currentVertex)
      // Filter by type
      .filter(b -> b.getTypes().contains(Character.toString(destinationType)))
      // Map to path lengths, setting nonexistent ones to -1
      .map(b -> {
        try {
          return graph.getPathCost(currentVertex, b);
        }
        catch (Exception e){
          return -1;
        }
      })
      // Remove null paths
      .filter(b -> b != -1)
      .mapToInt(b -> b)
      .min()
      .orElseThrow(() -> new NoSuchElementException("No paths were found."));
    
    return returnVal;
  }

  @Override
  public double getGeneralPathTime(BuildingInterface currentVertex, char destinationType)
      throws NoSuchElementException {
    return getGeneralPathCost(currentVertex, destinationType) * conditionMultiplier / travelSpeed;
  }

  @Override
  public void setTravelSpeed(double speed) throws IllegalArgumentException {
    if (speed <= 0.0) {
      throw new IllegalArgumentException("Cannot set zero or negative speed.");
    }
    travelSpeed = speed;
  }

  @Override
  public double getTravelSpeed() {
    return travelSpeed;
  }

  @Override
  public void setTravelConditions(double conditionMultiplier) throws IllegalArgumentException {
    if (conditionMultiplier <= 0.0) {
      throw new IllegalArgumentException("Cannot set zero or negative condition multiplier.");
    }
    this.conditionMultiplier = conditionMultiplier;
  }

  @Override
  public double getTravelConditions() {
    return conditionMultiplier;
  }

  @Override
  public List<BuildingInterface> getBuildings() {
    return dataReader.getBuildings();
  }

  public List<BuildingInterface> getAlongPath(BuildingInterface currentVertex, BuildingInterface destinationVertex) throws NoSuchElementException {
    // Set up necessary lists: one for buildings along the route to return, and one of the path itself
    ArrayList<BuildingInterface> returnVal = new ArrayList<BuildingInterface>();
    List<BuildingInterface> path = shortestPath(currentVertex, destinationVertex);
    
    // Iterate through all path elements, and if a connected node is not the current or destination or 
    // a path, add it to the return list
    for (int i = 0; i < path.size(); i++) {
      List<BuildingInterface> surrounding = path.get(i).getConnectedNodes();
      for (int j = 0; j < surrounding.size(); j++) {
        BuildingInterface current = surrounding.get(j);
        if (!current.getTypes().contains(Character.toString('P')) && !current.equals(currentVertex) && !current.equals(destinationVertex)) {
          returnVal.add(current);
        }
      }
    }
    
    // Return this list
    return returnVal;
  }
  
  public List<BuildingInterface> getAlongGeneralPath(BuildingInterface currentVertex, char destinationType) throws NoSuchElementException {
    // Set up necessary lists: one for buildings along the route to return, and one of the path itself
    ArrayList<BuildingInterface> returnVal = new ArrayList<BuildingInterface>();
    List<BuildingInterface> path = shortestGeneralPath(currentVertex, destinationType);
    
    // Iterate through all path elements, and if a connected node is not the current or destination or 
    // a path, add it to the return list
    for (int i = 0; i < path.size(); i++) {
      List<BuildingInterface> surrounding = path.get(i).getConnectedNodes();
      for (int j = 0; j < surrounding.size(); j++) {
        BuildingInterface current = surrounding.get(j);
        // Note that because we don't know the destination from the call, it's best to use the path directly
        if (!current.getTypes().contains(Character.toString('P')) && 
            !current.equals(path.get(0)) && !current.equals(path.get(path.size() - 1))) {
          returnVal.add(current);
        }
      }
    }
    
    // Return this list
    return returnVal;
  }
  
  public String[][] getBuildingSpecifiers() {
    return dataReader.getBuildingSpecifiers();
  }
  
  /**
   * Gets the distance two Building objects in feet (based on Colby's Bridge Mapper code)
   * 
   * @param current the Building to center the calculation around
   * @param target the Building to calculate distance to
   * @return the distance between the two Building objects, in the nearest integer foot
   * @throws IllegalArgumentException if either Building is null or one of their coordinates is invalid
   */
  public int getDistance(BuildingInterface current, BuildingInterface target) throws IllegalArgumentException {
    if (current == null || target == null) {
      throw new IllegalArgumentException("Cannot calculate distance from null building.");
    }
    if (current.getLat() > 90 || current.getLat() < -90) {
      throw new IllegalArgumentException("Invalid latitude of current building: " + current.getLat());
    }
    if (current.getLon() > 180 || current.getLon() < -180) {
      throw new IllegalArgumentException("Invalid longitude of current building: " + current.getLon());
    }
    if (target.getLat() > 90 || target.getLat() < -90) {
      throw new IllegalArgumentException("Invalid latitude of target building: " + target.getLat());
    }
    if (target.getLon() > 180 || target.getLon() < -180) {
      throw new IllegalArgumentException("Invalid longitude of target building: " + target.getLon());
    }
    // Get deltas and latitudes in radians
    double dLat = Math.toRadians(target.getLat() - current.getLat()); 
    double dLon = Math.toRadians(target.getLon() - current.getLon()); 
    double currentLatRad = Math.toRadians(current.getLat()); 
    double targetLatRad = Math.toRadians(target.getLat()); 

    // Apply Haversine Formula 
    double inArcsin = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) *  
               Math.cos(currentLatRad) * Math.cos(targetLatRad); 
    double rEarth = 3958.8 * 5280.0; 
    double distance = 2 * rEarth * Math.asin(Math.sqrt(inArcsin)); 
    
    // Round to nearest value, note that maximum value is well below the threshold for overflow errors
    // from the downcast of types
    return (int) Math.round(distance);
  }

  @Override
  
  // Pass-through GraphADT methods
  
  public boolean insertVertex(BuildingInterface data) {
    return graph.insertVertex(data);
  }

  @Override
  public boolean removeVertex(BuildingInterface data) {
    return graph.removeVertex(data);
  }

  @Override
  public boolean insertEdge(BuildingInterface source, BuildingInterface target, int weight) {
    return graph.insertEdge(source, target, weight);
  }

  @Override
  public boolean removeEdge(BuildingInterface source, BuildingInterface target) {
    return graph.removeEdge(source, target);
  }

  @Override
  public boolean containsVertex(BuildingInterface data) {
    return graph.containsVertex(data);
  }

  @Override
  public boolean containsEdge(BuildingInterface source, BuildingInterface target) {
    return graph.containsEdge(source, target);
  }

  @Override
  public int getWeight(BuildingInterface source, BuildingInterface target) {
    return graph.getWeight(source, target);
  }

  @Override
  public List<BuildingInterface> shortestPath(BuildingInterface start, BuildingInterface end) {
    return graph.shortestPath(start, end);
  }

  @Override
  public int getPathCost(BuildingInterface start, BuildingInterface end) {
    return graph.getPathCost(start, end);
  }

  @Override
  public boolean isEmpty() {
    return graph.isEmpty();
  }

  @Override
  public int getEdgeCount() {
    return graph.getEdgeCount();
  }

  @Override
  public int getVertexCount() {
    return graph.getVertexCount();
  }
}
