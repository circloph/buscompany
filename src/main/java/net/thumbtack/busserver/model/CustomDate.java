package net.thumbtack.busserver.model;

import lombok.Data;

@Data
public class CustomDate {

    private Integer id;
    private String dayName;

    public CustomDate(String dayName) {
        this.dayName = dayName;
    }
}
