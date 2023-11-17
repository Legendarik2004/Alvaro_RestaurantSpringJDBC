package dao.mappers;


import common.constants.ConstantsSQLTableAttributes;
import model.MenuItem;
import model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemMapper implements RowMapper<OrderItem> {

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem o = new OrderItem();
        MenuItem m = new MenuItem();
        m.setMenuItemId(rs.getInt(ConstantsSQLTableAttributes.MENU_ITEM_ID));
        m.setName(rs.getString(ConstantsSQLTableAttributes.NAME));
        m.setDescription(rs.getString(ConstantsSQLTableAttributes.DESCRIPTION));
        m.setPrice(rs.getDouble(ConstantsSQLTableAttributes.PRICE));
        o.setMenuItem(m);
        o.setOrderItemId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ITEM_ID));
        o.setQuantity(rs.getInt(ConstantsSQLTableAttributes.QUANTITY));
        o.setOrderId(rs.getInt(ConstantsSQLTableAttributes.ORDER_ID));
        return o;
    }
}
