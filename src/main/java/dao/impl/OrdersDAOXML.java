package dao.impl;

import common.SQLQueries;
import common.constants.Constants;
import common.constants.ConstantsErrorMessages;
import dao.OrdersDAO;
import dao.mappers.OrderItemMapper;
import dao.mappers.OrderMapper;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.errors.Error;
import model.xml.OrderItemXML;
import model.xml.OrderXML;
import model.xml.OrdersXML;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Named("ordersDAOXML")
public class OrdersDAOXML implements OrdersDAO {
    private final DBConnectionPool db;

    @Inject
    public OrdersDAOXML(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<Error, List<Order>> getAll() {
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, List<Order>> result;

        try {
            result = Either.right(jtm.query(SQLQueries.GETALL_ORDERS, new OrderMapper()));
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDERS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, List<Order>> getAll(int id) {
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        Either<Error, List<Order>> result;


        try {
            List<Order> orders = jtm.query(SQLQueries.GET_ORDER_OF_CUSTOMER, new OrderMapper(), id);

            if (!orders.isEmpty()) {
                result = Either.right(orders);
            } else {
                result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDERS_FOUND));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.NO_ORDERS_FOUND));
        }
        return result;
    }

    @Override
    public Either<Error, Order> get(int id) {
        return null;
    }

    @Override
    public Either<Error, Integer> add(Order order) {
        return null;
    }

    @Override
    public Either<Error, Integer> update(Order order) {
        return null;
    }

    @Override
    public Either<Error, Integer> delete(Order order) {
        return null;
    }

    @Override
    public Either<Error, Integer> save(List<Order> orders) {
        int customerId = orders.get(0).getCustomerId();
        Path ordersXMLPath = Paths.get(Constants.DATA_XML_PATH + Constants.CUSTOMER + customerId + Constants.ORDERS + Constants.XML_EXTENSION);

        try {
            JAXBContext context = JAXBContext.newInstance(OrdersXML.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            OrdersXML ordersList = new OrdersXML();
            ordersList.setOrderList(new ArrayList<>());

            for (Order order : orders) {
                OrderXML newOrderXML = new OrderXML();
                newOrderXML.setId(order.getOrderId());
                newOrderXML.setOrderItem(new ArrayList<>());


                if (order.getOrderItems() != null) {
                    order.getOrderItems().forEach(orderItem -> {
                        OrderItemXML newOrderItemXML = new OrderItemXML();
                        newOrderItemXML.setMenuItem(orderItem.getMenuItem().getName());
                        newOrderItemXML.setQuantity(orderItem.getQuantity());
                        newOrderXML.getOrderItem().add(newOrderItemXML);
                    });
                }

                ordersList.getOrderList().add(newOrderXML);
            }

            try (Writer writer = Files.newBufferedWriter(ordersXMLPath)) {
                marshaller.marshal(ordersList, writer);
            }

            return Either.right(0);

        } catch (Exception e) {
            log.error(e.getMessage());
            return Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_SAVING_ORDERS_XML));
        }
    }


}