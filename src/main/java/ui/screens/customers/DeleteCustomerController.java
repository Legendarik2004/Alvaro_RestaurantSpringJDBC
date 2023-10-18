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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        idOrderColumn.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
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
        setTables();
    }

    @Override
    public void principalCargado() throws IOException {
        setTables();
    }

    private void setTables() {
        customersTable.getItems().clear();
        customerService.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));


        ordersTable.getItems().clear();
        if (selectedCustomer != null)
            ordersTable.getItems().addAll(orderService.get(selectedCustomer.getId()).get());

    }

    public void deleteCustomer(ActionEvent actionEvent) {
        if (selectedCustomer == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.THERE_IS_AN_EMPTY_FIELD);
            a.show();
        } else {
            if (!ordersTable.getItems().isEmpty()) {
                // Mostrar un diálogo de confirmación antes de eliminar si hay órdenes en la tabla
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setContentText("¿Seguro que quieres eliminar?");


                a.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        customerService.delete(selectedCustomer).peek(success -> {
                                    if (success == 0) {
                                        setTables();
                                        getPrincipalController().showConfirmationAlert(Constants.CUSTOMER_DELETED);
                                    }
                                })
                                .peekLeft(customerError -> {
                                    getPrincipalController().showErrorAlert(Constants.FAILED_TO_DELETE_THE_CUSTOMER);
                                });
                    }
                });
            } else {
                customerService.delete(selectedCustomer).peek(success -> {
                            if (success == 0) {
                                setTables();
                                getPrincipalController().showConfirmationAlert(Constants.CUSTOMER_DELETED);
                            }
                        })
                        .peekLeft(customerError -> {
                            getPrincipalController().showErrorAlert(Constants.FAILED_TO_DELETE_THE_CUSTOMER);
                        });
            }
        }
    }
}
