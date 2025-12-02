package co.com.financial.api.application.port.in;

import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.domain.model.Account;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {

    Account createAccount( Account account );

    Account getAccountById( UUID id );

    List<Account> getAccountsByCustomer( UUID customerId );

    String changeStatus( UUID id, AccountStatus status, UUID performedBy );

    void cancelAccount( UUID id, UUID performedBy );
}
