package ui.screens.orders;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Item;
import model.Order;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class AddOrderController extends BaseScreenController {

    private final OrderService orderService;
    @FXML
    public TableView<Item> itemsTable;
    @FXML
    public TableColumn<Integer, Item> idItemColumn;
    @FXML
    public TableColumn<String, Item> menuItemColumn;
    @FXML
    public TableColumn<Integer, Item> quantityItemColumn;
    @FXML
    public TextField tableOrderField;
    public ComboBox customerOrderFieldCombo;
    public TextField menuItemField;
    @FXML
    public TextField quantityItemField;
    @FXML
    public ComboBox itemsComboBox;

    @Inject
    public AddOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void initialize() throws IOException {
        idItemColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("menuItem"));
        quantityItemColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        customerOrderFieldCombo.getItems().addAll(1, 2, 3);
    }

    @Override
    public void principalCargado() throws IOException {
        setTable();
    }

    private void setTable() {
        itemsTable.getItems().clear();
    }

    public void addOrder(ActionEvent actionEvent) {

        if (tableOrderField.getText().isEmpty() || customerOrderFieldCombo.getValue() == "Customer") {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.THERE_IS_AN_EMPTY_FIELD);
            a.show();
        } else {
            int idMayor = orderService.getAll().get().stream().mapToInt(Order::getIdOrder).max().getAsInt();
            idMayor++;
            orderService.save(new Order(idMayor, Timestamp.from(Instant.now()), Integer.parseInt(tableOrderField.getText()), Integer.parseInt(customerOrderFieldCombo.getValue().toString()))).peek(success -> {
                        if (success == 0) {
                            setTable();
                            getPrincipalController().showConfirmationAlert(Constants.ORDER_ADDED_SUCCESSFULLY);

                        }
                    })
                    .peekLeft(customerError -> {
                        getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_ORDER);

                    });
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
