package transaction;

import tables.*;
import lockmgr.*;
import java.rmi.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Resource Manager for the Distributed Travel Reservation System.
 *
 * Description: toy implementation of the RM, for initial testing
 */

public class ResourceManagerImpl
    extends java.rmi.server.UnicastRemoteObject
    implements ResourceManager {

    // in this toy, we don't care about location or flight number
    protected int flightcounter, flightprice, carscounter, carsprice, roomscounter, roomsprice;

    protected int xidCounter;

    //get instances of tables
    private Cars cars = Cars.getInstance();
    private Hotels hotels = Hotels.getInstance();
    private Flights flights = Flights.getInstance();
    private Reservations reservations = Reservations.getInstance();

    //copy for active database
    private Cars actCars;
    private Hotels actHotels;
    private Flights actFlights;
    private Reservations actReservations;

    //lock Manager
    private static LockManager lm;

    //transactions
    private HashMap<Integer, ArrayList<OperationPair>> activeTransactions;

    //first part of Data id
    public static final String FLIGHT = "flight";
    public static final String CAR = "car";
    public static final String HOTEL = "hotel";
    public static final String RESERVATION = "reservation";

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
    	actCars=Cars.getInstance();
      actHotels=Hotels.getInstance();
      actFlights=Flights.getInstance();
      actReservations=Reservations.getInstance();

      activeTransactions = new HashMap<Integer, ArrayList<OperationPair>>();
      lm = new LockManager();

    	xidCounter = 0;
    }


    // TRANSACTION INTERFACE
    public int start()
    	throws RemoteException {
      xidCounter++;

      activeTransactions.put(xidCounter, new ArrayList<OperationPair>());

      return xidCounter;
    }

    public boolean commit(int xid) throws RemoteException, TransactionAbortedException,
	       InvalidTransactionException {
    	System.out.println("Committing");

      if(!activeTransactions.containsKey(xid))
        return false;

      //reflect the changes over the non-active databases
      ArrayList<OperationPair> operations = activeTransactions.get(xid);

      for(OperationPair op: operations){
        //go over each operation and merge it in the non-active database
        switch(op.getTable()){
          case FLIGHT: if(actFlights.containsFlight(op.getKey())) //if the flight exits, we just update it
                          flights.addFlight(op.getKey(), actFlights.getFlight(op.getKey()));
                        else  //otherwise we need to delete it in non-active db
                          flights.deleteFlight(op.getKey());
                        break;

          case CAR:    if(actCars.containsCar(op.getKey()))
                          cars.addCar(op.getKey(), actCars.getCar(op.getKey()));
                       else
                          cars.deleteCar(op.getKey());
                       break;

          case HOTEL:  if(actHotels.containsHotel(op.getKey()))
                          hotels.addHotel(op.getKey(), actHotels.getHotel(op.getKey()));
                       else
                          hotels.deleteHotel(op.getKey());
                       break;

          case RESERVATION:  reservations.addReservations(op.getKey(), actReservations.getReservations(op.getKey()));
                        break;

          default: throw new InvalidTransactionException(xid, "Problem merging values to non-active db");
        }
      }

      lm.unlockAll(xid);
    	return true;
    }

    public void abort(int xid)throws RemoteException, InvalidTransactionException {
	    return;
    }


    // ADMINISTRATIVE INTERFACE
    public boolean addFlight(int xid, String flightNum, int numSeats, int price)
	throws RemoteException, TransactionAbortedException, InvalidTransactionException {

      //first get a lock for updating the table
      try{
        if(!lm.lock(xid, FLIGHT + flightNum, LockManager.WRITE)){
          return false;
        }
      }catch(DeadlockException e){
        //deal with the deadlock
      }

      //the transaction got the X-lock
      //add/uptade the flight in the table
      actFlights.addFlight(flightNum, numSeats, price);


      //keep tracking of operations
      ArrayList<OperationPair> operations = activeTransactions.get(xid);
      operations.add(new OperationPair(FLIGHT, flightNum));
      activeTransactions.put(xid, operations);



    	return true;
    }

    public boolean deleteFlight(int xid, String flightNum)
    	throws RemoteException,
    	       TransactionAbortedException,
    	       InvalidTransactionException {

      //first get a lock a update the table
      try{
        if(!lm.lock(xid, FLIGHT + flightNum, LockManager.WRITE)){
          return false;
        }
      }catch(DeadlockException e){
        //deal with the deadlock
      }

      //the transaction got the X-lock
      //add/uptade the flight in the table
      actFlights.deleteFlight(flightNum);

      //keep tracking of operations
      ArrayList<OperationPair> operations = activeTransactions.get(xid);
      operations.add(new OperationPair(FLIGHT, flightNum));
      activeTransactions.put(xid, operations);



    	return true;
    }

    public boolean addRooms(int xid, String location, int numRooms, int price)
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException {
      try{
     	  if(!lm.lock(xid, HOTEL + location, LockManager.WRITE)){
    				return false;
    	  }
		  }catch(DeadlockException e){
     			//deal with the deadlock
     	}
      ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
	    Operation newOperation = new Operation(HOTEL, location);
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
      try{
    			if(!lm.lock(xid, HOTEL + location, LockManager.WRITE)){
    				return false;
    	    }
      }catch(DeadlockException e){
     			//deal with the deadlock
     	}
     	ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
     	Operation newOperation = new Operation(HOTEL, location);
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
     try{
      if(!lm.lock(xid, CARS + location, LockManager.WRITE)){
     			return false;
     	}
     }catch(DeadlockException e){
     		//deal with the deadlock
     }
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
     try{
      	if(!lm.lock(xid, CARS + location, LockManager.WRITE)){
    				return false;
    		}
    }catch(DeadlockException e){
    			//deal with the deadlock
    }
    ArrayList<Operation> tmpOperations = new ArrayList<Operation>();
    Operation newOperation = new Operation(CARS, location);
    this.actCars.deleteCars(location, numCars);
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

    public String toStringNonActiveDB(String nameTable){
      String s="table: "+nameTable+"\n";
      switch(nameTable){
        case FLIGHT: s=flights.toString();
                     break;
        default:     break;
      }
      return s;
    }


  	private class OperationPair{
  		private String table;
  		private String key;

  		public OperationPair(String table, String key){
  			this.table = table;
  			this.key = key;
  		}

  		public String getTable(){
  			return table;
  		}

  		public String getKey(){
  			return key;
  		}
  	}

}
