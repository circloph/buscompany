package net.thumbtack.busserver.model;

import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Component
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class Administration extends User {
    private String position;

    @Builder
    public Administration(String lastname, String firstname, String patronymic, String position, String login, String password) {
        super(lastname, firstname, patronymic, login, password);
        this.position = position;
    }

    public Administration(String lastname, String firstname, String patronymic, String position, String password) {
        super(lastname, firstname, patronymic, password);
        this.position = position;
    }

}

    
