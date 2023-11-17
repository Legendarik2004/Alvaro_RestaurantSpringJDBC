package dao.deprecated;

import common.SQLQueries;
import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsSQLTableAttributes;
import dao.impl.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.OrderItem;
import model.errors.Error;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@Log4j2
public class OrderDaoJDBC {

    private final DBConnectionPool db;

    @Inject
    public OrderDaoJDBC(DBConnectionPool db) {
        this.db = db;
    }


    public Either<Error, List<Order>> getAll() {

        Either<Error, List<Order>> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GETALL_CUSTOMERS)) {

            ResultSet rs = preparedStatement.executeQuery();

            List<Order> orders = readRS(rs).get();

            if (!orders.isEmpty()) {
                result = Either.right(orders);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_CUSTOMERS_FOUND));
        }
        return result;
    }

    public Either<Error, Order> get(int id) {

        Either<Error, Order> result;
        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GET_ORDER)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            Order order = readRS(rs).get().stream().findFirst().orElse(null);
            if (order != null) {
                result = Either.right(order);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_FOUND));
            }
        } catch (SQLException e) {
            Logger.getLogger(CustomerDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_FOUND));
        }
        return result;
    }

    private Either<Error, List<Order>> readRS(ResultSet rs) {
        Either<Error, List<Order>> result;
        try {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ID));
                o.setDate(rs.getTimestamp(ConstantsSQLTableAttributes.ORDER_DATE));
                o.setCustomerId(rs.getInt(ConstantsSQLTableAttributes.CUSTOMER_ID));
                o.setTableId(rs.getInt(ConstantsSQLTableAttributes.TABLE_ID));
                orders.add(o);
            }
            result = Either.right(orders);
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);

            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDERS_FOUND));
        }
        return result;
    }

    public Either<Error, Integer> save(Order order) {
        Either<Error, Integer> result = null;
        List<OrderItem> orderItems = order.getOrderItems();

        try (Connection myConnection = db.getConnection()) {
            myConnection.setAutoCommit(false);
            try (PreparedStatement preparedStatementOrder = myConnection.prepareStatement(SQLQueries.ADD_ORDER, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatementOrderItems = myConnection.prepareStatement(SQLQueries.ADD_ORDERITEM)) {
                preparedStatementOrder.setTimestamp(1, order.getDate());
                preparedStatementOrder.setInt(2, order.getCustomerId());
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

                            if (rowsItemAdded > 0) {
                                myConnection.commit();
                                result = Either.right(0);
                            } else {
                                myConnection.rollback();
                                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ITEM));
                                break;
                            }
                        }
                    } else {
                        myConnection.rollback();
                        result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ORDER));
                    }
                } else {
                    myConnection.rollback();
                    result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ORDER));
                }
            } catch (SQLException e) {
                Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
                myConnection.rollback();
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ORDER));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ORDER));
        }
        return result;
    }


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


    public Either<Error, Integer> delete(Order order) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.DELETE_ORDER)) {

            preparedStatement.setInt(1, order.getOrderId());

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ORDER));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ORDER));
        }
        return result;
    }

    public Either<Error, Integer> save(List<Order> orders) {

        Either<Error, Integer> result;
        if (orders != null) {
            result = Either.right(0);
        } else {
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_SAVING_ORDERS_XML));
        }
        return result;
    }
}
