package co.com.financial.api.application.port.out;

import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.domain.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    Account save( Account account );

    Optional<Account> findById( UUID id );

    List<Account> getAccountsByCustomer( UUID customerId );

    void changeStatus( UUID id, AccountStatus status, UUID performedBy );

    Optional<Account> findByIdAndOwner( UUID id, UUID performedBy );

    void cancelAccount( UUID id, UUID performedBy );
}
