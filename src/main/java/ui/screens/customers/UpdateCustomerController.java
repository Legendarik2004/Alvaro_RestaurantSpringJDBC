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
import javafx.scene.input.MouseEvent;
import model.Credentials;
import model.Customer;
import services.CustomerService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateCustomerController extends BaseScreenController {
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
    public TextField idField;
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

    private Customer selectedCustomer;

    @Inject
    public UpdateCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void initialize() {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.ID));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.FIRST_NAME));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.LAST_NAME));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.EMAIL));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.PHONE));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>(ConstantsObjectAttributes.DOB));
        customersTable.setOnMouseClicked(this::handleTableClick);
        idField.setDisable(true);
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Customer newSelectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (newSelectedCustomer != null) {
                this.selectedCustomer = newSelectedCustomer;
                idField.setText(String.valueOf(selectedCustomer.getId()));
                fnameField.setText(selectedCustomer.getFirstName());
                lnameField.setText(selectedCustomer.getLastName());
                emailField.setText(selectedCustomer.getEmail());
                phoneField.setText(selectedCustomer.getPhone());
                dobField.setValue(selectedCustomer.getDob());
            }
        }
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

    public void updateCustomer() {
        if (fnameField.getText().isEmpty() || lnameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || dobField.getValue() == null) {
            getPrincipalController().showErrorAlert(ConstantsErrorMessages.EMPTY_FIELD);
        } else {
            customerService.update(new Customer(selectedCustomer.getId(), fnameField.getText(), lnameField.getText(), emailField.getText(), phoneField.getText(), dobField.getValue(), new Credentials(0,null,null))).peek(success -> {
                        if (success == 0) {
                            setTable();
                            getPrincipalController().showConfirmationAlert(ConstantsSuccessMessage.CUSTOMER_UPDATED_SUCCESSFULLY);
                        }
                    })
                    .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
        }
    }
}
