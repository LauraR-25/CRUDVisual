package com.crudvisual.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {
    private Connection connection;

    /**
     * Conecta a la base de datos
     */
    public boolean connect(String url, String user, String password) {
        try{
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión exitosa a: " + url);
            return true;
        }catch (SQLException e){
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las tablas de la base de datos
     */
    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});

        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    /**
     * Obtiene las columnas de una tabla específica
     */
    public List<String> getColumns(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, null);

        while (rs.next()) {
            columns.add(rs.getString("COLUMN_NAME"));
        }
        return columns;
    }

    /**
     * Ejecuta una consulta SELECT
     */
    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * Ejecuta INSERT, UPDATE, DELETE
     */
    public int executeUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(query);
    }

    /**
     * Obtiene todos los registros de una tabla
     */
    public ResultSet getAllRecords(String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        return executeQuery(query);
    }

    /**
     * Inserta un nuevo registro
     */
    public int insertRecord(String tableName, Map<String, Object> values) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String column : values.keySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(column);
            placeholders.append("?");
        }

        String query = "INSERT INTO " + tableName +
                " (" + columns + ") VALUES (" + placeholders + ")";

        PreparedStatement pstmt = connection.prepareStatement(query);
        int i = 1;
        for (Object value : values.values()) {
            pstmt.setObject(i++, value);
        }

        return pstmt.executeUpdate();
    }

    /**
     * Actualiza un registro
     */
    public int updateRecord(String tableName, Map<String, Object> values, String whereClause) throws SQLException {
        StringBuilder setClause = new StringBuilder();

        for (String column : values.keySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(column).append(" = ?");
        }

        String query = "UPDATE " + tableName +
                " SET " + setClause +
                " WHERE " + whereClause;

        PreparedStatement pstmt = connection.prepareStatement(query);
        int i = 1;
        for (Object value : values.values()) {
            pstmt.setObject(i++, value);
        }

        return pstmt.executeUpdate();
    }

    /**
     * Elimina un registro
     */
    public int deleteRecord(String tableName, String whereClause) throws SQLException {
        String query = "DELETE FROM " + tableName + " WHERE " + whereClause;
        return executeUpdate(query);
    }

    /**
     * Cierra la conexión
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
        }
    }

    /**
     * Prueba la conexión
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}