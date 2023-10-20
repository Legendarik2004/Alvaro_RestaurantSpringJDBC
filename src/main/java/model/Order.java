package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Order {
    private int idOrder;
    private Timestamp date;
    private int customerId;
    private int tableId;

}
