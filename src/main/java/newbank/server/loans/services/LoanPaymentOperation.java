package newbank.server.loans.services;

import java.util.List;

import newbank.server.Customer;
import newbank.server.loans.models.Account;
import newbank.server.loans.models.LoanRequest;
import newbank.server.loans.models.LoanPayment;

public interface LoanPaymentOperation {
	Boolean paymentValidation(Double amount, Account sourceAccount);
	Boolean activeLoan(Long loanRequestId);
	LoanRequest getLoanRequest(Long loanRequestId);
	LoanRequest makePayment(Long loanRequestId, Double amount, Account sourceAccount);
	List<LoanPayment> getPaymentsHistory(Long loanRequestId);
	List<LoanRequest> getActiveLoans(Customer customer);
	Double getCurrentLoanDebt(Long loanRequestId);
}
