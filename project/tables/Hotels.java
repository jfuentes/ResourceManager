package tables;

import java.util.HashMap;
import java.util.Map;

public class Hotels{
	private Map<String, Hotel> table;

	public Hotels(){
		table = new HashMap<String, Hotel>();
	}

	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addHotel(String location, int price, int numRooms){
		if(table.get(location)!=null){
			//the flight already exists
			return false;
		}
		table.put(location, new Hotel(location, price, numRooms, numRooms));
		return true;
	}
	
	public boolean deleteHotel(String location){
		if(table.get(location) == null){
			return false;
		}
		table.remove(location);
		return true;
	}
	
	public Hotel getHotel(String location){
		return table.get(location);
	}
}