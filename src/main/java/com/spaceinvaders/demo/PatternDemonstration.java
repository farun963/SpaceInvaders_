package com.spaceinvaders.demo;

import com.spaceinvaders.entities.*;
import com.spaceinvaders.factories.*;
import com.spaceinvaders.singletons.GameManager;
import com.spaceinvaders.strategies.*;

/**
 * Clase para demostrar individualmente cada patrón de diseño
 * implementado en el juego Space Invaders
 */
public final class PatternDemonstration {

    /**
     * Demuestra el patrón Singleton
     */
    public static void demonstrateSingleton() {
        System.out.println("\n🔹 DEMOSTRACIÓN DEL PATRÓN SINGLETON");
        System.out.println("=".repeat(40));

        // Crear múltiples referencias al GameManager
        GameManager gm1 = GameManager.getInstance();
        GameManager gm2 = GameManager.getInstance();
        GameManager gm3 = GameManager.getInstance();

        System.out.println("GameManager 1: " + gm1.hashCode());
        System.out.println("GameManager 2: " + gm2.hashCode());
        System.out.println("GameManager 3: " + gm3.hashCode());

        // Verificar que son la misma instancia
        System.out.println("¿gm1 == gm2? " + (gm1 == gm2));
        System.out.println("¿gm2 == gm3? " + (gm2 == gm3));
        System.out.println("¿gm1 == gm3? " + (gm1 == gm3));

        // Demostrar estado compartido
        System.out.println("\n📊 Probando estado compartido:");
        gm1.startGame();
        gm1.addScore(100);
        System.out.println("Score desde gm1: " + gm1.getScore());
        System.out.println("Score desde gm2: " + gm2.getScore());
        System.out.println("Score desde gm3: " + gm3.getScore());

        gm2.addScore(200);
        System.out.println("Después de agregar 200 puntos desde gm2:");
        System.out.println("Score desde gm1: " + gm1.getScore());

        // Probar record patterns
        var gameState = gm1.getGameState();
        System.out.println("Estado del juego: " + gameState);
        System.out.println("Estado formateado: " + gm1.getGameStatus());

        gm1.resetGame();
        System.out.println("✅ Patrón Singleton demostrado correctamente");
    }

    /**
     * Demuestra el patrón Strategy
     */
    public static void demonstrateStrategy() {
        System.out.println("\n🔹 DEMOSTRACIÓN DEL PATRÓN STRATEGY");
        System.out.println("=".repeat(40));

        // Crear posición inicial y límites
        var initialPosition = new MovementStrategy.Position(400, 200);
        var screenBounds = new MovementStrategy.ScreenBounds(0, 0, 800, 600);

        // Crear diferentes estrategias
        System.out.println("🎯 Creando diferentes estrategias de movimiento:");

        // 1. Estrategia Lineal
        MovementStrategy linearStrategy = new LinearMovementStrategy(3);
        System.out.println("1. " + linearStrategy.getStrategyName());

        // 2. Estrategia Zigzag
        MovementStrategy zigzagStrategy = new ZigzagMovementStrategy(2, 20);
        System.out.println("2. " + zigzagStrategy.getStrategyName());

        // 3. Estrategia Circular
        MovementStrategy circularStrategy = new CircularMovementStrategy(
                new MovementStrategy.Position(400, 300), 50, 0.1);
        System.out.println("3. " + circularStrategy.getStrategyName());

        // 4. Estrategia Agresiva
        MovementStrategy aggressiveStrategy = new AggressiveMovementStrategy(
                2, new MovementStrategy.Position(400, 500));
        System.out.println("4. " + aggressiveStrategy.getStrategyName());

        // Simular movimientos
        System.out.println("\n🎮 Simulando movimientos:");
        MovementStrategy[] strategies = {linearStrategy, zigzagStrategy, circularStrategy, aggressiveStrategy};
        String[] names = {"Linear", "Zigzag", "Circular", "Aggressive"};

        for (int i = 0; i < strategies.length; i++) {
            System.out.println("\n" + names[i] + " Strategy:");
            var position = initialPosition;

            for (int step = 0; step < 5; step++) {
                position = strategies[i].calculateNextPosition(position, screenBounds);
                System.out.println("  Paso " + (step + 1) + ": (" + position.x() + ", " + position.y() + ")");
                strategies[i].update();
            }
        }

        // Demostrar factory de estrategias
        System.out.println("\n🏭 Usando MovementStrategyFactory:");
        var factoryLinear = MovementStrategyFactory.createStrategy(
                MovementStrategyFactory.StrategyType.LINEAR, 5);
        var factoryZigzag = MovementStrategyFactory.createStrategy(
                MovementStrategyFactory.StrategyType.ZIGZAG, 3, 25);

        System.out.println("Factory Linear: " + factoryLinear.getStrategyName());
        System.out.println("Factory Zigzag: " + factoryZigzag.getStrategyName());

        System.out.println("✅ Patrón Strategy demostrado correctamente");
    }

