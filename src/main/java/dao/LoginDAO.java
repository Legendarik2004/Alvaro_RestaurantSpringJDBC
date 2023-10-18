package dao;

import model.User;

public interface LoginDAO {
    boolean doLogin(User user);
}
