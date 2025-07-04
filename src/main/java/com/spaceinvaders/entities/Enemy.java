package com.spaceinvaders.entities;

import com.spaceinvaders.strategies.MovementStrategy;

public final class Enemy extends GameEntity {
    private final int damage;
    private MovementStrategy movementStrategy;
    private final String name;
    private final MovementStrategy.ScreenBounds screenBounds;
    private long lastShotTime;
    private final double shotProbability;

    public Enemy(String name, int x, int y, int health, int damage,
                 MovementStrategy strategy, String sprite) {
        super(x, y, health, sprite);
        this.name = name;
        this.damage = damage;
        this.movementStrategy = strategy;
        this.screenBounds = new MovementStrategy.ScreenBounds(0, 0, 800, 600);
        this.lastShotTime = 0;
        this.shotProbability = 0.002;
    }

    @Override
    public void update() {
        if (alive && movementStrategy != null) {
            var newPosition = movementStrategy.calculateNextPosition(getPosition(), screenBounds);
            setPosition(newPosition.x(), newPosition.y());
            movementStrategy.update();
        }
    }

    @Override
    public void render() {
        if (alive) {
            String healthBar = "▓".repeat(Math.max(1, (int)(getHealthPercentage() * 5)));
            System.out.println(name + " " + sprite + " at (" + x + ", " + y + ") " + healthBar);
        }
    }

    public Projectile shoot() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime < 1000) {
            return null;
        }

        if (Math.random() > shotProbability) {
            return null;
        }

        lastShotTime = currentTime;
        return new Projectile(x, y + 10, 0, 3, damage, "🔻", false);
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public boolean isOffScreen() {
        return y > screenBounds.maxY() + 50 || x < screenBounds.minX() - 50 || x > screenBounds.maxX() + 50;
    }
}