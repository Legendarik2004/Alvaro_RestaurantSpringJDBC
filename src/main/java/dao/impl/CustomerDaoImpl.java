package dao.impl;


import common.Configuration;
import common.Constants;
import dao.CustomersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Customer;
import model.errors.CustomerError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Log4j2
public class CustomerDaoImpl implements CustomersDAO {
    private final DBConnection db;

    @Inject
    public CustomerDaoImpl(DBConnection db) {
        this.db = db;
    }

    @Override
    public Either<CustomerError, List<Customer>> getAll() {

        Either<CustomerError, List<Customer>> result;

        try (Connection myConnection = db.getConnection();
             Statement statement = myConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = statement.executeQuery("select * from customers");


            result = Either.right(readRS(rs).get());
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoImplCsv.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new CustomerError(0, Constants.ERROR));
        }
        return result;
    }

    private Either<CustomerError, List<Customer>> readRS(ResultSet rs) {
        Either<CustomerError, List<Customer>> either;
        try {
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                Customer resultCustomer = new Customer(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("date_of_birth").toLocalDate()
                );
                customers.add(resultCustomer);
            }
            either = Either.right(customers);
        } catch (SQLException e) {
            e.printStackTrace();
            either = Either.left(new CustomerError(0, Constants.ERROR));
        }
        return either;
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
        Either<CustomerError, Integer> result;

        try (Connection myConnection = db.getConnection();
             Statement statement = myConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            if (statement.executeUpdate("INSERT INTO customers (id, first_name, last_name, email, phone, date_of_birth) VALUES (" + customer.getId() + ", '" + customer.getFirstName() + "', '" + customer.getLastName() + "', '" + customer.getEmail() + "', '" + customer.getPhone() + "', '" + customer.getDob() + "')")
                    > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new CustomerError(0, Constants.ERROR));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoImplCsv.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new CustomerError(0, Constants.ERROR));
        }
        return result;
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
                List<String> stringList = customerList.stream().map(Customer::toString).collect(Collectors.toList());

                Files.write(path, stringList);

                result = Either.right(0);
            } else {
                result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.CUSTOMER_DOESNT_EXIST));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            result = Either.left(new CustomerError(Constants.THE_CUSTOMER_LIST_IS_EMPTY_N, Constants.FAILED_TO_DELETE_THE_CUSTOMER));
        }
        return result;
    }

}
