
public class GoldState extends State {

	public GoldState(State state) {
		this.balance = state.balance;
		this.account = state.account;
		Initialize();
	}

	public GoldState(double balance, Account account) {
		this.balance = balance;
		this.account = account;
		Initialize();
	}

	private void Initialize() {
		interest = 0.05;
		lowerLimit = 1000.0;
		upperLimit = 10000000.0;
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
		if (balance < 0.0) {
			account.setState(new RedState(this));
		} else if (balance < upperLimit) {
			account.setState(new GoldState(this));
		}
	}

}
