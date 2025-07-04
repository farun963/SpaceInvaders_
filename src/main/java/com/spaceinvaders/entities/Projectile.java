package com.spaceinvaders.entities;

import com.spaceinvaders.strategies.MovementStrategy;

public final class Projectile extends GameEntity {
    private final int velocityX, velocityY;
    private final int damage;
    private final boolean fromPlayer;
    private final MovementStrategy.ScreenBounds screenBounds;

    public Projectile(int x, int y, int velocityX, int velocityY, int damage, String sprite, boolean fromPlayer) {
        super(x, y, 1, sprite);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        this.fromPlayer = fromPlayer;
        this.screenBounds = new MovementStrategy.ScreenBounds(-50, -50, 850, 650);
    }

    @Override
    public void update() {
        if (!alive) return;

        setPosition(x + velocityX, y + velocityY);

        if (!screenBounds.contains(getPosition())) {
            alive = false;
        }
    }

    @Override
    public void render() {
        if (alive) {
            System.out.println("Projectile " + sprite + " at (" + x + ", " + y + ")");
        }
    }

    public int getDamage() {
        return damage;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public boolean canCollideWith(GameEntity entity) {
        if (entity instanceof Projectile) {
            return false;
        }

        if (entity instanceof Player && fromPlayer) {
            return false;
        }

        if (entity instanceof Enemy && !fromPlayer) {
            return false;
        }

        return true;
    }
}