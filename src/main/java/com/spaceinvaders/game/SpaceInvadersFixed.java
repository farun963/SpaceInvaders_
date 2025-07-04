package com.spaceinvaders.game;

import com.spaceinvaders.entities.*;
import com.spaceinvaders.factories.*;
import com.spaceinvaders.singletons.GameManager;
import java.util.*;

public class SpaceInvadersFixed {
    private Player player;
    private List<Enemy> enemies;
    private List<Projectile> playerProjectiles;
    private List<Projectile> enemyProjectiles;
    private GameManager gameManager;
    private Scanner scanner;

    public SpaceInvadersFixed() {
        this.gameManager = GameManager.getInstance();
        this.player = new Player(400, 550);
        this.enemies = new ArrayList<>();
        this.playerProjectiles = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        initializeEnemies();
    }

    private void initializeEnemies() {
        System.out.println("🎮 Creando enemigos...");
        var waveEnemies = EnemyFactory.createEnemyWave(gameManager.getLevel());
        enemies.addAll(waveEnemies);
        System.out.println("✅ " + enemies.size() + " enemigos creados");
    }

    public void startGame() {
        displayWelcome();
        gameManager.startGame();

        // BUCLE PRINCIPAL SINCRÓNICO (SIN THREADS)
        while (gameManager.isGameRunning() && !enemies.isEmpty()) {
            displayGameState();

            // ESPERAR ENTRADA DEL USUARIO
            System.out.print("💡 Comando (a/d/w/s/space/q/help): ");
            String input = scanner.nextLine().toLowerCase().trim();

            // PROCESAR COMANDO
            if (!processInput(input)) {
                break; // Salir si usuario presiona 'q'
            }

            // ACTUALIZAR JUEGO DESPUÉS DEL COMANDO
            updateGame();
            checkCollisions();
            checkGameConditions();

            // PEQUEÑA PAUSA PARA LEER
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }

        displayGameOver();
    }

    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 ¡SPACE INVADERS - VERSIÓN FIJA! 🚀");
        System.out.println("=".repeat(60));
        System.out.println("📋 CONTROLES:");
        System.out.println("   a = Mover izquierda");
        System.out.println("   d = Mover derecha");
        System.out.println("   w = Mover arriba");
        System.out.println("   s = Mover abajo");
        System.out.println("   space = Disparar");
        System.out.println("   q = Salir");
        System.out.println("   help = Ayuda");
        System.out.println("\n🎯 OBJETIVO: Elimina todos los enemigos");
        System.out.println("=".repeat(60));
    }

    private void displayGameState() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎮 NIVEL: " + gameManager.getLevel() +
                " | ❤️ VIDAS: " + gameManager.getLives() +
                " | 🎯 PUNTOS: " + gameManager.getScore());
        System.out.println("👾 Enemigos restantes: " + enemies.size());

        // Mostrar jugador
        String healthBar = "❤".repeat(Math.max(0, player.getHealth() / 20));
        System.out.println("🚀 Jugador en X=" + player.getX() + " Y=" + player.getY() +
                " " + healthBar + " (Salud: " + player.getHealth() + "/100)");

        // Mostrar algunos enemigos
        System.out.println("\n--- ENEMIGOS VISIBLES ---");
        for (int i = 0; i < Math.min(5, enemies.size()); i++) {
            Enemy enemy = enemies.get(i);
            String enemyHealthBar = "▓".repeat(Math.max(1, (int)(enemy.getHealthPercentage() * 5)));
            System.out.println((i+1) + ". " + enemy.getName() + " " + enemy.getSprite() +
                    " en X=" + enemy.getX() + " Y=" + enemy.getY() + " " + enemyHealthBar);
        }
        if (enemies.size() > 5) {
            System.out.println("... y " + (enemies.size() - 5) + " enemigos más");
        }

        // Mostrar proyectiles
        if (!playerProjectiles.isEmpty()) {
            System.out.println("🔸 Tus proyectiles: " + playerProjectiles.size() + " activos");
        }
        if (!enemyProjectiles.isEmpty()) {
            System.out.println("🔻 Proyectiles enemigos: " + enemyProjectiles.size() + " activos");
        }
    }

    private boolean processInput(String input) {
        switch (input) {
            case "a":
                if (player.moveLeft()) {
                    System.out.println("👈 Te moviste a la izquierda (X=" + player.getX() + ")");
                } else {
                    System.out.println("⚠️ No puedes moverte más a la izquierda");
                }
                break;

            case "d":
                if (player.moveRight()) {
                    System.out.println("👉 Te moviste a la derecha (X=" + player.getX() + ")");
                } else {
                    System.out.println("⚠️ No puedes moverte más a la derecha");
                }
                break;

            case "w":
                if (player.moveUp()) {
                    System.out.println("👆 Te moviste hacia arriba (Y=" + player.getY() + ")");
                } else {
                    System.out.println("⚠️ No puedes moverte más arriba");
                }
                break;

            case "s":
                if (player.moveDown()) {
                    System.out.println("👇 Te moviste hacia abajo (Y=" + player.getY() + ")");
                } else {
                    System.out.println("⚠️ No puedes moverte más abajo");
                }
                break;

            case "space":
                Projectile shot = player.shoot();
                if (shot != null) {
                    playerProjectiles.add(shot);
                    System.out.println("💥 ¡DISPARASTE! Proyectil lanzado");
                } else {
                    System.out.println("⏳ Recargando... (cooldown: " + player.getRemainingCooldown() + "ms)");
                }
                break;

            case "q":
                System.out.println("👋 Saliendo del juego...");
                gameManager.endGame();
                return false;

            case "help":
                displayHelp();
                break;

            case "stats":
                displayStats();
                break;

            default:
                System.out.println("⚠️ Comando no válido: '" + input + "'");
                System.out.println("💡 Usa: a, d, w, s, space, q, help");
        }
        return true;
    }

    private void displayHelp() {
        System.out.println("\n📖 AYUDA:");
        System.out.println("a/d = Mover horizontalmente para apuntar a enemigos");
        System.out.println("w/s = Mover verticalmente para esquivar");
        System.out.println("space = Disparar (hay cooldown de 250ms)");
        System.out.println("q = Salir del juego");
        System.out.println("stats = Ver estadísticas detalladas");
    }

    private void displayStats() {
        System.out.println("\n📊 ESTADÍSTICAS:");
        System.out.println("🎯 Puntuación: " + gameManager.getScore());
        System.out.println("🏆 Nivel: " + gameManager.getLevel());
        System.out.println("❤️ Vidas: " + gameManager.getLives());
        System.out.println("👾 Enemigos restantes: " + enemies.size());
        System.out.println("🔸 Tus proyectiles: " + playerProjectiles.size());
        System.out.println("🔻 Proyectiles enemigos: " + enemyProjectiles.size());
        System.out.println("💚 Tu salud: " + player.getHealth() + "/100");
    }

    private void updateGame() {
        // Actualizar enemigos
        for (Enemy enemy : enemies) {
            enemy.update();

            // Enemigos disparan ocasionalmente
            if (Math.random() < 0.1) { // 10% de probabilidad cada turno
                Projectile enemyShot = enemy.shoot();
                if (enemyShot != null) {
                    enemyProjectiles.add(enemyShot);
                    System.out.println("🔻 " + enemy.getName() + " te disparó!");
                }
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
    }

    private void checkCollisions() {
        // Tus proyectiles vs enemigos
        var projectilesToRemove = new ArrayList<Projectile>();
        var enemiesToRemove = new ArrayList<Enemy>();

        for (Projectile projectile : playerProjectiles) {
            for (Enemy enemy : enemies) {
                if (projectile.checkCollision(enemy)) {
                    enemy.takeDamage(projectile.getDamage());
                    projectilesToRemove.add(projectile);

                    if (!enemy.isAlive()) {
                        enemiesToRemove.add(enemy);
                        int points = calculatePoints(enemy);
                        gameManager.addScore(points);
                        System.out.println("💥 ¡" + enemy.getName() + " ELIMINADO! +" + points + " puntos");
                    } else {
                        System.out.println("🎯 ¡Impacto! " + enemy.getName() +
                                " tiene " + enemy.getHealth() + " HP restante");
                    }
                    break;
                }
            }
        }

        playerProjectiles.removeAll(projectilesToRemove);
        enemies.removeAll(enemiesToRemove);

        // Proyectiles enemigos vs ti
        var enemyProjectilesToRemove = new ArrayList<Projectile>();

        for (Projectile projectile : enemyProjectiles) {
            if (projectile.checkCollision(player)) {
                player.takeDamage(projectile.getDamage());
                enemyProjectilesToRemove.add(projectile);

                if (!player.isAlive()) {
                    gameManager.loseLife();
                    System.out.println("💀 ¡IMPACTO MORTAL! Perdiste una vida");

                    if (gameManager.getLives() > 0) {
                        player = new Player(400, 550); // Respawn
                        System.out.println("🆘 Respawn en posición inicial");
                    }
                } else {
                    System.out.println("💔 ¡Te impactaron! Salud: " + player.getHealth() + "/100");
                }
                break;
            }
        }

        enemyProjectiles.removeAll(enemyProjectilesToRemove);
    }

    private int calculatePoints(Enemy enemy) {
        String enemyName = enemy.getName();
        if ("Basic Invader".equals(enemyName)) return 100;
        if ("Scout".equals(enemyName)) return 150;
        if ("Heavy Invader".equals(enemyName)) return 200;
        if ("Aggressive Hunter".equals(enemyName)) return 300;
        if ("Boss".equals(enemyName)) return 1000;
        return 50;
    }

    private void checkGameConditions() {
        // Victoria: todos los enemigos eliminados
        if (enemies.isEmpty()) {
            gameManager.nextLevel();
            System.out.println("🎉 ¡NIVEL COMPLETADO!");
            System.out.println("🚀 Preparando nivel " + gameManager.getLevel() + "...");
            initializeEnemies();
        }

        // Derrota: sin vidas
        if (gameManager.getLives() <= 0) {
            gameManager.endGame();
            System.out.println("💀 ¡GAME OVER! Sin vidas restantes");
        }

        // Derrota: enemigos llegaron abajo
        for (Enemy enemy : enemies) {
            if (enemy.getY() > 500) {
                gameManager.endGame();
                System.out.println("💀 ¡GAME OVER! Los enemigos llegaron a la Tierra");
                break;
            }
        }
    }

    private void displayGameOver() {
        System.out.println("\n" + "=".repeat(60));
        if (gameManager.isGameOver()) {
            System.out.println("💀 GAME OVER 💀");
        } else {
            System.out.println("👋 JUEGO TERMINADO 👋");
        }
        System.out.println("=".repeat(60));
        System.out.println("🏆 PUNTUACIÓN FINAL: " + gameManager.getScore());
        System.out.println("🎯 NIVEL ALCANZADO: " + gameManager.getLevel());

        int score = gameManager.getScore();
        String ranking;
        if (score >= 5000) ranking = "🏆 ¡MAESTRO DEL ESPACIO!";
        else if (score >= 3000) ranking = "🥈 ¡COMANDANTE ESPACIAL!";
        else if (score >= 1000) ranking = "🥉 ¡PILOTO EXPERIMENTADO!";
        else if (score >= 500) ranking = "🎖️ Soldado Espacial";
        else ranking = "🌟 Novato";

        System.out.println("🎖️ RANGO: " + ranking);
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        try {
            SpaceInvadersFixed game = new SpaceInvadersFixed();
            game.startGame();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}