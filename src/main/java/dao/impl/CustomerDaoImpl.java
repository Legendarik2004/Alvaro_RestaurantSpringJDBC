package dao.impl;

import common.SQLQueries;
import common.constants.ConstantsErrorMessages;
import dao.CustomersDAO;
import dao.mappers.CustomerMapper;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Customer;
import model.errors.Error;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

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
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Customer> get(int id) {

        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, Customer> result;

        try {
            List<Customer> customers = jtm.query(SQLQueries.GET_CUSTOMER, new CustomerMapper(), id);

            if (!customers.isEmpty()) {
                result = Either.right(customers.get(0));
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMER_FOUND));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMER_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Integer> save(Customer customer) {
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(db.getDataSource());
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        Either<Error, Integer> result;

        try {
            JdbcTemplate jtm = new JdbcTemplate(Objects.requireNonNull(transactionManager.getDataSource()));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int credentialsAdded = jtm.update(
                    connection -> {
                        PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.ADD_CREDENTIALS, Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, customer.getCredentials().getUsername());
                        preparedStatement.setString(2, customer.getCredentials().getPassword());
                        return preparedStatement;
                    },
                    keyHolder);
            if (credentialsAdded > 0) {
                Number generatedCredentialId = keyHolder.getKey();
                if (generatedCredentialId != null) {
                    try {
                        int customerAdded = jtm.update(SQLQueries.ADD_CUSTOMER, generatedCredentialId.intValue(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getPhone(), Date.valueOf(customer.getDob()));

                        if (customerAdded > 0) {
                            transactionManager.commit(txStatus);
                            result = Either.right(0);
                        } else {
                            transactionManager.rollback(txStatus);
                            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CUSTOMER));
                        }
                    } catch (Exception e) {
                        transactionManager.rollback(txStatus);
                        result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CUSTOMER));
                    }
                } else {
                    transactionManager.rollback(txStatus);
                    result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
                }
            } else {
                transactionManager.rollback(txStatus);
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
            }
        } catch (DuplicateKeyException e) {
            transactionManager.rollback(txStatus);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.USER_EXISTS));
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
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
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_CUSTOMER));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_CUSTOMER));
        }
        return result;
    }


    @Override
    public Either<Error, Integer> delete(Customer customer, boolean delete) {
        Either<Error, Integer> result;

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(db.getDataSource());
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
                JdbcTemplate jtm = new JdbcTemplate(Objects.requireNonNull(transactionManager.getDataSource()));
                if (delete) {
                    int rowsAffected1 = jtm.update(SQLQueries.DELETE_CUSTOMER_ORDERITEMS, customer.getId());
                    int rowsAffected2 = jtm.update(SQLQueries.DELETE_CUSTOMER_ORDERS, customer.getId());
                    int rowsAffected3 = jtm.update(SQLQueries.DELETE_CUSTOMER, customer.getId());
                    int rowsAffected4 = jtm.update(SQLQueries.DELETE_CREDENTIALS, customer.getId());

                    if (rowsAffected1 == 0 || rowsAffected2 == 0 || rowsAffected3 == 0 || rowsAffected4 == 0) {
                        transactionManager.rollback(txStatus);
                        result = Either.left(new Error(0, ConstantsErrorMessages.ERROR_DELETING_CUSTOMER_WITH_ORDERS));
                    } else {
                        transactionManager.commit(txStatus);
                        result = Either.right(0);
                    }
                } else {
                    int rowsAffected1 = jtm.update(SQLQueries.DELETE_CUSTOMER, customer.getId());
                    int rowsAffected2 = jtm.update(SQLQueries.DELETE_CREDENTIALS, customer.getId());

                    if (rowsAffected1 == 0 || rowsAffected2 == 0) {
                        transactionManager.rollback(txStatus);
                        result = Either.left(new Error(0, ConstantsErrorMessages.ERROR_DELETING_CUSTOMER));
                    } else {
                        transactionManager.commit(txStatus);
                        result = Either.right(0);
                    }
                }
        } catch (DataAccessException ex) {
            transactionManager.rollback(txStatus);
            if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                result = Either.left(new Error(2, ConstantsErrorMessages.THE_CUSTOMER_HAS_ORDERS));
            } else {
                result = Either.left(new Error(1, ConstantsErrorMessages.ERROR_CONNECTING_DATABASE));
            }
        }

        return result;
    }
}
