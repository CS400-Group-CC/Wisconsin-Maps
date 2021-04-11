// --== CS400 File Header Information ==--
// Name: Jeremy Peplinski
// Email: japeplinski@wisc.edu
// Team: Red
// Group: CC
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>
///////////////////////////////////////////////////////////////////////////////

import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * @author japep
 *
 */
// Interface upon which the MadisonMapperReader is based on
public interface MapperReaderInterface {
  // Generates a list of BuildingInterfaces from the dataset that 
  // is passed in
  public void readDataSet(Scanner inputScanner) throws DataFormatException;

  // Returns a list of all of the BuildingInterfaces found in this 
  //dataset
  public List<BuildingInterface> getBuildings();

  // Returns the different specifiers that can be used for each 
  // building in this dataset. One "column" contains the full 
  // specifier text, while the other contains the "char" that 
  // represents this full text specifier.
  public String[][] getBuildingSpecifiers();
}

