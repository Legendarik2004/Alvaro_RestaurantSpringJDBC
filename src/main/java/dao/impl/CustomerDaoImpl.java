package dao.impl;


import common.Configuration;
import common.Constants;
import dao.CustomersDAO;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import model.Customer;
import model.errors.CustomerError;
import model.errors.CustomerErrorEmptyList;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class CustomerDaoImpl implements CustomersDAO {

    @Override
    public Either<CustomerError, List<Customer>> getAll() {

        Either<CustomerError, List<Customer>> result;
        Path file = Paths.get(Configuration.getInstance().getProperty("pathDataCustomers"));
        List<Customer> customerList = new ArrayList<>();
        try {

            List<String> lines = Files.readAllLines(file);
            lines.forEach(line -> customerList.add(new Customer(line)));
            if (customerList.isEmpty()) {
                result = Either.left(new CustomerErrorEmptyList());
            } else {
                result = Either.right(customerList);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.ERROR_ADDING_CUSTOMER));
        }
        return result;
    }

    @Override
    public Either<CustomerError, Customer> get(int id) {
        List<Customer> customersList = getAll().get();
        Either<CustomerError, Customer> result;
        try {
            Customer customer = customersList.stream().filter(customers -> customers.getId() == id).findFirst().orElse(null);
            if (customer != null) {
                result = Either.right(customer);
            } else {
                result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.CUSTOMER_DOESNT_EXIST));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.ERROR_ADDING_CUSTOMER));
        }
        return result;
    }

    public Either<CustomerError, Integer> save(Customer customer) {
        Path path = Paths.get(Configuration.getInstance().getProperty("pathDataCustomers"));

        try {
            String customerData = customer.toStringTextFile();

            Files.write(path, Collections.singletonList(customerData), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            return Either.right(0);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.ERROR_ADDING_CUSTOMER));
        }
    }

    @Override
    public Either<CustomerError, Integer> update(Customer c) {
        return Either.right(0);
    }

    @Override
    public Either<CustomerError, Integer> delete(Customer c) {
        Path path = Paths.get(Configuration.getInstance().getProperty("pathDataCustomers"));
        Either<CustomerError, Integer> result = null;
        try {
            List<Customer> customerList = getAll().get();

            Customer customer = customerList.stream()
                    .filter(customer1 -> customer1.getId() == c.getId())
                    .findFirst()
                    .orElse(null);

            if (customer != null) {
                customerList.remove(customer);
                List<String> stringList = customerList.stream().map(Customer::toStringTextFile).collect(Collectors.toList());

                Files.write(path, stringList);

                result = Either.right(0);
            }else{
                result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.CUSTOMER_DOESNT_EXIST));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.FAILED_TO_DELETE_THE_CUSTOMER));
        }
        return result;
    }

}
