package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Most methods only read, so the whole class is read-only. */
@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    public List<String> names() {
        return customers.findAll().stream().map(Customer::getName).sorted().toList();
    }

    public List<String> vipNames() {
        return customers.findByVipTrue().stream().map(Customer::getName).sorted().toList();
    }

    public Long register(String name) {
        Customer customer = new Customer(name);
        customers.save(customer);
        return customer.getId();
    }

    public void rename(Long id, String newName) {
        customers.findById(id).orElseThrow().rename(newName);
    }

    public void markVip(Long id) {
        customers.findById(id).orElseThrow().markVip();
    }
}
