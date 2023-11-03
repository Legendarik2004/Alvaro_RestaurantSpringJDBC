package common;

public class Constants {

    private Constants() {
    }

    public static final int NUM_ERROR = 1;

    public static final String ERROR = "Error";


    //Login
    public static final String INCORRECT_USER_OR_PASSWORD = "Incorrect user or password";
    public static final String ERROR_ADDING_CREDENTIALS = "Error adding credentials";
    public static final String ERROR_ONLOGIN = "An error occurred during login";


    //Empty List Errors
    public static final String EMPTY_FIELD = "There is an empty field";


    //Customer Success
    public static final String CUSTOMER_ADDED_SUCCESSFULLY = "Customer added successfully.";
    public static final String CUSTOMER_UPDATED_SUCCESSFULLY = "Customer updated successfully";
    public static final String CUSTOMER_DELETED_SUCCESSFULLY = "Customer deleted successfully";

    //Customer Errors
    public static final String ERROR_ADDING_CUSTOMER = "Error adding customer";
    public static final String ERROR_UPDATING_CUSTOMER = "Error updating customer";
    public static final String ERROR_DELETING_CUSTOMER = "Error deleting customer";


    //Order Success
    public static final String ORDER_ADDED_SUCCESSFULLY = "Order added successfully";
    public static final String ORDER_UPDATED_SUCCESSFULLY = "Order updated successfully";
    public static final String ORDER_DELETED_SUCCESSFULLY = "Order deleted successfully";

    //Order Errors
    public static final String ERROR_ADDING_ORDER = "Error adding order";
    public static final String ERROR_UPDATING_ORDER = "Error updating order";
    public static final String ERROR_DELETING_ORDER = "Error deleting order";


    //Item Success
    public static final String ITEM_ADDED_SUCCESSFULLY = "Item added successfully";
    public static final String ITEM_REMOVED_SUCCESSFULLY = "Item removed successfully";

    //Item Errors
    public static final String ERROR_ADDING_ITEM = "Error adding item";
    public static final String ERROR_DELETING_ITEM = "Error deleting item";


    //Nothing selected
    public static final String SELECT_CUSTOMER_FIRST = "To delete a customer select it first";
    public static final String SELECT_ORDER_FIRST = "To delete an order select it first";




    //Variables
    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String PASSWORD = "password";

}