package newbank.server.Models;

import newbank.server.Account;
import newbank.server.Customer;

public class Transfer {
    private Customer sender;
    private Customer receiver;
    private double amount;

    private Account senderAccount;
    private Account receiverAccount;

    public Transfer(Account senderAccount, Account receiverAccount, 
                    double amount               
    ) {
        this.sender = senderAccount.getCustomer();
        this.receiver = receiverAccount.getCustomer();
        this.amount = amount;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
    }

    public Customer getSender() {
        return sender;
    }

    public Customer getReceiver() {
        return receiver;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public double getAmount() {
        return amount;
    }
}
