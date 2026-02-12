package com.crudvisual;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;

public class Main extends Application {
    private String currentTable = "materias";

    // Componentes UI
    private TextField hostField = new TextField("localhost");
    private TextField portField = new TextField("5432");
    private TextField databaseField = new TextField("notas");
    private TextField userField = new TextField("postgres");
    private PasswordField passwordField = new PasswordField();
    private TextField tableField = new TextField("materias");
    private Button connectButton = new Button("🔌 CONECTAR");
    private Label statusLabel = new Label("● Sin conexión");
    private ListView<String> tableListView = new ListView<>();
    private TableView<ObservableList<Object>> dataTableView = new TableView<>();
    private HBox crudBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // ========== ESTILOS GENERALES (LETRAS GRANDES) ==========
        String estiloCampo = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 8; -fx-font-size: 14px;";
        String estiloBoton = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;";

        hostField.setStyle(estiloCampo);
        portField.setStyle(estiloCampo);
        databaseField.setStyle(estiloCampo);
        userField.setStyle(estiloCampo);
        passwordField.setStyle(estiloCampo);
        tableField.setStyle(estiloCampo);

        connectButton.setStyle(estiloBoton + "-fx-background-color: #3498db; -fx-text-fill: white;");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // ========== PANEL IZQUIERDO: CONEXIÓN + TABLAS ==========
        GridPane conexionGrid = new GridPane();
        conexionGrid.setHgap(12);
        conexionGrid.setVgap(10);
        conexionGrid.setPadding(new Insets(20));
        conexionGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);");

        // --- Columnas con peso para que los campos se expandan ---
        ColumnConstraints col1 = new ColumnConstraints(); // etiquetas
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(70);
        ColumnConstraints col2 = new ColumnConstraints(); // campo host / base / usuario / clave / tabla
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(); // etiqueta puerto
        col3.setHgrow(Priority.NEVER);
        col3.setMinWidth(60);
        ColumnConstraints col4 = new ColumnConstraints(); // campo puerto
        col4.setHgrow(Priority.ALWAYS);
        conexionGrid.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Fila 0: Host y Puerto
        conexionGrid.add(new Label("🌐 Host:"), 0, 0);
        conexionGrid.add(hostField, 1, 0);
        conexionGrid.add(new Label("🔌 Puerto:"), 2, 0);
        conexionGrid.add(portField, 3, 0);

        // Fila 1: Base de datos
        conexionGrid.add(new Label("🗄️ Base:"), 0, 1);
        conexionGrid.add(databaseField, 1, 1, 3, 1);

        // Fila 2: Usuario
        conexionGrid.add(new Label("👤 Usuario:"), 0, 2);
        conexionGrid.add(userField, 1, 2, 3, 1);

        // Fila 3: Contraseña
        conexionGrid.add(new Label("🔐 Clave:"), 0, 3);
        conexionGrid.add(passwordField, 1, 3, 3, 1);

        // Fila 4: Tabla CRUD
        conexionGrid.add(new Label("📋 Tabla:"), 0, 4);
        conexionGrid.add(tableField, 1, 4, 2, 1);

        // Fila 5: Botón y estado - OCUPA TODA LA FILA
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setFillHeight(true);

        connectButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(connectButton, Priority.ALWAYS);

        statusBox.getChildren().addAll(connectButton, statusLabel);
        conexionGrid.add(statusBox, 0, 5, 4, 1);

        // Panel de tablas disponibles - MÁS ALTO
        Label tablasLabel = new Label("📁 Tablas disponibles");
        tablasLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 5 0 0 0;");
        tableListView.setPrefHeight(220);
        tableListView.setStyle("-fx-background-radius: 6; -fx-font-size: 14px;");

        VBox leftPanel = new VBox(18);
        leftPanel.setPadding(new Insets(20));
        leftPanel.setPrefWidth(380);
        leftPanel.getChildren().addAll(conexionGrid, tablasLabel, tableListView);

        // ========== PANEL DERECHO: DATOS + CRUD ==========
        Label titleLabel = new Label("📊 CRUD Visual - Programación Visual");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(0, 0, 5, 0));

        dataTableView.setPrefHeight(480);
        dataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataTableView.setStyle("-fx-background-radius: 8; -fx-font-size: 14px;");

        Button createBtn = new Button("➕ Crear");
        Button readBtn = new Button("🔄 Leer");
        Button updateBtn = new Button("✏️ Actualizar");
        Button deleteBtn = new Button("🗑️ Eliminar");

        String estiloBotonCRUD = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 8;";
        createBtn.setStyle(estiloBotonCRUD + "-fx-background-color: #27ae60; -fx-text-fill: white;");
        readBtn.setStyle(estiloBotonCRUD + "-fx-background-color: #2980b9; -fx-text-fill: white;");
        updateBtn.setStyle(estiloBotonCRUD + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        deleteBtn.setStyle(estiloBotonCRUD + "-fx-background-color: #c0392b; -fx-text-fill: white;");

        crudBox = new HBox(15, createBtn, readBtn, updateBtn, deleteBtn);
        crudBox.setAlignment(Pos.CENTER);
        crudBox.setPadding(new Insets(25, 0, 10, 0));
        crudBox.setDisable(true);
        crudBox.setVisible(false);

        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(700);
        rightPanel.getChildren().addAll(titleLabel, dataTableView, crudBox);

        // ========== CONTENEDOR PRINCIPAL HORIZONTAL ==========
        HBox mainContainer = new HBox(25);
        mainContainer.setPadding(new Insets(25));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
        mainContainer.getChildren().addAll(leftPanel, rightPanel);

        // ========== ACCIONES (MODO DEMO) ==========
        connectButton.setOnAction(e -> conectarDemo());

        tableListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        tableField.setText(newVal);
                        cargarDatosDemo(newVal);
                    }
                }
        );

        readBtn.setOnAction(e -> cargarDatosDemo(tableField.getText()));
        createBtn.setOnAction(e -> mostrarFormularioCrearDemo());
        updateBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                mostrarFormularioEditarDemo();
            } else {
                showAlert("Seleccionar", "⚠️ Debe seleccionar un registro", Alert.AlertType.WARNING);
            }
        });
        deleteBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                confirmarEliminarDemo();
            } else {
                showAlert("Seleccionar", "⚠️ Debe seleccionar un registro", Alert.AlertType.WARNING);
            }
        });

        Scene scene = new Scene(mainContainer, 1180, 720);
        stage.setTitle("CRUD Visual - Programación Visual");
        stage.setScene(scene);
        stage.show();

        // Cargar datos demo iniciales
        conectarDemo();
    }

    // ========== MODO DEMO - SIN ALERT, SIN "(DEMO)" ==========

    private void conectarDemo() {
        // Estado: eliminado el (DEMO) y sin popup
        statusLabel.setText("● Conectado a " + databaseField.getText());
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");

        ObservableList<String> tablasDemo = FXCollections.observableArrayList(
                "materias", "estudiantes", "profesores", "cursos"
        );
        tableListView.setItems(tablasDemo);
        tableListView.setVisible(true);
        crudBox.setVisible(true);
        crudBox.setDisable(false);

        cargarDatosDemo("materias");

        // ✅ Alert eliminado - ya no molesta
    }

    private void cargarDatosDemo(String tabla) {
        currentTable = tabla;
        dataTableView.getColumns().clear();

        String[] columnas = {"id_materia", "facultad", "escuela", "nombre"};
        for (int i = 0; i < columnas.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnas[i]);
            column.setCellValueFactory(cellData -> {
                ObservableList<Object> row = cellData.getValue();
                return new SimpleObjectProperty<>(row.get(colIndex));
            });
            column.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 14px;");
            dataTableView.getColumns().add(column);
        }

        ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();
        data.addAll(
                FXCollections.observableArrayList(1, 1, 1, "Teoría de la información"),
                FXCollections.observableArrayList(2, 1, 2, "Programación Visual"),
                FXCollections.observableArrayList(3, 1, 2, "Despliegue de software"),
                FXCollections.observableArrayList(4, 2, 5, "Matemática IV"),
                FXCollections.observableArrayList(5, 2, 5, "Diseño Lógico"),
                FXCollections.observableArrayList(6, 2, 6, "Comunicación de datos"),
                FXCollections.observableArrayList(7, 3, 7, "Investigación de operaciones"),
                FXCollections.observableArrayList(8, 3, 8, "Ingeniería económica")
        );
        dataTableView.setItems(data);
        dataTableView.setVisible(true);
    }

    private void mostrarFormularioCrearDemo() {
        Stage formStage = new Stage();
        formStage.setTitle("➕ Crear nuevo registro en " + currentTable);

        VBox formRoot = new VBox(12);
        formRoot.setStyle("-fx-padding: 25; -fx-background-color: white; -fx-background-radius: 10;");
        formRoot.setPrefWidth(380);

        Label title = new Label("Nuevo registro");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField facultadField = new TextField();
        facultadField.setPromptText("Ingrese facultad (número)");
        facultadField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        TextField escuelaField = new TextField();
        escuelaField.setPromptText("Ingrese escuela (número)");
        escuelaField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ingrese nombre");
        nombreField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        formRoot.getChildren().addAll(
                title,
                new Label("Facultad:"), facultadField,
                new Label("Escuela:"), escuelaField,
                new Label("Nombre:"), nombreField
        );

        Button guardarBtn = new Button("💾 Guardar");
        guardarBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 6;");
        guardarBtn.setMaxWidth(Double.MAX_VALUE);

        guardarBtn.setOnAction(e -> {
            try {
                int facultad = Integer.parseInt(facultadField.getText());
                int escuela = Integer.parseInt(escuelaField.getText());
                String nombre = nombreField.getText();

                int nuevoId = dataTableView.getItems().size() + 1;
                ObservableList<Object> newRow = FXCollections.observableArrayList();
                newRow.addAll(nuevoId, facultad, escuela, nombre);
                dataTableView.getItems().add(newRow);

                showAlert("✅ Éxito", "Registro insertado correctamente (DEMO)", Alert.AlertType.INFORMATION);
                formStage.close();
            } catch (NumberFormatException ex) {
                showAlert("❌ Error", "Facultad y Escuela deben ser números", Alert.AlertType.ERROR);
            }
        });

        formRoot.getChildren().add(guardarBtn);

        Scene formScene = new Scene(formRoot);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void mostrarFormularioEditarDemo() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedRow == null) return;

        Stage formStage = new Stage();
        formStage.setTitle("✏️ Editar registro en " + currentTable);

        VBox formRoot = new VBox(12);
        formRoot.setStyle("-fx-padding: 25; -fx-background-color: white; -fx-background-radius: 10;");
        formRoot.setPrefWidth(380);

        Label title = new Label("Editar registro");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label idLabel = new Label("🆔 ID: " + selectedRow.get(0));
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField facultadField = new TextField(selectedRow.get(1).toString());
        facultadField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        TextField escuelaField = new TextField(selectedRow.get(2).toString());
        escuelaField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        TextField nombreField = new TextField(selectedRow.get(3).toString());
        nombreField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        formRoot.getChildren().addAll(
                title,
                idLabel,
                new Label("Facultad:"), facultadField,
                new Label("Escuela:"), escuelaField,
                new Label("Nombre:"), nombreField
        );

        Button actualizarBtn = new Button("💾 Actualizar");
        actualizarBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 6;");
        actualizarBtn.setMaxWidth(Double.MAX_VALUE);

        actualizarBtn.setOnAction(e -> {
            try {
                selectedRow.set(1, Integer.parseInt(facultadField.getText()));
                selectedRow.set(2, Integer.parseInt(escuelaField.getText()));
                selectedRow.set(3, nombreField.getText());
                dataTableView.refresh();
                showAlert("✅ Éxito", "Registro actualizado correctamente (DEMO)", Alert.AlertType.INFORMATION);
                formStage.close();
            } catch (NumberFormatException ex) {
                showAlert("❌ Error", "Facultad y Escuela deben ser números", Alert.AlertType.ERROR);
            }
        });

        formRoot.getChildren().add(actualizarBtn);

        Scene formScene = new Scene(formRoot);
        formStage.setScene(formScene);
        formStage.show();
    }

    private void confirmarEliminarDemo() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedRow == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("🗑️ Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este registro?");
        confirm.setContentText("ID: " + selectedRow.get(0) + " - " + selectedRow.get(3));
        confirm.getDialogPane().setStyle("-fx-font-size: 14px;");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataTableView.getItems().remove(selectedRow);
                showAlert("✅ Éxito", "Registro eliminado correctamente (DEMO)", Alert.AlertType.INFORMATION);
            }
        });
    }

    // ========== UTILIDADES ==========

    private VBox createVBox(String labelText, Control field) {
        VBox vbox = new VBox(5);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        vbox.getChildren().addAll(label, field);
        vbox.setFillWidth(true);
        return vbox;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-font-size: 14px;");
        alert.showAndWait();
    }
}