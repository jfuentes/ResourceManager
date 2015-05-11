//implementation of the Reservations table
package tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Reservations{
	/**
	* This table represents the combination of Customers and their reservations
	* The table is represented with a Hash Table where the primary key is custName
	* and a list of pairs (resvType, resvKey)
	**/
	private static Reservations instance = null;
	private Map<String, ArrayList<ResvPair>> table;

	private Reservations(){
		table = new HashMap<String, ArrayList<ResvPair>>();
	}

	public static Reservations getInstance(){
		if(instance==null)
			instance = new Reservations();
		return instance;
	}

	public boolean addReservation(String custName, int resvType, int resvKey){
		if(!table.containsKey(custName)){
			//We assume that if the customer already exists we add a new reservation
			ArrayList<ResvPair> resvPairs = table.get(custName);
			resvPairs.add(new ResvPair(resvType, resvKey));
			table.put(custName, resvPairs);
		}else{
			ArrayList<ResvPair> resvPairs = new ArrayList<ResvPair>();
			resvPairs.add(new ResvPair(resvType, resvKey));
			table.put(custName, resvPairs);
		}
		return true;
	}

	public boolean deleteReservation(String custName, int resvType, int resvKey){
		if(!table.containsKey(custName)){
			return false;
		}

		ArrayList<ResvPair> reservations = table.get(custName);
		reservations.remove(new ResvPair(resvType, resvKey));
		return true;
	}

	//combine resvType and resvKey as a value pair
	private class ResvPair{
		private int resvType;
		private int resvKey;

		public ResvPair(int resvType, int resvKey){
			this.resvType = resvType;
			this.resvKey = resvKey;
		}

		public int getResvType(){
			return resvType;
		}

		public int getResvKey(){
			return resvKey;
		}
	}

}
