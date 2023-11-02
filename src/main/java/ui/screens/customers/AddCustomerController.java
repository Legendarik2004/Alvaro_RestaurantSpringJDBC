package ui.screens.customers;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import services.CustomerService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.time.LocalDate;

public class AddCustomerController extends BaseScreenController {
    private final CustomerService servicesCustomers;
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

    public TextField fnameField;
    public TextField lnameField;
    public TextField emailField;
    public TextField phoneField;
    public DatePicker dobField;

    @Inject
    public AddCustomerController(CustomerService servicesCustomers) {
        this.servicesCustomers = servicesCustomers;
    }

    public void initialize() throws IOException {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
    }

    @Override
    public void principalCargado() throws IOException {
        setTable();
    }

    private void setTable() throws IOException {
        customersTable.getItems().clear();
        servicesCustomers.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
    }

    public void addCustomer(ActionEvent actionEvent) {
        if (fnameField.getText().isEmpty() || lnameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || dobField.getValue() == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(Constants.EMPTY_FIELD);
            a.show();
        } else {
            int idMayor = servicesCustomers.getAll().get().stream().mapToInt(Customer::getId).max().getAsInt();
            idMayor++;
            servicesCustomers.save(new Customer(idMayor, fnameField.getText(), lnameField.getText(), emailField.getText(), phoneField.getText(), dobField.getValue())).peek(success -> {
                        if (success == 0) {
                            try {
                                setTable();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            getPrincipalController().showConfirmationAlert(Constants.CUSTOMER_ADDED_SUCCESSFULLY);

                        }
                    })
                    .peekLeft(customerError -> {
                        getPrincipalController().showErrorAlert(Constants.ERROR_ADDING_CUSTOMER);

                    });
        }
    }
}