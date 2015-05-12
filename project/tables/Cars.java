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

	public boolean addCars(String location, int price, int numCars){
		Car car = table.get(location);
		if(!table.containsKey(location)){//no existing cars
			car = new Car(location, price, numCars, numCars);
		}else{//update existing cars
			car.setPrice(Math.max(price,  car.getPrice()));
			car.setNumCars(car.getNumCars()+numCars);
			car.setNumAvail(car.getNumAvail()+numCars);
		}
		table.put(location, car);
		return true;
	}

	public boolean deleteCars(String location, int numCars){
		if(!table.containsKey(location)){
			return false;
		}
		Car car = table.get(location);
		if(car.getNumAvail() < numCars)
			return false;
		car.setNumAvail(car.getNumAvail()-numCars);
		return true;
	}

	public Car getCar(String location){
		return table.get(location);
	}
}
