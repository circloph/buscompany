package net.thumbtack.busserver.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import net.thumbtack.busserver.model.Role;
import net.thumbtack.busserver.model.RoleEnumTypeHandler;

@Mapper
public interface RoleMapper {

    @Select("select roleId, name from users join roles on users.roleId = roles.id where users.id = #{id};")  
    @Result(column = "name", property = "name", typeHandler = RoleEnumTypeHandler.class)
    Role getRoleByUserId(Integer id);

    @Select("select roles.id, name from users join roles on users.roleId = roles.id where login = #{login}")
    @Result(column = "name", property = "name", typeHandler = RoleEnumTypeHandler.class)
    Role getRoleByLogin(String login);

    @Select("SELECT roles.id, name FROM session_user join users on users.id = session_user.userId JOIN roles on roles.id = users.roleId WHERE sessionId = #{sessionId}")
    @Result(column = "name", property = "name", typeHandler = RoleEnumTypeHandler.class)
    Role getRoleBySessionId(String sessionId);

    
}
