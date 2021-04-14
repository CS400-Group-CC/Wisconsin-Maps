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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

/**
 * A class to organize Building objects into a GraphADT representation of campus, providing methods
 * of interaction with this data for pathfinding.
 */
public class Backend implements BackendInterface {

  CS400Graph<BuildingInterface> graph;
  MadisonMapperReader dataReader;
  double travelSpeed, conditionMultiplier;
  boolean genKMLs;

  /**
   * A single-argument constructor for the Backend, taking an existing dataReader as input. This
   * constructs a CS400Graph object from the Building objects and sets default travel parameters.
   * 
   * @param dataReader the MadisonMapperReader object that has read the dataset.
   * @throws DataFormatException if some aspect of the data cannot be placed into the graph.
   */
  public Backend(MadisonMapperReader dataReader) throws DataFormatException {
    this.dataReader = dataReader;
    this.graph = new CS400Graph<BuildingInterface>();

    List<BuildingInterface> buildings = dataReader.getBuildings();
    // Iterate through all buildings, adding vertices
    for (int i = 0; i < buildings.size(); i++) {
      try {
        graph.insertVertex(buildings.get(i));
      } catch (Exception e) {
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
        } catch (Exception e) {
          throw new DataFormatException("Invalid path encountered.");
        }
      }
    }
    // Set default travel speed and condition multiplier
    // I've chosen 4fps based on 8 minutes from Lucky to Birge (4.25) being slightly fast
    travelSpeed = 4.0;
    conditionMultiplier = 1.0;

