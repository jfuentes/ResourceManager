package tables;

import java.util.HashMap;
import java.util.Map;

public class Cars{
	private static Cars instance=null; //instance of Cars table
	private Map<String, Car> table;

	private Cars(){
		table = new HashMap<String, Car>();
	}

	public static Cars getInstance(){
		if(instance==null)
			instance = new Cars();
		return instance;
	}

	/**
	 * Methods to add, update, remove and search tuples on the table
	 **/

	public boolean addCar(String location, int price, int numCars){
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

	public boolean containsCar(String location){
    return table.containsKey(location);
  }
}
