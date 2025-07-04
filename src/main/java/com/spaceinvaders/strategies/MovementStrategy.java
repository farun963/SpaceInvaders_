package com.spaceinvaders.strategies;

/**
 * Interfaz Strategy para definir diferentes comportamientos de movimiento
 */
public interface MovementStrategy {

    /**
     * Record para encapsular la posición de manera inmutable
     */
    record Position(int x, int y) {
        public Position {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Position coordinates must be non-negative");
            }
        }

        public Position add(int deltaX, int deltaY) {
            return new Position(x + deltaX, y + deltaY);
        }

        public double distanceTo(Position other) {
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    /**
     * Record para los límites de la pantalla
     */
    record ScreenBounds(int minX, int minY, int maxX, int maxY) {
        public boolean contains(Position pos) {
            return pos.x() >= minX && pos.x() <= maxX &&
                    pos.y() >= minY && pos.y() <= maxY;
        }
    }

    /**
     * Método principal para calcular el próximo movimiento
     */
    Position calculateNextPosition(Position currentPosition, ScreenBounds bounds);

    /**
     * Método opcional para actualizar parámetros internos
     */
    default void update() {}

    /**
     * Método para obtener información sobre la estrategia
     */
    String getStrategyName();
}