
public abstract class State {
	protected Account account;
	protected double balance;

	protected double interest;
	protected double lowerLimit;
	protected double upperLimit;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public abstract void Deposit(double amount);
	public abstract void Withdraw(double amount);
	public abstract void PayInterest();

}
