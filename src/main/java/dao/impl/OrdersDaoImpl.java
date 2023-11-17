package dao.impl;

import common.SQLQueries;
import common.constants.ConstantsErrorMessages;
import dao.OrdersDAO;
import dao.deprecated.OrderDaoJDBC;
import dao.mappers.OrderMapper;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.OrderItem;
import model.errors.Error;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
@Named("ordersDAO")
public class OrdersDaoImpl implements OrdersDAO {

    private final DBConnectionPool db;

    @Inject
    public OrdersDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<Error, List<Order>> getAll() {

        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, List<Order>> result;

        try {
            result = Either.right(jtm.query(SQLQueries.GETALL_ORDERS, new OrderMapper()));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, List<Order>> getAll(int id) {
        return null;
    }

    @Override
    public Either<Error, Order> get(int id) {

        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, Order> result;

        try {
            List<Order> orders = jtm.query(SQLQueries.GET_ORDER, new OrderMapper(), id);

            if (!orders.isEmpty()) {
                result = Either.right(orders.get(0));
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_FOUND));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Integer> add(Order order) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(db.getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            try {
                List<OrderItem> orderItems = order.getOrderItems();
                int customerId = order.getCustomerId();

                try (Connection myConnection = db.getConnection();
                     PreparedStatement preparedStatementOrder = myConnection.prepareStatement(SQLQueries.ADD_ORDER, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement preparedStatementOrderItems = myConnection.prepareStatement(SQLQueries.ADD_ORDERITEM)) {

                    preparedStatementOrder.setTimestamp(1, order.getDate());
                    preparedStatementOrder.setInt(2, customerId);
                    preparedStatementOrder.setInt(3, order.getTableId());

                    int rowsAdded = preparedStatementOrder.executeUpdate();

                    if (rowsAdded > 0 && !orderItems.isEmpty()) {
                        ResultSet generatedKeys = preparedStatementOrder.getGeneratedKeys();

                        if (generatedKeys.next()) {
                            for (OrderItem orderItem : orderItems) {
                                preparedStatementOrderItems.setInt(1, generatedKeys.getInt(1));
                                preparedStatementOrderItems.setInt(2, orderItem.getMenuItem().getMenuItemId());
                                preparedStatementOrderItems.setInt(3, orderItem.getQuantity());

                                int rowsItemAdded = preparedStatementOrderItems.executeUpdate();

                                if (rowsItemAdded <= 0) {
                                    throw new SQLException(ConstantsErrorMessages.ERROR_ADDING_ITEM);
                                }
                            }
                        } else {
                            throw new SQLException(ConstantsErrorMessages.ERROR_ADDING_ORDER);
                        }
                    } else {
                        throw new SQLException(ConstantsErrorMessages.ERROR_ADDING_ORDER);
                    }
                }
                return Either.right(0);
            } catch (SQLException e) {
                Logger.getLogger(OrdersDaoImpl.class.getName()).log(Level.SEVERE, null, e);
                return Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ORDER));
            }
        });
    }


    @Override
    public Either<Error, Integer> update(Order order) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();

             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.UPDATE_ORDER)) {

            preparedStatement.setInt(1, order.getTableId());
            preparedStatement.setInt(2, order.getOrderId());

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_ORDER));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_UPDATING_ORDER));
        }
        return result;
    }

    @Override
    public Either<Error, Integer> delete(Order order) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(db.getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            try {
                try (Connection myConnection = db.getConnection();
                     PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.DELETE_ORDER)) {

                    preparedStatement.setInt(1, order.getOrderId());

                    int rowsDeleted = preparedStatement.executeUpdate();

                    if (rowsDeleted > 0) {
                        return Either.right(0);
                    } else {
                        throw new SQLException(ConstantsErrorMessages.ERROR_DELETING_ORDER);
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
                return Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ORDER));
            }
        });
    }


    @Override
    public Either<Error, Integer> save(List<Order> orders) {
        return null;
    }
}
