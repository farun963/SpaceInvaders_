package com.spaceinvaders.strategies;

/**
 * Estrategia de movimiento agresivo - Persigue al jugador
 */
public final class AggressiveMovementStrategy implements MovementStrategy {
    private final int speed;
    private Position playerPosition;
    private final double aggressionFactor;

    public AggressiveMovementStrategy(int speed, Position initialPlayerPosition) {
        this.speed = Math.max(1, speed);
        this.playerPosition = initialPlayerPosition;
        this.aggressionFactor = 0.8; // Factor de agresividad (0.0 - 1.0)
    }

    @Override
    public Position calculateNextPosition(Position currentPosition, ScreenBounds bounds) {
        if (playerPosition == null) {
            return currentPosition;
        }

        // Calcular vector hacia el jugador
        int deltaX = playerPosition.x() - currentPosition.x();
        int deltaY = playerPosition.y() - currentPosition.y();

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 5) { // Muy cerca del jugador
            return currentPosition;
        }

        // Normalizar y aplicar velocidad con factor de agresividad
        double moveX = (deltaX / distance) * speed * aggressionFactor;
        double moveY = (deltaY / distance) * speed * aggressionFactor;

        int newX = currentPosition.x() + (int)moveX;
        int newY = currentPosition.y() + (int)moveY;

        // Mantener dentro de límites
        newX = Math.max(bounds.minX(), Math.min(newX, bounds.maxX()));
        newY = Math.max(bounds.minY(), Math.min(newY, bounds.maxY()));

        return new Position(newX, newY);
    }

    public void updatePlayerPosition(Position newPlayerPosition) {
        this.playerPosition = newPlayerPosition;
    }

    @Override
    public String getStrategyName() {
        return "Aggressive Movement (Speed: " + speed + ", Aggression: " + aggressionFactor + ")";
    }
}