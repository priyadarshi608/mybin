import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

public class Main {

	//	private static List<Shop> shops = Arrays.asList(new Shop("1sShop"),
	//			new Shop("2ndShop"),
	//			new Shop("3rdShop"),
	//			new Shop("4thShop"));
	//	
	//	private static List<Shop> shops = Arrays.asList(new Shop("1sShop"),
	//			new Shop("2ndShop"),
	//			new Shop("3rdShop"),
	//			new Shop("4thShop"),
	//			new Shop("5thShop"));
	//	
	private static List<Shop> shops = Arrays.asList(new Shop("1sShop"),
			new Shop("2ndShop"),
			new Shop("3rdShop"),
			new Shop("4thShop"),
			new Shop("5thShop"),
			new Shop("6thShop"),
			new Shop("7thShop"),
			new Shop("8thShop"),
			new Shop("9thShop"));

	private static final Executor EXECUTOR = Executors.newFixedThreadPool(
			Math.min(shops.size(), 100), new ThreadFactory() {
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r);
					thread.setDaemon(true);
					return thread;
				}
			}
			);

	public static void main(String[] args) {
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("number of processors available in this machine: "
				+ Runtime.getRuntime().availableProcessors());
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();

		long start = System.nanoTime();

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// This project have delay methods with variable time of 2 - 2.5 seconds.
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		CompletableFuture [] futures = findPricesStream("myPhone")
				.map(f -> f.thenAccept(s -> System.out.println(s + " (done in " +
						((System.nanoTime() - start) / 1_000_000) + " msecs)")))
				.toArray(size -> new CompletableFuture[size]);

		CompletableFuture.allOf(futures).join();

		System.out.println();
		System.out.println("All shops have now responded in "
				+ ((System.nanoTime() - start) / 1_000_000) + " msecs");
	}

	public static Stream<CompletableFuture<String>> findPricesStream(String product) {
		return shops.stream()
				.map(shop -> CompletableFuture.supplyAsync(
						() -> shop.getPrice(product), EXECUTOR))
				.map(future -> future.thenApply(Quote::parse))
				.map(future -> future.thenCompose(
						quote -> CompletableFuture.supplyAsync(
								() -> Discount.applyDiscount(quote), EXECUTOR)));
	}

}
