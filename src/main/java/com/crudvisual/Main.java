package com.crudvisual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Ruta CORRECTA: /view/ConnectionForm.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/ConnectionForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("CRUD Visual - Conexión a BD");
        stage.setScene(scene);
        stage.show();
    }
}