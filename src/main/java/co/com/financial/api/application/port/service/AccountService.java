package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.controller.exceptions.NotFoundException;
import co.com.financial.api.adapters.in.web.mappers.AccountMapper;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.application.port.in.AccountUseCase;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.domain.enums.AccountType;
import co.com.financial.api.domain.model.Account;
import co.com.financial.api.domain.model.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort repository;
    private final AccountMapper mapper;
    private final CustomerRepositoryPort customerRepository;

    @Override
    public Account createAccount( Account account ) {
        var owner = customerRepository.findById(account.getOwner().getId());
        if (owner.isEmpty()) throw new NotFoundException("Owner not found");

        var ownerShallow = new Customer();
        ownerShallow.setId(owner.get().getId());

        account.setOwner(ownerShallow);
        account.setAccountNumber(generateAccountNumber(account.getAccountType()));
        return repository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById( UUID id ) {
        return repository.findById(id).orElse(new Account());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomer( UUID customerId ) {
        return repository.getAccountsByCustomer(customerId);
    }

    @Override
    public String changeStatus( UUID id, AccountStatus status, UUID performedBy ) {
        repository.findByIdAndOwner(id, performedBy)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
        repository.changeStatus(id, status, performedBy);
        return "Updated.";
    }

    @Override
    public void cancelAccount( UUID id, UUID performedBy ) {
        var account = repository.findByIdAndOwner(id, performedBy)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Only accounts with zero balance can be cancelled");
        }
        repository.cancelAccount(id, performedBy);
    }

    public String generateAccountNumber( AccountType accountType ) {
        final Map<AccountType, AtomicLong> counters = new ConcurrentHashMap<>();
        final Map<AccountType, String> PREFIX = Map.of(
                AccountType.SAVINGS, "53",
                AccountType.CURRENT, "33"
        );

        counters.put(AccountType.SAVINGS, new AtomicLong(0));
        counters.put(AccountType.CURRENT, new AtomicLong(0));

        AtomicLong counter = counters.get(accountType);
        long next = counter.incrementAndGet();

        if (next > 99_999_999L) {
            throw new IllegalStateException("Sequence limit reached for account numbers");
        }

        String prefix = PREFIX.get(accountType);
        String padded = String.format("%08d", next);

        return prefix + padded;
    }

}
