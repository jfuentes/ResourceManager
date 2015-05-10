//implementation of the Flights table
package tables;

import java.util.HashMap;
import java.util.Map;


public class Flights{
  //attributes
  private Map<String, Flight> table;
  //perhaps we will add more attributes to guarantee ACID


  //constructor
  public Flights(){
    table= new HashMap<String, Flight>();
  }

  //methods

  /**
  * Methods to add, update, remove and search tuples on the table
  **/

  public boolean addFlight(String flightNum, int price, int numSeats){
    if(table.get(flightNum)!=null){
      //the flight already exists
      return false;
    }
    table.put(flightNum, new Flight(flightNum, price, numSeats, numSeats));
    return true;
  }

  public boolean deleteFlight(String flightNum){
    if(table.get(flightNum)==null){
      //the flight does not exist
      return false;
    }
    table.remove(flightNum);
    return true;
  }

  public Flight getFlight(String flightNum){
    return table.get(flightNum);
  }
}
