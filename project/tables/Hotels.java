package tables;

import java.util.HashMap;
import java.util.Map;

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

	public boolean addHotel(String location, int price, int numRooms){
		if(table.containsKey(location)){
			Hotel hotel = table.get(location);
			hotel.setPrice(price); //update to the new price
			hotel.setNumRooms(numRooms+hotel.getNumRooms()); //add the new cars
			table.put(location, hotel);
		}else
			table.put(location, new Hotel(location, price, numRooms, numRooms));
		return true;
	}

	public void addHotel(String location, Hotel hotel){
		table.put(location, hotel);
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

	public boolean containsHotel(String location){
    return table.containsKey(location);
  }
}
