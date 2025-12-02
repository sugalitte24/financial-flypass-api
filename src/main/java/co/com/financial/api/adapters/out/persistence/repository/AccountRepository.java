package co.com.financial.api.adapters.out.persistence.repository;

import co.com.financial.api.adapters.out.persistence.entities.AccountEntity;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import feign.Param;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByIdAndOwner_Id( UUID idAccount, UUID idCustomer );

    @Modifying
    @Query("UPDATE AccountEntity a SET a.status = :status WHERE a.id = :id and a.owner.id = :ownerId")
    void updateStatus( @Param("id") UUID id, @Param("status") AccountStatus status, @Param("ownerId") UUID ownerId );

}
