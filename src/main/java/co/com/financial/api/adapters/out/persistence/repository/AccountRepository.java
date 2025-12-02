package co.com.financial.api.adapters.out.persistence.repository;

import co.com.financial.api.adapters.out.persistence.entities.AccountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
}
