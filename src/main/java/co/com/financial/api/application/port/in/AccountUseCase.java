package co.com.financial.api.application.port.in;

import co.com.financial.api.domain.model.Account;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {

    Account createAccount( Account account);

    Account updateAccount(UUID id, Account account);

    Account getAccountById(UUID id);

    List<Account> getAccountsByCustomer( UUID customerId);

    Account changeStatus(UUID id, String status, String performedBy);

    void cancelAccount( UUID id,  String performedBy );
}
