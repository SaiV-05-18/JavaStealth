package com.stealthgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Group;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Rectangle player = new Rectangle(50, 50, Color.BLUE);
        player.setX(100);
        player.setY(100);

        Group root = new Group(player);
        Scene scene = new Scene(root, 600, 400, Color.LIGHTGRAY);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    player.setY(player.getY() - 5);
                    break;
                case S:
                    player.setY(player.getY() + 5);
                    break;
                case A:
                    player.setX(player.getX() - 5);
                    break;
                case D:
                    player.setX(player.getX() + 5);
                    break;
                default:
                    break;
            }
        });

        stage.setTitle("JavaFX Stealth Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
