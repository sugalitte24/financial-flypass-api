package co.com.financial.api.adapters.in.web.controller;

import co.com.financial.api.adapters.in.web.dto.transaction.DepositRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.TransactionResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.WithdrawRequest;
import co.com.financial.api.adapters.in.web.mappers.TransactionMapper;
import co.com.financial.api.application.port.in.TransactionUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase txUseCase;
    private final TransactionMapper mapper;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit( @Valid @RequestBody DepositRequest req ) {
        var created = txUseCase.deposit(req.toAccountNumber(), req.amount(), req.performedBy());
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId())).body(mapper.transactionDomainToResponse(created));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw( @Valid @RequestBody WithdrawRequest req ) {
        var created = txUseCase.withdraw(req.fromAccountNumber(), req.amount(), req.performedBy());
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId())).body(mapper.transactionDomainToResponse(created));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer( @Valid @RequestBody TransferRequest req ) {
        var created = txUseCase.transfer(req.fromAccountNumber(), req.toAccountNumber(), req.amount(), req.performedBy());
        return ResponseEntity.created(URI.create("/api/transactions/" + created.transactionId())).body(created);
    }
}
