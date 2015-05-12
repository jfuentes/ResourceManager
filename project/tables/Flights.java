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
		Flight flight = table.get(flightNum);
		if(!table.containsKey(flightNum)){//no existing flight
			flight = new Flight(flightNum, price, numSeats, numSeats);
		}else{
			flight.setPrice(price);
			flight.setNumSeats(flight.getNumSeats()+numSeats);
			flight.setNumAvail(flight.getNumAvail()+numSeats);
		}
		table.put(flightNum, flight);
		return true;
	}

	public boolean deleteFlight(String flightNum){
		if(!table.containsKey(flightNum)){
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
