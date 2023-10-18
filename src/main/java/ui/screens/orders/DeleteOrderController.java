package ui.screens.orders;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Item;
import model.Order;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.security.Timestamp;
import java.time.LocalDateTime;

public class DeleteOrderController extends BaseScreenController {
    private final OrderService orderService;
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
    private Order selectedOrder;


    @Inject
    public DeleteOrderController(OrderService orderService) {
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
                this.selectedOrder = selectedOrder;
            }
        }
    }

    @Override
    public void principalCargado() throws IOException {
        setTables();
    }

    private void setTables() {
        ordersTable.getItems().clear();
        orderService.getAll().peek(orders -> ordersTable.getItems().addAll(orders))
                .peekLeft(orderError -> getPrincipalController().showErrorAlert(orderError.getMessage()));
    }

    public void deleteOrder(ActionEvent actionEvent) {
        if (selectedOrder != null) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.SELECT_AN_ORDER);
            a.show();

        } else {
            int idMayor = orderService.getAll().get().stream().mapToInt(Order::getIdOrder).max().getAsInt();
            idMayor++;
//            orderService.save(new Order(idMayor, LocalDateTime.now(), Integer.parseInt(tableOrderField.getText()), Integer.parseInt(customerOrderFieldCombo.getValue().toString()))).peek(success -> {
//                        if (success == 0) {
//                            setTables();
//                            getPrincipalController().showConfirmationAlert(Constants.ORDER_ADDED_SUCCESSFULLY);
//
//                        }
//                    })
//                    .peekLeft(customerError -> {
//                        getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_ORDER);
//
//                    });
//

        }
    }
}
