package services;

import dao.MenuItemsDAO;
import dao.OrderItemsDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.errors.Error;

import java.util.List;

public class OrderItemsService {
    @Inject
    private OrderItemsDAO dao;
    @Inject
    private MenuItemsDAO menuItemsDao;

    public Either<Error, List<OrderItem>> getAllOrderItems(int id) {
        return dao.getAll(id);
    }

    public Either<Error, List<Order>> get(int id) {
        return dao.get(id);
    }

    public Either<Error, Integer> save(OrderItem orderItem) {
        return dao.save(orderItem);
    }

    public Either<Error, Integer> update(OrderItem orderItem) {
        return dao.update(orderItem);
    }

    public Either<Error, Integer> delete(OrderItem orderItem) {
        return dao.delete(orderItem);
    }

    public Either<Error, List<MenuItem>> getAllMenuItems() {
        return menuItemsDao.getAll();
    }

}
