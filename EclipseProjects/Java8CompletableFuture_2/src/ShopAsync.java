import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ShopAsync {

	private static Random random = new Random();

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

	public Future<Double> getPriceAsync1(String product) {
		CompletableFuture<Double> priceFuture = new CompletableFuture<>();
		Runnable myRunnable = () -> {};
		Runnable runnable = () -> {
				try {
					Double price = calculatePrice(product);
					priceFuture.complete(price);
				} catch (Exception e) {
					priceFuture.completeExceptionally(e);
				}
			};
		new Thread(runnable).start();
//		new Thread(() -> {
//			try {
//				Double price = calculatePrice(product);
//				priceFuture.complete(price);
//			} catch (Exception e) {
//				priceFuture.completeExceptionally(e);
//			}
//		}).start();
		return priceFuture;
	}

	public Future<Double> getPriceAsync2(String product) {
		return CompletableFuture.supplyAsync(() -> calculatePrice(product));
	}

}