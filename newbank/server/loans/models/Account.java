package newbank.server.loans.models;

public class Account {
    private Long accountId;
    private Customer customer;
    private Double balance;

    public Account(Long accountId, Customer customer, Double balance) {
        this.accountId = accountId;
        this.customer = customer;
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Double getBalance() {
        return balance;
    }

    public void makePayment(Double amount) {
        this.balance -= amount;
    }
}
