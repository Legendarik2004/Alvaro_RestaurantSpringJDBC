package dao.impl;

import common.constants.ConstantsErrorMessages;
import common.constants.ConstantsSQLTableAttributes;
import common.SQLQueries;
import dao.LoginDAO;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import model.Credentials;
import model.errors.Error;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class LoginDaoImpl implements LoginDAO {
    private final DBConnectionPool db;
    private Credentials newCredentials;

    @Inject
    public LoginDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<Error, Boolean> doLogin(Credentials credentials) {
        Either<Error, Boolean> result;

        try (Connection myConnection = db.getConnection();
             PreparedStatement preparedStatement = myConnection.prepareStatement(SQLQueries.CHECK_CREDENTIALS)) {
            preparedStatement.setString(1, credentials.getUsername());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next() && credentials.getPassword().equals(rs.getString(ConstantsSQLTableAttributes.PASSWORD))) {
                    newCredentials = new Credentials();
                    newCredentials.setCustomerId(rs.getInt(ConstantsSQLTableAttributes.CREDENTIAL_ID));
                    newCredentials.setUsername(rs.getString(ConstantsSQLTableAttributes.USER_NAME));
                    newCredentials.setPassword(rs.getString(ConstantsSQLTableAttributes.PASSWORD));
                    result = Either.right(true);
                } else {
                    result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ON_LOGIN));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(LoginDaoImpl.class.getName()).log(Level.SEVERE, null, e);
            result = Either.left(new Error(ConstantsErrorMessages.NUM_ERROR, ConstantsErrorMessages.ERROR_ON_LOGIN));
        }
        return result;
    }

    @Override
    public Credentials get() {
        return newCredentials;
    }

}
