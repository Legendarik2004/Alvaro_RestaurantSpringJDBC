package dao.impl;

import common.constants.ConstantsErrorMessages;
import common.SQLQueries;
import dao.OrderItemsDAO;
import dao.mappers.OrderItemMapper;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.OrderItem;
import model.errors.Error;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class OrderItemsDaoImpl implements OrderItemsDAO {
    private final DBConnectionPool db;

    @Inject
    public OrderItemsDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<Error, List<OrderItem>> get(int id) {

        Either<Error, List<OrderItem>> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());

        try {
            result = Either.right(jtm.query(SQLQueries.GETALL_ORDERITEMS, new OrderItemMapper(), id));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_ITEMS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Integer> save(OrderItem orderItem) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.ADD_ORDERITEM)) {

            preparedStatement.setInt(1, orderItem.getOrderId());
            preparedStatement.setInt(2, orderItem.getMenuItem().getMenuItemId());
            preparedStatement.setInt(3, orderItem.getQuantity());

            int rowsAdded = preparedStatement.executeUpdate();

            if (rowsAdded > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ITEM));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderItemsDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ITEM));
        }
        return result;
    }


    @Override
    public Either<Error, Integer> delete(OrderItem orderItem) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.DELETE_ORDERITEM)) {

            preparedStatement.setInt(1, orderItem.getOrderItemId());


            int rowsAdded = preparedStatement.executeUpdate();

            if (rowsAdded > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ITEM));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderItemsDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ITEM));
        }
        return result;
    }
}
