//implementation of the Reservations table
package tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Reservations{
	private Map<String, ArrayList<ResvPair>> table;
	
	public Reservations(){
		table = new HashMap<String, ArrayList<ResvPair>>();
	}
	
	public boolean addReservation(String custName, int resvType, int resvKey){
		if(table.get(custName) != null){
			return false;
		}
		ArrayList<ResvPair> resvPairs = new ArrayList<ResvPair>();
		resvPairs.add(new ResvPair(resvType, resvKey));
		table.put(custName, resvPairs);
		return true;
	}
	
	public boolean deleteReservation(String custName, int resvType, int resvKey){
		if(table.get(custName) == null){
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

