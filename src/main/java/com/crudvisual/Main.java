package com.crudvisual;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // HOST Y PUERTO
        TextField hostField = new TextField("localhost");
        TextField portField = new TextField("5433");

        HBox hostBox = new HBox(10);
        hostBox.getChildren().addAll(
                createVBox("Host:", hostField),
                createVBox("Puerto:", portField)
        );

        // BASE DE DATOS
        TextField databaseField = new TextField();
        databaseField.setPromptText("nombre_db");

        // USUARIO Y CONTRASEÑA
        TextField userField = new TextField();
        PasswordField passwordField = new PasswordField();

        // TABLA
        TextField tableField = new TextField();
        tableField.setPromptText("materias");

        // BOTÓN CONECTAR
        Button connectButton = new Button("CONECTAR");
        connectButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Label statusLabel = new Label("● Sin conexión");
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        HBox statusBox = new HBox(10, connectButton, statusLabel);

        // LISTA DE TABLAS
        ListView<String> tableListView = new ListView<>();
        tableListView.setPrefHeight(150);
        tableListView.setVisible(false);

        // BOTONES CRUD
        Button createBtn = new Button("Crear");
        Button readBtn = new Button("Leer");
        Button updateBtn = new Button("Actualizar");
        Button deleteBtn = new Button("Eliminar");

        HBox crudBox = new HBox(10, createBtn, readBtn, updateBtn, deleteBtn);
        crudBox.setDisable(true);

        // TABLA DE DATOS
        TableView dataTableView = new TableView();
        dataTableView.setPrefHeight(200);
        dataTableView.setVisible(false);

        // CONTENEDOR PRINCIPAL
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-background-color: #f8f9fa;");
        root.getChildren().addAll(
                new Label("CRUD Gráfico - Conexión a Base de Datos"),
                hostBox,
                createVBox("Base de datos:", databaseField),
                createVBox("Usuario:", userField),
                createVBox("Contraseña:", passwordField),
                createVBox("Tabla para CRUD:", tableField),
                statusBox,
                new Separator(),
                new Label("Tablas disponibles:"),
                tableListView,
                crudBox,
                dataTableView
        );

        // ACCIÓN DEL BOTÓN CONECTAR
        connectButton.setOnAction(e -> {
            statusLabel.setText("● Conectado");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            tableListView.setVisible(true);

            // SIMULACIÓN - AQUÍ CONECTARÁS A LA BD
            tableListView.getItems().addAll("materias", "estudiantes", "profesores", "cursos");

            crudBox.setDisable(false);
            dataTableView.setVisible(true);
        });

        Scene scene = new Scene(root, 550, 700);
        stage.setTitle("CRUD Visual");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createVBox(String labelText, Control field) {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(new Label(labelText), field);
        return vbox;
    }
}