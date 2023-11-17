package common.constants;


public class ConstantsErrorMessages {

    private ConstantsErrorMessages() {
        //NOP
    }


    public static final int NUM_ERROR = 1;


    //Login
    public static final String INCORRECT_USER_OR_PASSWORD = "Incorrect user or password";
    public static final String ERROR_ADDING_CREDENTIALS = "Error adding credentials";
    public static final String USER_EXISTS = "Customer already exists";
    public static final String ERROR_ON_LOGIN = "An error occurred during login";
    public static final String ERROR_DELETING_CREDENTIALS = "Error deleting credentials";


    //Customer Errors
    public static final String ERROR_ADDING_CUSTOMER = "Error adding customer";
    public static final String ERROR_UPDATING_CUSTOMER = "Error updating customer";
    public static final String ERROR_DELETING_CUSTOMER = "Error deleting customer";
    public static final String ERROR_DELETING_CUSTOMER_WITH_ORDERS = "Error deleting customer with orders";


    //Order Errors
    public static final String ERROR_ADDING_ORDER = "Error adding order";
    public static final String ERROR_UPDATING_ORDER = "Error updating order";
    public static final String ERROR_DELETING_ORDER = "Error deleting order";


    //Item Errors
    public static final String ERROR_ADDING_ITEM = "Error adding item";
    public static final String ERROR_DELETING_ITEM = "Error deleting item";


    //Nothing selected
    public static final String SELECT_CUSTOMER_FIRST = "Select a customer first";
    public static final String SELECT_ORDER_FIRST = "Select an order first";


    //Empty List Errors
    public static final String EMPTY_FIELD = "There is an empty field";
    public static final String NO_CUSTOMER_FOUND = "No customer found";
    public static final String NO_CUSTOMERS_FOUND = "No customers found";
    public static final String NO_ORDER_FOUND = "No order found";
    public static final String NO_ORDERS_FOUND = "No orders found";
    public static final String NO_ORDER_ITEMS_FOUND = "No order items found";
    public static final String NO_MENU_ITEMS_FOUND = "No menu items found";


    //XML
    public static final String ERROR_SAVING_ORDERS_XML = "Error saving orders";
    public static final String THE_CUSTOMER_HAS_ORDERS = "The customer has orders";
    public static final String ERROR_CONNECTING_DATABASE = "Error connecting database";
}