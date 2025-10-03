package com.stealthgame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.ArrayList;

/**
 * Stealth Game (Optimized & Refactored)
 * - Player moves with WASD
 * - Enemies patrol via waypoints
 * - Player must reach exit without touching enemies
 */
public class App extends Application {

    private Rectangle player;
    private List<PatrolEnemy> enemies;
    private Rectangle exit;
    private List<Rectangle> walls;

    // Cached wall primitive arrays
    private double[] wallX, wallY, wallW, wallH;

    // Player movement state
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    // Game loop
    private AnimationTimer gameLoop;
    private long lastUpdate = -1;

    @Override
    public void start(Stage stage) {
        // --- Build Maze Walls ---
        walls = MazeBuilder.createWalls();

        // Cache wall primitives for fast collision
        cacheWalls();

        // --- Player ---
        player = new Rectangle(Config.PLAYER_W, Config.PLAYER_H, Color.BLACK);
        resetPlayer();

        // --- Exit ---
        exit = new Rectangle(40, 40, Color.GREEN);
        exit.setX(Config.SCENE_W - 80);
        exit.setY(Config.SCENE_H - 80);

        // --- Enemies ---
        enemies = new ArrayList<>();
        enemies.add(new PatrolEnemy(Color.RED,
                new double[][]{{320, 70}, {320, 300}}, 80));
        enemies.add(new PatrolEnemy(Color.DARKRED,
                new double[][]{{120, 260}, {240, 260}, {360, 260}, {240, 260}}, 100));
        enemies.add(new PatrolEnemy(Color.ORANGE,
                new double[][]{{480, 200}, {520, 200}, {520, 320}, {480, 320}}, 90));

        // --- Scene Setup ---
        Group root = new Group();
        root.getChildren().addAll(walls);
        root.getChildren().add(exit);
        enemies.forEach(e -> root.getChildren().add(e.getRect()));
        root.getChildren().add(player);

        Scene scene = new Scene(root, Config.SCENE_W, Config.SCENE_H, Color.LIGHTGRAY);
        setInputHandlers(scene);

        // --- Game Loop ---
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate < 0) { lastUpdate = now; return; }
                double dt = (now - lastUpdate) / 1e9;
                lastUpdate = now;

                updatePlayer(dt);
                enemies.forEach(e -> e.update(dt, App.this::canMove));

                checkCollision();
            }
        };

        resetGame();
        gameLoop.start();

        stage.setTitle("Stealth Game (Optimized)");
        stage.setScene(scene);
        stage.show();
    }

    /** Cache wall primitives */
    private void cacheWalls() {
        int wc = walls.size();
        wallX = new double[wc];
        wallY = new double[wc];
        wallW = new double[wc];
        wallH = new double[wc];
        for (int i = 0; i < wc; i++) {
            Rectangle r = walls.get(i);
            wallX[i] = r.getX();
            wallY[i] = r.getY();
            wallW[i] = r.getWidth();
            wallH[i] = r.getHeight();
        }
    }

    /** Handles player movement */
    private void updatePlayer(double dt) {
        Point2D dir = Point2D.ZERO;
        if (upPressed) dir = dir.add(0, -1);
        if (downPressed) dir = dir.add(0, 1);
        if (leftPressed) dir = dir.add(-1, 0);
        if (rightPressed) dir = dir.add(1, 0);

        if (dir.magnitude() > 0) {
            dir = dir.normalize().multiply(Config.PLAYER_SPEED * dt);
            double newX = player.getX() + dir.getX();
            double newY = player.getY() + dir.getY();

            if (canMove(newX, newY, Config.PLAYER_W, Config.PLAYER_H)) {
                player.setX(newX);
                player.setY(newY);
            } else {
                // Slide along walls
                if (canMove(player.getX() + dir.getX(), player.getY(), Config.PLAYER_W, Config.PLAYER_H))
                    player.setX(player.getX() + dir.getX());
                if (canMove(player.getX(), player.getY() + dir.getY(), Config.PLAYER_W, Config.PLAYER_H))
                    player.setY(player.getY() + dir.getY());
            }
        }
    }

    /** Collision checks */
    private void checkCollision() {
        for (PatrolEnemy e : enemies) {
            if (intersects(player.getX(), player.getY(), Config.PLAYER_W, Config.PLAYER_H,
                    e.getX(), e.getY(), e.getW(), e.getH())) {
                gameOver("Game Over", "You were caught!");
                return;
            }
        }
        if (intersects(player.getX(), player.getY(), Config.PLAYER_W, Config.PLAYER_H,
                exit.getX(), exit.getY(), exit.getWidth(), exit.getHeight())) {
            gameOver("Victory!", "You escaped!");
        }
    }

    /** Axis-aligned rect intersection */
    private static boolean intersects(double ax, double ay, double aw, double ah,
                                      double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    /** Can move check against walls */
    private boolean canMove(double x, double y, double w, double h) {
        if (x < 0 || y < 0 || x + w > Config.SCENE_W || y + h > Config.SCENE_H) return false;
        for (int i = 0; i < wallX.length; i++) {
            if (intersects(x, y, w, h, wallX[i], wallY[i], wallW[i], wallH[i])) return false;
        }
        return true;
    }

    /** Game Over */
    private void gameOver(String title, String message) {
        gameLoop.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
        resetGame();
        gameLoop.start();
    }

    /** Reset game state */
    private void resetGame() {
        resetPlayer();
        enemies.forEach(PatrolEnemy::reset);
    }

    private void resetPlayer() {
        player.setX(30);
        player.setY(30);
        upPressed = downPressed = leftPressed = rightPressed = false;
    }

    /** Input handlers */
    private void setInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> upPressed = true;
                case S -> downPressed = true;
                case A -> leftPressed = true;
                case D -> rightPressed = true;
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> upPressed = false;
                case S -> downPressed = false;
                case A -> leftPressed = false;
                case D -> rightPressed = false;
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
