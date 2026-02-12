package com.crudvisual;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private String currentTable = "materias";

    // Componentes UI
    private TextField hostField = new TextField("localhost");
    private TextField portField = new TextField("5432");
    private TextField databaseField = new TextField("notas");
    private TextField userField = new TextField("postgres");
    private PasswordField passwordField = new PasswordField();
    private TextField tableField = new TextField("materias");
    private Button connectButton = new Button("CONECTAR");
    private Label statusLabel = new Label("● Sin conexión");
    private ListView<String> tableListView = new ListView<>();
    private TableView<ObservableList<Object>> dataTableView = new TableView<>();
    private HBox crudBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Estilos básicos
        connectButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Layout de host y puerto
        HBox hostBox = new HBox(10);
        hostBox.getChildren().addAll(
                createVBox("Host:", hostField),
                createVBox("Puerto:", portField)
        );

        // Estado y botón
        HBox statusBox = new HBox(10, connectButton, statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        // ListView config
        tableListView.setPrefHeight(150);
        tableListView.setVisible(false);

        // Botones CRUD
        Button createBtn = new Button("Crear");
        Button readBtn = new Button("Leer");
        Button updateBtn = new Button("Actualizar");
        Button deleteBtn = new Button("Eliminar");

        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        readBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        updateBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        crudBox = new HBox(10, createBtn, readBtn, updateBtn, deleteBtn);
        crudBox.setDisable(true);
        crudBox.setVisible(false);

        // TableView config
        dataTableView.setPrefHeight(200);
        dataTableView.setVisible(false);
        dataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ========== ACCIONES ==========
        connectButton.setOnAction(e -> conectarDemo());  // MODO DEMO

        // Seleccionar tabla del ListView
        tableListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        tableField.setText(newVal);
                        cargarDatosDemo(newVal);
                    }
                }
        );

        // ACCIONES CRUD - MODO DEMO
        readBtn.setOnAction(e -> cargarDatosDemo(tableField.getText()));
        createBtn.setOnAction(e -> mostrarFormularioCrearDemo());
        updateBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                mostrarFormularioEditarDemo();
            } else {
                showAlert("Seleccionar", "Debe seleccionar un registro", Alert.AlertType.WARNING);
            }
        });
        deleteBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                confirmarEliminarDemo();
            } else {
                showAlert("Seleccionar", "Debe seleccionar un registro", Alert.AlertType.WARNING);
            }
        });

        // Layout principal
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
                new Label("Datos de la tabla:"),
                dataTableView
        );

        Scene scene = new Scene(root, 600, 800);
        stage.setTitle("CRUD Visual - Programación Visual");
        stage.setScene(scene);
        stage.show();
    }

    // ========== MODO DEMO - FUNCIONA SIN BASE DE DATOS ==========

    private void conectarDemo() {
        statusLabel.setText("● Conectado a " + databaseField.getText() + " (DEMO)");
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Mostrar tablas de ejemplo
        ObservableList<String> tablasDemo = FXCollections.observableArrayList(
                "materias", "estudiantes", "profesores", "cursos"
        );
        tableListView.setItems(tablasDemo);
        tableListView.setVisible(true);
        crudBox.setVisible(true);
        crudBox.setDisable(false);

        // Cargar datos de materias
        cargarDatosDemo("materias");

        showAlert("✅ MODO DEMO", "Conectado en modo demostración\nLos datos son del proyecto", Alert.AlertType.INFORMATION);
    }

    private void cargarDatosDemo(String tabla) {
        currentTable = tabla;
        dataTableView.getColumns().clear();

        // Columnas fijas para materias
        String[] columnas = {"id_materia", "facultad", "escuela", "nombre"};
        for (int i = 0; i < columnas.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnas[i]);
            column.setCellValueFactory(cellData -> {
                ObservableList<Object> row = cellData.getValue();
                return new SimpleObjectProperty<>(row.get(colIndex));
            });
            dataTableView.getColumns().add(column);
        }

        // Datos del proyecto
        ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

        ObservableList<Object> row1 = FXCollections.observableArrayList();
        row1.addAll(1, 1, 1, "Teoría de la información");
        data.add(row1);

        ObservableList<Object> row2 = FXCollections.observableArrayList();
        row2.addAll(2, 1, 2, "Programación Visual");
        data.add(row2);

        ObservableList<Object> row3 = FXCollections.observableArrayList();
        row3.addAll(3, 1, 2, "Despliegue de software");
        data.add(row3);

        ObservableList<Object> row4 = FXCollections.observableArrayList();
        row4.addAll(4, 2, 5, "Matemática IV");
        data.add(row4);

        ObservableList<Object> row5 = FXCollections.observableArrayList();
        row5.addAll(5, 2, 5, "Diseño Lógico");
        data.add(row5);

        ObservableList<Object> row6 = FXCollections.observableArrayList();
        row6.addAll(6, 2, 6, "Comunicación de datos");
        data.add(row6);

        ObservableList<Object> row7 = FXCollections.observableArrayList();
        row7.addAll(7, 3, 7, "Investigación de operaciones");
        data.add(row7);

        ObservableList<Object> row8 = FXCollections.observableArrayList();
        row8.addAll(8, 3, 8, "Ingeniería económica");
        data.add(row8);

        dataTableView.setItems(data);
        dataTableView.setVisible(true);
    }

    private void mostrarFormularioCrearDemo() {
        Stage formStage = new Stage();
        formStage.setTitle("Crear nuevo registro en " + currentTable);

        VBox formRoot = new VBox(10);
        formRoot.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        TextField facultadField = new TextField();
        facultadField.setPromptText("Ingrese facultad");
        TextField escuelaField = new TextField();
        escuelaField.setPromptText("Ingrese escuela");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ingrese nombre");

        formRoot.getChildren().addAll(
                new Label("facultad:"), facultadField,
                new Label("escuela:"), escuelaField,
                new Label("nombre:"), nombreField
        );

        Button guardarBtn = new Button("Guardar");
        guardarBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        guardarBtn.setMaxWidth(Double.MAX_VALUE);

        guardarBtn.setOnAction(e -> {
            // Simular inserción
            ObservableList<Object> newRow = FXCollections.observableArrayList();
            newRow.addAll(dataTableView.getItems().size() + 1,
                    Integer.parseInt(facultadField.getText()),
                    Integer.parseInt(escuelaField.getText()),
                    nombreField.getText());
            dataTableView.getItems().add(newRow);
            showAlert("Éxito", "Registro insertado correctamente (DEMO)", Alert.AlertType.INFORMATION);
            formStage.close();
        });

        formRoot.getChildren().add(guardarBtn);

        Scene formScene = new Scene(formRoot, 350, 300);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void mostrarFormularioEditarDemo() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();

        Stage formStage = new Stage();
        formStage.setTitle("Editar registro en " + currentTable);

        VBox formRoot = new VBox(10);
        formRoot.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        TextField facultadField = new TextField(selectedRow.get(1).toString());
        TextField escuelaField = new TextField(selectedRow.get(2).toString());
        TextField nombreField = new TextField(selectedRow.get(3).toString());

        formRoot.getChildren().addAll(
                new Label("ID: " + selectedRow.get(0)),
                new Label("facultad:"), facultadField,
                new Label("escuela:"), escuelaField,
                new Label("nombre:"), nombreField
        );

        Button actualizarBtn = new Button("Actualizar");
        actualizarBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        actualizarBtn.setMaxWidth(Double.MAX_VALUE);

        actualizarBtn.setOnAction(e -> {
            selectedRow.set(1, Integer.parseInt(facultadField.getText()));
            selectedRow.set(2, Integer.parseInt(escuelaField.getText()));
            selectedRow.set(3, nombreField.getText());
            dataTableView.refresh();
            showAlert("Éxito", "Registro actualizado correctamente (DEMO)", Alert.AlertType.INFORMATION);
            formStage.close();
        });

        formRoot.getChildren().add(actualizarBtn);

        Scene formScene = new Scene(formRoot, 350, 350);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void confirmarEliminarDemo() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este registro?");
        confirm.setContentText("ID: " + selectedRow.get(0) + " - " + selectedRow.get(3));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataTableView.getItems().remove(selectedRow);
                showAlert("Éxito", "Registro eliminado correctamente (DEMO)", Alert.AlertType.INFORMATION);
            }
        });
    }

    // ========== UTILIDADES ==========

    private VBox createVBox(String labelText, Control field) {
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(new Label(labelText), field);
        vbox.setFillWidth(true);
        if (field instanceof TextField || field instanceof PasswordField) {
            ((TextField) field).setMaxWidth(Double.MAX_VALUE);
        }
        return vbox;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}