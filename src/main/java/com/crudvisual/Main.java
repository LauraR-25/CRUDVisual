package com.crudvisual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // ¡ESTE MÉTODO DEBE EXISTIR!
    public static void main(String[] args) {
        launch(args);  // Esta línea es clave
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/ConnectionForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("CRUD Visual - Conexión a BD");
        stage.setScene(scene);
        stage.show();
    }
}