package co.com.financial.api.service;

import co.com.financial.api.adapters.in.web.controller.exceptions.AlreadyExistException;
import co.com.financial.api.adapters.in.web.controller.exceptions.GenericException;
import co.com.financial.api.adapters.in.web.controller.exceptions.ResourceNotFoundException;
import co.com.financial.api.adapters.in.web.controller.exceptions.UnderAgeException;
import co.com.financial.api.adapters.in.web.mappers.CustomerMapper;
import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import co.com.financial.api.adapters.out.persistence.repository.AccountRepository;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.application.port.service.CustomerService;
import co.com.financial.api.domain.model.Customer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort repository;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private CustomerService sut;

    private UUID customerId;
    private Customer domain;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        domain = new Customer();
        domain.setId(customerId);
        domain.setIdentificationType(IdentificationType.CC);
        domain.setIdentificationNumber("123456");
        domain.setBirthDate(LocalDate.now().minusYears(30));
    }

    @Test
    void createCustomer_whenAlreadyExists_throwsAlreadyExist() {
        when(repository.findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber()))
                .thenReturn(Optional.of(new Customer()));
        assertThrows(AlreadyExistException.class, () -> sut.createCustomer(domain));
        verify(repository).findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber());
        verify(repository, never()).save(any());
    }

    @Test
    void createCustomer_whenUnderAge_throwsUnderAge() {
        domain.setBirthDate(LocalDate.now().minusYears(17));
        when(repository.findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber()))
                .thenReturn(Optional.empty());
        assertThrows(UnderAgeException.class, () -> sut.createCustomer(domain));
        verify(repository).findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber());
        verify(repository, never()).save(any());
    }

    @Test
    void createCustomer_success_callsSave() {
        when(repository.findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber()))
                .thenReturn(Optional.empty());
        when(repository.save(domain)).thenReturn(domain);
        Customer created = sut.createCustomer(domain);
        assertNotNull(created);
        verify(repository).save(domain);
    }

    @Test
    void getCustomerById_whenFound_returnsCustomer() {
        when(repository.findById(customerId)).thenReturn(Optional.of(domain));
        Customer result = sut.getCustomerById(customerId);
        assertNotNull(result);
        assertEquals(customerId, result.getId());
    }

    @Test
    void getCustomerById_whenNotFound_returnsEmptyCustomer() {
        when(repository.findById(customerId)).thenReturn(Optional.empty());
        Customer result = sut.getCustomerById(customerId);
        assertNotNull(result);
    }

    @Test
    void getAllCustomers_returnsList() {
        Customer c1 = new Customer();
        c1.setId(UUID.randomUUID());
        Customer c2 = new Customer();
        c2.setId(UUID.randomUUID());
        when(repository.findAll()).thenReturn(List.of(c1, c2));
        List<Customer> list = sut.getAllCustomers();
        assertEquals(2, list.size());
        verify(repository).findAll();
    }

    @Test
    void updateCustomer_whenNotFound_throwsResourceNotFound() {
        when(repository.findById(customerId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> sut.updateCustomer(customerId, domain));
    }

    @Test
    void updateCustomer_success_callsMapperAndSave() {
        Customer existing = new Customer();
        existing.setId(customerId);
        when(repository.findById(customerId)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        sut.updateCustomer(customerId, domain);
        verify(mapper).updateCustomer(domain, existing);
        verify(repository).save(existing);
    }

    @Test
    void deleteCustomer_whenHasAccounts_throwsGenericException() {
        co.com.financial.api.adapters.out.persistence.entities.AccountEntity acc = new co.com.financial.api.adapters.out.persistence.entities.AccountEntity();
        when(accountRepo.findByOwner_Id(customerId)).thenReturn(List.of(acc));
        assertThrows(GenericException.class, () -> sut.deleteCustomer(customerId));
        verify(accountRepo).findByOwner_Id(customerId);
        verify(repository, never()).deleteById(any());
    }


    @Test
    void deleteCustomer_success_deletes() {
        when(accountRepo.findByOwner_Id(customerId)).thenReturn(List.of());
        doNothing().when(repository).deleteById(customerId);
        sut.deleteCustomer(customerId);
        verify(repository).deleteById(customerId);
    }

    @Test
    void validarMayorDeEdad_under18_throwsUnderAge() {
        LocalDate fecha = LocalDate.now().minusYears(10);
        assertThrows(UnderAgeException.class, () -> sut.validarMayorDeEdad(fecha));
    }

    @Test
    void validarMayorDeEdad_18OrMore_noThrow() {
        LocalDate fecha = LocalDate.now().minusYears(18);
        assertDoesNotThrow(() -> sut.validarMayorDeEdad(fecha));
    }
}
