package com.stealthgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PatrolEnemy {
    private final Rectangle rect;
    private final double[][] waypoints;
    private final double speed;
    private int idx;
    private double x, y;
    private final double w = 30, h = 30;
    private static final double EPS = 3.0;

    public PatrolEnemy(Color color, double[][] waypoints, double speed) {
        this.rect = new Rectangle(w, h, color);
        this.waypoints = waypoints;
        this.speed = speed;
        reset();
    }

    public void update(double dt, CollisionChecker checker) {
        if (waypoints.length < 2) return;
        double tx = waypoints[idx][0], ty = waypoints[idx][1];
        double dx = tx - x, dy = ty - y;
        double dist = Math.hypot(dx, dy);

        if (dist < EPS) { idx = (idx + 1) % waypoints.length; return; }

        double vx = dx / dist * speed * dt;
        double vy = dy / dist * speed * dt;

        if (checker.canMove(x + vx, y + vy, w, h)) { x += vx; y += vy; }
        else if (checker.canMove(x + vx, y, w, h)) x += vx;
        else if (checker.canMove(x, y + vy, w, h)) y += vy;
        else idx = (idx + 1) % waypoints.length; // avoid stuck

        rect.setX(x); rect.setY(y);
    }

    public void reset() {
        x = waypoints[0][0];
        y = waypoints[0][1];
        idx = (waypoints.length > 1) ? 1 : 0;
        rect.setX(x); rect.setY(y);
    }

    public Rectangle getRect() { return rect; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getW() { return w; }
    public double getH() { return h; }

    @FunctionalInterface
    public interface CollisionChecker {
        boolean canMove(double x, double y, double w, double h);
    }
}
