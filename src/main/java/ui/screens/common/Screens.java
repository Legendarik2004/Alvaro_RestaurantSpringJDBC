package ui.screens.common;

public enum Screens {

    PRINCIPAL ("/fxml/principal.fxml"),
    CUSTOMERS ("/fxml/customers/showCustomers.fxml"),
    DELETECUSTOMERS ("/fxml/customers/deleteCustomer.fxml"),
    ADDCUSTOMERS ("/fxml/customers/addCustomer.fxml"),
    UPDATECUSTOMERS ("/fxml/customers/updateCustomer.fxml"),
    ORDERS ("/fxml/orders/showOrders.fxml"),
    ADDORDERS ("/fxml/orders/addOrder.fxml"),
    UPDATEORDERS ("/fxml/orders/updateOrder.fxml"),
    DELETEORDERS ("/fxml/orders/deleteOrder.fxml"),
    LOGIN ("/fxml/login.fxml"),
    WELCOME ("/fxml/welcome.fxml"),
    PANTALLANUEVA (ConstantsScreens.FXML_PANTALLA_NUEVA_FXML);

    private String ruta;
    Screens(String ruta) {
        this.ruta=ruta;
    }
    public String getRuta(){return ruta;}


}
