package common.constants;

public class ConstantsSQLTableAttributes {

    private ConstantsSQLTableAttributes() {
        //NOP
    }

    //Login
    public static final String CREDENTIAL_ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";

    //Customers
    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    //Orders
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_DATE = "order_date";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String TABLE_ID = "table_id";
    //Items_MenuItems
    public static final String ORDER_ITEM_ID = "order_item_id";
    public static final String QUANTITY = "quantity";
    public static final String MENU_ITEM_ID = "menu_item_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    //Tables
    public static final String TABLE_NUMBER_ID = "table_number_id";
    public static final String NUMBER_OF_SEATS = "number_of_seats";
}