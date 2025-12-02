package co.com.financial.api.adapters.out.persistence.repository;

import co.com.financial.api.adapters.out.persistence.entities.AccountEntity;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.adapters.out.persistence.enums.AccountType;
import feign.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByIdAndOwner_Id( UUID idAccount, UUID idCustomer );

    @Modifying
    @Query("UPDATE AccountEntity a SET a.status = :status WHERE a.id = :id and a.owner.id = :ownerId")
    void updateStatus( @Param("id") UUID id, @Param("status") AccountStatus status, @Param("ownerId") UUID ownerId );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.accountNumber = :accountNumber")
    Optional<AccountEntity> findByAccountNumberForUpdate( @Param("accountNumber") String accountNumber );

    @Query("SELECT MAX(a.accountNumber) FROM AccountEntity a WHERE a.accountType = :type")
    String findMaxAccountNumber( AccountType type );

    List<AccountEntity> findByOwner_Id( UUID ownerId );
}
