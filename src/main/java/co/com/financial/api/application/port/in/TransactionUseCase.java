package co.com.financial.api.application.port.in;

import co.com.financial.api.domain.model.Transaction;

public interface TransactionUseCase {


    Transaction deposit( String accountNumber, double amount );

    Transaction withdraw( String accountNumber, double amount );

    Transaction transfer( String sourceAccountNumber, String targetAccountNumber, double amount );

}
