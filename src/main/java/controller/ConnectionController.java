package com.crudvisual.controller;

import com.crudvisual.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ConnectionController{
    // Campos de conexión
    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private TextField databaseField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private TextField tableField;

    // Estado y listas
    @FXML private Label connectionStatusLabel;
    @FXML private ListView<String> tablesListView;
    @FXML private TableView<ObservableList<Object>> dataTableView;

    // Botones CRUD
    @FXML private Button createButton;
    @FXML private Button readButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private DatabaseConnector dbConnector;

    @FXML
    private void initialize() {
        // Valores por defecto para PostgreSQL
        hostField.setText("localhost");
        portField.setText("5433");

        // Deshabilitar botones CRUD inicialmente
        setCRUDButtonsDisabled(true);
    }

    @FXML
    private void handleConnect() {
        String host = hostField.getText();
        String port = portField.getText();
        String database = databaseField.getText();
        String user = userField.getText();
        String password = passwordField.getText();
        String table = tableField.getText();

        // Validar campos
        if (host.isEmpty() || port.isEmpty() || database.isEmpty() ||
                user.isEmpty() || table.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return;
        }

        // Construir URL según el puerto (detectar tipo de BD)
        String url = buildDatabaseURL(host, port, database);

        // Conectar
        dbConnector = new DatabaseConnector();
        boolean connected = dbConnector.connect(url, user, password);

        if (connected) {
            // Actualizar estado
            connectionStatusLabel.setText("● Conectado");
            connectionStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

            // Cargar todas las tablas de la BD
            loadDatabaseTables();

            // Si hay tabla seleccionada, cargar sus datos
            if (!table.isEmpty()) {
                loadTableData(table);
                setCRUDButtonsDisabled(false);
            }

            showAlert("Éxito", "Conectado a " + database, Alert.AlertType.INFORMATION);

        }else{
            connectionStatusLabel.setText("● Error de conexión");
            connectionStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

            showAlert("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
        }
    }

    /**
     * Construye la URL de conexión según el puerto
     */
    private String buildDatabaseURL(String host, String port, String database) {
        // Detectar tipo de BD por puerto
        if (port.equals("5432") || port.equals("5433")) {
            return "jdbc:postgresql://" + host + ":" + port + "/" + database;
        } else if (port.equals("3306")) {
            return "jdbc:mysql://" + host + ":" + port + "/" + database;
        } else if (port.equals("1433")) {
            return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database;
        } else if (port.equals("1521")) {
            return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
        } else {
            // Por defecto PostgreSQL
            return "jdbc:postgresql://" + host + ":" + port + "/" + database;
        }
    }

    /**
     * Carga TODAS las tablas de la base de datos
     */
    private void loadDatabaseTables() {
        try {
            var tables = dbConnector.getTables();
            tablesListView.setItems(FXCollections.observableArrayList(tables));
            tablesListView.setVisible(true);
            tablesListView.setManaged(true);

            // Al seleccionar una tabla, cargar sus datos
            tablesListView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            tableField.setText(newVal);
                            loadTableData(newVal);
                            setCRUDButtonsDisabled(false);
                        }
                    }
            );

        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar las tablas: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Carga los datos de una tabla específica
     */
    private void loadTableData(String tableName) {
        // Aquí implementaremos la carga dinámica de datos en TableView
        // Esto es para el READ del CRUD
    }

    private void setCRUDButtonsDisabled(boolean disabled) {
        createButton.setDisable(disabled);
        readButton.setDisable(disabled);
        updateButton.setDisable(disabled);
        deleteButton.setDisable(disabled);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        if (dbConnector != null) {
            dbConnector.close();
        }
        javafx.application.Platform.exit();
    }
}