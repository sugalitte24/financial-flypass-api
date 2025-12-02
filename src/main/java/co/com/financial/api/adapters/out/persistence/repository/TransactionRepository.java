package co.com.financial.api.adapters.out.persistence.repository;

import co.com.financial.api.adapters.out.persistence.entities.TransactionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
