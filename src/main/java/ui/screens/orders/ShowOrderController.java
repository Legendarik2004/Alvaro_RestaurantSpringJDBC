package ui.screens.orders;


import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Item;
import model.Order;

import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.security.Timestamp;

import java.util.Objects;
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
    public TableColumn<Integer, Item> quantityItemColumn;
    @FXML
    public TableColumn<String, Item> menuItemColumn;

    private final OrderService orderService;
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
    public TableView<Item> itemsTable;

    @Inject
    public ShowOrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    public void initialize() throws IOException {
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("menuItem"));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        filterComboBox.getItems().addAll("Default", "Date", "Customer");
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

        });
    }


    @FXML
    public void filter() {
        String selectedFilter = filterComboBox.getValue().toString();

        if ("Date".equals(selectedFilter)) {
            dateField.setVisible(true);
            customerOrderComboBox.setVisible(false);
            filterButton.setVisible(true);

            filterButton.setOnAction(event -> {

                if (dateField.getValue() != null) {
                    ordersTable.getItems().clear();
                    orderService.getAll()
                            .map(orders -> orders.stream()
                                    .filter(order -> order.getDate().toLocalDateTime().toLocalDate().equals(dateField.getValue()))
                                    .collect(Collectors.toList()))
                            .peek(ordersTable.getItems()::addAll)
                            .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
                    dateField.setValue(null);
                }

            });
        } else if ("Customer".equals(selectedFilter)) {
            dateField.setVisible(false);
            customerOrderComboBox.setVisible(true);
            filterButton.setVisible(true);

            filterButton.setOnAction(event -> {

                if (customerOrderComboBox.getValue() != null) {
                    ordersTable.getItems().clear();
                    orderService.getAll()
                            .map(orders -> orders.stream()
                                    .filter(order -> {
                                        String customerIdAsString = String.valueOf(order.getCustomerId());
                                        return customerIdAsString.equals(customerOrderComboBox.getValue().toString());
                                    })
                                    .collect(Collectors.toList()))
                            .peek(ordersTable.getItems()::addAll)
                            .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
                }
            });
        }else if ("Default".equals(selectedFilter)||selectedFilter == null){
            setTables();
        }
    }
}
