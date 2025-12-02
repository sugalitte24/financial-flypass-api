package co.com.financial.api.controller;

import co.com.financial.api.adapters.in.web.controller.AccountController;
import co.com.financial.api.adapters.in.web.dto.account.AccountRequest;
import co.com.financial.api.adapters.in.web.dto.account.AccountResponse;
import co.com.financial.api.adapters.in.web.mappers.AccountMapper;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.application.port.in.AccountUseCase;
import co.com.financial.api.domain.model.Account;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountUseCase accountUseCase;

    @Mock
    private AccountMapper mapper;

    @InjectMocks
    private AccountController sut;

    private UUID accountId;
    private UUID ownerId;
    private Account domain;
    private AccountRequest req;
    private AccountResponse resp;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        domain = new Account();
        domain.setId(accountId);
        domain.setAccountNumber("5300000001");
        domain.setBalance(new BigDecimal("100.00"));
        domain.setAvailableBalance(new BigDecimal("100.00"));
        domain.setOwnerId(ownerId);

        req = new AccountRequest(ownerId, null, true);
        resp = new AccountResponse(accountId, "SAVINGS", "131232132", "ACTIVE",
                new BigDecimal("100.00"), new BigDecimal("100.00"), false, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createAccount_returnsCreatedResponse() {
        when(mapper.toDomain(req)).thenReturn(domain);
        when(accountUseCase.createAccount(domain)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(resp);

        ResponseEntity<AccountResponse> result = sut.createAccount(req);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        assertEquals(URI.create("/api/accounts/" + resp.id()), result.getHeaders().getLocation());
        verify(mapper).toDomain(req);
        verify(accountUseCase).createAccount(domain);
        verify(mapper).toResponse(domain);
    }

    @Test
    void getAccount_returnsOk() {
        when(accountUseCase.getAccountById(accountId)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(resp);

        ResponseEntity<AccountResponse> result = sut.getAccount(accountId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        verify(accountUseCase).getAccountById(accountId);
        verify(mapper).toResponse(domain);
    }

    @Test
    void getAccountsByCustomer_returnsList() {
        Account other = new Account();
        other.setId(UUID.randomUUID());
        AccountResponse r1 = resp;
        AccountResponse r2 = new AccountResponse(accountId, "SAVINGS", "131232132", "ACTIVE",
                new BigDecimal("100.00"), new BigDecimal("100.00"), false, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(accountUseCase.getAccountsByCustomer(ownerId)).thenReturn(List.of(domain, other));
        when(mapper.toResponse(domain)).thenReturn(r1);
        when(mapper.toResponse(other)).thenReturn(r2);

        ResponseEntity<List<AccountResponse>> result = sut.getAccountsByCustomer(ownerId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        assertTrue(result.getBody().contains(r1));
        assertTrue(result.getBody().contains(r2));
        verify(accountUseCase).getAccountsByCustomer(ownerId);
    }

    @Test
    void changeStatus_returnsOkMessage() {
        when(accountUseCase.changeStatus(accountId, AccountStatus.ACTIVE, ownerId)).thenReturn("Updated.");

        ResponseEntity<String> result = sut.changeStatus(accountId, AccountStatus.ACTIVE, ownerId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Updated.", result.getBody());
        verify(accountUseCase).changeStatus(accountId, AccountStatus.ACTIVE, ownerId);
    }

    @Test
    void cancelAccount_returnsOkAndCallsUseCase() {
        doNothing().when(accountUseCase).cancelAccount(accountId, ownerId);

        ResponseEntity<String> result = sut.cancelAccount(accountId, ownerId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Cancelada", result.getBody());
        verify(accountUseCase).cancelAccount(accountId, ownerId);
    }
}

