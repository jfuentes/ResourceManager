package tables;
//implementation of a Tuple for Flights table

public class Hotel{
	//attributes
	private String location;
	private int price;
	private int numRooms;
	private int numAvail;
	
	public Hotel(String location, int price, int numRooms, int numAvail){
		this.location = location;
		this.price = price; 
		this.numRooms = numRooms;
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
	
	public int getNumRooms(){
		return numRooms;
	}
	
	public void setNumRooms(int numRooms){
		this.numRooms = numRooms;
	}
	
	public int getNumAvail(){
		return numAvail;
	}
	
	public void setNumAvail(int numAvail){
		this.numAvail = numAvail;
	}
}