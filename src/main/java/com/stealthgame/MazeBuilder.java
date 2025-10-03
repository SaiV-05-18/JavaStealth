package com.stealthgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class MazeBuilder {
    public static List<Rectangle> createWalls() {
        return List.of(
                new Rectangle(0, 0, 600, 20),      // top
                new Rectangle(0, 0, 20, 400),      // left
                new Rectangle(0, 380, 600, 20),    // bottom
                new Rectangle(580, 0, 20, 400),    // right
                new Rectangle(60, 60, 200, 20),
                new Rectangle(260, 60, 20, 200),
                new Rectangle(280, 240, 200, 20),
                new Rectangle(160, 160, 20, 120),
                new Rectangle(120, 300, 180, 20),
                new Rectangle(420, 80, 20, 120),
                new Rectangle(420, 200, 120, 20)
        ).stream().peek(r -> r.setFill(Color.GRAY)).toList();
    }
}
