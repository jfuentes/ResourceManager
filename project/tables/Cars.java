package tables;

import java.util.HashMap;
import java.util.Map;

public class Cars{
	private Map<String, Car> table;

	public Cars(){
		table = new HashMap<String, Car>();
	}

	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addCar(String location, int price, int numCars){
		if(table.get(location)!=null){
			//the flight already exists
			return false;
		}
		table.put(location, new Car(location, price, numCars, numCars));
		return true;
	}
	
	public boolean deleteCar(String location){
		if(table.get(location) == null){
			return false;
		}
		table.remove(location);
		return true;
	}
	
	public Car getCar(String location){
		return table.get(location);
	}
}