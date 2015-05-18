package tables;

//combine resvType and resvKey as a value pair
public class ResvPair{
   private int resvType;
   private String resvKey;

   public ResvPair(int resvType, String resvKey){
      this.resvType = resvType;
      this.resvKey = resvKey;
   }

   public int getResvType(){
      return resvType;
   }

   public String getResvKey(){
      return resvKey;
   }
}
