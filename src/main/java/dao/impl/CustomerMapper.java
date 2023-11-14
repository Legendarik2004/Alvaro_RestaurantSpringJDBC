package dao.impl;


import common.Constants;
import model.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt(Constants.ID));
        c.setFirstName(rs.getString(Constants.FIRST_NAME));
        c.setLastName(rs.getString(Constants.LAST_NAME));
        c.setEmail(rs.getString(Constants.EMAIL));
        c.setPhone(rs.getString(Constants.PHONE));
        c.setDob(rs.getDate(Constants.DATE_OF_BIRTH).toLocalDate());
        return c;
    }
}
