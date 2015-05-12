package newTests;

import java.util.*;
import transaction.*;
import java.rmi.*;

class FlightsTest {

    static ResourceManagerImpl rm;

    public static void main (String[] args) {
      try{
      rm = new ResourceManagerImpl();
    }catch(RemoteException e){

    }
      test1();
      System.out.println(rm.toStringNonActiveDB(rm.FLIGHT));
      System.exit(0);
    }

    /**
    * Each test has the following operations:
    * - add x y z: add/update a flight, where x is the flightNum, y the price and z the numSeats
    * - del x: delete a flight, x is the flightNum
    * - sl x: sleep the transaction for x miliseconds
    * - c: commit
    * - a: abort
    **/

    static void test1()
    {
    	System.out.println("test");
      //start transaction
      Transaction t1, t2;
    	try{
        t1 = new Transaction(rm.start(), "add 1 1000 100 sl 100 add 2 2000 200 del 1 add 7 5000 300 c");
    	  t2 = new Transaction(rm.start(), "add 1 3000 300 add 4 2000 100 add 1 3500 100 del 7 c");


    	t1.start();
    	t2.start();
    	try {
    	    t1.join();
    	    t2.join();
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
        			String param1 = st.nextToken();
              String param2 = st.nextToken();
              String param3 = st.nextToken();
        			rm.addFlight(xid, param1, Integer.parseInt(param2), Integer.parseInt(param3));
              System.out.println("Transaction " + xid +
        					   " adds flight (" + param1 + ")");
    		    }
    		    else if (opcode.equalsIgnoreCase("del")) {
    			    String param = st.nextToken();
              rm.deleteFlight(xid, param);
              System.out.println("Transaction " + xid +
                 " deletes flight (" + param + ")");
    		    }
    		    else if (opcode.equalsIgnoreCase("c")) {
    			    if(rm.commit(xid))
              System.out.println("Transaction " + xid +
                 " commited correctly");
              else
              System.out.println("Transaction " + xid +
                 " could not commit correctly");


    		    }
    		    else if (opcode.equalsIgnoreCase("c")) {
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
