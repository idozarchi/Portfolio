package gdbm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GDBM {
    private Connection conn;

    public GDBM(String dbName, String url, String username, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, username, password);

        runSQLQueryUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        this.conn = DriverManager.getConnection(url + "/" + dbName, username, password);
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths) throws SQLException {
        createTable(name, values, "", varcharsLengths, null, null);
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, String primaryKey, List<String> varcharsLengths) throws SQLException {
        createTable(name, values, primaryKey, varcharsLengths, null, null);
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values,
                            List<String> varcharsLengths,
                            String foreignTable, String foreignAttribute) throws SQLException {
        createTable(name, values, "", varcharsLengths, foreignTable, foreignAttribute);
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values,
                            String primaryKey, List<String> varcharsLengths,
                            String foreignTable, String foreignAttribute) throws SQLException {
        int varcharIndex = 0;
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (");

        for(Map.Entry<DataTypes, String> entry : values){
            query.append(entry.getValue());
            query.append(" ");
            query.append(entry.getKey().name());

            if(entry.getKey().equals(DataTypes.VARCHAR)) {
                String varcharSize = " (" + varcharsLengths.get(varcharIndex) + ")";
                query.append(varcharSize);
                ++varcharIndex;
            }

            if (Objects.equals(primaryKey, entry.getValue())) {
                query.append(" PRIMARY KEY");
            }

            query.append(", ");
        }
        if (foreignAttribute != null) {
            String s = "FOREIGN KEY (" + foreignAttribute + ") REFERENCES " + foreignTable + "(" + foreignAttribute + "), ";
            query.append(s);
        }

        String res = query.substring(0, query.length() - 2) + " )";

        runSQLQueryUpdate(res);
    }

    public void insertRecord(String table, List<String> data) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO " + table + " VALUES (");

        for(String value : data){
            query.append(value);
            query.append(", ");
        }
        String res = query.substring(0, query.length() - 2) + ")";

        runSQLQueryUpdate(res);
    }

    public List<List<String>> getRecords(String table) throws SQLException {
        String query = "SELECT * FROM " + table;
        ResultSet rs = runSQLQuery(query);

        return parseResultSet(rs);
    }

    public List<List<String>> getRecords(String table, String attribute) throws SQLException {
        String query = "SELECT " + attribute + " FROM " + table;
        ResultSet rs = runSQLQuery(query);

        return parseResultSet(rs);
    }

    public List<List<String>> getRecords(String table, String attribute, String val) throws SQLException {
        String query = "SELECT " + attribute + " FROM " + table + " WHERE " + attribute + "=" + val;
        ResultSet rs = runSQLQuery(query);

        return parseResultSet(rs);
    }

    public void deleteRecords(String table) throws SQLException {
        String query = "TRUNCATE " + table;
        runSQLQueryUpdate(query);
    }

    public void deleteRecords(String table, String attribute, String val) throws SQLException {
        String query = "DELETE FROM " + table + " WHERE " + attribute + "=" + val;
        runSQLQueryUpdate(query);
    }

    public void updateRecord(String table, String attribute, String val, String newVal) throws SQLException {
        String query = "UPDATE " + table + " SET " + attribute + "=" + newVal + " WHERE " + attribute + "=" + val;
        runSQLQueryUpdate(query);
    }

    private void runSQLQueryUpdate(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    private ResultSet runSQLQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    private List<List<String>> parseResultSet(ResultSet rs) throws SQLException {
        List<List<String>> resultSetList = new ArrayList<>();

        while(rs.next()) {
            int columnCount = rs.getMetaData().getColumnCount();

            List<String> row = new ArrayList<>();
            for(int i = 1; i <= columnCount; ++i) {
                row.add(rs.getString(i));
            }

            resultSetList.add(row);
        }

        return resultSetList;
    }

    public enum DataTypes {
        VARCHAR,
        INTEGER,
        SMALLINT,
        DOUBLE,
        BOOLEAN
    }
}