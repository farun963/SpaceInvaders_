package com.spaceinvaders.factories;

import com.spaceinvaders.entities.Enemy;
import com.spaceinvaders.strategies.*;

public final class EnemyFactory {

    public record EnemyConfig(
            EnemyType type,
            MovementStrategy.Position position,
            MovementStrategy movementStrategy,
            int healthMultiplier,
            int damageMultiplier
    ) {
        public EnemyConfig {
            if (healthMultiplier <= 0 || damageMultiplier <= 0) {
                throw new IllegalArgumentException("Multipliers must be positive");
            }
        }

        public EnemyConfig(EnemyType type, MovementStrategy.Position position) {
            this(type, position, null, 1, 1);
        }
    }

    public static final class EnemyBuilder {
        private EnemyType type;
        private MovementStrategy.Position position;
        private MovementStrategy movementStrategy;
        private int healthMultiplier = 1;
        private int damageMultiplier = 1;

        public EnemyBuilder type(EnemyType type) {
            this.type = type;
            return this;
        }

        public EnemyBuilder position(int x, int y) {
            this.position = new MovementStrategy.Position(x, y);
            return this;
        }

        public EnemyBuilder position(MovementStrategy.Position position) {
            this.position = position;
            return this;
        }

        public EnemyBuilder movementStrategy(MovementStrategy strategy) {
            this.movementStrategy = strategy;
            return this;
        }

        public EnemyBuilder healthMultiplier(int multiplier) {
            this.healthMultiplier = multiplier;
            return this;
        }

        public EnemyBuilder damageMultiplier(int multiplier) {
            this.damageMultiplier = multiplier;
            return this;
        }

        public Enemy build() {
            if (type == null || position == null) {
                throw new IllegalStateException("Type and position are required");
            }

            var config = new EnemyConfig(type, position, movementStrategy,
                    healthMultiplier, damageMultiplier);
            return EnemyFactory.createEnemy(config);
        }
    }

    public static Enemy createEnemy(EnemyConfig config) {
        MovementStrategy strategy = config.movementStrategy != null ?
                config.movementStrategy : createDefaultStrategy(config.type, config.position);

        int finalHealth = config.type.getHealth() * config.healthMultiplier;
        int finalDamage = config.type.getDamage() * config.damageMultiplier;

        return new Enemy(
                config.type.getName(),
                config.position.x(),
                config.position.y(),
                finalHealth,
                finalDamage,
                strategy,
                config.type.getSprite()
        );
    }

    public static Enemy createEnemy(EnemyType type, int x, int y) {
        var position = new MovementStrategy.Position(x, y);
        var config = new EnemyConfig(type, position);
        return createEnemy(config);
    }

    private static MovementStrategy createDefaultStrategy(EnemyType type, MovementStrategy.Position position) {
        return switch (type) {
            case BASIC -> MovementStrategyFactory.createStrategy(
                    MovementStrategyFactory.StrategyType.LINEAR, 2);
            case SCOUT -> MovementStrategyFactory.createStrategy(
                    MovementStrategyFactory.StrategyType.ZIGZAG, 3, 15);
            case HEAVY -> MovementStrategyFactory.createStrategy(
                    MovementStrategyFactory.StrategyType.LINEAR, 1);
            case BOSS -> MovementStrategyFactory.createStrategy(
                    MovementStrategyFactory.StrategyType.CIRCULAR, position, 50, 0.05);
            case HUNTER -> MovementStrategyFactory.createStrategy(
                    MovementStrategyFactory.StrategyType.AGGRESSIVE, 2,
                    new MovementStrategy.Position(400, 500));
        };
    }

    public static Enemy createBasicEnemy(int x, int y) {
        return createEnemy(EnemyType.BASIC, x, y);
    }

    public static Enemy createScoutEnemy(int x, int y) {
        return createEnemy(EnemyType.SCOUT, x, y);
    }

    public static Enemy createHeavyEnemy(int x, int y) {
        return createEnemy(EnemyType.HEAVY, x, y);
    }

    public static Enemy createBossEnemy(int x, int y) {
        return createEnemy(EnemyType.BOSS, x, y);
    }

    public static Enemy createHunterEnemy(int x, int y, MovementStrategy.Position playerPosition) {
        var strategy = new AggressiveMovementStrategy(2, playerPosition);
        var config = new EnemyConfig(
                EnemyType.HUNTER,
                new MovementStrategy.Position(x, y),
                strategy, 1, 1
        );
        return createEnemy(config);
    }

    public static java.util.List<Enemy> createEnemyWave(int level) {
        var enemies = new java.util.ArrayList<Enemy>();

        int baseHealth = Math.max(1, level / 2);
        int baseDamage = Math.max(1, level / 3);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                int x = 100 + col * 60;
                int y = 50 + row * 40;

                EnemyType type;
                if (row == 0) {
                    type = EnemyType.BASIC;
                } else if (row == 1) {
                    type = EnemyType.SCOUT;
                } else {
                    type = EnemyType.HEAVY;
                }

                Enemy enemy = new EnemyBuilder()
                        .type(type)
                        .position(x, y)
                        .healthMultiplier(baseHealth)
                        .damageMultiplier(baseDamage)
                        .build();

                enemies.add(enemy);
            }
        }

        if (level % 3 == 0) {
            Enemy boss = new EnemyBuilder()
                    .type(EnemyType.BOSS)
                    .position(400, 100)
                    .healthMultiplier(level)
                    .damageMultiplier(level)
                    .build();
            enemies.add(boss);
        }

        if (level > 5) {
            for (int i = 0; i < level / 5; i++) {
                Enemy hunter = createHunterEnemy(
                        200 + i * 200,
                        150,
                        new MovementStrategy.Position(400, 500)
                );
                enemies.add(hunter);
            }
        }

        return enemies;
    }

    public static EnemyBuilder builder() {
        return new EnemyBuilder();
    }

    public static String getEnemyInfo(EnemyType type) {
        return switch (type) {
            case BASIC -> "Enemigo básico con movimiento lineal predecible";
            case SCOUT -> "Explorador rápido con movimiento zigzag";
            case HEAVY -> "Enemigo pesado, lento pero resistente";
            case BOSS -> "Jefe con movimiento circular y alta resistencia";
            case HUNTER -> "Cazador agresivo que persigue al jugador";
        };
    }
}