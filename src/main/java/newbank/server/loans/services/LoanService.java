package newbank.server.loans.services;

import java.util.List;

import newbank.server.loans.models.Customer;
import newbank.server.loans.models.Account;
import newbank.server.loans.models.LoanPayment;
import newbank.server.loans.models.LoanRequest;

public class LoanService implements LoanPaymentOperation {

	private List<Customer> customers;

	public LoanService() {
	}

	public LoanService(List<Customer> customers) {
		this.customers = customers;
	}

	@Override
	public Boolean paymentValidation(Double amount, Account sourceAccount) {
		// TODO: implement paymentValidation
		return null;
	}

	@Override
	public Boolean activeLoan(Long loanRequestId) {
		// TODO: implement activeLoan
		return null;
	}

	@Override
	public LoanRequest getLoanRequest(Long loanRequestId) {
		// TODO: implement getLoanRequest
		return null;
	}

	@Override
	public LoanRequest makePayment(Long loanRequestId, Double amount, Account sourceAccount) {
		// TODO: implement makePayment
		return null;
	}

	@Override
	public List<LoanPayment> getPaymentsHistory(Long loanRequestId) {
		// TODO: implement getPaymentsHistory
		return null;
	}

	@Override
	public List<LoanRequest> getActiveLoans(Customer customer) {
		// TODO: implement getActiveLoans
		return null;
	}

	@Override
	public Double getCurrentLoanDebt(Long loanRequestId) {
		// TODO: implement getCurrentLoanDebt
		return null;
	}

}
