import java.util.Random;

public class Shop {

	private String name;

	private static final Random random = new Random();

	public static void randomDelay() {
		int delay = 500 + random.nextInt(2000);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static double calculatePrice(String product) {
		randomDelay();
		return random.nextDouble() * product.charAt(0) + product.charAt(1);
	}

	public Shop(String shopName) {
		this.name = shopName;
	}

	public String getName() {
		return name;
	}

	public String getPrice(String product) {
		double price = calculatePrice(product);
		Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
		return String.format("%s:%.2f:%s", name, price, code);
	}

}