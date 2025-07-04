package com.spaceinvaders.strategies;

public final class MovementStrategyFactory {

    public enum StrategyType {
        LINEAR, ZIGZAG, AGGRESSIVE, CIRCULAR
    }

    public static MovementStrategy createStrategy(StrategyType type, Object... params) {
        switch (type) {
            case LINEAR:
                int speed = params.length > 0 ? (Integer) params[0] : 2;
                return new LinearMovementStrategy(speed);
            case ZIGZAG:
                int zigSpeed = params.length > 0 ? (Integer) params[0] : 3;
                int amplitude = params.length > 1 ? (Integer) params[1] : 15;
                return new ZigzagMovementStrategy(zigSpeed, amplitude);
            case AGGRESSIVE:
                int aggSpeed = params.length > 0 ? (Integer) params[0] : 2;
                var playerPos = params.length > 1 ?
                        (MovementStrategy.Position) params[1] :
                        new MovementStrategy.Position(400, 500);
                return new AggressiveMovementStrategy(aggSpeed, playerPos);
            case CIRCULAR:
                var center = params.length > 0 ?
                        (MovementStrategy.Position) params[0] :
                        new MovementStrategy.Position(400, 200);
                int radius = params.length > 1 ? (Integer) params[1] : 50;
                double angularSpeed = params.length > 2 ? (Double) params[2] : 0.05;
                return new CircularMovementStrategy(center, radius, angularSpeed);
            default:
                return new LinearMovementStrategy(2);
        }
    }
}