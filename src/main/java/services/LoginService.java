package services;

import dao.LoginDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.User;
import model.errors.Error;


public class LoginService {
    @Inject
    private LoginDAO dao;


    public Either<Error,Boolean> doLogin(User user) {
        return dao.doLogin(user);
    }

    public User get() {
        return dao.get();
    }
}
