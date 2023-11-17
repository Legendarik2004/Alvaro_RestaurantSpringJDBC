package ui.screens.customers;

import common.constants.ConstantsObjectAttributes;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import services.CustomerService;
import ui.screens.common.BaseScreenController;

import java.io.IOException;
import java.time.LocalDate;

public class ShowCustomersController extends BaseScreenController {
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

    @Inject
    public ShowCustomersController(CustomerService customerService) {
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
        setTables();
    }

    private void setTables() {
        customersTable.getItems().clear();
        customerService.getAll().peek(customers -> customersTable.getItems().addAll(customers))
                .peekLeft(customerError -> getPrincipalController().showErrorAlert(customerError.getMessage()));
    }

}
