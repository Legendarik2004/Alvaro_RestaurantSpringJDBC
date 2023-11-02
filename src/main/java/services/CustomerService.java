package services;

import dao.CustomersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Customer;
import model.errors.Error;

import java.util.List;

public class CustomerService {
    @Inject
    private CustomersDAO dao;

    public Either<Error, List<Customer>> getAll() {
        return dao.getAll();
    }

    public Either<Error, Customer> get(int id) {
        return dao.get(id);
    }

    public Either<Error, Integer> save(Customer c) {
        return dao.save(c);
    }

    public Either<Error, Integer> update(Customer c) {
        return dao.update(c);
    }

    public Either<Error, Integer> delete(Customer c) {
        return dao.delete(c);
    }
}
