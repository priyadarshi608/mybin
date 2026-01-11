import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) {
		ShopAsync shop = new ShopAsync();
		long start = System.nanoTime();
		
//		Future<Double> myPhonePriceFuture = shop.getPriceAsync1("myPhone");
		Future<Double> myPhonePriceFuture = shop.getPriceAsync2("myPhone");
		
		long invocationTime = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Invocation returned after " + invocationTime + " msec.");
		
		// DO SOME OTHER TASKS
		// doSomeOtherTask();

		try {
			Double myPhonePrice = myPhonePriceFuture.get();
			System.out.println(String.format("my phone price: %.2f", myPhonePrice));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		long retrievalTime = (System.nanoTime() - start) / 1_000_000;
		System.out.println("Price returned after " + retrievalTime + " msec.");
	}

}
