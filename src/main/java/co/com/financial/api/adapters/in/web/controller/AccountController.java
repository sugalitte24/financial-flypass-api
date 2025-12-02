package co.com.financial.api.adapters.in.web.controller;

import co.com.financial.api.adapters.in.web.dto.account.AccountRequest;
import co.com.financial.api.adapters.in.web.dto.account.AccountResponse;
import co.com.financial.api.adapters.in.web.mappers.AccountMapper;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.application.port.in.AccountUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;
    private final AccountMapper mapper;


    @PostMapping
    public ResponseEntity<AccountResponse> createAccount( @Valid @RequestBody AccountRequest req ) {
        var domain = mapper.toDomain(req);
        var created = accountUseCase.createAccount(domain);
        var resp = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/accounts/" + resp.id())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount( @PathVariable UUID id ) {
        return ResponseEntity.ok(mapper.toResponse(accountUseCase.getAccountById(id)));
    }

    @GetMapping("/get-by-customer/{ownerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer( @PathVariable UUID ownerId ) {
        var list = accountUseCase.getAccountsByCustomer(ownerId).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeStatus( @PathVariable UUID id,
                                                @RequestParam AccountStatus status,
                                                @RequestParam UUID performedBy ) {
        var updated = accountUseCase.changeStatus(id, status, performedBy);
        return ResponseEntity.ok((updated));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelAccount( @PathVariable UUID id, @RequestParam UUID performedBy ) {
        accountUseCase.cancelAccount(id, performedBy);
        return ResponseEntity.ok("Cancelada");
    }
}