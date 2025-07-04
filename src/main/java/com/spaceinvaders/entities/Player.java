package com.spaceinvaders.entities;

import com.spaceinvaders.strategies.MovementStrategy;

public final class Player extends GameEntity {
    private final int speed;
    private int score;
    private final MovementStrategy.ScreenBounds screenBounds;
    private long lastShotTime;
    private final long shotCooldown;

    public Player(int x, int y) {
        super(x, y, 100, "🚀");
        this.speed = 5;
        this.score = 0;
        this.screenBounds = new MovementStrategy.ScreenBounds(0, 0, 800, 600);
        this.lastShotTime = 0;
        this.shotCooldown = 250;
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        String healthBar = "❤".repeat(Math.max(0, health / 20));
        System.out.println("Player " + sprite + " at (" + x + ", " + y + ") " + healthBar + " Score: " + score);
    }

    public boolean moveLeft() {
        if (x > screenBounds.minX()) {
            setPosition(x - speed, y);
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (x < screenBounds.maxX()) {
            setPosition(x + speed, y);
            return true;
        }
        return false;
    }

    public boolean moveUp() {
        if (y > screenBounds.minY()) {
            setPosition(x, y - speed);
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (y < screenBounds.maxY() - 50) {
            setPosition(x, y + speed);
            return true;
        }
        return false;
    }

    public Projectile shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < shotCooldown) {
            return null;
        }

        lastShotTime = currentTime;
        return new Projectile(x, y - 10, 0, -8, 25, "🔸", true);
    }

    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime >= shotCooldown;
    }

    public long getRemainingCooldown() {
        long remaining = shotCooldown - (System.currentTimeMillis() - lastShotTime);
        return Math.max(0, remaining);
    }
}