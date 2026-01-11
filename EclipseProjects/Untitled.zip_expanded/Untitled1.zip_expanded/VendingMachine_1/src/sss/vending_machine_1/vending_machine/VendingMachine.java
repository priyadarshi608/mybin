package sss.vending_machine_1.vending_machine;

import java.util.List;

import sss.vending_machine_1.stuffs.Bucket;
import sss.vending_machine_1.stuffs.Coin;
import sss.vending_machine_1.stuffs.Item;

/**
 * Declare public API for Vending Machine
 * @author Shubham Priyadarshi
 */
public interface VendingMachine {
	
	/**
	 * This method returns the list of all the items.
	 * 
	 * @return Returns collection of items.
	 */
	public List<String> itemList();
	
	/**
	 * This method provides the price of the item selected by the user.
	 * 
	 * @param item The item selected by the user
	 * @return The price of the selected item
	 * @exception SoldOutException
	 */
    public long selectItemAndGetPrice(Item item);
    
    /**
     * This method accepts the coin inserted by the user.
     * 
     * @param coin The coin inserted by the user.
     */
    public void insertCoin(Coin coin);
    
    /**
     * This method is used for refund in case of refund.
     * 
     * @return Returns the collection of the coins.
     */
    public List<Coin> refund();
    
    /**
     * This method serves the purchased item with the change to the user.
     * 
     * @return The Bucket object having the sold item and the change.
     */
    public Bucket<Item, List<Coin>> collectItemAndChange();
    
    /**
     * This method can be used by the operator to reset the operation.
     */
    public void reset();
}
