package dao.impl;

import common.Constants;
import common.SQLQueries;
import dao.OrderItemsDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.errors.Error;

import java.sql.*;
import java.util.ArrayList;
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
    public Either<Error, List<OrderItem>> getAll(int id) {
        Either<Error, List<OrderItem>> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.GETALL_ORDERITEMS);
        ) {
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            result = Either.right(readRS(rs).get());
        } catch (SQLException e) {
            Logger.getLogger(OrderItemsDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR));
        }
        return result;
    }


    private Either<Error, List<OrderItem>> readRS(ResultSet rs) {
        Either<Error, List<OrderItem>> result;
        try {
            List<OrderItem> orderItems = new ArrayList<>();
            while (rs.next()) {
                int orderItemId = rs.getInt("order_item_id");
                int orderId = rs.getInt("order_id");
                int menuItemId = rs.getInt("menu_item_id");
                int quantity = rs.getInt("quantity");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");

                orderItems.add(new OrderItem(orderItemId, orderId, menuItemId, quantity, new MenuItem(menuItemId, name, description, price)));
            }
            result = Either.right(orderItems);
        } catch (SQLException e) {
            e.printStackTrace();
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR));
        }
        return result;
    }


    @Override
    public Either<Error, List<Order>> get(int id) {

        //return Either.right(getAll().get().stream().filter(order -> order.getCustomerId() == id).toList());
        return Either.right(new ArrayList<>());
    }


    @Override
    public Either<Error, Integer> save(OrderItem orderItem) {
        Either<Error, Integer> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.ADD_ORDERITEM)) {

            preparedStatement.setInt(1, orderItem.getOrderId());
            preparedStatement.setInt(2, orderItem.getMenuItemId());
            preparedStatement.setInt(3, orderItem.getQuantity());

            int rowsAdded = preparedStatement.executeUpdate();

            if (rowsAdded > 0) {
                result = Either.right(0);
            } else {
                result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_ITEM));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(Constants.NUM_ERROR, Constants.ERROR_ADDING_ITEM));
        }
        return result;
    }


    @Override
    public Either<Error, Integer> update(OrderItem orderItem) {
        return Either.right(0);
    }

    @Override
    public Either<Error, Integer> delete(OrderItem orderItem) {
        return Either.right(0);
    }
}
