package com.spaceinvaders.singletons;

/**
 * Patrón Singleton para gestionar el estado global del juego
 * Utiliza características modernas de JDK 22 incluyendo Records
 */
public final class GameManager {
    private static volatile GameManager instance;

    // Record para encapsular el estado del juego (JDK 22 feature)
    public record GameState(int score, int lives, int level, boolean gameRunning, boolean gameOver) {

        // Compact constructor con validación
        public GameState {
            if (lives < 0) {
                throw new IllegalArgumentException("Lives cannot be negative");
            }
            if (level < 1) {
                throw new IllegalArgumentException("Level must be positive");
            }
            if (score < 0) {
                throw new IllegalArgumentException("Score cannot be negative");
            }
        }

        // Métodos inmutables para actualizar el estado
        public GameState addScore(int points) {
            return new GameState(score + points, lives, level, gameRunning, gameOver);
        }

        public GameState loseLife() {
            int newLives = Math.max(0, lives - 1);
            boolean newGameOver = newLives <= 0;
            boolean newGameRunning = !newGameOver && gameRunning;
            return new GameState(score, newLives, level, newGameRunning, newGameOver);
        }

        public GameState nextLevel() {
            return new GameState(score, lives, level + 1, gameRunning, gameOver);
        }

        public GameState startGame() {
            return new GameState(score, lives, level, true, false);
        }

        public GameState endGame() {
            return new GameState(score, lives, level, false, true);
        }

        public GameState resetGame() {
            return new GameState(0, 3, 1, false, false);
        }
    }

    private GameState currentState;

    // Constructor privado para evitar instanciación externa
    private GameManager() {
        this.currentState = new GameState(0, 3, 1, false, false);
    }

    /**
     * Implementación thread-safe del Singleton usando double-checked locking
     * Optimizado para JDK 22
     */
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    // Métodos públicos que utilizan el estado inmutable
    public void startGame() {
        this.currentState = currentState.startGame();
        System.out.println("🎮 ¡Juego iniciado! Nivel: " + currentState.level());
    }

    public void endGame() {
        this.currentState = currentState.endGame();
        System.out.println("💀 Game Over! Puntuación final: " + currentState.score());
    }

    public void addScore(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        this.currentState = currentState.addScore(points);
        System.out.println("🎯 Puntuación actual: " + currentState.score());
    }

    public void loseLife() {
        var previousLives = currentState.lives();
        this.currentState = currentState.loseLife();

        // Verificación simple de game over
        if (currentState.gameOver()) {
            System.out.println("💀 ¡Sin vidas restantes! Game Over");
        } else {
            System.out.println("💔 Vidas restantes: " + currentState.lives());
        }
    }

    public void nextLevel() {
        this.currentState = currentState.nextLevel();
        System.out.println("🎉 ¡Nivel completado! Avanzando al nivel: " + currentState.level());
    }

    public void resetGame() {
        this.currentState = currentState.resetGame();
        System.out.println("🔄 Juego reiniciado");
    }

    // Getters usando delegación al record
    public int getScore() {
        return currentState.score();
    }

    public int getLives() {
        return currentState.lives();
    }

    public int getLevel() {
        return currentState.level();
    }

    public boolean isGameRunning() {
        return currentState.gameRunning();
    }

    public boolean isGameOver() {
        return currentState.gameOver();
    }

    public GameState getGameState() {
        return currentState;
    }

    /**
     * Método para obtener información completa del estado usando if-else tradicional
     */
    public String getGameStatus() {
        if (currentState.gameRunning() && !currentState.gameOver()) {
            return "🎮 Jugando - Nivel: " + currentState.level() +
                    ", Vidas: " + currentState.lives() +
                    ", Puntos: " + currentState.score();
        } else if (!currentState.gameRunning() && currentState.gameOver()) {
            return "💀 Game Over - Puntuación final: " + currentState.score();
        } else if (!currentState.gameRunning() && !currentState.gameOver()) {
            return "⏸️ Pausa - Nivel: " + currentState.level() +
                    ", Vidas: " + currentState.lives() +
                    ", Puntos: " + currentState.score();
        } else {
            return "❓ Estado desconocido";
        }
    }

    /**
     * Método para verificar si el juego puede continuar
     */
    public boolean canContinue() {
        return currentState.lives() > 0 && !currentState.gameOver();
    }

    /**
     * Método para obtener estadísticas del juego
     */
    public record GameStats(int totalScore, int currentLevel, int livesRemaining,
                            String status, long sessionTime) {}

    public GameStats getGameStats() {
        return new GameStats(
                currentState.score(),
                currentState.level(),
                currentState.lives(),
                getGameStatus(),
                System.currentTimeMillis() // Simplificado para el ejemplo
        );
    }

    @Override
    public String toString() {
        return "GameManager{" +
                "state=" + currentState +
                ", status='" + getGameStatus() + '\'' +
                '}';
    }
}