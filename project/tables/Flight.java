package tables;
//implementation of a Tuple for Flights table

public class Flight{
  //attributes
  private String flightNum; //primary key
  private int price;
  private int numSeats;
  private int numAvail;
  //add more attributes needed to guarantee ACID

  //constructor
  public Flight(String flightNum, int price, int numSeats, int numAvail){
    this.flightNum=flightNum;
    this.price=price;
    this.numSeats=numSeats;
    this.numAvail=numAvail;
  }


  //methods
  public String getFlightNum(){
    return flightNum;
  }

  public void setFlightNum(String flightNum){
    this.flightNum=flightNum;
  }

  public int getPrice(){
    return price;
  }

  public void setPrice(int price){
    this.price=price;
  }

  public int getNumSeats(){
    return numSeats;
  }

  public void setNumSeats(int numSeats){
    this.numSeats=numSeats;
  }

  public int getNumAvail(){
    return numAvail;
  }

  public void setNumAvail(int numAvail){
    this.numAvail=numAvail;
  }

}
