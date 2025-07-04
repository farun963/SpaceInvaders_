package com.spaceinvaders.game;

import com.spaceinvaders.entities.*;
import com.spaceinvaders.factories.*;
import com.spaceinvaders.singletons.GameManager;
import com.spaceinvaders.strategies.MovementStrategy;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase principal del juego Space Invaders
 * Implementa los patrones Strategy, Factory y Singleton usando características de JDK 22
 */
public final class SpaceInvadersGame {

    // Records para configuración del juego
    public record GameConfig(
            int screenWidth,
            int screenHeight,
            int targetFPS,
            int frameDelayMs
    ) {
        public GameConfig {
            if (screenWidth <= 0 || screenHeight <= 0 || targetFPS <= 0) {
                throw new IllegalArgumentException("Screen dimensions and FPS must be positive");
            }
        }
    }

    public record InputCommand(String command, long timestamp) {}

    // Configuración del juego
    private static final GameConfig CONFIG = new GameConfig(800, 600, 10, 100);

    // Estado del juego
    private Player player;
    private final List<Enemy> enemies;
    private final List<Projectile> playerProjectiles;
    private final List<Projectile> enemyProjectiles;
    private final GameManager gameManager;
    private final Scanner scanner;
    private final Queue<InputCommand> inputQueue;
    private boolean gameLoop;
    private long lastUpdateTime;
    private int frameCount;

    public SpaceInvadersGame() {
        // Usar el patrón Singleton para obtener el GameManager
        this.gameManager = GameManager.getInstance();
        this.player = new Player(CONFIG.screenWidth() / 2, CONFIG.screenHeight() - 50);
        this.enemies = new CopyOnWriteArrayList<>();
        this.playerProjectiles = new CopyOnWriteArrayList<>();
        this.enemyProjectiles = new CopyOnWriteArrayList<>();
        this.scanner = new Scanner(System.in);
        this.inputQueue = new LinkedList<>();
        this.gameLoop = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.frameCount = 0;

        initializeEnemies();
    }

