
import sss.dpcommand.order.BuyStockOrder;
import sss.dpcommand.order.SellStockOrder;
import sss.dpcommand.order_invoker.OrderInvoker;
import sss.dpcommand.stuffs.Stock;

public class CommandPatternDemo {
	public static void main(String[] args) {
		Stock abcStock = new Stock("ABC", 10);
//		abcStock.buy();
//		abcStock.sell();

		BuyStockOrder buyStockOrder = new BuyStockOrder(abcStock);
		SellStockOrder sellStockOrder = new SellStockOrder(abcStock);

		OrderInvoker broker = new OrderInvoker();
		broker.takeOrder(buyStockOrder);
		broker.takeOrder(sellStockOrder);

		broker.placeOrders();
	}
}
