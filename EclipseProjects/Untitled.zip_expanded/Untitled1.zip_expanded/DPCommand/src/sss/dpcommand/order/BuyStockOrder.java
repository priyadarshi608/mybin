package sss.dpcommand.order;

import sss.dpcommand.stuffs.Stock;

public class BuyStockOrder implements Order {
	private Stock stock;

	public BuyStockOrder(Stock stock){
		this.stock = stock;
	}

	public void execute() {
		stock.buy();
	}
}
