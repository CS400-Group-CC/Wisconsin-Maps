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

/**
 * @author japep
 *
 */
public interface BuildingInterface {
    // Returns the name of the building
    public String getName();

    // Returns the multiple specifier codes of the building
    public String getTypes();

    // Returns the latitude of the building or path
    public double getLat();

    // Returns the longitude of the building or path
    public double getLon();

    // Returns a list of BuildingInterfaces that are connected to 
    // this node
    public List<BuildingInterface> getConnectedNodes();

    // Returns true if the name, latitude, and longitude of the nodes 
    // are equal; false otherwise
    @Override
    public boolean equals(Object o);
}

