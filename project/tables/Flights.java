//implementation of the Flights table
package tables;

import java.util.HashMap;
import java.util.Map;


public class Flights{
  //attributes
  private static Flights instance=null;
  private Map<String, Flight> table;
  //perhaps we will add more attributes to guarantee ACID


  //constructor
  private Flights(){
    table= new HashMap<String, Flight>();
  }

  //methods

  public static Flights getInstance(){
    if(instance==null)
      instance = new Flights();
    return instance;
  }

  /**
  * Methods to add, update, remove and search tuples on the table
  **/

  public boolean addFlight(String flightNum, int price, int numSeats){
    if(!table.containsKey(flightNum)){
      //the flight already exists, update
      Flight flight = table.get(flightNum);
			flight.setPrice(price); //update to the new price
			flight.setNumSeats(numSeats+flight.getNumSeats()); //add the new cars
			table.put(flightNum, flight);
    }else
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
