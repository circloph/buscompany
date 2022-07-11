package net.thumbtack.busserver.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.thumbtack.busserver.model.User;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users (roleId, lastname, firstname, patronymic, login, password, enabled)" +
    "VALUES (1, #{lastname}, #{firstname}, #{patronymic}, #{login}, #{password}, true)")
    @Options(useGeneratedKeys = true, keyProperty = "id",  keyColumn = "id")
    void insertClient(User user);

    @Insert("INSERT INTO users (roleId, lastname, firstname, patronymic, login, password, enabled)" +
    "VALUES (2, #{lastname}, #{firstname}, #{patronymic}, #{login}, #{password}, true)")
    @Options(useGeneratedKeys = true, keyProperty = "id",  keyColumn = "id")
    void insertAdministration(User user);

    @Delete("DELETE FROM users")
    void deleteAllUsers();

    @Select("select exists (select * from users where login = #{login});")
    boolean checkExistenceByLogin(String login);

    @Update("UPDATE users SET enabled = false WHERE login = #{login}")
    void deleteUser(String login);

    @Select("SELECT count(*) FROM users WHERE roleId = 2")
    Integer isLastAdministration();
    
}
