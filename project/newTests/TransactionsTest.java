package newTests;

import java.util.*;
import transaction.*;
import java.rmi.*;

class TransactionsTest {

    static ResourceManagerImpl rm;

    public static void main (String[] args) {
      try{
      rm = new ResourceManagerImpl();
    }catch(RemoteException e){

    }
      test1();
      System.out.println(rm.toStringNonActiveDB(rm.FLIGHT));
      System.out.println(rm.toStringNonActiveDB(rm.CAR));
      System.out.println(rm.toStringNonActiveDB(rm.HOTEL));


      System.exit(0);
    }

    /**
    * Each test has the following operations:
    * - add obj x y z: add/update a object, where obj can be [car, flight, hotel], x is the flightNum, y the price and z the numSeats
    * - del x: delete a flight, x is the flightNum
    * - sl x: sleep the transaction for x miliseconds
    * - c: commit
    * - a: abort
    **/

    static void test1()
    {
    	System.out.println("test");
      //start transaction
      Transaction t1, t2, t3, t4;
    	try{
        //flights
        t1 = new Transaction(rm.start(), "add flight 1 1000 100 sl 100 add flight 2 2000 200 del flight 1 add flight 7 5000 300 c");
    	  t2 = new Transaction(rm.start(), "add flight 1 3000 300 add flight 4 2000 100 add flight 1 3500 100 del flight 7 c");
        //cars
        t3 = new Transaction(rm.start(), "add car 1 3000 300 add car 4 2000 100 add car 1 3500 100 del flight 7 c");
        //hotels
        t4 = new Transaction(rm.start(), "add hotel 1 3000 300 add hotel 4 2000 100 add hotel 1 3500 100 add hotel 6 23 6442 c");


    	t1.start();
    	t2.start();
      t3.start();
      t4.start();
    	try {
    	    t1.join();
    	    t2.join();
          t3.join();
          t4.join();
    	} catch (Exception e) {
        System.out.println("Exception in the threads");
    	}
      }catch(RemoteException e){
        System.out.println("Remote Exception by doing operations over the db");
      }
    }



    static class Transaction extends Thread {

    	int xid;
    	StringTokenizer st;

    	public Transaction(int xid, String ops)
    	{
    	    this.xid = xid;
    	    st = new StringTokenizer(ops);
    	}

    	public void run()
    	{
        try{
    		while (st.hasMoreTokens()) {
    		    String opcode = st.nextToken();

    		    if (opcode.equalsIgnoreCase("add")) {
                String param0 = st.nextToken();
                String param1 = st.nextToken();
                String param2 = st.nextToken();
                String param3 = st.nextToken();
                switch(param0){
        		       case "car":  rm.addCars(xid, param1, Integer.parseInt(param2), Integer.parseInt(param3));
                                System.out.println("Transaction " + xid + " adds car (" + param1 + ")");
                                break;
                   case "flight": rm.addFlight(xid, param1, Integer.parseInt(param2), Integer.parseInt(param3));
                                  System.out.println("Transaction " + xid + " adds flight (" + param1 + ")");
                                  break;
                   case "hotel":  rm.addRooms(xid, param1, Integer.parseInt(param2), Integer.parseInt(param3));
                                  System.out.println("Transaction " + xid + " adds hotel (" + param1 + ")");
                                  break;
                  default:        System.out.println("Unknown opcode " + param0);
                                  break;
                }
    		    }
    		    else if (opcode.equalsIgnoreCase("del")) {
                String param0 = st.nextToken();
    			    String param = st.nextToken();
                switch(param0){
                   /*case "car":    rm.deleteCars(xid, param);
                                  System.out.println("Transaction " + xid + " deletes car (" + param + ")");
                                  break;*/
                   case "flight": rm.deleteFlight(xid, param);
                                  System.out.println("Transaction " + xid + " deletes flight (" + param + ")");
                                  break;
                   /*case "hotel":  rm.deleteHotel(xid, param);
                                  System.out.println("Transaction " + xid + " deletes hotel (" + param + ")");
                                  break;*/
                   default:       System.out.println("Unknown opcode " + param);
                                  break;
               }

    		    }
    		    else if (opcode.equalsIgnoreCase("c")) {
    			    if(rm.commit(xid))
              System.out.println("Transaction " + xid +
                 " commited correctly");
              else
              System.out.println("Transaction " + xid +
                 " could not commit correctly");


    		    }
    		    else if (opcode.equalsIgnoreCase("a")) {
    			         rm.abort(xid);
            }
            else if (opcode.equalsIgnoreCase("sl")) {
    			    String param = st.nextToken();
    			    int sleepTime = Integer.parseInt(param);
    			    try {
    			      this.sleep(sleepTime);
    			    }
    			    catch (InterruptedException ie) {
    			    }
    		    }
    		    else {
      			System.out.println("Unknown opcode " + opcode);
    			     break;
    		    }
    		}
        }catch(RemoteException e){
          System.out.println("Remote Exception by doing operations over the db");
        }
        catch(TransactionAbortedException e){
          System.out.println("TransactionAbortedException by doing operations over the db");
        }
        catch(InvalidTransactionException e){
          System.out.println("InvalidTransactionException by doing operations over the db");
        }
        finally {

  	    }

      }

    }
}
