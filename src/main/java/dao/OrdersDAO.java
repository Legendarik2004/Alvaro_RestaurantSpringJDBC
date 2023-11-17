package dao;

import io.vavr.control.Either;
import model.Order;
import model.errors.Error;

import java.util.List;

public interface OrdersDAO {
    Either<Error, List<Order>> getAll();
    Either<Error, List<Order>> getAll(int id);

    Either<Error, Order> get(int id);

    Either<Error, Integer> add(Order order);

    Either<Error, Integer> update(Order order);

    Either<Error, Integer> delete(Order order);

    Either<Error, Integer> save(List<Order> orders);
}
