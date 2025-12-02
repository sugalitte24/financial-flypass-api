package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.dto.transaction.TransferResponse;
import co.com.financial.api.adapters.out.persistence.enums.TransactionType;
import co.com.financial.api.application.port.in.TransactionUseCase;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.application.port.out.TransactionRepositoryPort;
import co.com.financial.api.domain.model.Account;
import co.com.financial.api.domain.model.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort txRepo;

    @Override
    @Transactional
    public Transaction deposit( String toAccountNumber, BigDecimal amount, String performedBy ) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");

        var to = accountRepo.findByAccountNumberForUpdate(toAccountNumber)
                .orElseThrow(() -> new IllegalStateException("Destination account not found: " + toAccountNumber));

        to.credit(amount);
        accountRepo.save(to);

        Transaction tx = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(amount)
                .toAccount(to)
                .balanceAfter(to.getBalance())
                .performedBy(performedBy)
                .build();
        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public Transaction withdraw( String fromAccountNumber, BigDecimal amount, String performedBy ) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");

        var from = accountRepo.findByAccountNumberForUpdate(fromAccountNumber)
                .orElseThrow(() -> new IllegalStateException("Account not found: " + fromAccountNumber));

        from.debit(amount);
        accountRepo.save(from);

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(amount)
                .fromAccount(from)
                .balanceAfter(from.getBalance())
                .performedBy(performedBy)
                .build();

        return txRepo.save(tx);
    }


    @Override
    @Transactional
    public TransferResponse transfer( String fromAccountNumber, String toAccountNumber, BigDecimal amount, String performedBy ) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (fromAccountNumber.equals(toAccountNumber))
            throw new IllegalArgumentException("Cannot transfer to same account");

        String first = fromAccountNumber.compareTo(toAccountNumber) < 0 ? fromAccountNumber : toAccountNumber;
        String second = fromAccountNumber.compareTo(toAccountNumber) < 0 ? toAccountNumber : fromAccountNumber;

        var firstAcc = accountRepo.findByAccountNumberForUpdate(first)
                .orElseThrow(() -> new IllegalStateException("Account not found: " + first));

        var secondAcc = accountRepo.findByAccountNumberForUpdate(second)
                .orElseThrow(() -> new IllegalStateException("Account not found: " + second));

        Account from = firstAcc.getAccountNumber().equals(fromAccountNumber) ? firstAcc : secondAcc;
        Account to = firstAcc.getAccountNumber().equals(toAccountNumber) ? firstAcc : secondAcc;

        from.debit(amount);
        to.credit(amount);

        accountRepo.save(from);
        accountRepo.save(to);

        Transaction txDebit = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER)
                .amount(amount)
                .fromAccount(from)
                .toAccount(to)
                .balanceAfter(from.getBalance())
                .performedBy(performedBy)
                .build();
        var txDebitSaved = txRepo.save(txDebit);

        Transaction txCredit = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER)
                .amount(amount)
                .fromAccount(from)
                .toAccount(to)
                .balanceAfter(to.getBalance())
                .performedBy(performedBy)
                .build();
        txRepo.save(txCredit);

        return new TransferResponse(
                txDebitSaved.getId(),
                fromAccountNumber,
                toAccountNumber,
                amount,
                txDebitSaved.getCreatedAt(),
                from.getBalance(),
                to.getBalance()
        );
    }
}
