package ui.screens.customers;

import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsObjectAttributes;
import common.constants.ConstantsSuccessMessage;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Credentials;
import model.Customer;
import services.CustomerService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.time.LocalDate;

public class AddCustomerController extends BaseScreenController {
    private final CustomerService customerService;
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
    public TextField userNameField;
    @FXML
    public TextField passwordField;
    @FXML
    public TextField fnameField;
    @FXML
    public TextField lnameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField phoneField;
    @FXML
    public DatePicker dobField;

    @Inject
    public AddCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void initialize() {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.ID));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.FIRST_NAME));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.LAST_NAME));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.EMAIL));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.PHONE));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.DOB));
    }

    @Override
    public void principalCargado() throws IOException {
        setTable();
    }

    private void setTable() {
        customersTable.getItems().clear();
        customerService.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
    }

    public void addCustomer() {
        if (fnameField.getText().isEmpty() || lnameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || dobField.getValue() == null || userNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.EMPTY_FIELD);
        } else {

            customerService.save(new Customer(0, fnameField.getText(), lnameField.getText(), emailField.getText(), phoneField.getText(), dobField.getValue(),new Credentials(0,userNameField.getText(),passwordField.getText()))).peek(success -> {
                        if (success == 0) {
                            setTable();
                            getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.CUSTOMER_ADDED_SUCCESSFULLY);
                        }
                    })
                    .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
        }
    }
}