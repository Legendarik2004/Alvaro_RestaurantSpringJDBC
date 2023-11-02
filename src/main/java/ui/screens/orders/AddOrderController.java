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
import services.CustomerService;
import services.OrderItemsService;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class AddOrderController extends BaseScreenController {

    private final OrderService orderService;
    private final OrderItemsService orderItemsService;
    private final CustomerService customerService;
    @FXML
    public TableView<OrderItem> itemsTable;
    @FXML
    public TableColumn<Integer, OrderItem> idItemColumn;
    @FXML
    public TableColumn<OrderItem, String> menuItemColumn;
    @FXML
    public TableColumn<Integer, OrderItem> quantityItemColumn;
    @FXML
    public TextField tableOrderField;
    @FXML
    public ComboBox<String> itemsComboBox;
    @FXML
    public TextField quantityItemField;
    private OrderItem selectedOrderItem;
    private User actualUser;

    @Inject
    public AddOrderController(OrderService orderService, OrderItemsService orderItemsService, CustomerService customerService) {
        this.orderService = orderService;
        this.orderItemsService = orderItemsService;
        this.customerService = customerService;
    }

    public void initialize() throws IOException {
        idItemColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemId"));
        menuItemColumn.setCellValueFactory((cellData -> new SimpleStringProperty(cellData.getValue().getMenuItem().getName())));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemsTable.setOnMouseClicked(this::handleTableClick);

        itemsComboBox.getItems().addAll(orderItemsService.getAllMenuItems().get().stream().map(MenuItem::getName).toList());
    }

    private void handleTableClick(MouseEvent event) {
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
        setTable();
    }

    private void setTable() {
        itemsTable.getItems().clear();
    }

    public void addOrder(ActionEvent actionEvent) {

        if (tableOrderField.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.EMPTY_FIELD);
            a.show();
        } else {

            orderService.save(new Order(0, Timestamp.from(Instant.now()), actualUser.getId(), Integer.parseInt(tableOrderField.getText()))).peek(success -> {

                        if (success == 0) {
                            int id = orderService.getAddedOrderId();
                            List<OrderItem> orderItemList = itemsTable.getItems().stream().toList();
                            for (OrderItem orderItem : orderItemList) {
                                orderItem.setOrderId(id);
                                orderItemsService.save(orderItem);
                            }
                            setTable();
                            getPrincipalController().showConfirmationAlert(Constants.ORDER_ADDED_SUCCESSFULLY);

                        }
                    })
                    .peekLeft(customerError -> {
                        getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_ORDER);

                    });
        }
    }

    @FXML
    public void addItem(ActionEvent actionEvent) {
        if (quantityItemField.getText().isEmpty() || itemsComboBox.getValue().equals("Menu items")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.EMPTY_FIELD);
            a.show();
        } else {
            MenuItem add = orderItemsService.getAllMenuItems().get().stream()
                    .filter(menuItem -> menuItem.getName().equals(itemsComboBox.getValue().toString()))
                    .findFirst().orElse(null);
            if (add != null) {
                itemsTable.getItems().add(new OrderItem(0, 0, add.getMenuItemId(), Integer.parseInt(quantityItemField.getText()), add));
                quantityItemField.clear();
                getPrincipalController().showConfirmationAlert(Constants.ITEM_ADDED_SUCCESSFULLY);
            } else {
                getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_ITEM);
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
