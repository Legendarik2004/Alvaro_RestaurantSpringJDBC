package services.impl;

import dao.CustomersDAO;
import dao.OrderItemsDAO;
import dao.OrdersDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Customer;
import model.Order;
import model.errors.Error;
import services.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomersDAO dao;
    private final OrdersDAO orderXML;
    private final OrderItemsDAO orderItemsDAO;

    @Inject
    public CustomerServiceImpl(CustomersDAO dao, @Named("ordersDAOXML") OrdersDAO orderXML, OrderItemsDAO orderItemsDAO) {
        this.dao = dao;
        this.orderXML = orderXML;
        this.orderItemsDAO = orderItemsDAO;
    }

    @Override
    public Either<Error, List<Customer>> getAll() {
        return dao.getAll();
    }

    @Override
    public Either<Error, Customer> get(int id) {
        return dao.get(id);
    }

    @Override
    public Either<Error, Integer> save(Customer c) {
        return dao.save(c);
    }

    @Override
    public Either<Error, Integer> update(Customer c) {
        return dao.update(c);
    }

    @Override
    public Either<Error, Integer> delete(Customer c, boolean delete) {
        if (delete) {
            List<Order> orders = orderXML.getAll(c.getId()).get();
            for (Order o : orders) {
                o.setOrderItems(orderItemsDAO.get(o.getOrderId()).get());
            }
            orderXML.save(orders);
        }
        Either<Error, Integer> resultDao = dao.delete(c, delete);
        Either<Error, Integer> result;
        if (resultDao.isLeft()) {
            if (resultDao.getLeft().getNumError() == 2){
                result = Either.right(0);
            }else {
                result = Either.left(resultDao.getLeft());
            }
        } else {
            result = Either.right(1);
        }
        return result;
    }
}
