package transaction;

import tables.*;
import lockmgr.*;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Resource Manager for the Distributed Travel Reservation System.
 *
 * Description: toy implementation of the RM, for initial testing
 */

public class ResourceManagerImpl
extends java.rmi.server.UnicastRemoteObject
implements ResourceManager {

	// in this toy, we don't care about location or flight number
	//protected int flightcounter, flightprice, carscounter, carsprice, roomscounter, roomsprice;

	protected int xidCounter;

	//get instances of tables
	private Cars cars = Cars.getInstance();
	private Hotels hotels = Hotels.getInstance();
	private Flights flights = Flights.getInstance();
	private Reservations reservations = Reservations.getInstance();

	//active instances of tables
	private Cars actCars = Cars.getInstance();
	private Hotels actHotels = Hotels.getInstance();
	private Flights actFlights = Flights.getInstance();
	private Reservations actReservations = Reservations.getInstance();
	
	//Hashtable to keep track of the changes
	private HashMap <Integer, ArrayList<Operation>> operations = new HashMap <Integer, ArrayList<Operation>>();
	
	private class Operation{
		private String tableName;
		private String primKey;
		
		public Operation(String tableName, String primKey){
			this.tableName = tableName;
			this.primKey = primKey;
		}
		
		public String getTableName(){
			return tableName;
		}
		
		public String getPrimKey(){
			return primKey;
		}

	}
	
	//lock Manager
	private static LockManager lm = new LockManager();

	//first part of Data id
	private static final String FLIGHT = "flight-";
	private static final String CARS = "car-";
	private static final String HOTEL = "hotel-";

	public static void main(String args[]) {
		System.setSecurityManager(new RMISecurityManager());

		String rmiName = System.getProperty("rmiName");
		if (rmiName == null || rmiName.equals("")) {
			rmiName = ResourceManager.DefaultRMIName;
		}

		String rmiRegPort = System.getProperty("rmiRegPort");
		if (rmiRegPort != null && !rmiRegPort.equals("")) {
			rmiName = "//:" + rmiRegPort + "/" + rmiName;
		}

		try {
			ResourceManagerImpl obj = new ResourceManagerImpl();
			Naming.rebind(rmiName, obj);
			System.out.println("RM bound");
		}
		catch (Exception e) {
			System.err.println("RM not bound:" + e);
			System.exit(1);
		}
	}


	public ResourceManagerImpl() throws RemoteException {
/*		flightcounter = 0;
		flightprice = 0;
		carscounter = 0;
		carsprice = 0;
		roomscounter = 0;
		roomsprice = 0;
		flightprice = 0;
*/
		xidCounter = 1;
	}


	// TRANSACTION INTERFACE
	public int start()
			throws RemoteException {
		return (xidCounter++);
	}

	public boolean commit(int xid) throws RemoteException, TransactionAbortedException,
	InvalidTransactionException {
		System.out.println("Committing");
		return true;
	}

	public void abort(int xid)throws RemoteException, InvalidTransactionException {
		return;
	}


	// ADMINISTRATIVE INTERFACE
	public boolean addFlight(int xid, String flightNum, int numSeats, int price)
			throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {

		//first get a lock a update the table
		if(!lm.lock(xid, FLIGHT + flightNum, LockManager.WRITE)){
			return false;
		}

		//the transaction got the X-lock
		//add/uptade the flight in the table
		flights.addFlight(flightNum, numSeats, price);

		//check the integrity


		return true;
	}

	public boolean deleteFlight(int xid, String flightNum)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {

		//first get a lock a update the table
		if(!lm.lock(xid, FLIGHT + flightNum, LockManager.WRITE)){
			return false;
		}

		//the transaction got the X-lock
		//add/uptade the flight in the table
		//cars.addFlight(flightNum, numSeats, price);

		//check the integrity

		return true;
	}

	public boolean addRooms(int xid, String location, int numRooms, int price)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
		Operation newOperation = new Operation(CARS, location);
		this.actHotels.addRooms(location, price, numRooms);
		if(this.operations.containsKey(xid)){
			tmpOperations = this.operations.get(xid);
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}else{
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}

		return true;
	}

	public boolean deleteRooms(int xid, String location, int numRooms)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		
		ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
		Operation newOperation = new Operation(CARS, location);
		this.actHotels.deleteRooms(location, numRooms);
		if(this.operations.containsKey(xid)){
			tmpOperations = this.operations.get(xid);
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}else{
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}
	
		return true;
	}

	public boolean addCars(int xid, String location, int numCars, int price)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
		Operation newOperation = new Operation(CARS, location);
		this.actCars.addCars(location, price, numCars);
		if(this.operations.containsKey(xid)){
			tmpOperations = this.operations.get(xid);
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}else{
			tmpOperations.add(newOperation);
			this.operations.put(xid, tmpOperations);
		}

		return true;
	}

	public boolean deleteCars(int xid, String location, int numCars)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
	//	carscounter = 0;
	//	carsprice = 0;
		return true;
	}

	public boolean newCustomer(int xid, String custName)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return true;
	}

	public boolean deleteCustomer(int xid, String custName)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return true;
	}


	// QUERY INTERFACE
	public int queryFlight(int xid, String flightNum)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return flightcounter;
	}

	public int queryFlightPrice(int xid, String flightNum)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return flightprice;
	}

	public int queryRooms(int xid, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return roomscounter;
	}

	public int queryRoomsPrice(int xid, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return roomsprice;
	}

	public int queryCars(int xid, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return carscounter;
	}

	public int queryCarsPrice(int xid, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return carsprice;
	}

	public int queryCustomerBill(int xid, String custName)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		return 0;
	}


	// RESERVATION INTERFACE
	public boolean reserveFlight(int xid, String custName, String flightNum)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		flightcounter--;
		return true;
	}

	public boolean reserveCar(int xid, String custName, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		carscounter--;
		return true;
	}

	public boolean reserveRoom(int xid, String custName, String location)
			throws RemoteException,
			TransactionAbortedException,
			InvalidTransactionException {
		roomscounter--;
		return true;
	}


	// TECHNICAL/TESTING INTERFACE
	public boolean shutdown()
			throws RemoteException {
		System.exit(0);
		return true;
	}

	public boolean dieNow()
			throws RemoteException {
		System.exit(1);
		return true; // We won't ever get here since we exited above;
		// but we still need it to please the compiler.
	}

	public boolean dieBeforePointerSwitch()
			throws RemoteException {
		return true;
	}

	public boolean dieAfterPointerSwitch()
			throws RemoteException {
		return true;
	}

}
