package sss.vending_machine_1.vending_machine;

/**
 * Factory class to create instance of Vending Machine, this can be extended to create instance of
 * different types of vending machines.
 * @author Javin Paul
 */
public class VendingMachineFactory {      
   public static VendingMachine createVendingMachine() {
       return new VendingMachineImpl();
   }
}
