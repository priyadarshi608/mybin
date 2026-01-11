package sss.vending_machine_1.vending_machine;

import org.junit.Test;
import org.junit.Ignore;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sss.vending_machine_1.stuffs.*;
import sss.vending_machine_1.vending_machine.*;
import sss.vending_machine_1.exceptions.*;

public class VendingMachineImplTest {

	    private static VendingMachine vm;
	   
	    @BeforeClass
	    public static void setUp(){
	        vm = VendingMachineFactory.createVendingMachine();
	    }
	   
	    @AfterClass
	    public static void tearDown(){
	        vm = null;
	    }
	   
	    @Test
	    public void testBuyItemWithExactPrice() {
	        //select item, price in cents
	        long price = vm.selectItemAndGetPrice(Item.COKE); 
	        //price should be Coke's price      
	        assertEquals(Item.COKE.getPrice(), price);
	        //25 cents paid              
	        vm.insertCoin(Coin.QUARTER);                           
	       
	        Bucket<Item, List<Coin>> bucket = vm.collectItemAndChange();
	        Item item = bucket.getFirst();
	        List<Coin> change = bucket.getSecond();
	       
	        //should be Coke
	        assertEquals(Item.COKE, item);
	        //there should not be any change                              
	        assertTrue(change.isEmpty());                              
	    }
	   
	    @Test
	    public void testBuyItemWithMorePrice(){
	        long price = vm.selectItemAndGetPrice(Item.SODA);
	        assertEquals(Item.SODA.getPrice(), price);
	       
	        vm.insertCoin(Coin.QUARTER);       
	        vm.insertCoin(Coin.QUARTER);      
	       
	        Bucket<Item, List<Coin>> bucket = vm.collectItemAndChange();
	        Item item = bucket.getFirst();
	        List<Coin> change = bucket.getSecond();
	       
	        //should be Coke
	        assertEquals(Item.SODA, item);
	        //there should not be any change                                     
	        assertTrue(!change.isEmpty());        
	        //comparing change                             
	        assertEquals(50 - Item.SODA.getPrice(), getTotal(change));  
	       
	    }  
	  
	   
	    @Test
	    public void testRefund(){
	        long price = vm.selectItemAndGetPrice(Item.PEPSI);
	        assertEquals(Item.PEPSI.getPrice(), price);       
	        vm.insertCoin(Coin.DIME);
	        vm.insertCoin(Coin.NICKLE);
	        vm.insertCoin(Coin.PENNY);
	        vm.insertCoin(Coin.QUARTER);
	       
	        assertEquals(41, getTotal(vm.refund()));       
	    }
	   
	    @Test(expected=SoldOutException.class)
	    public void testSoldOut(){
	        for (int i = 0; i < 5; i++) {
	            vm.selectItemAndGetPrice(Item.COKE);
	            vm.insertCoin(Coin.QUARTER);
	            vm.collectItemAndChange();
	        }
	     
	    }
	   
	    @Test(expected=NotSufficientChangeException.class)
	    public void testNotSufficientChangeException(){
	        for (int i = 0; i < 5; i++) {
	            vm.selectItemAndGetPrice(Item.SODA);
	            vm.insertCoin(Coin.QUARTER);
	            vm.insertCoin(Coin.QUARTER);
	            vm.collectItemAndChange();
	           
	            vm.selectItemAndGetPrice(Item.PEPSI);
	            vm.insertCoin(Coin.QUARTER);
	            vm.insertCoin(Coin.QUARTER);
	            vm.collectItemAndChange();
	        }
	    }
	   
	   
	    @Test(expected=SoldOutException.class)
	    public void testReset(){
	        VendingMachine vmachine = VendingMachineFactory.createVendingMachine();
	        vmachine.reset();
	       
	        vmachine.selectItemAndGetPrice(Item.COKE);
	       
	    }
	   
	    @Ignore
	    public void testVendingMachineImpl(){
	        VendingMachineImpl vm = new VendingMachineImpl();
	    }
	   
	    private long getTotal(List<Coin> change){
	        long total = 0;
	        for(Coin c : change){
	            total = total + c.getDenomination();
	        }
	        return total;
	    }

	@Test
	public void testSelectItemAndGetPrice() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertCoin() {
		fail("Not yet implemented");
	}

	@Test
	public void testCollectItemAndChange() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintStats() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTotalSales() {
		fail("Not yet implemented");
	}

}
