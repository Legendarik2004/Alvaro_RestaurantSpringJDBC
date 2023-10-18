package services;

import dao.CustomersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Customer;
import model.errors.CustomerError;

import java.io.IOException;
import java.util.List;

public class CustomerService {
    @Inject
    private CustomersDAO dao;

    public Either<CustomerError, List<Customer>> getAll() {
        return dao.getAll();
    }

    public Either<CustomerError, Customer> get(int id) {
        return dao.get(id);
    }

    public Either<CustomerError, Integer> save(Customer c) {
        return dao.save(c);
    }

    public Either<CustomerError, Integer> update(Customer c) {
        return dao.update(c);
    }

    public Either<CustomerError, Integer> delete(Customer c) {
        return dao.delete(c);
    }
}
