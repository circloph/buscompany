package net.thumbtack.busserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class User {
    private Integer id;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String login;
    private String password;
    private Role role;
    private boolean enabled;

    public User(String lastname, String firstname, String patronymic, String login, String password) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.patronymic = patronymic;
        this.login = login;
        this.password = password;
    }


    public User(String lastname, String firstname, String patronymic, String password) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.patronymic = patronymic;
        this.password = password;
    }

}
