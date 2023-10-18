package ui.screens.orders;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Item;
import model.Order;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.security.Timestamp;
import java.time.LocalDate;

public class UpdateOrderController  extends BaseScreenController {
    public TableView<Order> ordersTable;
    public TableColumn<Integer, Order> idOrderColumn;
    public TableColumn<Timestamp, Order> dateOrderColumn;
    public TableColumn<Integer, Order> customerOrderColumn;
    public TableColumn<Integer, Order> tableOrderColumn;
    public TableView<Item> itemsTable;
    public TableColumn<Integer, Item> idItemColumn;
    public TableColumn<String, Item> nameItemColumn;
    public TableColumn<Float, Item> priceItemColumn;
    public TableColumn<String, Item> descriptionItemColumn;
    private final OrderService orderService;
    public DatePicker dateField;
    public TextField tableOrderField;
    public TextField customerOrderField;
    public TextField menuItemField;
    public TextField quantityItemField;
    public ComboBox itemsComboBox;

    @Inject
    public UpdateOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void initialize() throws IOException {
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        idItemColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameItemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceItemColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionItemColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        ordersTable.setOnMouseClicked(this::handleTableClick);
    }
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                tableOrderField.setText(String.valueOf(selectedOrder.getTableId()));
                customerOrderField.setText(String.valueOf(selectedOrder.getCustomerId()));
                dateField.setValue(LocalDate.from(selectedOrder.getDate()));
            }
        }
    }
    @Override
    public void principalCargado() throws IOException {
        setTables();
    }

    private void setTables(){
        ordersTable.getItems().clear();
        orderService.getAll().peek(orders -> ordersTable.getItems().addAll(orders))
                .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
        itemsTable.getItems().clear();
    }

    public void updateOrder(ActionEvent actionEvent) {
        if (dateField.getValue() == null || tableOrderField.getText().isEmpty() || customerOrderField.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.ERROR);
            a.show();
        } else {
            //temporal
            //if (orderService.save(new Order())) == 1) {
            if (true) {
                setTables();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(Constants.ORDER_ADDED);
                a.setContentText(Constants.ORDER_UPDATED_SUCCESSFULLY);
                a.setHeaderText(null);
                a.show();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle(Constants.ERROR);
                a.setContentText(Constants.ERROR_UPDATING_CLIENT);
                a.setHeaderText(null);
                a.show();
            }
        }
    }

    public void addItem(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(Constants.ITEM_ADDED);
        a.setContentText(Constants.ITEM_ADDED_SUCCESSFULLY);
        a.setHeaderText(null);
        a.show();
    }

    public void removeItem(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(Constants.ITEM_REMOVED);
        a.setContentText(Constants.ITEM_REMOVED_SUCCESSFULLY);
        a.setHeaderText(null);
        a.show();
    }
}
