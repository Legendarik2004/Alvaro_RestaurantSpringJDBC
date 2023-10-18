package dao;

import io.vavr.control.Either;
import model.Order;
import model.errors.OrderError;
import model.xml.OrderItemXML;
import model.xml.OrderXML;

import java.util.List;

public interface OrderDAOXml {
    Either<OrderError, List<OrderXML>> getAll();

    Either<OrderError, List<OrderItemXML>> get(int id);

    Either<OrderError, Integer> save(Order c);

    Either<OrderError, Integer> update(Order c);

    Either<OrderError, Integer> delete(Order c);


}
