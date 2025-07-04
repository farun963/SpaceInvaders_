package com.spaceinvaders.strategies;

/**
 * Estrategia de movimiento circular - Jefes y enemigos especiales
 */
public final class CircularMovementStrategy implements MovementStrategy {
    private final Position center;
    private final int radius;
    private double currentAngle;
    private final double angularSpeed;

    public CircularMovementStrategy(Position center, int radius, double angularSpeed) {
        this.center = center;
        this.radius = Math.max(10, radius);
        this.angularSpeed = angularSpeed;
        this.currentAngle = 0.0;
    }

    @Override
    public Position calculateNextPosition(Position currentPosition, ScreenBounds bounds) {
        currentAngle += angularSpeed;

        // Normalizar ángulo
        if (currentAngle >= 2 * Math.PI) {
            currentAngle -= 2 * Math.PI;
        }

        int newX = center.x() + (int)(radius * Math.cos(currentAngle));
        int newY = center.y() + (int)(radius * Math.sin(currentAngle));

        // Mantener dentro de límites
        newX = Math.max(bounds.minX(), Math.min(newX, bounds.maxX()));
        newY = Math.max(bounds.minY(), Math.min(newY, bounds.maxY()));

        return new Position(newX, newY);
    }

    @Override
    public String getStrategyName() {
        return "Circular Movement (Radius: " + radius + ", Speed: " + angularSpeed + ")";
    }

    public double getCurrentAngle() {
        return currentAngle;
    }
}