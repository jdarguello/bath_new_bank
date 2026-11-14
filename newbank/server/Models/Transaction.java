package newbank.server.Models;

import newbank.server.Account;
import newbank.server.Customer;
import java.time.LocalDateTime;

public class Transaction {
    private Customer sender;
    private Customer receiver;
    private double amount;

    private Account senderAccount;
    private Account receiverAccount;
    private LocalDateTime date;

    public Transaction(Account senderAccount, Account receiverAccount, 
                    double amount               
    ) {
        this.sender = senderAccount.getCustomer();
        this.receiver = receiverAccount.getCustomer();
        this.amount = amount;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.date = LocalDateTime.now();
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

    public LocalDateTime getDate() {
        return date;
    }

    public String toString() {
        return "Transfer from " + sender.getName() + " (" + senderAccount.getAccountName() + ") to " +
               receiver.getName() + " (" + receiverAccount.getAccountName() + ") of amount " + amount +
               " on " + date;
    }
}
