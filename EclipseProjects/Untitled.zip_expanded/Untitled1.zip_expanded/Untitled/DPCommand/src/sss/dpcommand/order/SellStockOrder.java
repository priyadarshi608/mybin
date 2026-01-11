package sss.dpcommand.order;

import sss.dpcommand.stuffs.Stock;

public class SellStockOrder implements Order {
	private Stock stock;

	public SellStockOrder(Stock stock){
		this.stock = stock;
	}

	public void execute() {
		stock.sell();
	}
}
