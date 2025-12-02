package co.com.financial.api.application.port.out;

import co.com.financial.api.domain.model.Transaction;

public interface TransactionRepositoryPort {

    Transaction save( Transaction transaction );
}