    /**
     * Inicializa los enemigos usando el patrón Factory
     */
    private void initializeEnemies() {
        System.out.println("🎮 Inicializando enemigos usando Factory Pattern...");

        // Usar la factory para crear una oleada completa
        var waveEnemies = EnemyFactory.createEnemyWave(gameManager.getLevel());
        enemies.addAll(waveEnemies);

        System.out.println("✅ " + enemies.size() + " enemigos creados para el nivel " + gameManager.getLevel());

        // Mostrar información de tipos de enemigos
        var enemyTypes = enemies.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        enemy -> enemy.getName(),
                        java.util.stream.Collectors.counting()
                ));

        enemyTypes.forEach((type, count) ->
                System.out.println("  - " + type + ": " + count + " unidades"));
    }

    /**
     * Método principal del juego
     */
    public void startGame() {
        displayWelcomeMessage();
        gameManager.startGame();

        // Thread para capturar entrada asíncrona
        Thread inputThread = new Thread(this::handleAsyncInput);
        inputThread.setDaemon(true);
        inputThread.start();

        // Bucle principal del juego
        while (gameLoop && gameManager.isGameRunning()) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastUpdateTime >= CONFIG.frameDelayMs()) {
                processInputQueue();
                updateGame();
                checkCollisions();
                checkGameConditions();

                if (frameCount % 5 == 0) { // Renderizar cada 5 frames
                    displayGameState();
                }

                lastUpdateTime = currentTime;
                frameCount++;
            }

            try {
                Thread.sleep(10); // Pequeña pausa para no sobrecargar CPU
            } catch (InterruptedException e) {
                System.out.println("Juego interrumpido");
                break;
            }
        }

        displayGameOverMessage();
    }

    /**
     * Maneja la entrada de usuario de manera asíncrona
     */
    private void handleAsyncInput() {
        while (gameLoop) {
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().toLowerCase().trim();
                    synchronized (inputQueue) {
                        inputQueue.offer(new InputCommand(input, System.currentTimeMillis()));
                    }
                }
                Thread.sleep(50);
            } catch (Exception e) {
                break;
            }
        }
    }

    /**
     * Procesa la cola de entrada
     */
    private void processInputQueue() {
        synchronized (inputQueue) {
            while (!inputQueue.isEmpty()) {
                var command = inputQueue.poll();
                processInput(command.command());
            }
        }
    }

    /**
     * Muestra el mensaje de bienvenida
     */
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 ¡BIENVENIDO A SPACE INVADERS! 🚀");
        System.out.println("=".repeat(60));
        System.out.println("📋 CONTROLES:");
        System.out.println("   A/a = Mover izquierda    D/d = Mover derecha");
        System.out.println("   W/w = Mover arriba       S/s = Mover abajo");
        System.out.println("   SPACE/space = Disparar   Q/q = Salir");
        System.out.println("");
        System.out.println("🎯 PATRONES IMPLEMENTADOS:");
        System.out.println("   ✓ Singleton Pattern (GameManager)");
        System.out.println("   ✓ Strategy Pattern (MovementStrategy)");
        System.out.println("   ✓ Factory Pattern (EnemyFactory)");
        System.out.println("");
        System.out.println("🎮 ¡Presiona cualquier tecla para comenzar!");
        System.out.println("=".repeat(60));
    }

    /**
     * Muestra el estado actual del juego
     */
    private void displayGameState() {
        // Limpiar consola (simulado)
        System.out.println("\n".repeat(3));
        System.out.println("=".repeat(80));

        // Información del juego
        System.out.println(gameManager.getGameStatus());
        System.out.println("Frame: " + frameCount + " | Enemigos: " + enemies.size() +
                " | Proyectiles: P=" + playerProjectiles.size() + " E=" + enemyProjectiles.size());

        // Estado del jugador
        player.render();

        // Mostrar algunos enemigos (para no saturar)
        if (!enemies.isEmpty()) {
            System.out.println("\n--- ENEMIGOS ACTIVOS ---");
            enemies.stream()
                    .limit(5)
                    .forEach(Enemy::render);

            if (enemies.size() > 5) {
                System.out.println("... y " + (enemies.size() - 5) + " enemigos más");
            }
        }

        // Información de proyectiles
        if (!playerProjectiles.isEmpty() || !enemyProjectiles.isEmpty()) {
            System.out.println("\n--- PROYECTILES ---");
            if (!playerProjectiles.isEmpty()) {
                System.out.println("🔸 Jugador: " + playerProjectiles.size() + " activos");
            }
            if (!enemyProjectiles.isEmpty()) {
                System.out.println("🔻 Enemigos: " + enemyProjectiles.size() + " activos");
            }
        }

        System.out.println("\n💡 Comando: ");
    }

    /**
     * Procesa la entrada del usuario
     */
    private void processInput(String input) {
        switch (input) {
            case "a":
                if (player.moveLeft()) {
                    System.out.println("👈 Jugador se mueve a la izquierda");
                }
                break;
            case "d":
                if (player.moveRight()) {
                    System.out.println("👉 Jugador se mueve a la derecha");
                }
                break;
            case "w":
                if (player.moveUp()) {
                    System.out.println("👆 Jugador se mueve arriba");
                }
                break;
            case "s":
                if (player.moveDown()) {
                    System.out.println("👇 Jugador se mueve abajo");
                }
                break;
            case "space":
            case " ":
                Projectile shot = player.shoot();
                if (shot != null) {
                    playerProjectiles.add(shot);
                    System.out.println("💥 ¡Disparo del jugador!");
                } else {
                    System.out.println("⏳ Recargando... (" + player.getRemainingCooldown() + "ms)");
                }
                break;
            case "q":
                gameLoop = false;
                gameManager.endGame();
                System.out.println("👋 Saliendo del juego...");
                break;
            case "stats":
                displayDetailedStats();
                break;
            case "help":
                displayHelp();
                break;
            default:
                System.out.println("⚠️ Comando no válido: '" + input + "'. Usa 'help' para ver comandos");
                break;
        }
    }

    /**
     * Actualiza el estado del juego
     */
    private void updateGame() {
        // Actualizar jugador
        player.update();

        // Actualizar enemigos y sus disparos
        for (Enemy enemy : enemies) {
            enemy.update();

            // Los enemigos disparan ocasionalmente
            Projectile enemyShot = enemy.shoot();
            if (enemyShot != null) {
                enemyProjectiles.add(enemyShot);
            }
        }

        // Actualizar proyectiles del jugador
        playerProjectiles.removeIf(projectile -> {
            projectile.update();
            return !projectile.isAlive();
        });

        // Actualizar proyectiles enemigos
        enemyProjectiles.removeIf(projectile -> {
            projectile.update();
            return !projectile.isAlive();
        });

        // Remover enemigos fuera de pantalla
        enemies.removeIf(enemy -> !enemy.isAlive() || enemy.isOffScreen());
    }

    /**
     * Verifica las colisiones usando streams
     */
    private void checkCollisions() {
        // Colisiones de proyectiles del jugador con enemigos
        var playerProjectilesToRemove = new ArrayList<Projectile>();
        var enemiesToRemove = new ArrayList<Enemy>();

        for (Projectile projectile : playerProjectiles) {
            for (Enemy enemy : enemies) {
                if (projectile.checkCollision(enemy) && projectile.canCollideWith(enemy)) {
                    enemy.takeDamage(projectile.getDamage());
                    playerProjectilesToRemove.add(projectile);

                    if (!enemy.isAlive()) {
                        enemiesToRemove.add(enemy);
                        int points = calculatePoints(enemy);
                        gameManager.addScore(points);
                        System.out.println("💥 ¡" + enemy.getName() + " eliminado! +" + points + " puntos");
                    }
                    break;
                }
            }
        }

        playerProjectiles.removeAll(playerProjectilesToRemove);
        enemies.removeAll(enemiesToRemove);

        // Colisiones de proyectiles enemigos con el jugador
        var enemyProjectilesToRemove = new ArrayList<Projectile>();

        for (Projectile projectile : enemyProjectiles) {
            if (projectile.checkCollision(player) && projectile.canCollideWith(player)) {
                player.takeDamage(projectile.getDamage());
                enemyProjectilesToRemove.add(projectile);

                if (!player.isAlive()) {
                    gameManager.loseLife();
                    if (gameManager.canContinue()) {
                        // Respawn del jugador
                        player = new Player(CONFIG.screenWidth() / 2, CONFIG.screenHeight() - 50);
                        System.out.println("💔 ¡Jugador impactado! Vida perdida. Respawn...");
                    }
                } else {
                    System.out.println("💔 ¡Jugador impactado! Salud: " + player.getHealth());
                }
                break;
            }
        }

        enemyProjectiles.removeAll(enemyProjectilesToRemove);
    }

    /**
     * Calcula puntos basados en el tipo de enemigo
     */
    private int calculatePoints(Enemy enemy) {
        String enemyName = enemy.getName();
        if ("Basic Invader".equals(enemyName)) {
            return 100;
        } else if ("Scout".equals(enemyName)) {
            return 150;
        } else if ("Heavy Invader".equals(enemyName)) {
            return 200;
        } else if ("Aggressive Hunter".equals(enemyName)) {
            return 300;
        } else if ("Boss".equals(enemyName)) {
            return 1000;
        } else {
            return 50;
        }
    }

    /**
     * Verifica las condiciones de fin de juego
     */
    private void checkGameConditions() {
        // Victoria: todos los enemigos eliminados
        if (enemies.isEmpty()) {
            gameManager.nextLevel();
            System.out.println("🎉 ¡Nivel completado! Preparando siguiente nivel...");

            // Reiniciar enemigos para el siguiente nivel
            initializeEnemies();
        }

        // Derrota: enemigos llegan al jugador
        for (Enemy enemy : enemies) {
            if (enemy.getY() > CONFIG.screenHeight() - 100) {
                gameManager.endGame();
                System.out.println("💀 ¡Los enemigos han llegado a la Tierra!");
                gameLoop = false;
                break;
            }
        }

        // Verificar si el juego puede continuar
        if (!gameManager.canContinue()) {
            gameLoop = false;
        }
    }

    /**
     * Muestra estadísticas detalladas del juego
     */
    private void displayDetailedStats() {
        var stats = gameManager.getGameStats();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 ESTADÍSTICAS DETALLADAS");
        System.out.println("=".repeat(50));
        System.out.println("🎯 Puntuación: " + stats.totalScore());
        System.out.println("🏆 Nivel actual: " + stats.currentLevel());
        System.out.println("❤️ Vidas restantes: " + stats.livesRemaining());
        System.out.println("🎮 Estado: " + stats.status());
        System.out.println("🕐 Frames renderizados: " + frameCount);
        System.out.println("👾 Enemigos activos: " + enemies.size());
        System.out.println("🔸 Proyectiles jugador: " + playerProjectiles.size());
        System.out.println("🔻 Proyectiles enemigos: " + enemyProjectiles.size());
        System.out.println("🎯 Salud del jugador: " + player.getHealth() + "/" + player.getMaxHealth());
        System.out.println("=".repeat(50));
    }

    /**
     * Muestra la ayuda del juego
     */
    private void displayHelp() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("❓ AYUDA - SPACE INVADERS");
        System.out.println("=".repeat(50));
        System.out.println("📋 CONTROLES:");
        System.out.println("  a/A     = Mover izquierda");
        System.out.println("  d/D     = Mover derecha");
        System.out.println("  w/W     = Mover arriba");
        System.out.println("  s/S     = Mover abajo");
        System.out.println("  space   = Disparar");
        System.out.println("  q/Q     = Salir del juego");
        System.out.println("  stats   = Ver estadísticas detalladas");
        System.out.println("  help    = Mostrar esta ayuda");
        System.out.println("");
        System.out.println("🎯 OBJETIVO:");
        System.out.println("  - Elimina todos los enemigos para avanzar de nivel");
        System.out.println("  - Evita que los enemigos lleguen abajo");
        System.out.println("  - Esquiva los proyectiles enemigos");
        System.out.println("");
        System.out.println("👾 TIPOS DE ENEMIGOS:");
        System.out.println("  👾 Basic Invader  = 100 puntos");
        System.out.println("  🛸 Scout          = 150 puntos");
        System.out.println("  👿 Heavy Invader  = 200 puntos");
        System.out.println("  😈 Hunter         = 300 puntos");
        System.out.println("  👹 Boss           = 1000 puntos");
        System.out.println("=".repeat(50));
    }

    /**
     * Muestra el mensaje de fin de juego
     */
    private void displayGameOverMessage() {
        System.out.println("\n" + "=".repeat(60));
        if (gameManager.isGameOver()) {
            System.out.println("💀 GAME OVER 💀");
        } else {
            System.out.println("👋 JUEGO TERMINADO 👋");
        }
        System.out.println("=".repeat(60));

        var finalStats = gameManager.getGameStats();
        System.out.println("🏆 PUNTUACIÓN FINAL: " + finalStats.totalScore());
        System.out.println("🎯 NIVEL ALCANZADO: " + finalStats.currentLevel());
        System.out.println("⏱️ FRAMES TOTALES: " + frameCount);

        // Mostrar ranking simple
        String ranking;
        int score = finalStats.totalScore();
        if (score >= 5000) {
            ranking = "🏆 ¡MAESTRO DEL ESPACIO!";
        } else if (score >= 3000) {
            ranking = "🥈 ¡COMANDANTE ESPACIAL!";
        } else if (score >= 1000) {
            ranking = "🥉 ¡PILOTO EXPERIMENTADO!";
        } else if (score >= 500) {
            ranking = "🎖️ Soldado Espacial";
        } else {
            ranking = "🌟 Novato";
        }

        System.out.println("🎖️ RANGO: " + ranking);
        System.out.println("");
        System.out.println("¡Gracias por jugar Space Invaders!");
        System.out.println("=".repeat(60));
    }

    /**
     * Método principal para ejecutar el juego
     */
    public static void main(String[] args) {
        try {
            SpaceInvadersGame game = new SpaceInvadersGame();
            game.startGame();

            // Demostrar el patrón Singleton
            System.out.println("\n🔍 DEMOSTRACIÓN PATRÓN SINGLETON:");
            GameManager gm1 = GameManager.getInstance();
            GameManager gm2 = GameManager.getInstance();
            System.out.println("¿Misma instancia? " + (gm1 == gm2));
            System.out.println("Estado final: " + gm1.getGameStatus());

        } catch (Exception e) {
            System.err.println("❌ Error al ejecutar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
}