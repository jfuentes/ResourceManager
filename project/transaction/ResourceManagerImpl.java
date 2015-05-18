package transaction;

import tables.*;
import lockmgr.*;
import java.rmi.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.IOException;

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
   private Cars cars;
   private Hotels hotels;
   private Flights flights;
   private Reservations reservations;

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

   //paths for db on disk
   public static final String FLIGHT_DB = "tables/"+FLIGHT+".db";
   public static final String FLIGHT_ACTIVE_DB = "tables/"+FLIGHT+"_active.db";
   public static final String CAR_DB = "tables/"+CAR+".db";
   public static final String CAR_ACTIVE_DB = "tables/"+CAR+"_active.db";
   public static final String HOTEL_DB = "tables/"+HOTEL+".db";
   public static final String HOTEL_ACTIVE_DB = "tables/"+HOTEL+"_active.db";
   public static final String RESERVATION_DB = "tables/"+RESERVATION+".db";
   public static final String RESERVATION_ACTIVE_DB = "tables/"+RESERVATION+"_active.db";

   //shutdown flags
   private Boolean bool = true;
   private boolean shutdownflag = false;
   private final Object lock = new Object();

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
      recover();  //recover db
      actCars=new Cars();
      actHotels=new Hotels();
      actFlights= new Flights();
      actReservations= new Reservations();

      activeTransactions = new HashMap<Integer, ArrayList<OperationPair>>();
      lm = new LockManager();

      xidCounter = 0;
   }

   private boolean recover(){

      //flights
      File file = new File(FLIGHT_DB);

		try{
			//check if pointer file exists
			if(!file.exists()){
            flights = new Flights();
			}else{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FLIGHT_DB));
            flights = (Flights) in.readObject();
			   in.close();

            //check for active table (pendient)
         }
		} catch(IOException ioe) {
         ioe.printStackTrace();
			return false;
		} catch(ClassNotFoundException ioe) {
         ioe.printStackTrace();
			return false;
		}

      //cars
      file = new File(CAR_DB);

		try{
			//check if pointer file exists
			if(!file.exists()){
            flights = new Flights();
			}else{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(CAR_DB));
			   Object obj = in.readObject();
			   in.close();

            cars  = (Cars) obj;

            //check for active table (pendient)
         }
		} catch(IOException ioe) {
         ioe.printStackTrace();
			return false;
		} catch(ClassNotFoundException ioe) {
         ioe.printStackTrace();
			return false;
		}

      //hotels
      file = new File(HOTEL_DB);

		try{
			//check if pointer file exists
			if(!file.exists()){
            hotels = new Hotels();
			}else{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(HOTEL_DB));
			   Object obj = in.readObject();
			   in.close();

            hotels  = (Hotels) obj;

            //check for active table (pendient)
         }
		} catch(IOException ioe) {
         ioe.printStackTrace();
			return false;
		} catch(ClassNotFoundException ioe) {
         ioe.printStackTrace();
			return false;
		}

      //reservations
      file = new File(RESERVATION_DB);

		try{
			//check if pointer file exists
			if(!file.exists()){
            reservations = new Reservations();
			}else{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(RESERVATION_DB));
			   Object obj = in.readObject();
			   in.close();

            reservations  = (Reservations) obj;

            //check for active table (pendient)
         }
		} catch(IOException ioe) {
         ioe.printStackTrace();
			return false;
		} catch(ClassNotFoundException ioe) {
         ioe.printStackTrace();
			return false;
		}

      return true;

   }


   // TRANSACTION INTERFACE
   public int start()
   throws RemoteException {

      if(!shutdownflag){ //if there is no shutdown request

         xidCounter++;

         activeTransactions.put(xidCounter, new ArrayList<OperationPair>());

         return xidCounter;
      }else
         return -1;

   }

   public boolean commit(int xid) throws RemoteException, TransactionAbortedException,
   InvalidTransactionException {
      System.out.println("Committing");
      boolean updateFlight=false, updateCar=false, updateHotel=false, updateReservation=false;

      if(!activeTransactions.containsKey(xid))
         return false;

      //reflect the changes over the non-active database
      ArrayList<OperationPair> operations = activeTransactions.get(xid);

      for(OperationPair op: operations){
         //go over each operation and merge it on the non-active tables
         switch(op.getTable()){
            case FLIGHT: if(actFlights.containsFlight(op.getKey())) //if the flight exits, we just update it
            flights.addFlight(op.getKey(), actFlights.getFlight(op.getKey()));
            else  //otherwise we need to delete it in non-active db
            flights.deleteFlight(op.getKey());
            updateFlight=true;
            break;

            case CAR:    if(actCars.containsCar(op.getKey()))
            cars.addCar(op.getKey(), actCars.getCar(op.getKey()));
            else
            cars.deleteCar(op.getKey());
            updateCar=true;
            break;

            case HOTEL:  if(actHotels.containsHotel(op.getKey()))
            hotels.addHotel(op.getKey(), actHotels.getHotel(op.getKey()));
            else
            hotels.deleteHotel(op.getKey());
            updateHotel=true;
            break;

            case RESERVATION:  reservations.addReservations(op.getKey(), actReservations.getReservations(op.getKey()));
            updateReservation=true;
            break;

            default: throw new InvalidTransactionException(xid, "Problem merging values to non-active db");

         }
      }

      activeTransactions.remove(xid);

      //update tables on disk (active and non-active)
      if(updateFlight)
         updateTableOnDisk(FLIGHT);
      if(updateCar)
         updateTableOnDisk(CAR);
      if(updateHotel)
         updateTableOnDisk(HOTEL);
      if(updateReservation)
         updateTableOnDisk(RESERVATION);

      lm.unlockAll(xid); // release the lock

      if(shutdownflag){
         if(activeTransactions.size()==0){ //this is the last transaction
            goShutDown();

         }
      }
      return true;
   }

   public void abort(int xid)throws RemoteException, InvalidTransactionException {
      //PENDIENT: not commit, but undo active database?
      activeTransactions.remove(xid);

      lm.unlockAll(xid); // release the lock

      if(shutdownflag){
         if(activeTransactions.size()==0){ //this is the last transaction
            goShutDown();

         }
      }
      return;
   }

   public void updateTableOnDisk(String table){
      if(table==FLIGHT){
         try{
               //non-active
 		         FileOutputStream fout = new FileOutputStream(FLIGHT_DB);
		         ObjectOutputStream oos = new ObjectOutputStream(fout);
		         oos.writeObject(flights);
		         oos.close();

               //active
               fout = new FileOutputStream(FLIGHT_ACTIVE_DB);
               oos = new ObjectOutputStream(fout);
               oos.writeObject(actFlights);
               oos.close();
	      }catch(Exception ex){
		         ex.printStackTrace();
	      }
      }else if(table==CAR){
         try{
               //non-active
 		         FileOutputStream fout = new FileOutputStream(CAR_DB);
		         ObjectOutputStream oos = new ObjectOutputStream(fout);
		         oos.writeObject(cars);
		         oos.close();

               //active
               fout = new FileOutputStream(CAR_ACTIVE_DB);
               oos = new ObjectOutputStream(fout);
               oos.writeObject(actCars);
               oos.close();
	      }catch(Exception ex){
		         ex.printStackTrace();
	      }
      }else if(table==HOTEL){
         try{
               //non-active
 		         FileOutputStream fout = new FileOutputStream(HOTEL_DB);
		         ObjectOutputStream oos = new ObjectOutputStream(fout);
		         oos.writeObject(hotels);
		         oos.close();

               //active
               fout = new FileOutputStream(HOTEL_ACTIVE_DB);
               oos = new ObjectOutputStream(fout);
               oos.writeObject(actHotels);
               oos.close();
	      }catch(Exception ex){
		         ex.printStackTrace();
	      }
      }else if(table==RESERVATION){
         try{
               //non-active
 		         FileOutputStream fout = new FileOutputStream(RESERVATION_DB);
		         ObjectOutputStream oos = new ObjectOutputStream(fout);
		         oos.writeObject(reservations);
		         oos.close();

               //active
               fout = new FileOutputStream(RESERVATION_ACTIVE_DB);
               oos = new ObjectOutputStream(fout);
               oos.writeObject(actReservations);
               oos.close();
	      }catch(Exception ex){
		         ex.printStackTrace();
	      }
      }
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
         //abort the transaction
         abort(xid);
         return false;
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
      //check if there is any reservation over this flight
      //A s-lock for checking reservation is needed?
      if(reservations.existsReservation(Reservations.FLIGHT_TYPE, flightNum)){
         return false;
      }
      //otherwise the flight can be deleted

      //first get a lock a update the table
      try{
         if(!lm.lock(xid, FLIGHT + flightNum, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
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
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, HOTEL + location, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      //the transaction got the X-lock
      //add/uptade the hotel in the table
      actHotels.addRooms(location, numRooms, price);


      //keep tracking of operations
      ArrayList<OperationPair> operations = activeTransactions.get(xid);
      operations.add(new OperationPair(HOTEL, location));
      activeTransactions.put(xid, operations);


      return true;
   }

   public boolean deleteRooms(int xid, String location, int numRooms)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, HOTEL + location, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }
      //the transaction got the X-lock
      //add/uptade the hotel in the table
      if(actHotels.deleteRooms(location, numRooms)){
         //keep tracking of operations
         ArrayList<OperationPair> operations = activeTransactions.get(xid);
         operations.add(new OperationPair(HOTEL, location));
         activeTransactions.put(xid, operations);
         return true;
      }else
         return false;
   }

   public boolean addCars(int xid, String location, int numCars, int price)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, CAR + location, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      //the transaction got the X-lock
      //add/uptade the Car in the table
      actCars.addCars(location, numCars, price);

      //keep tracking of operations
      ArrayList<OperationPair> operations = activeTransactions.get(xid);
      operations.add(new OperationPair(CAR, location));
      activeTransactions.put(xid, operations);

      return true;
   }

   public boolean deleteCars(int xid, String location, int numCars)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, CAR + location, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }
      //the transaction got the X-lock
      //add/uptade the car in the table
      if(actCars.deleteCars(location, numCars)){
         //keep tracking of operations
         ArrayList<OperationPair> operations = activeTransactions.get(xid);
         operations.add(new OperationPair(CAR, location));
         activeTransactions.put(xid, operations);
         return true;
      }else
      return false;
   }

   public boolean newCustomer(int xid, String custName)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      //the transaction got the X-lock
      //add/uptade the reservation in the table
      if(actReservations.addCustomer(custName)){
         //keep tracking of operations
         ArrayList<OperationPair> operations = activeTransactions.get(xid);
         operations.add(new OperationPair(RESERVATION, custName));
         activeTransactions.put(xid, operations);
         return true;
      }else
      return false;
   }

   public boolean deleteCustomer(int xid, String custName)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      //first get a lock for updating the table
      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }
      //the transaction got the X-lock
      //add/uptade the reservation in the table
      if(actReservations.deleteCustomer(custName)){
         //keep tracking of operations
         ArrayList<OperationPair> operations = activeTransactions.get(xid);
         operations.add(new OperationPair(RESERVATION, custName));
         activeTransactions.put(xid, operations);
         return true;
      }else
      return false;
   }


   // QUERY INTERFACE
   public int queryFlight(int xid, String flightNum)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {

      try{
         if(!lm.lock(xid, FLIGHT + flightNum, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return flights.getFlight(flightNum).getNumAvail();
   }

   public int queryFlightPrice(int xid, String flightNum)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      try{
         if(!lm.lock(xid, FLIGHT + flightNum, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return flights.getFlight(flightNum).getPrice();
   }

   public int queryRooms(int xid, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      try{
         if(!lm.lock(xid, HOTEL + location, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return hotels.getHotel(location).getNumAvail();
   }

   public int queryRoomsPrice(int xid, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      try{
         if(!lm.lock(xid, HOTEL + location, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return hotels.getHotel(location).getPrice();
   }

   public int queryCars(int xid, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      try{
         if(!lm.lock(xid, CAR + location, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return cars.getCar(location).getNumAvail();
   }

   public int queryCarsPrice(int xid, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      try{
         if(!lm.lock(xid, CAR + location, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

      //transaction got the S-lock

      return cars.getCar(location).getPrice();
   }

   public int queryCustomerBill(int xid, String custName)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      int bill=0;

      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.READ)){
            return -1;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return -1;
      }

		ArrayList<ResvPair> reservs = reservations.getReservations(custName);

		for(ResvPair pair: reservs){
			switch(pair.getResvType()){
				case Reservations.FLIGHT_TYPE:	bill+=this.queryFlightPrice(xid, pair.getResvKey());
                                             break;
				case Reservations.ROOM_TYPE:     bill+=this.queryRoomsPrice(xid, pair.getResvKey());
                                             break;
				case Reservations.CAR_TYPE:      bill+=this.queryCarsPrice(xid, pair.getResvKey());
                                             break;
			}
		}

      return bill;
   }


   // RESERVATION INTERFACE
   public boolean reserveFlight(int xid, String custName, String flightNum)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {

      if(queryFlight(xid, flightNum)<1){
         //there is no seat available
         return false;
      }
      //otherwise, we can make the reservation
      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      return reservations.addReservation(custName, Reservations.FLIGHT_TYPE, flightNum);

   }

   public boolean reserveCar(int xid, String custName, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      if(queryCars(xid, location)<1){
         //there is no seat available
         return false;
      }
      //otherwise, we can make the reservation
      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      return reservations.addReservation(custName, Reservations.CAR_TYPE, location);
   }

   public boolean reserveRoom(int xid, String custName, String location)
   throws RemoteException,
   TransactionAbortedException,
   InvalidTransactionException {
      if(queryRooms(xid, location)<1){
         //there is no seat available
         return false;
      }
      //otherwise, we can make the reservation
      try{
         if(!lm.lock(xid, RESERVATION + custName, LockManager.WRITE)){
            return false;
         }
      }catch(DeadlockException e){
         //deal with the deadlock
         //abort the transaction
         abort(xid);
         return false;
      }

      return reservations.addReservation(custName, Reservations.ROOM_TYPE, location);
   }


   // TECHNICAL/TESTING INTERFACE
   public boolean shutdown()
   throws RemoteException {
      //wait for all active transactions end.
      //do not allow more transactions
      shutdownflag=true;
      waitForShutDown();
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

   private Boolean waitForShutDown(){
      synchronized(lock){
         try{
            while (bool) {
               bool.wait();
            }
         }catch(InterruptedException e){
            System.out.println("InterruptedException - waitForShutDown");
         }
      }
      return bool;
   }
   public void goShutDown(){
      synchronized(lock){
         bool = false;
         bool.notify();
      }
   }

   public String toStringNonActiveDB(String nameTable){
      String s="table: "+nameTable+"\n";
      switch(nameTable){
         case FLIGHT: s+=flights.toString();
         break;
         case CAR: s+=cars.toString();
         break;
         case HOTEL: s+=hotels.toString();
         break;
         case RESERVATION: s+=reservations.toString();
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
