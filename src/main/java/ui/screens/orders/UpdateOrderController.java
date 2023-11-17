package ui.screens.orders;

import common.constants.Constants;
import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsObjectAttributes;
import common.constants.ConstantsSuccessMessage;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.MenuItem;
import model.*;
import services.OrderItemService;
import services.OrderService;
import services.TablesService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class UpdateOrderController extends BaseScreenController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final TablesService tablesService;
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
    @FXML
    public TableColumn<Integer, OrderItem> quantityItemColumn;
    @FXML
    public TextField orderIdField;
    @FXML
    public TextField customerField;
    @FXML
    public DatePicker dateField;
    @FXML
    public ComboBox<Integer> tableComboBox;
    @FXML
    public TextField quantityItemField;
    @FXML
    public ComboBox<String> itemsComboBox;
    private Order selectedOrder;
    private OrderItem selectedOrderItem;
    private Credentials actualCredentials;

    @Inject
    public UpdateOrderController(OrderService orderService, OrderItemService orderItemService, TablesService tablesService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.tablesService = tablesService;
    }

    public void initialize() {
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.ORDER_ID));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.DATE));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.CUSTOMER_ID));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.TABLE_ID));

        nameItemColumn.setCellValueFactory((cellData -> new SimpleStringProperty(cellData.getValue().getMenuItem().getName())));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.QUANTITY));

        ordersTable.setOnMouseClicked(this::handleOrderTableClick);
        itemsTable.setOnMouseClicked(this::handleItemTableClick);

        itemsComboBox.getItems().addAll(orderItemService.getAllMenuItems().get().stream().map(MenuItem::getName).toList());
        tableComboBox.getItems().addAll(tablesService.getAll().get().stream().map(Table::getTableId).toList());

        orderIdField.setDisable(true);
        customerField.setDisable(true);
        dateField.setDisable(true);
    }

    private void handleOrderTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Order newSelectedOrder = ordersTable.getSelectionModel().getSelectedItem();
            this.selectedOrder = newSelectedOrder;

            if (newSelectedOrder != null) {
                tableComboBox.setValue(newSelectedOrder.getTableId());
                orderIdField.setText(String.valueOf(newSelectedOrder.getOrderId()));
                customerField.setText(String.valueOf(newSelectedOrder.getCustomerId()));
                dateField.setValue(newSelectedOrder.getDate().toLocalDateTime().toLocalDate());
            }
        }
        setOrderItemTable();
    }

    private void handleItemTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            OrderItem newSelectedOrderItem = itemsTable.getSelectionModel().getSelectedItem();
            if (newSelectedOrderItem != null) {
                this.selectedOrderItem = newSelectedOrderItem;

            }
        }

    }

    @Override
    public void principalCargado() throws IOException {
        actualCredentials = getPrincipalController().getActualCredentials();
        setTables();
    }

    private void setTables() {
        ordersTable.getItems().clear();
        if (actualCredentials.getCustomerId() < 0) {
            orderService.getAll().peek(orders -> ordersTable.getItems().addAll(orders))
                    .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));

        } else {
            orderService.getOrderOfCustomer(actualCredentials.getCustomerId()).peek(orders -> ordersTable.getItems().addAll(orders))
                    .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
        }
        itemsTable.getItems().clear();
        itemsComboBox.setDisable(true);
    }

    private void setOrderItemTable() {
        if (selectedOrder != null) {
            orderItemService.get(selectedOrder.getOrderId())
                    .peek(orderItems -> itemsTable.getItems().setAll(orderItems))
                    .peekLeft(noItems -> itemsTable.getItems().clear());
            itemsComboBox.setDisable(false);
        } else {
            itemsTable.getItems().clear();
        }
    }

    public void updateOrder() {
        if (tableComboBox.getValue() == null) {
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.EMPTY_FIELD);
        } else {
            List<OrderItem> orderItems = itemsTable.getItems().stream().toList();
            orderService.update(new Order(selectedOrder.getOrderId(), Timestamp.from(Instant.now()), 0, tableComboBox.getValue(), orderItems)).peek(success -> {
                        if (success == 0) {

                            orderItemService.get(selectedOrder.getOrderId())
                                    .peek(orderItemsList -> orderItemsList.forEach(orderItemService::delete))
                                    .peekLeft(noItems -> itemsTable.getItems().clear());

                            for (OrderItem orderItem : itemsTable.getItems().stream().toList()) {
                                orderItem.setOrderId(selectedOrder.getOrderId());
                                orderItemService.save(orderItem);
                            }
                            setTables();
                            getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.ORDER_UPDATED_SUCCESSFULLY);

                        }
                    })
                    .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
        }
    }

    @FXML
    public void addItem() {
        if (selectedOrder == null) {
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.SELECT_ORDER_FIRST);
        } else {
            if (quantityItemField.getText().isEmpty() || itemsComboBox.getValue().equals(Constants.MENU_ITEMS)) {
                getPrincipalController().showErrorAlert(ConstantsErrorMessages.EMPTY_FIELD);
            } else {
                MenuItem add = orderItemService.getAllMenuItems().get().stream()
                        .filter(menuItem -> menuItem.getName().equals(itemsComboBox.getValue()))
                        .findFirst().orElse(null);
                if (add != null) {
                    itemsTable.getItems().add(new OrderItem(0, selectedOrder.getOrderId(), Integer.parseInt(quantityItemField.getText()), add));
                    quantityItemField.clear();
                    getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.ITEM_ADDED_SUCCESSFULLY);
                } else {
                    getPrincipalController().showErrorAlert(ConstantsErrorMessages.ERROR_ADDING_ITEM);
                }
            }
        }
    }

    @FXML
    public void removeItem() {
        if (itemsTable.getItems().remove(selectedOrderItem)) {
            getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.ITEM_REMOVED_SUCCESSFULLY);
            selectedOrderItem = null;
        } else {
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.ERROR_DELETING_ITEM);
        }
    }
}
