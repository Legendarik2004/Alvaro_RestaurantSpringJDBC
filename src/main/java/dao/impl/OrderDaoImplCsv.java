package dao.impl;

import common.Configuration;
import common.Constants;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import model.Order;
import model.errors.OrderError;
import model.errors.OrderErrorEmptyList;
import model.xml.OrderItemXML;
import model.xml.OrderXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class OrderDaoImplCsv implements dao.OrderDAO {

    @Override
    public Either<OrderError, List<Order>> getAll() {
        Either<OrderError, List<Order>> result;
        Path file = Paths.get(Configuration.getInstance().getProperty("pathDataOrdersCsv"));

        //Fill with data
        List<Order> orderList = new ArrayList<>();

//        orderList.add(new Order(1, LocalDateTime.now(), 1, 1));
//        orderList.add(new Order(2, LocalDateTime.now(), 2, 2));
//        orderList.add(new Order(3, LocalDateTime.now(), 3, 3));
//        orderList.add(new Order(4, LocalDateTime.now(), 4, 4));
//        orderList.add(new Order(5, LocalDateTime.now(), 5, 5));
//        orderList.add(new Order(6, LocalDateTime.now(), 6, 6));

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                orderList.add(new Order(line));
            }

            if (orderList.isEmpty()) {
                result = Either.left(new OrderErrorEmptyList());
            } else {
                result = Either.right(orderList);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            result = Either.left(new OrderError(Constants.ERROR_ADDING_ORDER));
        }
        return result;
    }

    @Override
    public Either<OrderError, List<Order>> get(int id) {

        return Either.right(getAll().get().stream().filter(order -> order.getCustomerId() == id).toList());
    }

    @Override
    public Either<OrderError, Integer> save(Order order) {
        Path file = Paths.get(Configuration.getInstance().getProperty("pathDataOrdersCsv"));

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
