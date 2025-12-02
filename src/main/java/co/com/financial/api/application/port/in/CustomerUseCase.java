package co.com.financial.api.application.port.in;

import co.com.financial.api.domain.model.Customer;
import java.util.List;
import java.util.UUID;

public interface CustomerUseCase {

    Customer createCustomer( Customer customer);

    Customer updateCustomer(UUID id, Customer customer);

    Customer getCustomerById(UUID id);

    List<Customer> getAllCustomers();

    void deleteCustomer( UUID id);
}
