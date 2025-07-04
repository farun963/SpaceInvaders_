package com.spaceinvaders.entities;

import com.spaceinvaders.strategies.MovementStrategy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Clase base abstracta para todas las entidades del juego
 */
public abstract class GameEntity {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    protected final long id;
    protected int x, y;
    protected int health;
    protected int maxHealth;
    protected boolean alive;
    protected final String sprite;
    protected long creationTime;

    protected GameEntity(int x, int y, int health, String sprite) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.x = x;
        this.y = y;
        this.health = health;
        this.maxHealth = health;
        this.alive = true;
        this.sprite = sprite;
        this.creationTime = System.currentTimeMillis();
    }

    public abstract void update();
    public abstract void render();

    public long getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return alive; }
    public String getSprite() { return sprite; }
    public long getCreationTime() { return creationTime; }

    public void setPosition(int x, int y) {
        this.x = Math.max(0, x);
        this.y = Math.max(0, y);
    }

    public MovementStrategy.Position getPosition() {
        return new MovementStrategy.Position(x, y);
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }

        this.health = Math.max(0, this.health - damage);
        if (this.health <= 0) {
            this.alive = false;
        }
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }

        this.health = Math.min(maxHealth, this.health + amount);
    }

    public double getHealthPercentage() {
        return maxHealth > 0 ? (double) health / maxHealth : 0.0;
    }

    public boolean checkCollision(GameEntity other) {
        if (other == null || !other.isAlive() || !this.isAlive()) {
            return false;
        }

        int dx = this.x - other.x;
        int dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 25;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameEntity other && this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", pos=(" + x + "," + y + "), health=" + health + "}";
    }
}