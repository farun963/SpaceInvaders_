package com.spaceinvaders.factories;

public enum EnemyType {
    BASIC("Basic Invader", 100, 10, "👾"),
    SCOUT("Scout", 80, 15, "🛸"),
    HEAVY("Heavy Invader", 200, 25, "👿"),
    BOSS("Boss", 500, 50, "👹"),
    HUNTER("Aggressive Hunter", 150, 20, "😈");

    private final String name;
    private final int health;
    private final int damage;
    private final String sprite;

    EnemyType(String name, int health, int damage, String sprite) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.sprite = sprite;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public String getSprite() { return sprite; }
}