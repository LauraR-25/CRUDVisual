package com.crudvisual.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class ConnectionController {

    @FXML
    private void handleConnect() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conexión");
        alert.setHeaderText(null);
        alert.setContentText("¡Conectado exitosamente!");
        alert.showAndWait();
    }
}