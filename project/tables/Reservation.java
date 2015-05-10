package tables;
//implementation of a Tuple for Flights table

public class Reservation{
	//attributes
	private String custName;
	private int resvType;
	private int resvKey;
	
	public Reservation(String custName, int resvType, int resvKey){
		this.custName = custName;
		this.resvType = resvType; 
		this.resvKey = resvKey;
	}
	
	public String getCustName(){
		return custName;
	}
	
	public void setCustName(String custName){
		this.custName= custName;
	}
	
	public int getResvType(){
		return resvType;
	}
	
	public void setResvType(int resvType){
		this.resvType = resvType;
	}
	
	public int getResvKey(){
		return resvKey;
	}
	
	public void setResvKey(int resvKey){
		this.resvKey = resvKey;
	}
}