package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dob;

    public Customer(String fileLine) {
        String[] elemArray = fileLine.split(";");
        this.id = Integer.parseInt(elemArray[0]);
        this.firstName = elemArray[1];
        this.lastName = elemArray[2];
        this.email = elemArray[3];
        this.phone = elemArray[4];
        this.dob = LocalDate.parse(elemArray[5]);
    }

    public String toStringTextFile() {
        return id + ";" + firstName + ";" + lastName + ";" + email + ";" + phone + ";" + dob;
    }
}