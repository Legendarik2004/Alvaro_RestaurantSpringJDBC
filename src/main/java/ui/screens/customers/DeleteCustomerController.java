package ui.screens.customers;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    public void initialize() throws IOException {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));

        customersTable.setOnMouseClicked(this::handleTableClick);
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dateOrderColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerOrderColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tableOrderColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                this.selectedCustomer = selectedCustomer;
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
            ordersTable.getItems().addAll(orderService.get(selectedCustomer.getId()).get());
        }
    }

    //TODO arreglar metodo
    public void deleteCustomer(ActionEvent actionEvent) {
        if (selectedCustomer == null) {
            getPrincipalController().showErrorAlert(Constants.SELECT_CUSTOMER_FIRST);
        } else {
            if (!ordersTable.getItems().isEmpty()) {
                // Mostrar un diálogo de confirmación antes de eliminar si hay orders en la tabla
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setContentText("¿Seguro que quieres eliminar?");

                a.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        customerService.delete(selectedCustomer).peek(success -> {
                                    if (success == 0) {
                                        setCustomerTable();
                                        getPrincipalController().showConfirmationAlert(Constants.CUSTOMER_DELETED_SUCCESSFULLY);
                                    }
                                })
                                .peekLeft(customerError -> getPrincipalController().showErrorAlert(Constants.ERROR_DELETING_CUSTOMER));
                    }
                });
            } else {
                customerService.delete(selectedCustomer).peek(success -> {
                            if (success == 0) {
                                setCustomerTable();
                                getPrincipalController().showConfirmationAlert(Constants.CUSTOMER_DELETED_SUCCESSFULLY);
                            }
                        })
                        .peekLeft(customerError -> getPrincipalController().showErrorAlert(Constants.ERROR_DELETING_CUSTOMER));
            }
        }
    }
}
