package com.crudvisual;

import com.crudvisual.model.DatabaseConnector;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private DatabaseConnector dbConnector;
    private String currentTable = "";

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
        connectButton.setOnAction(e -> conectarBD());

        // Seleccionar tabla del ListView
        tableListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        tableField.setText(newVal);
                        cargarDatosTabla(newVal);
                    }
                }
        );

        // ACCIONES CRUD
        readBtn.setOnAction(e -> cargarDatosTabla(tableField.getText()));

        createBtn.setOnAction(e -> mostrarFormularioCrear());

        updateBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                mostrarFormularioEditar();
            } else {
                showAlert("Seleccionar", "Debe seleccionar un registro para actualizar", Alert.AlertType.WARNING);
            }
        });

        deleteBtn.setOnAction(e -> {
            if (dataTableView.getSelectionModel().getSelectedItem() != null) {
                confirmarEliminar();
            } else {
                showAlert("Seleccionar", "Debe seleccionar un registro para eliminar", Alert.AlertType.WARNING);
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

    // =============================================
    // CONEXIÓN Y CARGA DE DATOS
    // =============================================

    private void conectarBD() {
        String host = hostField.getText();
        String port = portField.getText();
        String database = databaseField.getText();
        String user = userField.getText();
        String password = passwordField.getText();

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        dbConnector = new DatabaseConnector();
        boolean conectado = dbConnector.connect(url, user, password);

        if (conectado) {
            statusLabel.setText("● Conectado a " + database);
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

            cargarTablasBD();

            tableListView.setVisible(true);
            crudBox.setVisible(true);
            crudBox.setDisable(false);

            if (!tableField.getText().isEmpty()) {
                cargarDatosTabla(tableField.getText());
            }

            showAlert("Éxito", "Conectado a " + database, Alert.AlertType.INFORMATION);
        } else {
            statusLabel.setText("● Error de conexión");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            showAlert("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void cargarTablasBD() {
        try {
            var tablas = dbConnector.getTables();
            tableListView.setItems(FXCollections.observableArrayList(tablas));
        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar las tablas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDatosTabla(String tabla) {
        currentTable = tabla;
        try {
            var rs = dbConnector.getAllRecords(tabla);
            var metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            dataTableView.getColumns().clear();

            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                String columnName = metaData.getColumnName(i);

                TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(columnName);
                column.setCellValueFactory(cellData -> {
                    ObservableList<Object> row = cellData.getValue();
                    return new SimpleObjectProperty<>(row.get(colIndex));
                });

                dataTableView.getColumns().add(column);
            }

            ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

            while (rs.next()) {
                ObservableList<Object> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            dataTableView.setItems(data);
            dataTableView.setVisible(true);

        } catch (SQLException e) {
            showAlert("Error", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // =============================================
    // CREATE - Insertar nuevo registro
    // =============================================

    private void mostrarFormularioCrear() {
        Stage formStage = new Stage();
        formStage.setTitle("Crear nuevo registro en " + currentTable);

        VBox formRoot = new VBox(10);
        formRoot.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        try {
            var columns = dbConnector.getColumns(currentTable);
            Map<String, TextField> fields = new HashMap<>();

            for (String column : columns) {
                if (!column.toLowerCase().contains("id") && !column.toLowerCase().contains("serial")) {
                    VBox fieldBox = new VBox(5);
                    fieldBox.getChildren().add(new Label(column + ":"));
                    TextField textField = new TextField();
                    textField.setPromptText("Ingrese " + column);
                    fieldBox.getChildren().add(textField);
                    formRoot.getChildren().add(fieldBox);
                    fields.put(column, textField);
                }
            }

            Button guardarBtn = new Button("Guardar");
            guardarBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
            guardarBtn.setMaxWidth(Double.MAX_VALUE);

            guardarBtn.setOnAction(e -> {
                Map<String, Object> valores = new HashMap<>();
                for (Map.Entry<String, TextField> entry : fields.entrySet()) {
                    String valor = entry.getValue().getText();
                    if (!valor.isEmpty()) {
                        valores.put(entry.getKey(), valor);
                    }
                }

                try {
                    dbConnector.insertRecord(currentTable, valores);
                    showAlert("Éxito", "Registro insertado correctamente", Alert.AlertType.INFORMATION);
                    formStage.close();
                    cargarDatosTabla(currentTable);
                } catch (SQLException ex) {
                    showAlert("Error", "No se pudo insertar: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            formRoot.getChildren().add(guardarBtn);

            Scene formScene = new Scene(formRoot, 400, 500);
            formStage.setScene(formScene);
            formStage.show();

        } catch (SQLException e) {
            showAlert("Error", "No se pudieron obtener las columnas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // =============================================
    // UPDATE - Editar registro existente
    // =============================================

    private void mostrarFormularioEditar() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();

        Stage formStage = new Stage();
        formStage.setTitle("Editar registro en " + currentTable);

        VBox formRoot = new VBox(10);
        formRoot.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        try {
            var columns = dbConnector.getColumns(currentTable);
            Map<String, TextField> fields = new HashMap<>();
            String idColumn = null;
            Object idValue = null;

            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                Object value = selectedRow.get(i);

                if (column.toLowerCase().contains("id") || column.toLowerCase().contains("serial")) {
                    idColumn = column;
                    idValue = value;
                } else {
                    VBox fieldBox = new VBox(5);
                    fieldBox.getChildren().add(new Label(column + ":"));
                    TextField textField = new TextField(value != null ? value.toString() : "");
                    fieldBox.getChildren().add(textField);
                    formRoot.getChildren().add(fieldBox);
                    fields.put(column, textField);
                }
            }

            Label idLabel = new Label("ID: " + idValue);
            idLabel.setStyle("-fx-font-weight: bold;");
            formRoot.getChildren().add(0, idLabel);

            Button actualizarBtn = new Button("Actualizar");
            actualizarBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
            actualizarBtn.setMaxWidth(Double.MAX_VALUE);

            String finalIdColumn = idColumn;
            Object finalIdValue = idValue;

            actualizarBtn.setOnAction(e -> {
                Map<String, Object> valores = new HashMap<>();
                for (Map.Entry<String, TextField> entry : fields.entrySet()) {
                    String valor = entry.getValue().getText();
                    if (!valor.isEmpty()) {
                        valores.put(entry.getKey(), valor);
                    }
                }

                try {
                    String whereClause = finalIdColumn + " = " + finalIdValue;
                    dbConnector.updateRecord(currentTable, valores, whereClause);
                    showAlert("Éxito", "Registro actualizado correctamente", Alert.AlertType.INFORMATION);
                    formStage.close();
                    cargarDatosTabla(currentTable);
                } catch (SQLException ex) {
                    showAlert("Error", "No se pudo actualizar: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            formRoot.getChildren().add(actualizarBtn);

            Scene formScene = new Scene(formRoot, 400, 500);
            formStage.setScene(formScene);
            formStage.show();

        } catch (SQLException e) {
            showAlert("Error", "No se pudieron obtener las columnas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // =============================================
    // DELETE - Eliminar registro
    // =============================================

    private void confirmarEliminar() {
        ObservableList<Object> selectedRow = dataTableView.getSelectionModel().getSelectedItem();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este registro?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    var columns = dbConnector.getColumns(currentTable);
                    String idColumn = null;
                    Object idValue = null;

                    for (int i = 0; i < columns.size(); i++) {
                        String column = columns.get(i);
                        if (column.toLowerCase().contains("id") || column.toLowerCase().contains("serial")) {
                            idColumn = column;
                            idValue = selectedRow.get(i);
                            break;
                        }
                    }

                    if (idColumn != null) {
                        String whereClause = idColumn + " = " + idValue;
                        dbConnector.deleteRecord(currentTable, whereClause);
                        showAlert("Éxito", "Registro eliminado correctamente", Alert.AlertType.INFORMATION);
                        cargarDatosTabla(currentTable);
                    }
                } catch (SQLException e) {
                    showAlert("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // =============================================
    // UTILIDADES
    // =============================================

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