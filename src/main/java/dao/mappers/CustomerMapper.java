package dao.mappers;


import common.constants.ConstantsSQLTableAttributes;
import model.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt(ConstantsSQLTableAttributes.ID));
        c.setFirstName(rs.getString(ConstantsSQLTableAttributes.FIRST_NAME));
        c.setLastName(rs.getString(ConstantsSQLTableAttributes.LAST_NAME));
        c.setEmail(rs.getString(ConstantsSQLTableAttributes.EMAIL));
        c.setPhone(rs.getString(ConstantsSQLTableAttributes.PHONE));
        c.setDob(rs.getDate(ConstantsSQLTableAttributes.DATE_OF_BIRTH).toLocalDate());
        return c;
    }
}
