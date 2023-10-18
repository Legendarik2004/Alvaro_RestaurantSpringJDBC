package services;

import dao.OrderDAOXml;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.errors.OrderError;
import model.xml.OrderItemXML;
import model.xml.OrderXML;

import java.util.List;

public class OrderServiceXML {

    @Inject
    private OrderDAOXml dao;

    public Either<OrderError, List<OrderXML>> getAll() {
        return dao.getAll();
    }

    public Either<OrderError, List<OrderItemXML>> get(int id) {
        return dao.get(id);
    }

}
