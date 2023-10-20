package dao.impl;

import common.Constants;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.errors.OrderError;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class OrderDaoImplCsv implements dao.OrderDAO {
    private final DBConnection db;

    @Inject
    public OrderDaoImplCsv(DBConnection db) {
        this.db = db;
    }

    @Override
    public Either<OrderError, List<Order>> getAll() {

        Either<OrderError, List<Order>> result;

        try (Connection myConnection = db.getConnection();
             Statement statement = myConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = statement.executeQuery("select * from orders");



            result = Either.right(readRS(rs).get());
        } catch (SQLException e) {
            Logger.getLogger(OrderDaoImplCsv.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new OrderError( Constants.ERROR));
        }
        return result;
    }

    private Either<OrderError, List<Order>> readRS(ResultSet rs) {
        Either<OrderError, List<Order>> either;
        try {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order resultOrder = new Order(
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getInt("order_id"),
                        rs.getInt("order_id"));
                orders.add(resultOrder);
            }
            either = Either.right(orders);
        } catch (SQLException e) {
            e.printStackTrace();
            either = Either.left(new OrderError(Constants.ERROR));
        }
        return either;
    }
    @Override
    public Either<OrderError, List<Order>> get(int id) {

        return Either.right(getAll().get().stream().filter(order -> order.getCustomerId() == id).toList());
    }

    @Override
    public Either<OrderError, Integer> save(Order order) {
        return Either.right(0);
    }


    @Override
    public Either<OrderError, Integer> update(Order c) {
        return Either.right(0);
    }

    @Override
    public Either<OrderError, Integer> delete(Order c) {
        return Either.right(0);
    }
}
