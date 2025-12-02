package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.controller.exceptions.NotFoundException;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.adapters.out.persistence.enums.AccountType;
import co.com.financial.api.application.port.in.AccountUseCase;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.domain.model.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort repository;
    private final CustomerRepositoryPort customerRepository;

    @Override
    public Account createAccount( Account account ) {
        if (account == null) throw new IllegalArgumentException("Account is required");
        if (account.getOwnerId() == null) throw new IllegalArgumentException("ownerId is required");

        var ownerOpt = customerRepository.findById(account.getOwnerId());
        if (ownerOpt.isEmpty()) throw new NotFoundException("Owner not found");

        account.setAccountNumber(generateAccountNumber(String.valueOf(account.getAccountType())));
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

    public String generateAccountNumber( String accountType ) {
        String prefix = Objects.equals(accountType, "SAVINGS") ? "53" : "33";
        String maxNumber = repository.findMaxAccountNumber(AccountType.valueOf(accountType));

        if (maxNumber == null) {
            return prefix + "00000001";
        }

        long current = Long.parseLong(maxNumber.substring(2));
        long next = current + 1;

        if (next > 99_999_999L) {
            throw new IllegalStateException("Sequence limit reached for account numbers");
        }

        return prefix + String.format("%08d", next);
    }

}
