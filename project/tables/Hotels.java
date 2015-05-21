package tables;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

public class Hotels implements Serializable{
	private Map<String, Hotel> table;

	public Hotels(){
		table = new HashMap<String, Hotel>();
	}


	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addRooms(String location, int price, int numRooms, int xid){
		if(table.containsKey(location)){
			Hotel hotel = table.get(location);
			if(price>=0)
				hotel.setPrice(price); //update to the new price
			hotel.setNumRooms(numRooms+hotel.getNumRooms()); //add the new cars
			hotel.setLastTransactionUpdate(xid);
			table.put(location, hotel);
		}else
			table.put(location, new Hotel(location, price, numRooms, numRooms, xid));
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

	public boolean deleteRooms(String location, int numRooms){
		if(!table.containsKey(location)){
			return false;
		}
		Hotel hotel = table.get(location);
		if(hotel.getNumAvail() < numRooms)
			return false;
		hotel.setNumAvail(hotel.getNumAvail()-numRooms); //add the new rooms
		table.put(location, hotel);
		return true;
	}

	public boolean reserveRoom(String location, int numRooms){
		if(table.containsKey(location)){
			Hotel hotel = table.get(location);
			if(hotel.getNumAvail()-numRooms>=0){
				hotel.setNumAvail(hotel.getNumAvail()-numRooms); //add the new seats
				table.put(location, hotel);
				return true;
			}else
				return false;
	}else
			return false;
	}

	public boolean isSameTransaction(String location, int xid){
     if(containsHotel(location))
         return table.get(location).getLastTransactionUpdate()==xid;
     else return false;
   }

	public boolean cancelReservation(String location){
     if(table.containsKey(location)){
      //the flight already exists, update
      Hotel hotel = table.get(location);
      hotel.setNumAvail(1+hotel.getNumAvail());
      table.put(location, hotel);
      return true;
     }else
      return false;
  	}

	public String toString(){
		String s="";
		Set<String> keys = table.keySet();
		for(String key: keys){
			s+="| "+key+" | "+table.get(key).toString()+" |\n";
		}
		return s;
	}




}
