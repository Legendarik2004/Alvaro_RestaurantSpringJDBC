package services;

import dao.OrderDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Order;
import model.errors.OrderError;

import java.util.List;

public class OrderService {
    @Inject
    private OrderDAO dao;

    public Either<OrderError, List<Order>> getAll() {
        return dao.getAll();
    }

    public Either<OrderError, List<Order>> get(int id) {
        return dao.get(id);
    }

    public Either<OrderError, Integer> save(Order o) {
        return dao.save(o);
    }

    public Either<OrderError, Integer> update(Order o) {
        return dao.update(o);
    }

    public Either<OrderError, Integer> delete(Order o) {
        return dao.delete(o);
    }
}
