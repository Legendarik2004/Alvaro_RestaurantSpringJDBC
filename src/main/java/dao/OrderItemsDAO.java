package dao;

import io.vavr.control.Either;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.errors.Error;

import java.util.List;

public interface OrderItemsDAO {
    Either<Error, List<OrderItem>> getAll(int id);

    Either<Error, List<Order>> get(int id);

    Either<Error, Integer> save(OrderItem orderItem);

    Either<Error, Integer> update(OrderItem orderItem);

    Either<Error, Integer> delete(OrderItem orderItem);


}
