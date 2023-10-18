package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Order {
    private int idOrder;
    private LocalDateTime date;
    private int customerId;
    private int tableId;

    public Order (String fileLine) {
        String[] elemArray = fileLine.split(";");
        this.idOrder = Integer.parseInt(elemArray[0]);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = LocalDateTime.parse(elemArray[1], formatter);
        this.customerId = Integer.parseInt(elemArray[2]);
        this.tableId = Integer.parseInt(elemArray[3]);
    }

    public String toStringTextFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return idOrder + ";" + date.format(formatter) + ";" + customerId + ";" + tableId;
    }
}
