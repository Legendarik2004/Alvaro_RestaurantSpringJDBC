module javafx {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.apache.logging.log4j;
    requires jakarta.inject;
    requires jakarta.cdi;
    requires jakarta.xml.bind;
    requires io.vavr;
    requires com.zaxxer.hikari;
    requires MaterialFX;
    requires java.sql;
    requires commons.dbcp2;
    requires spring.tx;
    requires spring.jdbc;

    exports ui.main to javafx.graphics;
    exports ui.screens.principal;
    exports ui.screens.login;
    exports ui.screens.common;
    exports ui.screens.customers;
    exports ui.screens.orders;
    exports ui.screens.welcome;
    exports common;
    exports common.constants;
    exports model;
    exports model.errors;
    exports model.xml;
    exports services;
    exports services.impl;
    exports dao;
    exports dao.impl;
    exports dao.deprecated;
    exports dao.mappers;

    opens model.xml to javafx.base, jakarta.xml.bind;
    opens ui.screens.login;
    opens ui.screens.principal;
    opens ui.screens.customers;
    opens ui.main;
    opens common;
    opens common.constants;
    opens services;
    opens services.impl;
    opens dao;
    opens dao.impl;
    opens dao.mappers;
    opens css;
    opens fxml;
}
