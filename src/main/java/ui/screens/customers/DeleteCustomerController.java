package ui.screens.customers;

import common.constants.Constants;
import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsObjectAttributes;
import common.constants.ConstantsSuccessMessage;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Customer;
import model.Order;
import services.CustomerService;
import services.OrderService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.security.Timestamp;
import java.time.LocalDate;


public class DeleteCustomerController extends BaseScreenController {
    private final CustomerService customerService;
    private final OrderService orderService;
    @FXML
    public TableView<Customer> customersTable;
    @FXML
    public TableColumn<Integer, Customer> idCustomerColumn;
    @FXML
    public TableColumn<String, Customer> firstnameCustomerColumn;
    @FXML
    public TableColumn<String, Customer> lastnameCustomerColumn;
    @FXML
    public TableColumn<String, Customer> emailCustomerColumn;
    @FXML
    public TableColumn<String, Customer> phoneCustomerColumn;
    @FXML
    public TableColumn<LocalDate, Customer> dobCustomerColumn;
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
    private Customer selectedCustomer;

    @Inject
    public DeleteCustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    public void initialize() {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.ID));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.FIRST_NAME));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.LAST_NAME));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.EMAIL));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.PHONE));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.DOB));

        customersTable.setOnMouseClicked(this::handleTableClick);

        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.ORDER_ID));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.DATE));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.CUSTOMER_ID));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.TABLE_ID));
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Customer newSelectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (newSelectedCustomer != null) {
                this.selectedCustomer = newSelectedCustomer;
            }
        }
        setOrderTable();
    }

    @Override
    public void principalCargado() throws IOException {
        setCustomerTable();
    }

    private void setCustomerTable() {
        customersTable.getItems().clear();
        customerService.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
    }

    private void setOrderTable() {
        ordersTable.getItems().clear();
        if (selectedCustomer != null) {
            ordersTable.getItems().addAll(orderService.getOrderOfCustomer(selectedCustomer.getId()).get());
        }
    }

    public void deleteCustomer() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText(Constants.CONFIRM_DELETE);
        a.getDialogPane().lookupButton(ButtonType.OK).setId("btn-ok");
        if (selectedCustomer != null) {
            customerService.delete(selectedCustomer, false).peek(result -> {
                if (result != 1) {
                    a.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            customerService.delete(selectedCustomer, true);
                        }
                    });
                } else {
                    getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.CUSTOMER_DELETED_SUCCESSFULLY);
                }
                setCustomerTable();
                setOrderTable();
            }).peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
        } else
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.SELECT_CUSTOMER_FIRST);
    }
}
