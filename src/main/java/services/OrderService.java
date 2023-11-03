package services;

import io.vavr.control.Either;
import model.Order;
import model.errors.Error;

import java.util.List;

public interface OrderService {
    Either<Error, List<Order>> getAll();

    Either<Error, List<Order>> get(int id);

    Either<Error, Integer> save(Order o);

    Either<Error, Integer> update(Order o);

    Either<Error, Integer> delete(Order o);

    int getAddedOrderId();
}
