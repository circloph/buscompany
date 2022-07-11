package net.thumbtack.busserver.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Component
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Client extends User {
    private String email;
    private String numberPhone;

    @Builder
    public Client(String lastname, String firstname, String patronymic, String email, String numberPhone, String login, String password) {
        super(lastname, firstname, patronymic, login, password);
        this.email = email;
        this.numberPhone = numberPhone;
    }
}

