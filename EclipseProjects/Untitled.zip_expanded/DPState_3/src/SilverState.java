
public class SilverState extends State {

	public SilverState(State state) {
		this.balance = state.balance;
		this.account = state.account;
		Initialize();
	}

	public SilverState(double balance, Account account) {
		this.balance = balance;
		this.account = account;
		Initialize();
	}

	private void Initialize() {
		interest = 0.0;
		lowerLimit = 0.0;
		upperLimit = 1000.0;
	}

	public void Deposit(double amount) {
		balance += amount;
		StateChangeCheck();
	}

	public void Withdraw(double amount) {
		balance -= amount;
		StateChangeCheck();
	}

	public void PayInterest() {
		balance += interest * balance;
		StateChangeCheck();
	}

	private void StateChangeCheck() {
		if (balance < lowerLimit) {
			account.setState(new RedState(this));
		} else if (balance > upperLimit) {
			account.setState(new GoldState(this));
		}
	}

}
