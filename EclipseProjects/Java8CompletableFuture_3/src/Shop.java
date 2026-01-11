import java.util.Random;

public class Shop {

	private static Random random = new Random();
	
	private String name;

	private static void delay() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static double calculatePrice(String product) {
		delay();
		return random.nextDouble() * product.charAt(0) + product.charAt(1);
	}

	public Shop(String shopName) {
		this.name = shopName;
	}
	
	public String getName() {
		return name;
	}
	
	public double getPrice(String product) {
		return calculatePrice(product);
	}

}