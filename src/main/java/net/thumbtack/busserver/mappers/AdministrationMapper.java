package net.thumbtack.busserver.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.thumbtack.busserver.model.Administration;
import net.thumbtack.busserver.model.Role;

public interface AdministrationMapper {

    @Insert("INSERT INTO administration (userId, position) VALUES (#{id}, #{position})")
    void insertAdministration(Administration administration);

    @Select("SELECT users.id as id, lastname, firstname, patronymic, position, login, password, enabled " +
    "FROM users JOIN administration ON users.id = administration.userId;")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "position", property = "position"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                    one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    List<Administration> getAllAdministration();


    @Select("SELECT users.id as id, lastname, firstname, patronymic, position, login, password, enabled FROM administration JOIN users on administration.userId = users.id WHERE login = #{login} AND enabled = true")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "position", property = "position"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                    one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    Administration getAdministrationByLogin(String login);


    @Update("UPDATE users JOIN administration on administration.userId = users.id SET " +
    "lastname = #{lastname}, firstname = #{firstname}, patronymic = #{patronymic}, position = #{position}, password = #{password} " +
    "WHERE administration.userId = #{id}")
    void updateAdministration(Administration administration);

    

    @Select("SELECT users.id as id, lastname, firstname, patronymic, position, login, password, enabled, sessionId FROM administration JOIN users on administration.userId = users.id JOIN session_user on session_user.userId = users.id WHERE sessionId = #{sessionId}")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "lastname", property = "lastname"),
        @Result(column = "firstname", property = "firstname"),
        @Result(column = "patronymic", property = "patronymic"),
        @Result(column = "position", property = "position"),
        @Result(column = "login", property = "login"),
        @Result(column = "password", property = "password"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "role", column = "id", javaType = Role.class,
                    one = @One(select = "net.thumbtack.busserver.mappers.RoleMapper.getRoleByUserId"))
    })
    Administration getAdministrationBySessionId(String sessionId);

}