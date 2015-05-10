package tables;
//implementation of a Tuple for Flights table

public class Car{
	//attributes
	private String location;
	private int price;
	private int numCars;
	private int numAvail;
	
	public Car(String location, int price, int numCars, int numAvail){
		this.location = location;
		this.price = price; 
		this.numCars = numCars;
		this.numAvail = numAvail;
	}
	
	public String getLocation(){
		return location;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	public int getPrice(){
		return price;
	}
	
	public void setPrice(int price){
		this.price = price;
	}
	
	public int getNumCars(){
		return numCars;
	}
	
	public void setNumRooms(int numCars){
		this.numCars = numCars;
	}
	
	public int getNumAvail(){
		return numAvail;
	}
	
	public void setNumAvail(int numAvail){
		this.numAvail = numAvail;
	}
}