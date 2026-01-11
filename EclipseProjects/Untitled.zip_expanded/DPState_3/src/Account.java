
/**
 * This is the CONTEXT class.
 * @author shubham
 */
public class Account {

	private State state;
	private String owner;

	public Account(String owner) {
		super();
		this.state = new SilverState(0.0, this);
		this.owner = owner;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void Deposit(double amount) {
		state.Deposit(amount);
		System.out.println("Deposited: " + amount);
		System.out.println("Balance: " + state.balance);
		System.out.println("Status: " + state.getClass().getName());
		System.out.println("");
	}

	public void Withdraw(double amount) {
		state.Withdraw(amount);
		System.out.println("Withdrew: " + amount);
		System.out.println("Balance: " + state.balance);
		System.out.println("Status: " + state.getClass().getName());
		System.out.println();
	}

	public void PayInterest() {
		state.PayInterest();
		System.out.println("Interest Paid...");
		System.out.println("Balance: " + state.balance);
		System.out.println("Status: " + state.getClass().getName());
		System.out.println();
	}

}
