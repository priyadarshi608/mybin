import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import sss.vending_machine_1.stuffs.Coin;
import sss.vending_machine_1.stuffs.Item;
import sss.vending_machine_1.vending_machine.VendingMachine;
import sss.vending_machine_1.vending_machine.VendingMachineFactory;

public class VendingMachineApplication {
	public static void main(String args[]) throws IOException {
		int myChoice = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		VendingMachine vm = VendingMachineFactory.createVendingMachine();
		List <String> items = vm.itemList();
		
		showItemList(items);
		
		myChoice = getValidChoice(br);
		long itemPrice = getPriceOfSelectedItem(vm, myChoice);

		System.out.println("Price of the selected item: " + itemPrice);
        vm.insertCoin(Coin.QUARTER);                           
        vm.insertCoin(Coin.QUARTER);                           
	}
	
	static void showItemList(List<String> items) {
		System.out.println("Item list: ");
		for (int i = 1; i <= items.size(); i++) {
			System.out.println(i + ". " + items.get(i - 1));
		}
	}
	
	static int getValidChoice(BufferedReader br) {
		int choice = 0;
		boolean isValid = false;
		while (!isValid) {
			System.out.println();
			System.out.println("Make your choice(1, 2 or 3): ");
			try {
				choice = Integer.parseInt(br.readLine());	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Invalid entry!!");
				System.out.println("You can enter only numbers 1, 2 or 3.");
			}
			if (choice > 0 && choice < 4) {
				isValid = true;
			}
		}
		return choice;
	}
	
	static long getPriceOfSelectedItem(VendingMachine vm, int choice) {
		long price = 0;
		
		// ***************************************************************
		// Search for better technique so that switch case is not required.
		switch (choice) {
			case 1: price = vm.selectItemAndGetPrice(Item.COKE);
			break;
			case 2: price = vm.selectItemAndGetPrice(Item.PEPSI);
			break;
			case 3: price = vm.selectItemAndGetPrice(Item.SODA);
			break;
		}
		vm.selectItemAndGetPrice(Item.SODA);
		return price;
	}
}
