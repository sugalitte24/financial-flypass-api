package co.com.financial.api.controller;


import co.com.financial.api.adapters.in.web.controller.TransactionController;
import co.com.financial.api.adapters.in.web.dto.transaction.DepositRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.TransactionResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.WithdrawRequest;
import co.com.financial.api.adapters.in.web.mappers.TransactionMapper;
import co.com.financial.api.adapters.out.persistence.enums.TransactionType;
import co.com.financial.api.application.port.in.TransactionUseCase;
import co.com.financial.api.domain.model.Account;
import co.com.financial.api.domain.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionUseCase txUseCase;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionController sut;

    private final String toAcc = "5300000002";
    private final String fromAcc = "3300000001";

    @BeforeEach
    void setUp() {
    }

    @Test
    void deposit_returnsCreatedAndBody() {
        DepositRequest req = new DepositRequest(toAcc, new BigDecimal("100.00"), "tester");
        UUID txId = UUID.randomUUID();
        Transaction domainTx = Transaction.builder()
                .id(txId)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .build();
        TransactionResponse resp = new TransactionResponse(txId, TransactionType.DEPOSIT,
                new BigDecimal("50.00"), new BigDecimal("950.00"), null, new Account(), String.valueOf(UUID.randomUUID()));

        when(txUseCase.deposit(toAcc, req.amount(), req.performedBy())).thenReturn(domainTx);
        when(mapper.transactionDomainToResponse(domainTx)).thenReturn(resp);

        ResponseEntity<TransactionResponse> result = sut.deposit(req);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        assertEquals("/api/transactions/" + txId, result.getHeaders().getLocation().toString());
        verify(txUseCase).deposit(toAcc, req.amount(), req.performedBy());
        verify(mapper).transactionDomainToResponse(domainTx);
    }

    @Test
    void withdraw_returnsCreatedAndBody() {
        WithdrawRequest req = new WithdrawRequest(fromAcc, new BigDecimal("50.00"), "tester");
        UUID txId = UUID.randomUUID();
        Transaction domainTx = Transaction.builder()
                .id(txId)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("50.00"))
                .build();
        TransactionResponse resp = new TransactionResponse(txId, TransactionType.WITHDRAWAL,
                new BigDecimal("50.00"), new BigDecimal("950.00"), null, new Account(), String.valueOf(UUID.randomUUID()));

        when(txUseCase.withdraw(fromAcc, req.amount(), req.performedBy())).thenReturn(domainTx);
        when(mapper.transactionDomainToResponse(domainTx)).thenReturn(resp);

        ResponseEntity<TransactionResponse> result = sut.withdraw(req);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        assertEquals("/api/transactions/" + txId, result.getHeaders().getLocation().toString());
        verify(txUseCase).withdraw(fromAcc, req.amount(), req.performedBy());
        verify(mapper).transactionDomainToResponse(domainTx);
    }

    @Test
    void transfer_returnsCreatedAndBody() {
        TransferRequest req = new TransferRequest(fromAcc, toAcc, new BigDecimal("150.00"), "tester");
        UUID txId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TransferResponse resp = new TransferResponse(txId, fromAcc, toAcc, req.amount(), now, new BigDecimal("850.00"), new BigDecimal("350.00"));

        when(txUseCase.transfer(fromAcc, toAcc, req.amount(), req.performedBy())).thenReturn(resp);

        ResponseEntity<TransferResponse> result = sut.transfer(req);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        assertEquals("/api/transactions/" + txId, result.getHeaders().getLocation().toString());
        verify(txUseCase).transfer(fromAcc, toAcc, req.amount(), req.performedBy());
    }
}
