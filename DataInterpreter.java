import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataInterpreter {

  public static class Point {
    public String name;
    public double lat;
    public double lon;
    public String types = "";
    public List<Point> connectedPoints = new ArrayList<Point>();

    public Point(String name, String coords, String types) {
      this.name = name;
      // Adding easy-to-recognize types
      if (name.contains("Library")) {
        types.concat("L;");
      }
      if (name.contains("School")) {
        types.concat("E;");
      }
      if (name.contains("Lot ")) {
        types.concat("K;");
      }
      if (name.contains("(NB)")) {
        types.concat("1;");
      }
      if (name.contains("(SB)")) {
        types.concat("2;");
      }
      if (name.contains("(EB)")) {
        types.concat("3;");
      }
      if (name.contains("(WB)")) {
        types.concat("4;");
      }
      this.types = types;
      double[] array = getCoords(coords);
      lon = array[0];
      lat = array[1];
    }

    public Point(String name, double lat, double lon, String types) {
      this.name = name;
      this.lat = lat;
      this.lon = lon;
      this.types = types;
      // Adding easy-to-recognize types
      if (name.contains("Library")) {
        this.types += "L;";
      }
      if (name.contains("School")) {
        this.types += "E;";
      }
      if (name.contains("Lot ")) {
        this.types += "K;";
      }
      if (name.contains("(NB)")) {
        this.types += "1;";
      }
      if (name.contains("(SB)")) {
        this.types += "2;";
      }
      if (name.contains("(EB)")) {
        this.types += "3;";
      }
      if (name.contains("(WB)")) {
        this.types += "4;";
      }
    }

    public void addConnectedPoint(Point input) {
      connectedPoints.add(input);
    }

    public double getDistance(Point otherPoint) throws IllegalArgumentException {

      if (otherPoint == null) {
        throw new IllegalArgumentException("Other point is empty");
      }

      double distance = calculateDistance(this.lat, otherPoint.lat, this.lon, otherPoint.lon);

      return distance;
    }

    private double calculateDistance(double lat1, double lat2, double lon1, double lon2) {

      double dLat = Math.toRadians(lat2 - lat1);
      double dLon = Math.toRadians(lon2 - lon1);

      // convert to radians
      lat1 = Math.toRadians(lat1);
      lat2 = Math.toRadians(lat2);

      // apply formulae
      double a = Math.pow(Math.sin(dLat / 2), 2)
          + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
      double rad = 3958.8;
      double c = 2 * Math.asin(Math.sqrt(a));
      return rad * c;
    }
  }

  public static String readFile(String path, Charset encoding) throws IOException {
    // This came from https://www.techiedelight.com/read-all-text-from-file-into-string-java/
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  public static Point constructBuilding(String name, String coords) {
    String types = "B;";

    // Fixing errors in names
    if (name.contains("&amp;")) {
      name = name.replace("&amp;", "&");
    }
    if (name.contains("&apos;")) {
      name = name.replace("&apos;", "'");
    }

    double lat = 0;
    double lon = 0;
    int count = 0;
    Scanner input = new Scanner(coords);
    input.useDelimiter(" ");
    while (input.hasNext()) {
      double[] currentPoint = getCoords(input.next());
      lat += currentPoint[1];
      lon += currentPoint[0];
      count++;
    }
    input.close();
    if (count != 0) {
      lat /= count;
      lon /= count;
    }
    return new Point(name, lat, lon, types);
  }

  public static double[] getCoords(String coords) {
    double[] array = new double[3];
    Scanner input = new Scanner(coords);
    input.useDelimiter(",");
    for (int i = 0; i < 3; i++) {
      array[i] = input.nextDouble();
    }
    input.close();
    return array;
  }

  public static void main(String[] args) {
    List<Point> allPoints = new ArrayList<Point>();
    List<Point> buildings = generateBuildings();
    List<Point> addresses = generateAddresses();
    List<Point> roads = generateRoads();
    List<Point> stations = generateStations();
    allPoints.addAll(buildings);
    allPoints.addAll(addresses);
    allPoints.addAll(stations);
    allPoints.addAll(roads);    

    // Connecting points
    for (int i = 0; i < allPoints.size(); i++) {
      for (int j = 0; j < allPoints.size(); j++) {
        Point iPoint = allPoints.get(i);
        Point jPoint = allPoints.get(j);
        if (i != j) {
          // Case where both points are paths
          if (iPoint.types.contains("P") && jPoint.types.contains("P")) {
            // Do not add connections between points on the same path
            if (!iPoint.name.substring(0, iPoint.name.indexOf("-"))
                .equals(jPoint.name.substring(0, jPoint.name.indexOf("-")))) {
              // Connect paths if they have points within 5 feet of each other
              if (iPoint.getDistance(jPoint) * 5280 < 5) {
                iPoint.addConnectedPoint(jPoint);
              }
            }
          } else {
            // Case where one point is a building or both points are buildings
            if (iPoint.types.contains("B") ^ jPoint.types.contains("B")) {
              // Connect points if they are within 150 feet of one another, this is necessary as
              // building positions are based on the center of the building
              if (iPoint.getDistance(jPoint) * 5280 < 150) {
                iPoint.addConnectedPoint(jPoint);
              }
            }
          }
        }
      }
    }
    System.out.println("Number of Buildings: " + buildings.size());
    System.out.println("Number of Addresses: " + addresses.size());
    System.out.println("Number of Paths Points: " + roads.size());
    System.out.println("Number of Stations: " + stations.size());
//    for (int i = 0; i < buildings.size(); i++) {
//      System.out.println("Building: " + buildings.get(i).name);
//      System.out.println("Types: " + buildings.get(i).types);
//      System.out.print("Connected points: ");
//      for (int j = 0; j < buildings.get(i).connectedPoints.size(); j++) {
//        System.out.print(buildings.get(i).connectedPoints.get(j).name + " ");
//      }
//      System.out.println("\n");
//    }
//    for (int i = 0; i < addresses.size(); i++) {
//      System.out.println("Address: " + addresses.get(i).name);
//      System.out.println("Types: " + addresses.get(i).types);
//      System.out.print("Connected points: ");
//      for (int j = 0; j < addresses.get(i).connectedPoints.size(); j++) {
//        System.out.print(addresses.get(i).connectedPoints.get(j).name + " ");
//      }
//      System.out.println("\n");
//    }
//    for (int i = 0; i < stations.size(); i++) {
//      System.out.println("Station: " + stations.get(i).name);
//      System.out.println("Types: " + stations.get(i).types);
//      System.out.print("Connected points: ");
//      for (int j = 0; j < stations.get(i).connectedPoints.size(); j++) {
//        System.out.print(stations.get(i).connectedPoints.get(j).name + " ");
//      }
//      System.out.println("\n");
//    }

    // System.out.println(buildings);
    // System.out.println(addresses);
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter("output.csv");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    PrintWriter printWriter = new PrintWriter(fileWriter);
    printWriter.println("Building Name,Building Type,Latitude,Longitude,Connected Nodes");
    for (int i = 0; i < allPoints.size(); i++) {
      printWriter.print(allPoints.get(i).name + "," + allPoints.get(i).types + "," + allPoints.get(i).lat + "," + allPoints.get(i).lon + ",");
      for (int j = 0; j < allPoints.get(i).connectedPoints.size(); j++) {
        printWriter.print(allPoints.get(i).connectedPoints.get(j).name);
        if (j < allPoints.get(i).connectedPoints.size() - 1) {
          printWriter.print(";");
        }
      }
      printWriter.println("");
    }
    printWriter.close();
  }

  public static List<Point> generateBuildings() {
    String filePath = "buildings.kml";
    List<Point> output = new ArrayList<Point>();
    String content = null;
    try {
      content = readFile(filePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println("Beyond string generation");

    if (content == null) {
      return null;
    }

    String regex =
        "<Placemark>\\s+<name>(.*)</name>(\\s+.*?)+<ExtendedData>(\\s+.*?)+</ExtendedData>(\\s+.*?)+<coordinates>\\s+(.*)\\s+</coordinates>";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);
    // System.out.println("Matcher created");
    // int count = 0;
    while (matcher.find()) {
      output.add(constructBuilding(matcher.group(1), matcher.group(5)));
      // count++;
    }
    // System.out.println(count);
    return output;
  }

  public static List<Point> generateStations() {
    String filePath = "stations.kml";
    List<Point> output = new ArrayList<Point>();
    String content = null;
    try {
      content = readFile(filePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println("Beyond string generation");

    if (content == null) {
      return null;
    }

    String regex =
        "<Placemark>\\s+<name>(.*)</name>(\\s*.*?)+<ExtendedData>(\\s+.*?)+</ExtendedData>(\\s*.*?)+<coordinates>\\s*(.*)\\s*</coordinates>";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);
    // System.out.println("Matcher created");
    // int count = 0;
    while (matcher.find()) {
      output.add(constructBuilding(matcher.group(1), matcher.group(5)));
      // count++;
    }
    // System.out.println(count);
    return output;
  }

  public static List<Point> generateAddresses() {
    String filePath = "addresses.kml";
    List<Point> output = new ArrayList<Point>();
    String content = null;
    try {
      content = readFile(filePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println("Beyond string generation");

    if (content == null) {
      return null;
    }

    String regex =
        "<Placemark>\\s+<name>(.*)</name>(\\s*.*?)+<ExtendedData>(\\s+.*?)+</ExtendedData>(\\s*.*?)+<coordinates>\\s*(.*)\\s*</coordinates>";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);
    // System.out.println("Matcher created");
    // int count = 0;
    while (matcher.find()) {
      output.add(constructBuilding(matcher.group(1), matcher.group(5)));
      // count++;
    }
    // System.out.println(count);
    return output;
  }

  public static List<Point> generateRoads() {

    String filePath = "roads.kml";
    List<Point> output = new ArrayList<Point>();
    String content = null;
    try {
      content = readFile(filePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println("Beyond string generation");

    if (content == null) {
      return null;
    }

    String regex =
        "\"osm_id\">(\\d+)</SimpleData>(\\s+.*?)+<coordinates>\\s*(.*)\\s*</coordinates>";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);
    // System.out.println("Matcher created");
    // int count = 0;
    while (matcher.find()) {
      output.addAll(constructRoad(matcher.group(1), matcher.group(3)));
    }
    // System.out.println(count);
    return output;
  }

  private static List<Point> constructRoad(String id, String coords) {
    List<Point> output = new ArrayList<Point>();
    Scanner input = new Scanner(coords);
    input.useDelimiter(" ");
    int count = 0;
    while (input.hasNext()) {
      count++;
      String name = id + "-" + count;
      output.add(new Point(name, input.next(), "P;"));
    }
    input.close();
    for (int i = 0; i < output.size(); i++) {
      if (i != 0) {
        output.get(i).addConnectedPoint(output.get(i - 1));
      }
      if (i != output.size() - 1) {
        output.get(i).addConnectedPoint(output.get(i + 1));
      }
    }
    return output;
  }
}
