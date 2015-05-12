package tables;

import java.util.HashMap;
import java.util.Map;
import java.math.*;

public class Hotels{
	private static Hotels instance = null;
	private Map<String, Hotel> table;

	private Hotels(){
		table = new HashMap<String, Hotel>();
	}

	public static Hotels getInstance(){
		if(instance==null)
			instance = new Hotels();
		return instance;
	}

	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addRooms(String location, int price, int numRooms){
		Hotel hotel = table.get(location);
		if(!table.containsKey(location)){//no existing hotel rooms
			hotel = new Hotel(location, price, numRooms, numRooms);
		}else{//update existing hotel rooms
			hotel.setPrice(Math.max(hotel.getPrice(), price));
			hotel.setNumRooms(hotel.getNumRooms() + numRooms);
			hotel.setNumAvail(hotel.getNumAvail() + numRooms);
		}
		table.put(location, hotel);
		return true;
	}
	
	public boolean deleteRooms(String location, int numRooms){
		if(!table.containsKey(location))
			return false;
		Hotel hotel = table.get(location);
		if(hotel.getNumAvail() < numRooms)
			return false;
		hotel.setNumAvail(hotel.getNumAvail()-numRooms);
		return true;
	}

	/*public boolean deleteHotel(String location){
		if(table.get(location) == null){
			return false;
		}
		table.remove(location);
		return true;
	}*/

	public Hotel getHotel(String location){
		return table.get(location);
	}
}
