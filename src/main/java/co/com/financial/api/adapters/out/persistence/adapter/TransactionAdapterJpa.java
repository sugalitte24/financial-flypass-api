package co.com.financial.api.adapters.out.persistence.adapter;

import co.com.financial.api.adapters.out.persistence.TransactionEntityMapper;
import co.com.financial.api.adapters.out.persistence.entities.TransactionEntity;
import co.com.financial.api.adapters.out.persistence.repository.TransactionRepository;
import co.com.financial.api.application.port.out.TransactionRepositoryPort;
import co.com.financial.api.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionAdapterJpa implements TransactionRepositoryPort {

    private final TransactionRepository txRepo;
    private final TransactionEntityMapper txMapper;

    @Override
    public Transaction save( Transaction transaction ) {
        TransactionEntity entity = txMapper.toTransactionEntity(transaction);
        TransactionEntity saved = txRepo.save(entity);
        return txMapper.toDomain(saved);
    }
}
