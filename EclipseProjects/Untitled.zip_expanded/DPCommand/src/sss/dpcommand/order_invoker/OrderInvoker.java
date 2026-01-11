package sss.dpcommand.order_invoker;

import java.util.ArrayList;
import java.util.List;

import sss.dpcommand.order.Order;

public class OrderInvoker {
	private List<Order> orderList = new ArrayList<Order>(); 

	public void takeOrder(Order order){
		orderList.add(order);
	}

	public void placeOrders(){

		for (Order order : orderList) {
			order.execute();
		}
		orderList.clear();
	}
}
