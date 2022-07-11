package net.thumbtack.busserver.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.thumbtack.busserver.model.Session;

public interface SessionMapper {

    @Insert("INSERT INTO session_user (userId, sessionId, expiration) VALUES (#{userId}, #{session.sessionId}, #{session.expiration})")
    void insertSession(@Param("userId") Integer userId, @Param("session") Session session);

    @Select("SELECT userId FROM session_user WHERE sessionId = #{sessionId}")
    Integer getUserIdBySessionId(String sessionId);

    @Select("SELECT * FROM session_user WHERE sessionId = #{sessionId}")
    Session getSessionBySessionId(String sessionId);

    @Update("UPDATE session_user SET expiration = #{expiration} WHERE sessionId = #{sessionId}")
    void updateExpiration(Session session);

    @Delete("DELETE FROM session_user WHERE sessionId = #{sessionId}")
    void deleteSession(String sessionId);

    @Delete("DELETE FROM session_user WHERE STR_TO_DATE(expiration, '%Y-%c-%dT%H:%i:%S.%f') < NOW()")
    void deleteSessionsByExpiration();


}
