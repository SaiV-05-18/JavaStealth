package com.stealthgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.util.Duration;
import javafx.scene.control.Alert;

public class App extends Application {
    private Rectangle player;
    private Rectangle enemy;
    private Rectangle exit;

    @Override
    public void start(Stage stage) {
        // Maze walls
        Rectangle wall1 = new Rectangle(200, 20, Color.GRAY);
        wall1.setX(100); wall1.setY(150);
        Rectangle wall2 = new Rectangle(20, 200, Color.GRAY);
        wall2.setX(300); wall2.setY(100);

        // Player
        player = new Rectangle(50, 50, Color.BLACK);
        player.setX(100); player.setY(100);

        // Enemy
        enemy = new Rectangle(50, 50, Color.RED);
        enemy.setX(400); enemy.setY(100);

        // Exit
        exit = new Rectangle(50, 50, Color.GREEN);
        exit.setX(500); exit.setY(350);

        Group root = new Group(wall1, wall2, player, enemy, exit);
        Scene scene = new Scene(root, 600, 400, Color.LIGHTGRAY);

        // Enemy patrol path (moves up and down)
        Timeline enemyPatrol = new Timeline(
            new KeyFrame(Duration.seconds(0.02), e -> {
                double y = enemy.getY();
                // Move enemy between y=100 and y=300
                if (enemy.getUserData() == null) enemy.setUserData("down");
                String dir = (String)enemy.getUserData();
                if ("down".equals(dir)) {
                    enemy.setY(y + 2);
                    if (enemy.getY() >= 300) enemy.setUserData("up");
                } else {
                    enemy.setY(y - 2);
                    if (enemy.getY() <= 100) enemy.setUserData("down");
                }
                checkCollision();
            })
        );
        enemyPatrol.setCycleCount(Timeline.INDEFINITE);
        enemyPatrol.play();

        scene.setOnKeyPressed(event -> {
            double x = player.getX();
            double y = player.getY();
            switch (event.getCode()) {
                case W:
                    player.setY(y - 5);
                    break;
                case S:
                    player.setY(y + 5);
                    break;
                case A:
                    player.setX(x - 5);
                    break;
                case D:
                    player.setX(x + 5);
                    break;
                default:
                    break;
            }
            checkCollision();
        });

        stage.setTitle("JavaFX Stealth Game");
        stage.setScene(scene);
        stage.show();
    }

    private void checkCollision() {
        // Player touches enemy
        if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
            showAlert("Game Over", "You were caught by the enemy!");
        }
        // Player reaches exit
        if (player.getBoundsInParent().intersects(exit.getBoundsInParent())) {
            showAlert("You Win!", "You reached the exit!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
        // Reset player position
        player.setX(100); player.setY(100);
    }

    public static void main(String[] args) {
        launch();
    }
}
