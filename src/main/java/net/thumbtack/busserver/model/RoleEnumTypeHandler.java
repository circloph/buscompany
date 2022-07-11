package net.thumbtack.busserver.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class RoleEnumTypeHandler extends BaseTypeHandler<Role> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Role parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(1, parameter.getName());
    }

    @Override
    public Role getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Role.getByName(rs.getString(columnName));
    }
    
    @Override
    public Role getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Role.getByName(rs.getString(columnIndex));
    }
    
    @Override
    public Role getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Role.getByName(cs.getString(columnIndex));
    }
    
}
