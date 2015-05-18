package tables;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

public class Cars implements Serializable{
	private Map<String, Car> table;

	public Cars(){
		table = new HashMap<String, Car>();
	}


	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addCars(String location, int price, int numCars){
		if(table.containsKey(location)){
			//the car already exists, we update
			Car car = table.get(location);
			car.setPrice(price); //update to the new price
			car.setNumCars(numCars+car.getNumCars()); //add the new cars
			table.put(location, car);
		}else
			table.put(location, new Car(location, price, numCars, numCars));
		return true;
	}

	public void addCar(String location, Car car){
		table.put(location, car);
	}

	public boolean deleteCars(String location, int numCars){
		if(!table.containsKey(location)){
			return false;
		}
		Car car = table.get(location);
		if(car.getNumAvail() < numCars)
			return false;
		car.setNumAvail(car.getNumAvail()-numCars); //add the new cars
		table.put(location, car);
		return true;
	}

	public void deleteCar(String location){
		table.remove(location);
	}

	public Car getCar(String location){
		return table.get(location);
	}

	public boolean containsCar(String location){
    return table.containsKey(location);
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
