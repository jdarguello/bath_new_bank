package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private Customer customer;

	public Account(String accountName, double openingBalance, Customer customer) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.customer = customer;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public Customer getCustomer() {
		return customer;
	}

}
