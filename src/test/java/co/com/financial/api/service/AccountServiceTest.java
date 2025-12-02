package co.com.financial.api.service;


import co.com.financial.api.adapters.in.web.controller.exceptions.NotFoundException;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.adapters.out.persistence.enums.AccountType;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.application.port.service.AccountService;
import co.com.financial.api.domain.model.Account;
import co.com.financial.api.domain.model.Customer;
import java.math.BigDecimal;
import java.util.List;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private CustomerRepositoryPort customerRepository;

    @InjectMocks
    private AccountService sut;

    private UUID ownerId;
    private UUID accountId;
    private Customer owner;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        owner = new Customer();
        owner.setId(ownerId);
    }

    @Test
    void createAccount_whenAccountIsNull_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sut.createAccount(null));
        assertEquals("Account is required", ex.getMessage());
    }

    @Test
    void createAccount_whenOwnerIdMissing_throwsIllegalArgument() {
        Account account = new Account();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sut.createAccount(account));
        assertEquals("ownerId is required", ex.getMessage());
    }

    @Test
    void createAccount_whenOwnerNotFound_throwsNotFound() {
        Account account = new Account();
        account.setOwnerId(ownerId);

        when(customerRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sut.createAccount(account));
        verify(customerRepository).findById(ownerId);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void createAccount_success_callsRepositorySave() {
        Account account = new Account();
        account.setOwnerId(ownerId);
        account.setAccountType(co.com.financial.api.domain.enums.AccountType.SAVINGS);

        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account created = sut.createAccount(account);

        assertNotNull(created);
        assertNotNull(created.getAccountNumber());
        assertTrue(created.getAccountNumber().startsWith("53") || created.getAccountNumber().startsWith("33"));
        verify(customerRepository).findById(ownerId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountById_whenFound_returnsAccount() {
        Account account = new Account();
        account.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = sut.getAccountById(accountId);
        assertNotNull(result);
        assertEquals(accountId, result.getId());
    }

    @Test
    void getAccountById_whenNotFound_returnsEmptyAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        Account result = sut.getAccountById(accountId);
        assertNotNull(result);
    }

    @Test
    void getAccountsByCustomer_delegatesToRepository() {
        Account a1 = new Account();
        a1.setId(UUID.randomUUID());
        Account a2 = new Account();
        a2.setId(UUID.randomUUID());
        when(accountRepository.getAccountsByCustomer(ownerId)).thenReturn(List.of(a1, a2));

        List<Account> list = sut.getAccountsByCustomer(ownerId);
        assertEquals(2, list.size());
        verify(accountRepository).getAccountsByCustomer(ownerId);
    }

    @Test
    void changeStatus_whenAccountNotFound_throwsNotFound() {
        when(accountRepository.findByIdAndOwner(accountId, ownerId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> sut.changeStatus(accountId, AccountStatus.ACTIVE, ownerId));
        verify(accountRepository).findByIdAndOwner(accountId, ownerId);
    }

    @Test
    void changeStatus_success_callsChangeStatusAndReturnsUpdated() {
        Account found = new Account();
        found.setId(accountId);
        found.setOwnerId(ownerId);

        when(accountRepository.findByIdAndOwner(accountId, ownerId)).thenReturn(Optional.of(found));
        doNothing().when(accountRepository).changeStatus(accountId, AccountStatus.ACTIVE, ownerId);

        String resp = sut.changeStatus(accountId, AccountStatus.ACTIVE, ownerId);
        assertEquals("Updated.", resp);
        verify(accountRepository).changeStatus(accountId, AccountStatus.ACTIVE, ownerId);
    }

    @Test
    void cancelAccount_whenAccountNotFound_throwsNotFound() {
        when(accountRepository.findByIdAndOwner(accountId, ownerId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> sut.cancelAccount(accountId, ownerId));
    }

    @Test
    void cancelAccount_whenBalanceNotZero_throwsIllegalState() {
        Account found = new Account();
        found.setId(accountId);
        found.setOwnerId(ownerId);
        found.setBalance(new BigDecimal("10.00"));

        when(accountRepository.findByIdAndOwner(accountId, ownerId)).thenReturn(Optional.of(found));
        assertThrows(IllegalStateException.class, () -> sut.cancelAccount(accountId, ownerId));
        verify(accountRepository, never()).cancelAccount(any(), any());
    }

    @Test
    void cancelAccount_success_callsRepositoryCancel() {
        Account found = new Account();
        found.setId(accountId);
        found.setOwnerId(ownerId);
        found.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByIdAndOwner(accountId, ownerId)).thenReturn(Optional.of(found));
        doNothing().when(accountRepository).cancelAccount(accountId, ownerId);

        sut.cancelAccount(accountId, ownerId);
        verify(accountRepository).cancelAccount(accountId, ownerId);
    }

    @Test
    void generateAccountNumber_whenNoMax_returnsFirstNumberSavings() {
        when(accountRepository.findMaxAccountNumber(AccountType.SAVINGS)).thenReturn(null);
        String res = sut.generateAccountNumber("SAVINGS");
        assertEquals("5300000001", res);
    }

    @Test
    void generateAccountNumber_whenMaxExists_returnsNext() {
        when(accountRepository.findMaxAccountNumber(AccountType.CURRENT)).thenReturn("3300000015");
        String res = sut.generateAccountNumber("CURRENT");
        assertEquals("3300000016", res);
    }

    @Test
    void generateAccountNumber_whenOverflow_throws() {
        when(accountRepository.findMaxAccountNumber(AccountType.SAVINGS)).thenReturn("5399999999");
        assertThrows(IllegalStateException.class, () -> sut.generateAccountNumber("SAVINGS"));
    }
}
