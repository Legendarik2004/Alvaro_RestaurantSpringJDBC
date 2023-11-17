package services.impl;

import common.constants.ConstantsErrorMessages;
import dao.OrdersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Order;
import model.errors.Error;
import services.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrdersDAO dao;

    @Inject
    public OrderServiceImpl(@Named("ordersDAO") OrdersDAO dao) {
        this.dao = dao;
    }

    @Override
    public Either<Error, List<Order>> getAll() {
        return dao.getAll();
    }
    @Override
    public Either<Error, Order> get(int id) {
        return dao.get(id);
    }

    @Override
    public Either<Error, List<Order>> getOrderOfCustomer(int id) {
        return Either.right(getAll().get().stream().filter(order -> order.getCustomerId() == id).toList());
    }

    @Override
    public Either<Error, Double> getTotalPrice(Order order) {
        Either<Error, Double> result;

        if (order.getOrderItems().isEmpty()) {
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDER_ITEMS_FOUND));
        } else {
            double totalPrice = order.getOrderItems().stream()
                    .mapToDouble(orderItem -> orderItem.getMenuItem().getPrice() * orderItem.getQuantity())
                    .sum();

            double roundedTotalPrice = Math.round(totalPrice * 100.0) / 100.0;
            result = Either.right(roundedTotalPrice);
        }
        return result;
    }

    @Override
    public Either<Error, Integer> add(Order o) {
        return dao.add(o);
    }

    @Override
    public Either<Error, Integer> update(Order o) {
        return dao.update(o);
    }

    @Override
    public Either<Error, Integer> delete(Order o) {
        return dao.delete(o);
    }
}
