package co.com.financial.api.application.port.out;

import co.com.financial.api.domain.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    Account save( Account account);

    Optional<Account> findById( UUID id);

    Optional<Account> findByAccountNumber( String accountNumber);

    List<Account> findByCustomerId( UUID customerId);

    void deleteById(UUID id);
}
