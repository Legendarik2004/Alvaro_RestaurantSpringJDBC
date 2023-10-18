package model;

import lombok.Data;


@Data
public class Item {
    private int idItem;
    private String name;
    private String description;
    private float price;

    public Item(String fileLine) {
        String[] elemArray = fileLine.split(";");
        this.idItem = Integer.parseInt(elemArray[0]);
        this.name = elemArray[1];
        this.description = elemArray[2];
        this.price = Float.parseFloat(elemArray[3]);

    }

    public String toStringTextFile() {
        return idItem + ";" + name + ";" + description + ";" + price;
    }
}