    /**
     * Demuestra el patrón Factory
     */
    public static void demonstrateFactory() {
        System.out.println("\n🔹 DEMOSTRACIÓN DEL PATRÓN FACTORY");
        System.out.println("=".repeat(40));

        System.out.println("🏭 Creando enemigos usando EnemyFactory:");

        // 1. Factory methods directos
        System.out.println("\n1. Métodos factory directos:");
        Enemy basicEnemy = EnemyFactory.createBasicEnemy(100, 100);
        Enemy scoutEnemy = EnemyFactory.createScoutEnemy(200, 100);
        Enemy heavyEnemy = EnemyFactory.createHeavyEnemy(300, 100);
        Enemy bossEnemy = EnemyFactory.createBossEnemy(400, 100);

        Enemy[] directEnemies = {basicEnemy, scoutEnemy, heavyEnemy, bossEnemy};
        for (Enemy enemy : directEnemies) {
            System.out.println("  - " + enemy.getName() + " (HP: " + enemy.getHealth() +
                    ", Damage: " + enemy.getDamage() + ") " + enemy.getSprite());
        }

        // 2. Factory con enum
        System.out.println("\n2. Factory usando enum:");
        for (EnemyType type : EnemyType.values()) {
            Enemy enemy = EnemyFactory.createEnemy(type, 100, 150);
            System.out.println("  - " + type + ": " + enemy.getName() +
                    " (HP: " + enemy.getHealth() + ", Damage: " + enemy.getDamage() + ")");
            System.out.println("    Info: " + EnemyFactory.getEnemyInfo(type));
        }

        // 3. Builder Pattern
        System.out.println("\n3. Usando Builder Pattern:");
        Enemy customEnemy1 = EnemyFactory.builder()
                .type(EnemyType.BASIC)
                .position(150, 200)
                .healthMultiplier(2)
                .damageMultiplier(3)
                .build();

        Enemy customEnemy2 = EnemyFactory.builder()
                .type(EnemyType.BOSS)
                .position(400, 50)
                .healthMultiplier(5)
                .damageMultiplier(2)
                .movementStrategy(new CircularMovementStrategy(
                        new MovementStrategy.Position(400, 50), 75, 0.05))
                .build();

        System.out.println("  - Custom Basic: HP=" + customEnemy1.getHealth() +
                " (original: " + EnemyType.BASIC.getHealth() + ")");
        System.out.println("  - Custom Boss: HP=" + customEnemy2.getHealth() +
                " (original: " + EnemyType.BOSS.getHealth() + ")");

        // 4. Factory para oleadas
        System.out.println("\n4. Creando oleada completa:");
        var waveEnemies = EnemyFactory.createEnemyWave(3);

        var enemyCount = waveEnemies.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Enemy::getName,
                        java.util.stream.Collectors.counting()));

        System.out.println("  Oleada nivel 3 creada:");
        enemyCount.forEach((name, count) ->
                System.out.println("    - " + name + ": " + count + " unidades"));
        System.out.println("  Total de enemigos: " + waveEnemies.size());

        // 5. Demostrar polimorfismo
        System.out.println("\n5. Demostrando polimorfismo:");
        System.out.println("  Simulando movimientos de diferentes enemigos:");
        Enemy[] testEnemies = {basicEnemy, scoutEnemy, customEnemy2};

        for (Enemy enemy : testEnemies) {
            System.out.println("  " + enemy.getName() + ":");
            System.out.println("    Estrategia: " + enemy.getMovementStrategy().getStrategyName());

            // Simular algunos movimientos
            for (int i = 0; i < 3; i++) {
                enemy.update();
                System.out.println("    Posición " + (i+1) + ": (" + enemy.getX() + ", " + enemy.getY() + ")");
            }
        }

        System.out.println("✅ Patrón Factory demostrado correctamente");
    }

    /**
     * Demuestra la integración de todos los patrones
     */
    public static void demonstrateIntegration() {
        System.out.println("\n🔹 DEMOSTRACIÓN DE INTEGRACIÓN DE PATRONES");
        System.out.println("=".repeat(50));

        // Singleton para gestión del juego
        GameManager gameManager = GameManager.getInstance();
        gameManager.resetGame();
        gameManager.startGame();

        // Factory para crear jugador y enemigos
        Player player = new Player(400, 500);
        var enemies = EnemyFactory.createEnemyWave(1);

        System.out.println("🎮 Simulación de juego integrando todos los patrones:");
        System.out.println("  Jugador creado en posición: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("  Enemigos creados: " + enemies.size());

        // Simular algunos frames del juego
        for (int frame = 1; frame <= 5; frame++) {
            System.out.println("\n  📺 Frame " + frame + ":");

            // Actualizar enemigos (Strategy pattern en acción)
            for (Enemy enemy : enemies) {
                enemy.update();
                if (frame == 1) {
                    System.out.println("    " + enemy.getName() +
                            " usando " + enemy.getMovementStrategy().getStrategyName());
                }
            }

            // Simular disparos
            if (frame % 2 == 0) {
                Projectile playerShot = player.shoot();
                if (playerShot != null) {
                    System.out.println("    🔸 Jugador dispara!");
                }

                // Simular disparo enemigo
                if (!enemies.isEmpty()) {
                    Projectile enemyShot = enemies.get(0).shoot();
                    if (enemyShot != null) {
                        System.out.println("    🔻 " + enemies.get(0).getName() + " dispara!");
                    }
                }
            }

            // Simular puntuación (Singleton mantiene estado)
            if (frame == 3) {
                gameManager.addScore(150);
                System.out.println("    🎯 ¡Enemigo eliminado! " + gameManager.getGameStatus());
            }
        }

        System.out.println("\n📊 Estado final:");
        System.out.println("  " + gameManager.getGameStatus());
        System.out.println("  Enemigos restantes: " + enemies.size());

        System.out.println("✅ Integración de patrones demostrada correctamente");
    }

    /**
     * Método principal para ejecutar todas las demostraciones
     */
    public static void main(String[] args) {
        System.out.println("🚀 DEMOSTRACIÓN DE PATRONES DE DISEÑO");
        System.out.println("Space Invaders - JDK 22 + Maven");
        System.out.println("=".repeat(60));

        try {
            demonstrateSingleton();
            demonstrateStrategy();
            demonstrateFactory();
            demonstrateIntegration();

            System.out.println("\n🎉 ¡TODAS LAS DEMOSTRACIONES COMPLETADAS!");
            System.out.println("Los patrones Singleton, Strategy y Factory han sido");
            System.out.println("implementados y demostrados exitosamente.");

        } catch (Exception e) {
            System.err.println("❌ Error durante la demostración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}