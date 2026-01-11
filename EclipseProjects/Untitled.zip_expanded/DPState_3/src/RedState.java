
public class RedState extends State {

    private double serviceFee;

    public RedState(State state) {
      this.balance = state.balance;
      this.account = state.account;
      Initialize();
    }

    private void Initialize() {
      interest = 0.0;
      lowerLimit = -100.0;
      upperLimit = 0.0;
      serviceFee = 15.00;
    }

    public void Deposit(double amount) {
      balance += amount;
      StateChangeCheck();
    }

    public void Withdraw(double amount) {
      amount = amount - serviceFee;
      System.out.println("No funds available for withdrawal!");
    }

    public void PayInterest() {
      // No interest is paid
    }

    private void StateChangeCheck() {
      if (balance > upperLimit) {
        account.setState(new SilverState(this));
      }
    }
    
}
