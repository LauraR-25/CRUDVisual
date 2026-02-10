package com.crudvisual.controller;

import com.crudvisual.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ConnectionController {
    @FXML private TextField urlField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private TextField tableField;

    private DatabaseConnector dbConnector;

    @FXML
    private void initialize() {
        // Valores por defecto para probar
        urlField.setText("jdbc:mysql://localhost:3306/notas");
        userField.setText("root");
        tableField.setText("materias");
    }

    @FXML
    private void handleConnect() {
        String url = urlField.getText();
        String user = userField.getText();
        String password = passwordField.getText();
        String table = tableField.getText();

        if (url.isEmpty() || user.isEmpty() || table.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return;
        }

        dbConnector = new DatabaseConnector();
        boolean connected = dbConnector.connect(url, user, password);

        if (connected) {
            showAlert("Éxito", "Conexión establecida correctamente", Alert.AlertType.INFORMATION);

            try {
                // Obtener columnas de la tabla
                var columns = dbConnector.getColumns(table);
                System.out.println("Columnas de " + table + ": " + columns);

                // Aquí abrirás la siguiente pantalla del CRUD
                // openMainView();

            } catch (Exception e) {
                showAlert("Error", "No se pudo obtener información de la tabla: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }

        } else {
            showAlert("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
        }
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
        // Cerrar la ventana
        Stage stage = (Stage) urlField.getScene().getWindow();
        stage.close();
    }
}