package dao.deprecated;

import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsSQLTableAttributes;
import common.SQLQueries;
import dao.impl.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Customer;
import model.errors.Error;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class CustomerDaoJDBC {
    private final DBConnectionPool db;

    @Inject
    public CustomerDaoJDBC(DBConnectionPool db) {
        this.db = db;
    }


    public Either<Error, List<Customer>> getAll() {

        Either<Error, List<Customer>> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GETALL_CUSTOMERS)) {

            ResultSet rs = preparedStatement.executeQuery();

            List<Customer> customers = readRS(rs).get();

            if (!customers.isEmpty()) {
                result = Either.right(customers);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
        }
        return result;
    }


    public Either<Error, Customer> get(int id) {

        Either<Error, Customer> result;
        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GET_CUSTOMER)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            Customer customer = readRS(rs).get().stream().findFirst().orElse(null);
            if (customer != null) {
                result = Either.right(customer);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMER_FOUND));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMER_FOUND));
        }
        return result;
    }

    private Either<Error, List<Customer>> readRS(ResultSet rs) {
        Either<Error, List<Customer>> either;
        try {
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt(ConstantsSQLTableAttributes.ID));
                c.setFirstName(rs.getString(ConstantsSQLTableAttributes.FIRST_NAME));
                c.setLastName(rs.getString(ConstantsSQLTableAttributes.LAST_NAME));
                c.setEmail(rs.getString(ConstantsSQLTableAttributes.EMAIL));
                c.setPhone(rs.getString(ConstantsSQLTableAttributes.PHONE));
                c.setDob(rs.getDate(ConstantsSQLTableAttributes.DATE_OF_BIRTH).toLocalDate());
                customers.add(c);
            }
            either = Either.right(customers);
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);

            either = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
        }
        return either;
    }

    public Either<Error, Integer> save(Customer customer) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection()) {
            myConnection.setAutoCommit(false);

            try (PreparedStatement preparedStatementCredentials = myConnection.prepareStatement(SQLQueries.ADD_CREDENTIALS, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatementCredentials.setString(1, customer.getCredentials().getUsername());
                preparedStatementCredentials.setString(2, customer.getCredentials().getPassword());

                int credentialsAdded = preparedStatementCredentials.executeUpdate();

                if (credentialsAdded == 0) {
                    result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
                } else {
                    ResultSet generatedKeys = preparedStatementCredentials.getGeneratedKeys();

                    if (generatedKeys != null) {
                        try (PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.ADD_CUSTOMER)) {
                            preparedStatement.setInt(1, generatedKeys.getInt(1));
                            preparedStatement.setString(2, customer.getFirstName());
                            preparedStatement.setString(3, customer.getLastName());
                            preparedStatement.setString(4, customer.getEmail());
                            preparedStatement.setString(5, customer.getPhone());
                            preparedStatement.setDate(6, Date.valueOf(customer.getDob()));

                            int rowsAdded = preparedStatement.executeUpdate();

                            if (rowsAdded > 0) {
                                result = Either.right(0);
                            } else {
                                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CUSTOMER));
                            }
                        } catch (SQLException e) {
                            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
                            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CUSTOMER));
                        }
                    } else {
                        result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CUSTOMER));
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
                myConnection.rollback();
            }
            myConnection.commit();
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_CREDENTIALS));
        }
        return result;
    }

    public Either<Error, Integer> update(Customer customer) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();

             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.UPDATE_CUSTOMER)) {
            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setDate(5, Date.valueOf(customer.getDob()));
            preparedStatement.setInt(6, customer.getId());

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_CUSTOMER));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_CUSTOMER));
        }
        return result;
    }


    public Either<Error, Integer> delete(Customer customer, boolean delete) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.DELETE_CUSTOMER)) {

            preparedStatement.setInt(1, customer.getId());

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                try (PreparedStatement preparedStatementCredentials = myConnection.prepareStatement(SQLQueries.DELETE_CREDENTIALS)) {
                    preparedStatementCredentials.setInt(1, customer.getId());

                    int rowsDeletedCredentials = preparedStatementCredentials.executeUpdate();

                    if (rowsDeletedCredentials > 0) {
                        result = Either.right(0);
                    } else {
                        result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_CREDENTIALS));
                    }
                } catch (SQLException e) {
                    Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
                    result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_CREDENTIALS));
                }
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_CUSTOMER));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_CUSTOMER));
        }
        return result;
    }
}
