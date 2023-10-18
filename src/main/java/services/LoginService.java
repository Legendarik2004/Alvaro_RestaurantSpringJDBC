package services;

import dao.LoginDAO;
import jakarta.inject.Inject;
import model.User;

public class LoginService {
    @Inject
    private LoginDAO dao;


    public boolean doLogin(User user) {
        return dao.doLogin(user);
    }
}
