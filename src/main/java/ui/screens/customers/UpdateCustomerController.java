package ui.screens.customers;

import common.Constants;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Customer;
import services.CustomerService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateCustomerController extends BaseScreenController {
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
    public TextField idField;
    public TextField fnameField;
    public TextField lnameField;
    public TextField emailField;
    public TextField phoneField;
    public DatePicker dobField;

    @Inject
    public UpdateCustomerController(CustomerService servicesCustomers) {
        this.servicesCustomers = servicesCustomers;
    }

    public void initialize() throws IOException {
        idCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnameCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        dobCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        customersTable.setOnMouseClicked(this::handleTableClick);
        idField.setEditable(false);
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
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

    private void setTable() throws IOException {
        customersTable.getItems().clear();
        servicesCustomers.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
    }

    public void updateCustomer(ActionEvent actionEvent) throws IOException {
//        if (Either.right(servicesCustomers
//                .save(new Customer(Integer.parseInt(idField.getText()), fnameField.getText(), lnameField.getText(), emailField.getText(), phoneField.getText(), dobField.getValue()))) == 1) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(Constants.ERROR);
            a.setHeaderText(null);
            a.setContentText(Constants.CUSTOMER_ADDED_SUCCESSFULLY);
            a.show();
            setTable();
  //      } else {
  //          Alert a = new Alert(Alert.AlertType.ERROR);
  //          a.setTitle(Constants.ERROR);
  //          a.setHeaderText(null);
  //          a.setContentText(Constants.FAILED_TO_UPDATE_THE_CUSTOMER);
  //          a.show();
  //      }
    }
}