    // Partially for debugging purposes, set default value of genKMLs to false
    genKMLs = false;
  }

  /**
   * A single-argument constructor for the Backend, taking a Scanner containing the contents of a
   * datafile. This constructs a CS400Graph object from the Building objects and sets default travel
   * parameters.
   * 
   * @param input the Scanner containing datafile contents.
   * @throws DataFormatException if the data provided is faulty in some way.
   */
  public Backend(Scanner input) throws DataFormatException {
    dataReader = new MadisonMapperReader(input);
    this.graph = new CS400Graph<BuildingInterface>();

    List<BuildingInterface> buildings = dataReader.getBuildings();
    // Iterate through all buildings, adding vertices
    for (int i = 0; i < buildings.size(); i++) {
      try {
        graph.insertVertex(buildings.get(i));
      } catch (Exception e) {
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
        } catch (Exception e) {
          throw new DataFormatException("Invalid path encountered.");
        }
      }
    }
    // Set default travel speed and condition multiplier
    // I've chosen 4fps based on 8 minutes from Lucky to Birge (4.25) being slightly fast
    travelSpeed = 4.0;
    conditionMultiplier = 1.0;

    // Partially for debugging purposes, set default value of genKMLs to false
    genKMLs = false;
  }

  /**
   * A single-argument constructor for the Backend, taking command-line arguments. This constructs a
   * CS400Graph object from the Building objects and sets default travel parameters.
   * 
   * @param args command-line arguments specifying a datafile location.
   * @throws DataFormatException if the data provided is faulty in some way.
   */
  public Backend(String[] args) throws DataFormatException {
    try {
      dataReader = new MadisonMapperReader(args);
    } catch (FileNotFoundException badFile) {
      throw new DataFormatException("Input file was not found.");
    }

    this.graph = new CS400Graph<BuildingInterface>();

    List<BuildingInterface> buildings = dataReader.getBuildings();
    // Iterate through all buildings, adding vertices
    for (int i = 0; i < buildings.size(); i++) {
      try {
        graph.insertVertex(buildings.get(i));
      } catch (Exception e) {
        throw new DataFormatException("Invalid building vertex encountered.");
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
        } catch (Exception e) {
          throw new DataFormatException("Invalid path encountered.");
        }
      }
    }
    // Set default travel speed and condition multiplier
    // I've chosen 4fps based on 8 minutes from Lucky to Birge (4.25) being slightly fast
    travelSpeed = 4.0;
    conditionMultiplier = 1.0;

    // Partially for debugging purposes, set default value of genKMLs to false
    genKMLs = false;
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
      return pathCost * conditionMultiplier / (travelSpeed * 60);
    } catch (NoSuchElementException notFound) {
      throw new NoSuchElementException("No path was found between buildings.");
    }
  }

  @Override
  public List<BuildingInterface> shortestGeneralPath(BuildingInterface currentVertex,
      char destinationType) throws NoSuchElementException {
    // Create a stream of buildings to filter by type
    Stream<BuildingInterface> buildingStream = dataReader.getBuildings().stream();

    ArrayList<List<BuildingInterface>> options = new ArrayList<List<BuildingInterface>>();

    // Modify stream into an array of path options
    buildingStream
    // Remove current node from consideration
    .filter(b -> b != currentVertex)
    // Filter by type
    .filter(b -> b.getTypes().contains(Character.toString(destinationType)))
    // Map to shortest paths, setting nonexistent ones to null
    .map(b -> {
      try {
        return graph.shortestPath(currentVertex, b);
      } catch (Exception e) {
        return null;
      }
    })
    // Remove null paths
    .filter(b -> b != null)
    // Convert to array
    .forEach(b -> options.add(b));

    // If there are no options, throw an exception
    if (options.size() == 0) {
      throw new NoSuchElementException("No paths were found.");
    }
    // Else, search through and return the shortest path
    List<BuildingInterface> returnVal = options.get(0);
    int minCost = getPathCost(returnVal);
    for (int i = 1; i < options.size(); i++) {
      int currentCost = getPathCost(options.get(i));
      if (currentCost < minCost) {
        minCost = currentCost;
        returnVal = options.get(i);
      }
    }

    // If genKMLs is true, we also want to try to generate a KML for this
    if (genKMLs) {
      try {
        getPathKML(options, destinationType);
      } catch (IOException e) {
        // couldn't generate file, but this doesn't need to be seen.
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
          } catch (Exception e) {
            return -1;
          }
        })
        // Remove null paths
        .filter(b -> b != -1).mapToInt(b -> b).min()
        .orElseThrow(() -> new NoSuchElementException("No paths were found."));

    return returnVal;
  }

  @Override
  public double getGeneralPathTime(BuildingInterface currentVertex, char destinationType)
      throws NoSuchElementException {
    return getGeneralPathCost(currentVertex, destinationType) * conditionMultiplier
        / (travelSpeed * 60);
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

  /**
   * A method to get buildings along the shortest path between the given vertices.
   * 
   * @param currentVertex     the starting point of the path.
   * @param destinationVertex the ending point of the path.
   * @return a List of BuildingInterfaces corresponding to non-path Building objects along the
   *         shortest path.
   * @throws NoSuchElementException if no paths are found or if the given vertices are invalid in
   *                                some way.
   */
  public List<BuildingInterface> getAlongPath(BuildingInterface currentVertex,
      BuildingInterface destinationVertex) throws NoSuchElementException {
    // Set up necessary lists: one for buildings along the route to return, and one of the path
    // itself
    ArrayList<BuildingInterface> returnVal = new ArrayList<BuildingInterface>();
    List<BuildingInterface> path = shortestPath(currentVertex, destinationVertex);

    // Iterate through all path elements, and if a connected node is not the current, destination,
    // already
    // in the list, or a path, add it to the return list
    for (int i = 0; i < path.size(); i++) {
      List<BuildingInterface> surrounding = path.get(i).getConnectedNodes();
      for (int j = 0; j < surrounding.size(); j++) {
        BuildingInterface current = surrounding.get(j);
        if (!current.getTypes().contains(Character.toString('P')) && !current.equals(currentVertex)
            && !current.equals(destinationVertex) && !returnVal.contains(current)) {
          returnVal.add(current);
        }
      }
    }

    // Return this list
    return returnVal;
  }

  /**
   * A method to get buildings along the shortest path from the current vertex to a different vertex
   * of the specified type.
   * 
   * @param currentVertex   the starting point of the path.
   * @param destinationType the type of vertex to find a shortest path to.
   * @return a List of BuildingInterfaces corresponding to non-path Building objects along the
   *         shortest path.
   * @throws NoSuchElementException if no paths are found or the vertex is invalid in some way.
   */
  public List<BuildingInterface> getAlongGeneralPath(BuildingInterface currentVertex,
      char destinationType) throws NoSuchElementException {
    // Set up necessary lists: one for buildings along the route to return, and one of the path
    // itself
    ArrayList<BuildingInterface> returnVal = new ArrayList<BuildingInterface>();
    List<BuildingInterface> path = shortestGeneralPath(currentVertex, destinationType);

    // Iterate through all path elements, and if a connected node is not the current, destination,
    // already
    // in the list, or a path, add it to the return list
    for (int i = 0; i < path.size(); i++) {
      List<BuildingInterface> surrounding = path.get(i).getConnectedNodes();
      for (int j = 0; j < surrounding.size(); j++) {
        BuildingInterface current = surrounding.get(j);
        // Note that because we don't know the destination from the call, it's best to use the path
        // directly
        if (!current.getTypes().contains(Character.toString('P')) && !current.equals(path.get(0))
            && !current.equals(path.get(path.size() - 1)) && !returnVal.contains(current)) {
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
   * Gets the distance two Building objects in feet (based on Colby's Bridge Database code).
   * 
   * @param current the Building to center the calculation around.
   * @param target  the Building to calculate distance to.
   * @return the distance between the two Building objects, in the nearest integer foot.
   * @throws IllegalArgumentException if either Building is null or one of their coordinates is
   *                                  invalid.
   */
  public int getDistance(BuildingInterface current, BuildingInterface target)
      throws IllegalArgumentException {
    if (current == null || target == null) {
      throw new IllegalArgumentException("Cannot calculate distance from null building.");
    }
    if (current.getLat() > 90 || current.getLat() < -90) {
      throw new IllegalArgumentException(
          "Invalid latitude of current building: " + current.getLat());
    }
    if (current.getLon() > 180 || current.getLon() < -180) {
      throw new IllegalArgumentException(
          "Invalid longitude of current building: " + current.getLon());
    }
    if (target.getLat() > 90 || target.getLat() < -90) {
      throw new IllegalArgumentException("Invalid latitude of target building: " + target.getLat());
    }
    if (target.getLon() > 180 || target.getLon() < -180) {
      throw new IllegalArgumentException(
          "Invalid longitude of target building: " + target.getLon());
    }
    // Get deltas and latitudes in radians
    double dLat = Math.toRadians(target.getLat() - current.getLat());
    double dLon = Math.toRadians(target.getLon() - current.getLon());
    double currentLatRad = Math.toRadians(current.getLat());
    double targetLatRad = Math.toRadians(target.getLat());

    // Apply Haversine Formula
    double inArcsin = Math.pow(Math.sin(dLat / 2), 2)
        + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(currentLatRad) * Math.cos(targetLatRad);
    double rEarth = 3958.8 * 5280.0;
    double distance = 2 * rEarth * Math.asin(Math.sqrt(inArcsin));

    // Round to nearest value, note that maximum value is well below the threshold for overflow
    // errors
    // from the downcast of types
    return (int) Math.round(distance);
  }

  /**
   * A method to directly calculate the cost of a provided path, for improved computational
   * efficiency.
   * 
   * @param path the path to calculate cost for (must be valid to avoid undeclared exceptions).
   * @return an int representing the cost of this path.
   */
  public int getPathCost(List<BuildingInterface> path) {
    int returnVal = 0;

    for (int i = 0; i < path.size() - 1; i++) {
      returnVal += getPathCost(path.get(i), path.get(i + 1));
    }

    return returnVal;
  }


  /**
   * A method to generate a basic .kml file representing a given path, named after the start and end
   * points of this path. This file contains a single path, with very basic formatting and style.
   * 
   * @param path the path to generate a KML file for, whose elements must contain lat and long
   *             values but do not specifically need to be connected in the graph.
   * @return a String representation of the name of the generated file, for reference.
   * @throws IOException if the file was not able to be created.
   */
  public String getPathKML(List<BuildingInterface> path) throws IOException {

    try {
      FileWriter fileWriter = null;
      String fileName = path.get(0).getName() + "-" + path.get(path.size() - 1).getName() + ".kml";
      fileWriter = new FileWriter(fileName);
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Initial header info
      printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
          + "<Document>");
      // File name, but first fix references to weird characters that kmls don't like
      if (fileName.contains("&")) {
        fileName = fileName.replace("&", "&amp;");
      }
      if (fileName.contains("'")) {
        fileName = fileName.replace("'", "&apos;");
      }

      printWriter.println("<name>" + fileName + "</name>");
      // Style information
      printWriter.println("<StyleMap id=\"m_ylw-pushpin\">\r\n" + "        <Pair>\r\n"
          + "            <key>normal</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin</styleUrl>\r\n" + "        </Pair>\r\n"
          + "        <Pair>\r\n" + "            <key>highlight</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin_hl</styleUrl>\r\n" + "        </Pair>\r\n"
          + "    </StyleMap>\r\n" + "    <Style id=\"s_ylw-pushpin_hl\">\r\n"
          + "        <IconStyle>\r\n" + "            <scale>1.3</scale>\r\n"
          + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <Style id=\"s_ylw-pushpin\">\r\n" + "        <IconStyle>\r\n"
          + "            <scale>1.1</scale>\r\n" + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>");
      // Actual line data
      printWriter.println("    <Placemark>");
      printWriter
      .println("        <name>" + fileName.substring(0, fileName.length() - 4) + "</name>");
      printWriter
      .println("     <description>Generated by the getPathKML() method.</description>\r\n"
          + "        <styleUrl>#m_ylw-pushpin</styleUrl>\r\n" + "        <LineString>\r\n"
          + "            <tessellate>1</tessellate>\r\n" + "            <coordinates>");
      printWriter.print("                ");
      BuildingInterface current;
      for (int i = 0; i < path.size(); i++) {
        current = path.get(i);
        printWriter.print(current.getLon() + "," + current.getLat() + ",0 ");
      }
      printWriter.print("\r\n");
      printWriter.print("</coordinates>\r\n" + "        </LineString>\r\n" + "    </Placemark>\r\n"
          + "</Document>\r\n" + "</kml>");

      // Close up and return
      printWriter.close();
      fileWriter.close();
      return fileName;
    } catch (IOException e) {
      throw new IOException("Error writing file.");
    }
  }

  /**
   * A method to generate a .kml file representing the options available for a given general path,
   * named after the start point and destination type. This file contains a path for each option,
   * with the selected path highlighted in red.
   * 
   * @param pathOptions a list of paths to generate a KML file for, with the elements of each path
   *                    requiring lat and long values but not specifically connected in the graph.
   * @param destType    the char representation of the destination type
   * @return a String representation of the name of the generated file, for reference.
   * @throws IOException if the file was not able to be created.
   */
  public String getPathKML(ArrayList<List<BuildingInterface>> pathOptions, char destType)
      throws IOException {
    try {
      FileWriter fileWriter = null;
      String fileName = pathOptions.get(0).get(0).getName() + "-" + destType + ".kml";
      fileWriter = new FileWriter(fileName);
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Initial header info
      printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
          + "<Document>");
      // File name, but first fix weird characters that kmls don't like
      if (fileName.contains("&")) {
        fileName = fileName.replace("&", "&amp;");
      }
      if (fileName.contains("'")) {
        fileName = fileName.replace("'", "&apos;");
      }

      printWriter.println("<name>" + fileName + "</name>");
      // Style information
      printWriter.println("<open>1</open>\r\n" + "    <StyleMap id=\"msn_ylw-pushpin\">\r\n"
          + "        <Pair>\r\n" + "            <key>normal</key>\r\n"
          + "            <styleUrl>#sn_ylw-pushpin</styleUrl>\r\n" + "        </Pair>\r\n"
          + "        <Pair>\r\n" + "            <key>highlight</key>\r\n"
          + "            <styleUrl>#sh_ylw-pushpin</styleUrl>\r\n" + "        </Pair>\r\n"
          + "    </StyleMap>\r\n" + "    <StyleMap id=\"m_ylw-pushpin\">\r\n" + "        <Pair>\r\n"
          + "            <key>normal</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin</styleUrl>\r\n" + "        </Pair>\r\n"
          + "        <Pair>\r\n" + "            <key>highlight</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin_hl0</styleUrl>\r\n" + "        </Pair>\r\n"
          + "    </StyleMap>\r\n" + "    <Style id=\"s_ylw-pushpin\">\r\n"
          + "        <IconStyle>\r\n" + "            <scale>1.1</scale>\r\n"
          + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <StyleMap id=\"m_ylw-pushpin0\">\r\n" + "        <Pair>\r\n"
          + "            <key>normal</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin0</styleUrl>\r\n" + "        </Pair>\r\n"
          + "        <Pair>\r\n" + "            <key>highlight</key>\r\n"
          + "            <styleUrl>#s_ylw-pushpin_hl</styleUrl>\r\n" + "        </Pair>\r\n"
          + "    </StyleMap>\r\n" + "    <Style id=\"sh_ylw-pushpin\">\r\n"
          + "        <IconStyle>\r\n" + "            <scale>1.3</scale>\r\n"
          + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <BalloonStyle>\r\n"
          + "        </BalloonStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <color>ff0000ff</color>\r\n" + "            <width>3</width>\r\n"
          + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <Style id=\"sn_ylw-pushpin\">\r\n" + "        <IconStyle>\r\n"
          + "            <scale>1.1</scale>\r\n" + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <BalloonStyle>\r\n"
          + "        </BalloonStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <color>ff0000ff</color>\r\n" + "            <width>3</width>\r\n"
          + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <Style id=\"s_ylw-pushpin_hl\">\r\n" + "        <IconStyle>\r\n"
          + "            <scale>1.3</scale>\r\n" + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <Style id=\"s_ylw-pushpin_hl0\">\r\n" + "        <IconStyle>\r\n"
          + "            <scale>1.3</scale>\r\n" + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>\r\n"
          + "    <Style id=\"s_ylw-pushpin0\">\r\n" + "        <IconStyle>\r\n"
          + "            <scale>1.1</scale>\r\n" + "            <Icon>\r\n"
          + "                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\r\n"
          + "            </Icon>\r\n"
          + "            <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
          + "        </IconStyle>\r\n" + "        <LineStyle>\r\n"
          + "            <width>2</width>\r\n" + "        </LineStyle>\r\n" + "    </Style>");
      // Actual line data
      // Start by determining which path will be shortest, so it can be printed in red
      // Note that we need to avoid recursion, if genKMLs is true this will re-call this method
      boolean genKML = genKMLs;
      if (genKML) {
        genKMLs = false;
      }
      List<BuildingInterface> listStorage =
          shortestGeneralPath(pathOptions.get(0).get(0), destType);
      if (genKML) {
        genKMLs = true;
      }
      BuildingInterface nearDest = listStorage.get(listStorage.size() - 1);
      String name1, name2;
      for (int i = 0; i < pathOptions.size(); i++) {
        listStorage = pathOptions.get(i);
        // Store and fix names
        name1 = listStorage.get(0).getName();
        name2 = listStorage.get(listStorage.size() - 1).getName();

        if (name1.contains("&")) {
          name1 = name1.replace("&", "&amp;");
        }
        if (name1.contains("'")) {
          name1 = name1.replace("'", "&apos;");
        }

        if (name2.contains("&")) {
          name2 = name2.replace("&", "&amp;");
        }
        if (name2.contains("'")) {
          name2 = name2.replace("'", "&apos;");
        }

        // Print line data
        if (listStorage.get(listStorage.size() - 1).equals(nearDest)) {
          printWriter.println("    <Placemark>");
          printWriter.println("        <name>" + name1 + "-" + name2 + "</name>");
          printWriter.println(
              "     <description>Shortest available route, generated by the getPathKML() method.</description>\r\n"
                  + "        <styleUrl>#msn_ylw-pushpin</styleUrl>\r\n" + "        <LineString>\r\n"
                  + "            <tessellate>1</tessellate>\r\n" + "            <coordinates>");
          printWriter.print("                ");
          BuildingInterface current;
          for (int j = 0; j < listStorage.size(); j++) {
            current = listStorage.get(j);
            printWriter.print(current.getLon() + "," + current.getLat() + ",0 ");
          }
          printWriter.print("\r\n");
          printWriter
          .print("</coordinates>\r\n" + "        </LineString>\r\n" + "    </Placemark>\r\n");
        } else {
          printWriter.println("    <Placemark>");
          printWriter.println("        <name>" + name2 + "-" + name2 + "</name>");
          printWriter.println(
              "     <description>Longer than shortest available route, generated by the getPathKML() method.</description>\r\n"
                  + "        <styleUrl>#m_ylw-pushpin</styleUrl>\r\n" + "        <LineString>\r\n"
                  + "            <tessellate>1</tessellate>\r\n" + "            <coordinates>");
          printWriter.print("                ");
          BuildingInterface current;
          for (int j = 0; j < listStorage.size(); j++) {
            current = listStorage.get(j);
            printWriter.print(current.getLon() + "," + current.getLat() + ",0 ");
          }
          printWriter.print("\r\n");
          printWriter.print("            </coordinates>\r\n" + "        </LineString>\r\n"
              + "    </Placemark>\r\n");
        }
      }
      printWriter.print("</Document>\r\n</kml>");

      // Close up and return
      printWriter.close();
      fileWriter.close();
      return fileName;
    } catch (IOException e) {
      throw new IOException("Error writing file.");
    }
  }

  public void setKMLGen(boolean setValue) {
    this.genKMLs = setValue;
  }

  public boolean getKMLGen() {
    return genKMLs;
  }

  // Pass-through GraphADT methods
  @Override
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
    List<BuildingInterface> returnVal = graph.shortestPath(start, end);

    if (genKMLs) {
      try {
        getPathKML(returnVal);
      } catch (IOException e) {
        // do nothing visibly, but file generation failed
      }
    }

    return returnVal;
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
