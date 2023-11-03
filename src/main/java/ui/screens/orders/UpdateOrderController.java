package ui.screens.orders;

import common.Constants;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.User;
import services.OrderItemsService;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class UpdateOrderController extends BaseScreenController {

    private final OrderService orderService;
    private final OrderItemsService orderItemsService;
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
    public TableView<OrderItem> itemsTable;
    @FXML
    public TableColumn<OrderItem, String> nameItemColumn;
    public TableColumn<Integer, OrderItem> quantityItemColumn;
    @FXML
    public DatePicker dateField;
    @FXML
    public TextField tableOrderField;
    @FXML
    public TextField customerOrderField;
    @FXML
    public TextField quantityItemField;
    @FXML
    public ComboBox<String> itemsComboBox;
    public Order selectedOrder;
    private OrderItem selectedOrderItem;
    private User actualUser;

    @Inject
    public UpdateOrderController(OrderService orderService, OrderItemsService orderItemsService) {
        this.orderService = orderService;
        this.orderItemsService = orderItemsService;
    }

    public void initialize() throws IOException {
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));

        nameItemColumn.setCellValueFactory((cellData -> new SimpleStringProperty(cellData.getValue().getMenuItem().getName())));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ordersTable.setOnMouseClicked(this::handleOrderTableClick);
        itemsTable.setOnMouseClicked(this::handleItemTableClick);
        itemsComboBox.getItems().addAll(orderItemsService.getAllMenuItems().get().stream().map(MenuItem::getName).toList());
        customerOrderField.setDisable(true);
        dateField.setDisable(true);
    }

    private void handleOrderTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
            this.selectedOrder = selectedOrder;

            if (selectedOrder != null) {
                tableOrderField.setText(String.valueOf(selectedOrder.getTableId()));
                customerOrderField.setText(String.valueOf(selectedOrder.getCustomerId()));
                dateField.setValue(selectedOrder.getDate().toLocalDateTime().toLocalDate());
            }
        }
        setOrderItemTable();
    }

    private void handleItemTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            OrderItem selectedOrderItem = itemsTable.getSelectionModel().getSelectedItem();
            if (selectedOrderItem != null) {
                this.selectedOrderItem = selectedOrderItem;

            }
        }

    }

    @Override
    public void principalCargado() throws IOException {
        actualUser = getPrincipalController().actualUser;
        setTables();
    }

    private void setTables() {
        ordersTable.getItems().clear();
        if (actualUser.getId() < 0) {
            orderService.getAll().peek(orders -> ordersTable.getItems().addAll(orders))
                    .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));

        } else {
            orderService.get(actualUser.getId()).peek(orders -> ordersTable.getItems().addAll(orders))
                    .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
        }
        itemsTable.getItems().clear();
        itemsComboBox.setDisable(true);
    }

    private void setOrderItemTable() {
        if (selectedOrder != null) {
            orderItemsService.getAllOrderItems(selectedOrder.getOrderId())
                    .peek(orderItems -> itemsTable.getItems().setAll(orderItems))
                    .peekLeft(noItems -> itemsTable.getItems().clear());
            itemsComboBox.setDisable(false);
        } else {
            itemsTable.getItems().clear();
        }
    }
//TODO añadir orderItems en database
    public void updateOrder(ActionEvent actionEvent) {
        if (tableOrderField.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.EMPTY_FIELD);
            a.show();
        } else {
            orderService.update(new Order(selectedOrder.getOrderId(), Timestamp.from(Instant.now()), Integer.parseInt(customerOrderField.getText()), Integer.parseInt(tableOrderField.getText()))).peek(success -> {
                        if (success == 0) {
                            setTables();
                            getPrincipalController().showConfirmationAlert(Constants.ORDER_UPDATED_SUCCESSFULLY);

                        }
                    })
                    .peekLeft(customerError -> getPrincipalController().showErrorAlert(Constants.ERROR_UPDATING_ORDER));
        }
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {
        if (selectedOrder == null) {
            getPrincipalController().showErrorAlert(Constants.SELECT_ORDER_FIRST);
        } else {
            if (quantityItemField.getText().isEmpty() || itemsComboBox.getValue().equals("Menu items")) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(Constants.EMPTY_FIELD);
                a.show();
            } else {
                MenuItem add = orderItemsService.getAllMenuItems().get().stream()
                        .filter(menuItem -> menuItem.getName().equals(itemsComboBox.getValue()))
                        .findFirst().orElse(null);
                if (add != null) {
                    itemsTable.getItems().add(new OrderItem(0, selectedOrder.getOrderId(), add.getMenuItemId(), Integer.parseInt(quantityItemField.getText()), add));
                    quantityItemField.clear();
                    getPrincipalController().showConfirmationAlert(Constants.ITEM_ADDED_SUCCESSFULLY);
                } else {
                    getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_ITEM);
                }
            }
        }
    }

    @FXML
    public void removeItem(ActionEvent actionEvent) {
        if (itemsTable.getItems().remove(selectedOrderItem)) {
            getPrincipalController().showConfirmationAlert(Constants.ITEM_REMOVED_SUCCESSFULLY);
            selectedOrderItem = null;
        } else {
            getPrincipalController().showErrorAlert(Constants.ERROR_DELETING_ITEM);
        }
    }
}
