package dao.mappers;


import common.constants.ConstantsSQLTableAttributes;
import model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ID));
        o.setDate(rs.getTimestamp(ConstantsSQLTableAttributes.ORDER_DATE));
        o.setCustomerId(rs.getInt(ConstantsSQLTableAttributes.CUSTOMER_ID));
        o.setTableId(rs.getInt(ConstantsSQLTableAttributes.TABLE_ID));
        return o;
    }
}
