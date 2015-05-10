package tables;

public class Customer{
	private String custName;
	
	public Customer(String custName){
		this.custName = custName;
	}
	
	public String getCustName(){
		return custName;
	}
	
	public void setCustName(String custName){
		this.custName = custName;
	}
}