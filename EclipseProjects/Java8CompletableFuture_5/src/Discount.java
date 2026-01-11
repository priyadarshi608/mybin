import java.util.Random;

public class Discount {
	
	private static final Random random = new Random();

	public static void randomDelay() {
		int delay = 500 + random.nextInt(2000);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Code {
		NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
		private final int percentage;
		Code(int percentage) {
			this.percentage = percentage;
		}
	}

	public static String applyDiscount(Quote quote) {
		return quote.getShopName() + " price is " +
				String.format("%.2f", Discount.apply(quote.getPrice(), quote.getDiscountCode()));
	}

	private static double apply(double price, Code code) {
		randomDelay();
		return (price * (100 - code.percentage) / 100);
	}

}