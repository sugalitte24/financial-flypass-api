package co.com.financial.api.controller;


import co.com.financial.api.adapters.in.web.controller.CustomerController;
import co.com.financial.api.adapters.in.web.dto.client.CustomerRequest;
import co.com.financial.api.adapters.in.web.dto.client.CustomerRequestUpdate;
import co.com.financial.api.adapters.in.web.dto.client.CustomerResponse;
import co.com.financial.api.adapters.in.web.mappers.CustomerMapper;
import co.com.financial.api.application.port.in.CustomerUseCase;
import co.com.financial.api.domain.model.Customer;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerUseCase customerUseCase;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerController sut;

    @Mock
    private CustomerRequest req;

    @Mock
    private CustomerRequestUpdate reqUpdate;

    @Mock
    private CustomerResponse resp;

    private UUID id;
    private Customer domain;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        domain = new Customer();
        domain.setId(id);
    }

    @Test
    void createCustomer_returnsCreated() {
        when(mapper.customerRequestToDomain(req)).thenReturn(domain);
        when(customerUseCase.createCustomer(domain)).thenReturn(domain);

        when(mapper.customerDomainToResponse(domain)).thenReturn(resp);
        when(resp.id()).thenReturn(id);

        ResponseEntity<CustomerResponse> result = sut.createCustomer(req);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        assertEquals(URI.create("/api/customer/" + id), result.getHeaders().getLocation());
        verify(mapper).customerRequestToDomain(req);
        verify(customerUseCase).createCustomer(domain);
        verify(mapper).customerDomainToResponse(domain);
    }


    @Test
    void getCustomer_returnsOk() {
        when(customerUseCase.getCustomerById(id)).thenReturn(domain);
        when(mapper.customerDomainToResponse(domain)).thenReturn(resp);

        ResponseEntity<CustomerResponse> result = sut.getCustomer(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        verify(customerUseCase).getCustomerById(id);
        verify(mapper).customerDomainToResponse(domain);
    }

    @Test
    void listCustomers_returnsList() {
        Customer c1 = new Customer();
        c1.setId(UUID.randomUUID());
        Customer c2 = new Customer();
        c2.setId(UUID.randomUUID());
        CustomerResponse r1 = mock(CustomerResponse.class);
        CustomerResponse r2 = mock(CustomerResponse.class);
        when(customerUseCase.getAllCustomers()).thenReturn(List.of(c1, c2));
        when(mapper.customerDomainToResponse(c1)).thenReturn(r1);
        when(mapper.customerDomainToResponse(c2)).thenReturn(r2);

        ResponseEntity<List<CustomerResponse>> result = sut.listCustomers();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        assertTrue(result.getBody().contains(r1));
        assertTrue(result.getBody().contains(r2));
        verify(customerUseCase).getAllCustomers();
    }

    @Test
    void updateCustomer_returnsOk() {
        Customer updated = new Customer();
        updated.setId(id);
        when(mapper.customerRequestToDomain(reqUpdate)).thenReturn(updated);
        when(customerUseCase.updateCustomer(id, updated)).thenReturn(updated);
        when(mapper.customerDomainToResponse(updated)).thenReturn(resp);

        ResponseEntity<CustomerResponse> result = sut.updateCustomer(id, reqUpdate);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(resp, result.getBody());
        verify(mapper).customerRequestToDomain(reqUpdate);
        verify(customerUseCase).updateCustomer(id, updated);
        verify(mapper).customerDomainToResponse(updated);
    }

    @Test
    void deleteCustomer_returnsNoContent() {
        doNothing().when(customerUseCase).deleteCustomer(id);

        ResponseEntity<Void> result = sut.deleteCustomer(id);

        assertEquals(204, result.getStatusCodeValue());
        assertNull(result.getBody());
        verify(customerUseCase).deleteCustomer(id);
    }
}
