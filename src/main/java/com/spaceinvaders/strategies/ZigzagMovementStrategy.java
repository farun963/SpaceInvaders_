package com.spaceinvaders.strategies;

/**
 * Estrategia de movimiento zigzag - Enemigos exploradores
 */
public final class ZigzagMovementStrategy implements MovementStrategy {
    private final int speed;
    private final int amplitude;
    private int time;
    private final double frequency;

    public ZigzagMovementStrategy(int speed, int amplitude) {
        this.speed = Math.max(1, speed);
        this.amplitude = Math.max(5, amplitude);
        this.frequency = 0.1;
        this.time = 0;
    }

    @Override
    public Position calculateNextPosition(Position currentPosition, ScreenBounds bounds) {
        time++;
        int newX = currentPosition.x() + speed;
        int oscillation = (int)(amplitude * Math.sin(time * frequency));
        int newY = currentPosition.y() + oscillation;

        // Mantener dentro de los límites
        newX = Math.max(bounds.minX(), Math.min(newX, bounds.maxX()));
        newY = Math.max(bounds.minY(), Math.min(newY, bounds.maxY()));

        return new Position(newX, newY);
    }

    @Override
    public String getStrategyName() {
        return "Zigzag Movement (Speed: " + speed + ", Amplitude: " + amplitude + ")";
    }
}