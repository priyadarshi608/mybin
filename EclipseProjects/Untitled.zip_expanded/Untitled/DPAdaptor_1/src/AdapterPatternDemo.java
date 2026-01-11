import sss.dpadaptor_1.creditcard.EligibleForCreditCardImpl;
import sss.dpadaptor_1.creditcard.EligibleForCreditCard;

public class AdapterPatternDemo {  
 public static void main(String args[]){  
  EligibleForCreditCard targetInterface=new EligibleForCreditCardImpl();  
  targetInterface.giveBankDetails();  
  System.out.print(targetInterface.getCreditCard());  
 }   
}
