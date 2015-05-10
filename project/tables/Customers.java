//implementation of the Customers table
package tables;

import java.util.HashMap;
import java.util.Map;

public class Customers{
	private Map<String, Customer> table;
	
	public Customers(){
		table = new HashMap<String, Customer>();
	}
	
	  //methods

	  /**
	  * Methods to add, update, remove and search tuples on the table
	  **/
	
	public boolean addCustomer(String custName){
		if(table.get(custName)!=null){
			return false;
		}
		table.put(custName, new Customer(custName));
		return true;
	}
	
	public boolean deleteCustomer(String custName){
		if(table.get(custName) == null){
			return false;
		}
		table.remove(custName);
		return true;
	}
	
	public Customer getCustomer(String custName){
		return table.get(custName);
	}
}