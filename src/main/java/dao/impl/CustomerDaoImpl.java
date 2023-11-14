package dao.impl;

import common.Constants;
import common.SQLQueries;
import dao.CustomersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Customer;
import model.errors.Error;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class CustomerDaoImpl implements CustomersDAO {
    private final DBConnectionPool db;

    @Inject
    public CustomerDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<Error, List<Customer>> getAll() {

        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, List<Customer>> result;

        try {
            result = Either.right(jtm.query(SQLQueries.GETALL_CUSTOMERS, new CustomerMapper()));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.NO_CUSTOMERS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Customer> get(int id) {
        List<Customer> customersList = getAll().get();
        Either<Error, Customer> result;
        try {
            Customer customer = customersList.stream().filter(customers -> customers.getId() == id).findFirst().orElse(null);
            if (customer != null) {
                result = Either.right(customer);
            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.NO_CUSTOMER_FOUND));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.NO_CUSTOMER_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Integer> save(Customer customer) {
        Either<Error, Integer> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            int credentialsAdded = jtm.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.ADD_CREDENTIALS, Statement.RETURN_GENERATED_KEYS);
                            preparedStatement.setString(1, customer.getCredentials().getUsername());
                            preparedStatement.setString(2, customer.getCredentials().getPassword());
                            return preparedStatement;
                        }
                    },
                    keyHolder
            );

            if (credentialsAdded > 0) {
                Number generatedCredentialId = keyHolder.getKey();

                if (generatedCredentialId != null) {
                    int customerId = generatedCredentialId.intValue();

                    int customerAdded = jtm.update(
                            SQLQueries.ADD_CUSTOMER,
                            customerId,
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getEmail(),
                            customer.getPhone(),
                            Date.valueOf(customer.getDob())
                    );

                    if (customerAdded > 0) {
                        result = Either.right(customerId);
                    } else {
                        result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_CUSTOMER));
                    }
                } else {
                    result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_CREDENTIALS));
                }
            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_CREDENTIALS));
            }
        } catch (DataAccessException e) {
            Logger.getLogger(CustomerDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_CREDENTIALS));
        }

        return result;
    }

    @Override
    public Either<Error, Integer> update(Customer customer) {
        Either<Error, Integer> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        try {
            int rowsUpdated = jtm.update(SQLQueries.UPDATE_CUSTOMER, customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getPhone(), Date.valueOf(customer.getDob()), customer.getId());

            if (rowsUpdated > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_UPDATING_CUSTOMER));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_UPDATING_CUSTOMER));
        }
        return result;
    }


    @Override
    public Either<Error, Integer> delete(Customer customer) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.DELETE_CUSTOMER)) {

            preparedStatement.setInt(1, customer.getId());

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {

                result = deleteCredential(myConnection, customer);

            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_DELETING_CUSTOMER));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_UPDATING_CUSTOMER));
        }
        return result;
    }

    public Either<Error, Integer> deleteCredential(Connection myConnection, Customer customer) {
        Either<Error, Integer> result;
        try (PreparedStatement preparedStatementCredentials = myConnection.prepareStatement(SQLQueries.DELETE_CREDENTIALS)) {
            preparedStatementCredentials.setInt(1, customer.getId());

            int rowsDeletedCredentials = preparedStatementCredentials.executeUpdate();

            if (rowsDeletedCredentials > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_DELETING_CREDENTIALS));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_DELETING_CREDENTIALS));
        }

        return result;
    }
}
