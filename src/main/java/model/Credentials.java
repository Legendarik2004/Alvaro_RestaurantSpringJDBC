package model;

import lombok.Data;

@Data
public class Credentials {
    private final int id;
    private final String username;
    private final String password;
}
