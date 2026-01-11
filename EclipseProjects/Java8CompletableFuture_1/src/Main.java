
public class Main {

	public static void main(String[] args) {

		Shop shop = new Shop();
		long start = System.nanoTime();
		Double myPhonePrice = shop.getPrice("myPhone");
		long duration = (System.nanoTime() - start) / 1_000_000;

		System.out.println(String.format("my phone price: %.2f", myPhonePrice));
		System.out.println("Done in " + duration + " msecs");
	}

}
