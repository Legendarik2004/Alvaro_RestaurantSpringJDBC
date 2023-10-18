package ui.screens.orders;


import io.vavr.control.Either;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import model.Order;

import model.errors.OrderError;
import model.xml.OrderItemXML;
import services.OrderService;
import services.OrderServiceXML;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.security.Timestamp;

import java.util.List;
import java.util.stream.Collectors;

public class ShowOrderController extends BaseScreenController {
    @FXML
    public TableView<Order> ordersTable;
    @FXML
    public TableColumn<Integer, Order> idOrderColumn;
    @FXML
    public TableColumn<Timestamp, Order> dateOrderColumn;
    @FXML
    public TableColumn<Integer, Order> customerOrderColumn;
    @FXML
    public TableColumn<Integer, Order> tableOrderColumn;
    @FXML
    public TableColumn<Integer, OrderItemXML> quantityItemColumn;
    @FXML
    public TableColumn<String, OrderItemXML> menuItemColumn;

    private final OrderService orderService;
    private final OrderServiceXML orderServiceXml;
    @FXML
    public ComboBox filterComboBox;
    @FXML
    public ComboBox customerOrderComboBox;
    @FXML
    public DatePicker dateField;
    @FXML
    public Button filterButton;
    @FXML
    public Label customernameLabel;
    @FXML
    public TableView<OrderItemXML> itemsTable;

    @Inject
    public ShowOrderController(OrderService orderService, OrderServiceXML orderServiceXml) {
        this.orderService = orderService;
        this.orderServiceXml = orderServiceXml;
    }


    public void initialize() throws IOException {
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("menuItem"));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        filterComboBox.getItems().addAll("Date", "Customer");
        customerOrderComboBox.getItems().addAll(orderService.getAll().get().stream().map(Order::getCustomerId).collect(Collectors.toList()));

        customerOrderComboBox.setVisible(false);
        dateField.setVisible(false);
        filterButton.setVisible(false);

    }

    @Override
    public void principalCargado() throws IOException {
        setTables();
    }

    private void setTables() {

        ordersTable.getItems().clear();
        orderService.getAll().peek(orders -> ordersTable.getItems().addAll(orders))
                .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
        ordersTable.setOnMouseClicked(event -> {
            Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
            itemsTable.getItems().clear();
            itemsTable.getItems().addAll(orderServiceXml.get(selectedOrder.getIdOrder()).get());
        });
    }


    @FXML
    public void filter() {
        String aux = null;
        setTables();
        if (filterComboBox.getValue() == "Date") {
            dateField.setVisible(true);
            customerOrderComboBox.setVisible(false);
            filterButton.setVisible(true);
            aux = "Date";
        }
        if (filterComboBox.getValue() == "Customer") {
            dateField.setVisible(false);
            customerOrderComboBox.setVisible(true);
            filterButton.setVisible(true);
            aux = "Customer";
        }
        String selectedFilter = aux;
        filterButton.setOnAction(event -> {
            ordersTable.getItems().clear();

            if ("Date".equals(selectedFilter)) {
                if (dateField.getValue() != null) {
                    orderService.getAll()
                            .map(orders -> orders.stream()
                                    .filter(order -> order.getDate().toLocalDate().equals(dateField.getValue()))
                                    .collect(Collectors.toList()))
                            .peek(ordersTable.getItems()::addAll)
                            .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
                }

            } else if ("Customer".equals(selectedFilter)) {

                if (customerOrderComboBox.getValue() != null) {
                    orderService.getAll()
                            .map(orders -> orders.stream()
                                    .filter(order -> {
                                        String customerIdAsString = String.valueOf(order.getCustomerId());
                                        return customerIdAsString.equals(customerOrderComboBox.getValue());
                                    })
                                    .collect(Collectors.toList()))
                            .peek(ordersTable.getItems()::addAll)
                            .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
                }

            }
        });
    }
}
