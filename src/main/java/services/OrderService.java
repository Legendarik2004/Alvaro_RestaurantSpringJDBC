package services;

import dao.OrderDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Order;
import model.errors.Error;

import java.util.List;

public class OrderService {
    @Inject
    private OrderDAO dao;

    public Either<Error, List<Order>> getAll() {
        return dao.getAll();
    }

    public Either<Error, List<Order>> get(int id) {
        return dao.get(id);
    }

    public Either<Error, Integer> save(Order o) {
        return dao.save(o);
    }

    public Either<Error, Integer> update(Order o) {
        return dao.update(o);
    }

    public Either<Error, Integer> delete(Order o) {
        return dao.delete(o);
    }

    public int getAddedOrderId() {return dao.getAddedOrderId();
    }
}
