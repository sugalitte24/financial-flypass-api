package co.com.financial.api.adapters.in.web.controller;

import co.com.financial.api.adapters.in.web.dto.client.CustomerRequest;
import co.com.financial.api.adapters.in.web.dto.client.CustomerRequestUpdate;
import co.com.financial.api.adapters.in.web.dto.client.CustomerResponse;
import co.com.financial.api.adapters.in.web.mappers.CustomerMapper;
import co.com.financial.api.application.port.in.CustomerUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final CustomerMapper mapper;

    public CustomerController( CustomerUseCase customerUseCase, CustomerMapper mapper ) {
        this.customerUseCase = customerUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer( @Valid @RequestBody CustomerRequest req ) {
        var domain = mapper.customerRequestToDomain(req);
        var created = customerUseCase.createCustomer(domain);
        var resp = mapper.customerDomainToResponse(created);
        return ResponseEntity.created(URI.create("/api/customer/" + resp.id())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer( @PathVariable UUID id ) {
        return ResponseEntity.ok(mapper.customerDomainToResponse(customerUseCase.getCustomerById(id)));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> listCustomers() {
        var list = customerUseCase.getAllCustomers().stream().map(mapper::customerDomainToResponse).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer( @PathVariable UUID id,
                                                            @Valid @RequestBody CustomerRequestUpdate req ) {
        var updated = customerUseCase.updateCustomer(id, mapper.customerRequestToDomain(req));
        return ResponseEntity.ok(mapper.customerDomainToResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer( @PathVariable UUID id ) {
        customerUseCase.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}