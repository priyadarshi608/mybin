package sss.dpcommand.stuffs;

public class Stock {

	private String stockName;
	private int quantity;

	public Stock(String name, int quantity) {
		this.stockName = name;
		this.quantity = quantity;
	}
	
	public void buy(){
		System.out.println("Stock [ Name: "+stockName+
				", Quantity: " + quantity +" ] bought");
	}
	
	public void sell(){
		System.out.println("Stock [ Name: "+stockName+
				", Quantity: " + quantity +" ] sold");
	}
}

