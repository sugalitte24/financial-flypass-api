package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.dto.transaction.TransferResponse;
import co.com.financial.api.adapters.out.persistence.enums.TransactionType;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.application.port.out.TransactionRepositoryPort;
import co.com.financial.api.domain.model.Account;
import co.com.financial.api.domain.model.Transaction;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepositoryPort accountRepo;

    @Mock
    private TransactionRepositoryPort txRepo;

    @InjectMocks
    private TransactionService sut;

    private final String accFrom = "3300000001";
    private final String accTo = "5300000002";
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account();
        fromAccount.setId(UUID.randomUUID());
        fromAccount.setAccountNumber(accFrom);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setAvailableBalance(new BigDecimal("1000.00"));

        toAccount = new Account();
        toAccount.setId(UUID.randomUUID());
        toAccount.setAccountNumber(accTo);
        toAccount.setBalance(new BigDecimal("200.00"));
        toAccount.setAvailableBalance(new BigDecimal("200.00"));
    }

    @Test
    void deposit_whenAmountNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> sut.deposit(accTo, null, "user"));
    }

    @Test
    void deposit_whenAccountNotFound_throws() {
        when(accountRepo.findByAccountNumberForUpdate(accTo)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> sut.deposit(accTo, new BigDecimal("50"), "user"));
    }

    @Test
    void deposit_success_persistsAccountAndTransaction() {
        when(accountRepo.findByAccountNumberForUpdate(accTo)).thenReturn(Optional.of(toAccount));
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        Transaction savedTx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("50"))
                .toAccount(toAccount)
                .balanceAfter(new BigDecimal("250.00"))
                .performedBy("user")
                .build();
        when(txRepo.save(any(Transaction.class))).thenReturn(savedTx);
        Transaction result = sut.deposit(accTo, new BigDecimal("50"), "user");
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        verify(accountRepo).save(toAccount);
        verify(txRepo).save(any(Transaction.class));
    }

    @Test
    void withdraw_whenAmountNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> sut.withdraw(accFrom, null, "user"));
    }

    @Test
    void withdraw_whenAccountNotFound_throws() {
        when(accountRepo.findByAccountNumberForUpdate(accFrom)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> sut.withdraw(accFrom, new BigDecimal("10"), "user"));
    }

    @Test
    void withdraw_success_persistsAccountAndTransaction() {
        when(accountRepo.findByAccountNumberForUpdate(accFrom)).thenReturn(Optional.of(fromAccount));
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        Transaction savedTx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100"))
                .fromAccount(fromAccount)
                .balanceAfter(new BigDecimal("900.00"))
                .performedBy("user")
                .build();
        when(txRepo.save(any(Transaction.class))).thenReturn(savedTx);
        Transaction result = sut.withdraw(accFrom, new BigDecimal("100"), "user");
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactionType());
        verify(accountRepo).save(fromAccount);
        verify(txRepo).save(any(Transaction.class));
    }

    @Test
    void transfer_whenAmountNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> sut.transfer(accFrom, accTo, null, "user"));
    }

    @Test
    void transfer_whenSameAccount_throws() {
        assertThrows(IllegalArgumentException.class, () -> sut.transfer(accFrom, accFrom, new BigDecimal("10"), "user"));
    }

    @Test
    void transfer_whenFirstAccountNotFound_throws() {
        when(accountRepo.findByAccountNumberForUpdate(accFrom)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> sut.transfer(accFrom, accTo, new BigDecimal("10"), "user"));
    }

    @Test
    void transfer_success_updatesBalancesAndCreatesTransactions() {
        String first = accFrom.compareTo(accTo) < 0 ? accFrom : accTo;
        String second = accFrom.compareTo(accTo) < 0 ? accTo : accFrom;
        when(accountRepo.findByAccountNumberForUpdate(first)).thenReturn(Optional.of(fromAccount));
        when(accountRepo.findByAccountNumberForUpdate(second)).thenReturn(Optional.of(toAccount));
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        Transaction savedDebit = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("150"))
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .balanceAfter(new BigDecimal("850.00"))
                .performedBy("user")
                .build();
        when(txRepo.save(any(Transaction.class))).thenReturn(savedDebit);
        TransferResponse resp = sut.transfer(accFrom, accTo, new BigDecimal("150"), "user");
        assertNotNull(resp);
        assertEquals(accFrom, resp.fromAccount());
        assertEquals(accTo, resp.toAccount());
        assertEquals(new BigDecimal("850.00"), resp.fromBalanceAfter());
        verify(accountRepo, times(2)).save(any(Account.class));
        verify(txRepo, times(2)).save(any(Transaction.class));
    }
}
