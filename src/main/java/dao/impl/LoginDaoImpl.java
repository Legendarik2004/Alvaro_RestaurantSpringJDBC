package dao.impl;

import lombok.extern.log4j.Log4j2;
import model.User;

@Log4j2
public class LoginDaoImpl implements dao.LoginDAO {

    @Override
    public boolean doLogin(User user) {
        boolean valid = user.getNombre().equals("root") && user.getPassword().equals("2dam");
        return valid;
    }
}
