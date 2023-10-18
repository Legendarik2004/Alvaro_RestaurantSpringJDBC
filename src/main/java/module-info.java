module javafx {


    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires lombok;
    requires org.apache.logging.log4j;


    requires jakarta.inject;
    requires jakarta.cdi;
    requires io.vavr;

    requires MaterialFX;

    opens model.xml to jakarta.xml.bind;


    requires jakarta.xml.bind;
    exports ui.main to javafx.graphics;
    exports ui.screens.principal;
    exports ui.screens.login;
    exports ui.screens.common;
    exports ui.screens.customers;
    exports ui.screens.orders;
    exports model;
    exports dao.impl;
    exports ui.screens.welcome;
    exports model.errors;
    exports model.xml;

    opens ui.screens.login;
    opens ui.screens.principal;
    opens ui.screens.customers;
    opens ui.main;
    opens css;
    opens fxml;
    opens services;
    opens common;
}
