/*
package co.com.financial.api.adapters.in.web.controller;

import co.com.financial.api.adapters.in.web.dto.transaction.DepositRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.TransactionResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferRequest;
import co.com.financial.api.adapters.in.web.dto.transaction.WithdrawRequest;
import co.com.financial.api.adapters.in.web.mappers.TransactionMapper;
import co.com.financial.api.application.port.in.TransactionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionUseCase txUseCase;
    private final TransactionMapper mapper;

    public TransactionController( TransactionUseCase txUseCase, TransactionMapper mapper ) {
        this.txUseCase = txUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit( @Valid @RequestBody DepositRequest req ) {
        var cmd = mapper.toDepositCommand(req);
        var created = txUseCase.deposit(cmd);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.id())).body(mapper.toResponse(created));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw( @Valid @RequestBody WithdrawRequest req ) {
        var cmd = mapper.toWithdrawCommand(req);
        var created = txUseCase.withdraw(cmd);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.id())).body(mapper.toResponse(created));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer( @Valid @RequestBody TransferRequest req ) {
        var cmd = mapper.toTransferCommand(req);
        var created = txUseCase.transfer(cmd);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.id())).body(mapper.toResponse(created));
    }
}*/
