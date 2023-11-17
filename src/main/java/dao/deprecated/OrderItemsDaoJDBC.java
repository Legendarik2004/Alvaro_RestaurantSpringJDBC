package dao.deprecated;

import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsSQLTableAttributes;
import common.SQLQueries;
import dao.impl.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.MenuItem;
import model.OrderItem;
import model.errors.Error;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class OrderItemsDaoJDBC {
    private final DBConnectionPool db;

    @Inject
    public OrderItemsDaoJDBC(DBConnectionPool db) {
        this.db = db;
    }

    public Either<Error, List<OrderItem>> get(int id) {
        Either<Error, List<OrderItem>> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GETALL_ORDERITEMS)) {
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            result = Either.right(readRS(rs).get());
        } catch (SQLException e) {
            Logger.getLogger(OrderItemsDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_ITEMS_FOUND));
        }
        return result;
    }

    private Either<Error, List<OrderItem>> readRS(ResultSet rs) {
        Either<Error, List<OrderItem>> result;
        try {
            List<OrderItem> orderItems = new ArrayList<>();
            while (rs.next()) {
                OrderItem o = new OrderItem();
                MenuItem m = new MenuItem();
                m.setMenuItemId(rs.getInt(ConstantsSQLTableAttributes.MENU_ITEM_ID));
                m.setName(rs.getString(ConstantsSQLTableAttributes.NAME));
                m.setDescription(rs.getString(ConstantsSQLTableAttributes.DESCRIPTION));
                m.setPrice(rs.getDouble(ConstantsSQLTableAttributes.PRICE));
                o.setMenuItem(m);
                o.setOrderItemId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ITEM_ID));
                o.setQuantity(rs.getInt(ConstantsSQLTableAttributes.QUANTITY));
                o.setOrderId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ID));
                orderItems.add(o);
            }
            result = Either.right(orderItems);
        } catch (SQLException e) {
            Logger.getLogger(OrderItemsDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_ITEMS_FOUND));
        }
        return result;
    }

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
            Logger.getLogger(OrderItemsDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ADDING_ITEM));
        }
        return result;
    }

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
            Logger.getLogger(OrderItemsDaoJDBC.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_DELETING_ITEM));
        }
        return result;
    }
}
