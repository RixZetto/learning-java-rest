package com.rixstudio.learning.miniserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rixstudio.learning.miniserver.exception.DatabaseException;

public class Database {

    public final static Database instance = new Database();

    private Connection createConnection() throws DatabaseException {
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:miniserver.db");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection == null)
            throw new DatabaseException("Can't create Database Connector");

        return connection;
    }

    private void addParameters(PreparedStatement ps, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter instanceof String)
                ps.setString(i + 1, (String) parameter);
            else if (parameter instanceof Integer)
                ps.setInt(i + 1, (Integer) parameter);
            else if (parameter instanceof Long)
                ps.setLong(i + 1, (Long) parameter);
            else if (parameter instanceof Double)
                ps.setDouble(i + 1, (Double) parameter);
            else if (parameter instanceof Float)
                ps.setFloat(i + 1, (Float) parameter);
            else if (parameter instanceof Boolean)
                ps.setBoolean(i + 1, (Boolean) parameter);
        }
    }

    private int queryInt(String sql, Object... parameters) throws DatabaseException {
        return queryInt(null, sql, parameters);
    }

    private int queryInt(Connection connection, String sql, Object... parameters) throws DatabaseException {
        ResultSet rs = query(connection, sql, parameters);
        try {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return 0;
    }

    private ResultSet query(String sql, Object... parameters) throws DatabaseException {
        return query(null, sql, parameters);
    }

    private int update(String sql, Object... parameters) throws DatabaseException {
        return update(null, sql, parameters);
    }

    private ResultSet query(Connection connection, String sql, Object... parameters) throws DatabaseException {
        if (connection == null)
            connection = createConnection();
        System.out.println("QUERY " + sql);
        System.out.println("QUERY-PARAMS:" + Arrays.toString(parameters));

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            addParameters(ps, parameters);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    private int update(Connection connection, String sql, Object... parameters) throws DatabaseException {
        if (connection == null)
            connection = createConnection();
        System.out.println("UPDATE " + sql);
        System.out.println("UPDATE-PARAMS:" + Arrays.toString(parameters));
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            addParameters(ps, parameters);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    private boolean createTableIfNeeded(String tablename, String... columns) throws DatabaseException {
        return createTableIfNeeded(null, tablename, columns);
    }

    private boolean createTableIfNeeded(Connection connection, String tablename, String... columns) throws DatabaseException {
        System.out.println("columns:" + columns.length + " " + Arrays.toString(columns));
        StringBuffer columnsBuffer = new StringBuffer();
        for (String column : columns) {
            if (columnsBuffer.length() > 0)
                columnsBuffer.append(", ");
            columnsBuffer.append(column);
        }
        return update(connection, "create table if not exists " + tablename + " (" + columnsBuffer.toString() + ")") == 1;
    }

    public void registerDevice(String deviceToken, DeviceType type) throws DatabaseException {
        Connection connection = createConnection();
        // update("drop table if exists Devices");
        createTableIfNeeded(connection, "Devices", "id string", "type int");
        int count = queryInt("select count(id) from Devices where id = ? and type = ?", deviceToken, DeviceType.toInt(type));
        if (count == 0)
            update(connection, "insert into Devices values (?, ?)", deviceToken, DeviceType.toInt(type));
    }

    public List<Device> listDevices(DeviceType type) throws DatabaseException {
        Connection connection = createConnection();
        StringBuffer sql = new StringBuffer();
        sql.append("select id, type from Devices");
        if (type != null)
            sql.append(" where type == ?");
        ResultSet rs = type != null ? query(connection, sql.toString(), type) : query(connection, sql.toString());
        List<Device> devices = null;
        try {
            while (rs.next()) {
                System.out.println(rs.getString(1) + "- " + rs.getString(2));
                if (devices == null)
                    devices = new ArrayList<Device>();
                Device device = new Device();
                device.setToken(rs.getString(1));
                device.setType(DeviceType.fromInt(rs.getInt(2)));
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

}
