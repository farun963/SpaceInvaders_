package com.spaceinvaders.strategies;

/**
 * Estrategia de movimiento lineal - Enemigos básicos
 */
public final class LinearMovementStrategy implements MovementStrategy {
    private int speed;
    private int direction; // 1 para derecha, -1 para izquierda
    private boolean shouldDescend;

    public LinearMovementStrategy(int speed) {
        this.speed = Math.max(1, speed);
        this.direction = 1;
        this.shouldDescend = false;
    }

    @Override
    public Position calculateNextPosition(Position currentPosition, ScreenBounds bounds) {
        int newX = currentPosition.x() + (speed * direction);
        int newY = currentPosition.y();

        // Verificar límites horizontales
        if (newX <= bounds.minX() || newX >= bounds.maxX()) {
            direction *= -1; // Cambiar dirección
            newX = currentPosition.x(); // Mantener X actual
            newY += 30; // Descender
            shouldDescend = true;
        }

        return new Position(Math.max(bounds.minX(), Math.min(newX, bounds.maxX())), newY);
    }

    @Override
    public String getStrategyName() {
        return "Linear Movement (Speed: " + speed + ")";
    }

    public boolean isShouldDescend() {
        boolean result = shouldDescend;
        shouldDescend = false; // Reset flag
        return result;
    }
}