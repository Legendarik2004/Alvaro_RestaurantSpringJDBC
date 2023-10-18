package dao.impl;

import common.ConfigurationXML;
import common.Constants;
import io.vavr.control.Either;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.errors.OrderError;
import model.xml.OrderItemXML;
import model.xml.OrderXML;
import model.xml.OrdersXML;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class OrderDaoImplXML implements dao.OrderDAOXml {

    @Override
    public Either<OrderError, List<OrderXML>> getAll() {

        Path file = Paths.get(ConfigurationXML.getInstance().getProperty("xmlPath"));
        try {
            JAXBContext context = JAXBContext.newInstance(OrdersXML.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            OrdersXML orders = (OrdersXML) unmarshaller.unmarshal(Files.newInputStream(file));

            return Either.right(orders.getOrderList());
        } catch (IOException | JAXBException e) {
            log.error(e.getMessage());
            return Either.left(new OrderError(Constants.ERROR_ADDING_ORDER));
        }
    }

    @Override
    public Either<OrderError, List<OrderItemXML>> get(int id) {
        Either<OrderError, List<OrderItemXML>> result;

        Either<OrderError, List<OrderXML>> allOrders = getAll();
        result = allOrders
                .flatMap(rightValue -> {
                    List<OrderXML> ordersList = rightValue;
                    Optional<OrderXML> matchingOrder = ordersList.stream()
                            .filter(order -> order.getId() == id)
                            .findFirst();

                    if (matchingOrder.isPresent()) {
                        return Either.right(matchingOrder.get().getOrderItem());
                    } else {
                        return Either.right(new ArrayList<>());
                    }
                });
        return result;
    }


    @Override
    public Either<OrderError, Integer> save(Order order) {
        Path file = Paths.get(ConfigurationXML.getInstance().getProperty("xmlPath"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), true))) {

            writer.write(order.toStringTextFile());
            writer.newLine();
            return Either.right(0);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Either.left(new OrderError(Constants.ERROR_ADDING_ORDER));
        }
    }


    @Override
    public Either<OrderError, Integer> update(Order c) {
        return Either.right(1);
    }

    @Override
    public Either<OrderError, Integer> delete(Order c) {
        return Either.right(1);
    }
}
