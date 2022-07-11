package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.thumbtack.busserver.model.Client;
import net.thumbtack.busserver.model.Role;

public interface ClientMapper {
    
    @Insert("INSERT INTO clients (userId, email, numberPhone) VALUES (#{id}, #{email}, #{numberPhone})")
    void insertClient(Client client);

    @Select("SELECT users.id as id, lastname, firstname, patronymic, email, numberPhone, login, password, enabled FROM clients JOIN users on clients.userId = users.id WHERE login = #{login} and enabled = true")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "email", property = "email"),
        @Result(column = "numberPhone", property = "numberPhone"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    Client getClientByLogin(String login);

    @Select("SELECT users.id as id, lastname, firstname, patronymic, email, numberPhone, login, password, enabled FROM clients JOIN users on clients.userId = users.id WHERE id = #{id} and enabled = true")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "email", property = "email"),
        @Result(column = "numberPhone", property = "numberPhone"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    Client getClientById(Integer id);

    @Select("SELECT users.id as id, lastname, firstname, patronymic, email, numberPhone, login, password, enabled " +
    "FROM users JOIN clients ON users.id = clients.userId where enabled = true")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "email", property = "email"),
        @Result(column = "numberPhone", property = "numberPhone"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    List<Client> getAllClients();

    @Update("UPDATE users JOIN clients on clients.userId = users.id SET " +
    "lastname = #{lastname}, firstname = #{firstname}, patronymic = #{patronymic}, email = #{email}, numberPhone = #{numberPhone}, password = #{password} " +
    "WHERE clients.userId = #{id}")
    void updateClient(Client client);

    @Select("SELECT users.id as id, lastname, firstname, patronymic, email, numberPhone, login, password, enabled, sessionId FROM clients JOIN users on clients.userId = users.id JOIN session_user on session_user.userId = users.id WHERE sessionId = #{sessionId};")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "email", property = "email"),
        @Result(column = "numberPhone", property = "numberPhone"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    Client getClientBySessionId(String sessionId);
    
}