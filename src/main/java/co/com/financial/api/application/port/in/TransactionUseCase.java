package co.com.financial.api.application.port.in;

import co.com.financial.api.adapters.in.web.dto.transaction.TransferResponse;
import co.com.financial.api.domain.model.Transaction;
import java.math.BigDecimal;

public interface TransactionUseCase {


    Transaction deposit( String accountNumber, BigDecimal amount, String performedBy );

    Transaction withdraw( String accountNumber, BigDecimal amount, String performedBy );

    TransferResponse transfer( String fromAccountNumber, String toAccountNumber, BigDecimal amount, String performedBy );

}
