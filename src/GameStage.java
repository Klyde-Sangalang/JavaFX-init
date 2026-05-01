
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameStage {

    // Boss Configuration (Editable Constants)
    private static final int BOSS_MAX_HEALTH = 50;
    private static final int MAX_BOSS_MINIONS = 30;
    private static final int BOSS_SUMMON_INTERVAL_FULL = 300; // 10 seconds at 50 FPS
    private static final int BOSS_SUMMON_INTERVAL_HALF = 150; // 5 seconds at 50 FPS

    private double shipX = 480; // Initial X position (center)
    private double shipY = 270; // Initial Y position (center)
    private double shipSpeed = 5.0;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Minion> minions = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private boolean spacePressedLastFrame = false;
    private int enemySpawnCounter = 0;
    private Random random = new Random();
    private boolean gameOver = false;
    private boolean playerHit = false;
    private Explosion playerExplosion = null;
    private int enemyKillCount = 45;
    private boolean bossSpawned = false;
    private Boss boss = null;
    private int screenFlashCounter = 0;
    private boolean screenFlashing = false;
    private int killsWhenBossDied = 0; // Tracks kill count when boss died
    private static final long INITIAL_TIME_MILLIS = 1 * 60 * 1000L;
    private static final long TIME_BONUS_PER_ENEMY_MILLIS = 2 * 1000L;
    private long remainingTimeMillis;
    private long lastUpdateTimeMillis;

    private class Projectile {
        ImageView view;
        double x, y;
        double speedY = -10.0;

        Projectile(ImageView view, double x, double y) {
            this.view = view;
            this.x = x;
            this.y = y;
        }
    }

    private class Enemy {
        ImageView view;
        double x, y;
        double speedY = 3.0;
        double speedX = 0;
        double direction = 1.0; // 1 for right, -1 for left
        int changeDirectionCounter = 0;
        int hits = 0; // Track number of hits
        int enemyType; // 1 or 2
        Image originalImage;
        Image hit1Image;
        Image hit2Image;

        Enemy(ImageView view, double x, double y, int type, Image original, Image h1, Image h2) {
            this.view = view;
            this.x = x;
            this.y = y;
            this.changeDirectionCounter = random.nextInt(40) + 20;
            this.enemyType = type;
            this.originalImage = original;
            this.hit1Image = h1;
            this.hit2Image = h2;
        }

        void takeHit() {
            hits++;
            if (hits == 1) {
                view.setImage(hit1Image);
            } else if (hits == 2) {
                view.setImage(hit2Image);
            }
        }
    }

    private class Explosion {
        ImageView view;
        double x, y;
        int frameIndex = 0;
        int frameCounter = 0;
        Image[] frames;

        Explosion(Image[] explosionFrames, double x, double y) {
            this.frames = explosionFrames;
            this.x = x;
            this.y = y;
            this.view = new ImageView(frames[0]);
            this.view.setFitWidth(40);
            this.view.setFitHeight(40);
            this.view.setPreserveRatio(true);
            this.view.setLayoutX(x);
            this.view.setLayoutY(y);
        }

        boolean updateFrame() {
            frameCounter++;
            if (frameCounter >= 10) { // Show each frame for 10 iterations
                frameCounter = 0;
                frameIndex++;
                if (frameIndex < frames.length) {
                    view.setImage(frames[frameIndex]);
                    return false; // Animation still playing
                }
                return true; // Animation complete
            }
            return false;
        }
    }

    private class Boss extends Enemy {
        int health = BOSS_MAX_HEALTH;
        int summonCounter = 0;
        int minionCount = 0;
        Image bossHitImage;
        int bossSize = 120; // Boss collision size
        int hitFrameCounter = 0; // Counter for showing hit frame

        Boss(ImageView view, double x, double y, Image hitImage) {
            super(view, x, y, 0, null, null, null);
            this.speedY = 0; // Boss doesn't move vertically
            this.direction = 1.0; // Start moving right
            this.changeDirectionCounter = random.nextInt(40) + 20;
            this.bossHitImage = hitImage;
        }

        void takeHit() {
            health--;
            if (health > 0) {
                hitFrameCounter = 10; // Show hit frame for 10 frames (~200ms)
                view.setImage(bossHitImage);
            }
        }

        void updateHitFrame() {
            if (hitFrameCounter > 0) {
                hitFrameCounter--;
                if (hitFrameCounter == 0) {
                    view.setImage(originalImage); // Restore original image
                }
            }
        }

        boolean shouldSummon() {
            summonCounter++;
            int interval = health > BOSS_MAX_HEALTH / 2 ? BOSS_SUMMON_INTERVAL_FULL : BOSS_SUMMON_INTERVAL_HALF;
            if (summonCounter >= interval && minionCount < MAX_BOSS_MINIONS) {
                summonCounter = 0;
                return true;
            }
            return false;
        }
    }

    private class Minion {
        ImageView view;
        double x, y;
        double speedY = 0;
        int delayCounter = 30; // 30 frames delay before moving

        Minion(ImageView view, double x, double y) {
            this.view = view;
            this.x = x;
            this.y = y;
        }

        void update() {
            if (delayCounter > 0) {
                delayCounter--;
            } else {
                speedY = 3.0; // Start moving down after delay
                y += speedY;
            }
        }
    }

    public void setStage(Stage stage) {

        // Create the game area
        Pane gameArea = new Pane();
        gameArea.setStyle("-fx-background-color: #1a1a1a;");

        // Load and create spaceship
        try {
            Image shipImage = new Image("file:assets/Mobs/SpaceshipComprog.png");
            Image projectileImage = new Image("file:assets/projectiles/fire1.png");
            Image enemy1Image = new Image("file:assets/Mobs/enemy1.png");
            Image enemy2Image = new Image("file:assets/Mobs/enemy2.png");
            Image enemy1Hit1Image = new Image("file:assets/Mobs/enemy1-hit1.png");
            Image enemy1Hit2Image = new Image("file:assets/Mobs/enemy1-hit2.png");
            Image enemy2Hit1Image = new Image("file:assets/Mobs/enemy2-hit1.png");
            Image enemy2Hit2Image = new Image("file:assets/Mobs/enemy2-hit2.png");
            Image bossImage = new Image("file:assets/BOSS/boss.png");
            Image bossHitImage = new Image("file:assets/BOSS/boss-hitted.png");
            Image[] explosionFrames = {
                    new Image("file:assets/explosion/explosion1.png"),
                    new Image("file:assets/explosion/explosion2.png"),
                    new Image("file:assets/explosion/explosion3.png")
            };

            ImageView spaceship = new ImageView(shipImage);
            spaceship.setFitWidth(50);
            spaceship.setFitHeight(50);
            spaceship.setPreserveRatio(true);
            spaceship.setLayoutX(shipX);
            spaceship.setLayoutY(shipY);

            gameArea.getChildren().add(spaceship);

            // Load Exit button image
            Image exitImg = new Image("file:assets/buttons/Exit.png");
            ImageView exitView = new ImageView(exitImg);
            exitView.setFitWidth(60);
            exitView.setFitHeight(60);
            exitView.setPreserveRatio(true);

            Label timerLabel = new Label(formatTime(INITIAL_TIME_MILLIS));
            timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
            timerLabel.setLayoutX(860); // near right edge (960 width)
            timerLabel.setLayoutY(500); // near bottom (540 height)

            gameArea.getChildren().add(timerLabel);

            Button backButton = new Button();
            backButton.setGraphic(exitView);
            backButton.setStyle("-fx-background-color: transparent;");
            backButton.toFront();

            // Create "You Lose" label
            Label youLoseLabel = new Label("YOU LOSE");
            youLoseLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: #ff0000; -fx-font-weight: bold;");
            youLoseLabel.setLayoutX(300);
            youLoseLabel.setLayoutY(200);
            youLoseLabel.setVisible(false);
            gameArea.getChildren().add(youLoseLabel);

            // Create boss health label
            Label bossHealthLabel = new Label("Boss Health: 0");
            bossHealthLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #ff0000; -fx-font-weight: bold;");
            bossHealthLabel.setLayoutX(400);
            bossHealthLabel.setLayoutY(10);
            bossHealthLabel.setVisible(false);
            gameArea.getChildren().add(bossHealthLabel);

            // Create screen flash overlay for boss spawn
            javafx.scene.shape.Rectangle flashOverlay = new javafx.scene.shape.Rectangle(960, 540);
            flashOverlay.setFill(javafx.scene.paint.Color.BLACK);
            flashOverlay.setOpacity(0);
            gameArea.getChildren().add(flashOverlay);

            // Layout for UI controls
            backButton.setLayoutX(10);
            backButton.setLayoutY(10);

            gameArea.getChildren().add(backButton);
            backButton.toFront();
            backButton.setStyle("-fx-background-color: transparent;");
            backButton.setCursor(javafx.scene.Cursor.HAND);

            // Main layout
            BorderPane root = new BorderPane();
            root.setCenter(gameArea);

            // Scene
            Scene gameScene = new Scene(root, 960, 540);

            // Keyboard controls
            gameScene.setOnKeyPressed(e -> {
                pressedKeys.add(e.getCode());
            });

            gameScene.setOnKeyReleased(e -> {
                pressedKeys.remove(e.getCode());
            });

            // Game loop for continuous movement
            Thread gameLoop = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(20); // ~50 FPS
                        long currentTime = System.currentTimeMillis();
                        long deltaMillis = currentTime - lastUpdateTimeMillis;
                        lastUpdateTimeMillis = currentTime;

                        if (!gameOver && !playerHit) {
                            remainingTimeMillis = Math.max(0, remainingTimeMillis - deltaMillis);
                        }

                        // If player is hit, check if explosion finished
                        if (playerHit && playerExplosion != null
                                && playerExplosion.frameIndex >= playerExplosion.frames.length - 1) {
                            gameOver = true;
                            javafx.application.Platform.runLater(() -> {
                                gameArea.getChildren().remove(playerExplosion.view);
                                youLoseLabel.setVisible(true);
                                timerLabel.setText(formatTime(remainingTimeMillis));
                            });
                            break; // Exit game loop
                        }

                        if (remainingTimeMillis <= 0) {
                            gameOver = true;
                            javafx.application.Platform.runLater(() -> {
                                timerLabel.setText(formatTime(0));
                                youLoseLabel.setVisible(true);
                            });
                            break;
                        }

                        if (gameOver) {
                            break; // Exit if game over
                        }

                        // Update spaceship position based on pressed keys
                        if (!playerHit) { // Only move if not hit
                            if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W)) {
                                shipY -= shipSpeed;
                            }
                            if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
                                shipY += shipSpeed;
                            }
                            if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A)) {
                                shipX -= shipSpeed;
                            }
                            if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
                                shipX += shipSpeed;
                            }

                            // Boundary checks
                            shipX = Math.max(0, Math.min(shipX, 910));
                            shipY = Math.max(0, Math.min(shipY, 490));
                        }

                        // Spawn enemies randomly
                        if (!playerHit && !bossSpawned) { // Only spawn if player not hit and boss not spawned
                            enemySpawnCounter++;
                            if (enemySpawnCounter > 60) { // Spawn every ~1.2 seconds
                                int type = random.nextInt(2) + 1; // 1 or 2
                                Image selectedImage, hit1, hit2;

                                if (type == 1) {
                                    selectedImage = enemy1Image;
                                    hit1 = enemy1Hit1Image;
                                    hit2 = enemy1Hit2Image;
                                } else {
                                    selectedImage = enemy2Image;
                                    hit1 = enemy2Hit1Image;
                                    hit2 = enemy2Hit2Image;
                                }

                                ImageView enemyView = new ImageView(selectedImage);
                                enemyView.setFitWidth(40);
                                enemyView.setFitHeight(40);
                                enemyView.setPreserveRatio(true);

                                double spawnX = random.nextDouble() * 920;
                                Enemy enemy = new Enemy(enemyView, spawnX, -50, type, selectedImage, hit1, hit2);
                                enemies.add(enemy);

                                javafx.application.Platform.runLater(() -> {
                                    gameArea.getChildren().add(enemyView);
                                });

                                enemySpawnCounter = 0;
                            }
                        }

                        // Update enemy positions
                        List<Enemy> enemiesToRemove = new ArrayList<>();
                        for (Enemy enemy : enemies) {
                            enemy.y += enemy.speedY;

                            // Update boss hit frame if applicable
                            if (enemy instanceof Boss) {
                                ((Boss) enemy).updateHitFrame();
                            }

                            // Movement logic for boss and regular enemies
                            if (enemy instanceof Boss) {
                                // Boss moves side to side
                                Boss bossEnemy = (Boss) enemy;
                                bossEnemy.changeDirectionCounter--;

                                // Random direction change
                                if (bossEnemy.changeDirectionCounter <= 0) {
                                    bossEnemy.direction = (random.nextDouble() > 0.5) ? 1.0 : -1.0;
                                    bossEnemy.changeDirectionCounter = random.nextInt(40) + 20;
                                }

                                bossEnemy.x += bossEnemy.direction * 2.0;

                                // Boundary checks for boss horizontal movement
                                if (bossEnemy.x < 0) {
                                    bossEnemy.x = 0;
                                    bossEnemy.direction = 1.0;
                                }
                                if (bossEnemy.x > 840) { // 960 - 120 (boss width)
                                    bossEnemy.x = 840;
                                    bossEnemy.direction = -1.0;
                                }
                            } else {
                                // Regular enemy movement
                                enemy.changeDirectionCounter--;

                                // Random direction change
                                if (enemy.changeDirectionCounter <= 0) {
                                    enemy.direction = (random.nextDouble() > 0.5) ? 1.0 : -1.0;
                                    enemy.changeDirectionCounter = random.nextInt(40) + 20;
                                }

                                enemy.x += enemy.direction * 2.0;

                                // Boundary checks for horizontal movement
                                if (enemy.x < 0) {
                                    enemy.x = 0;
                                    enemy.direction = 1.0;
                                }
                                if (enemy.x > 920) {
                                    enemy.x = 920;
                                    enemy.direction = -1.0;
                                }
                            }

                            // Remove if below screen
                            if (enemy.y > 540) {
                                enemiesToRemove.add(enemy);
                            }

                            // Collision detection with spaceship
                            if (Math.abs(enemy.x - shipX) < 40 && Math.abs(enemy.y - shipY) < 40 && !playerHit) {
                                // Spaceship gets hit - create explosion at spaceship
                                playerExplosion = new Explosion(explosionFrames, shipX, shipY);
                                explosions.add(playerExplosion);
                                enemiesToRemove.add(enemy);
                                playerHit = true;

                                javafx.application.Platform.runLater(() -> {
                                    spaceship.setVisible(false);
                                    gameArea.getChildren().add(playerExplosion.view);
                                });
                            }
                        }

                        // Boss minion summoning logic - spawn minions across entire width
                        if (boss != null && boss.shouldSummon()) {
                            // Spawn multiple minions spread across the screen width
                            int numMinions = 15; // Number of minions to spawn in a row
                            for (int i = 0; i < numMinions; i++) {
                                if (boss.minionCount < MAX_BOSS_MINIONS) {
                                    ImageView minionView = new ImageView(enemy1Image); // Use enemy1 as minion
                                    minionView.setFitWidth(40);
                                    minionView.setFitHeight(40);
                                    minionView.setPreserveRatio(true);

                                    // Spread minions evenly across screen width
                                    double spawnX = (i * 960.0) / numMinions + 40;
                                    Minion minion = new Minion(minionView, spawnX, boss.y + 50);
                                    minions.add(minion);
                                    boss.minionCount++;

                                    javafx.application.Platform.runLater(() -> {
                                        gameArea.getChildren().add(minionView);
                                    });
                                }
                            }
                        }

                        // Update minions
                        List<Minion> minionsToRemove = new ArrayList<>();
                        for (Minion minion : minions) {
                            minion.update();

                            // Remove if below screen
                            if (minion.y > 540) {
                                minionsToRemove.add(minion);
                                if (boss != null) {
                                    boss.minionCount--; // Decrement count when minion goes off-screen
                                }
                            }

                            // Collision with spaceship
                            if (Math.abs(minion.x - shipX) < 40 && Math.abs(minion.y - shipY) < 40 && !playerHit) {
                                playerExplosion = new Explosion(explosionFrames, shipX, shipY);
                                explosions.add(playerExplosion);
                                minionsToRemove.add(minion);
                                playerHit = true;

                                javafx.application.Platform.runLater(() -> {
                                    spaceship.setVisible(false);
                                    gameArea.getChildren().add(playerExplosion.view);
                                });
                            }
                        }

                        // Check projectile-minion collisions
                        List<Projectile> projectilesToRemove2 = new ArrayList<>();
                        for (Projectile proj : projectiles) {
                            for (Minion minion : minions) {
                                if (Math.abs(proj.x - minion.x) < 40 && Math.abs(proj.y - minion.y) < 40) {
                                    projectilesToRemove2.add(proj);
                                    minionsToRemove.add(minion);
                                    if (boss != null) {
                                        boss.minionCount--;
                                    }
                                    break;
                                }
                            }
                        }

                        // Check projectile-enemy collisions
                        List<Projectile> projectilesToRemove = new ArrayList<>();
                        List<Enemy> enemiesToExplode = new ArrayList<>();

                        for (Projectile proj : projectiles) {
                            for (Enemy enemy : enemies) {
                                // Use different collision distance for boss (80x80 = 40 half-size)
                                double collisionDist = (enemy instanceof Boss) ? 50 : 40;
                                if (Math.abs(proj.x - enemy.x) < collisionDist
                                        && Math.abs(proj.y - enemy.y) < collisionDist) {
                                    projectilesToRemove.add(proj);

                                    if (enemy instanceof Boss) {
                                        Boss bossEnemy = (Boss) enemy;
                                        bossEnemy.takeHit();
                                        if (bossEnemy.health <= 0) {
                                            enemiesToExplode.add(enemy);
                                        }
                                    } else {
                                        // Regular enemy
                                        enemy.takeHit();
                                        if (enemy.hits >= 2) {
                                            enemiesToExplode.add(enemy);
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        // Handle space bar for firing projectiles
                        if (pressedKeys.contains(KeyCode.SPACE) && !spacePressedLastFrame && !playerHit) {
                            // Fire projectile
                            ImageView projectileView = new ImageView(projectileImage);
                            projectileView.setFitWidth(20);
                            projectileView.setFitHeight(20);
                            projectileView.setPreserveRatio(true);

                            Projectile proj = new Projectile(projectileView, shipX + 25, shipY + 25);
                            projectiles.add(proj);

                            javafx.application.Platform.runLater(() -> {
                                gameArea.getChildren().add(projectileView);
                            });
                        }
                        spacePressedLastFrame = pressedKeys.contains(KeyCode.SPACE);

                        // Update projectile positions and remove off-screen projectiles
                        List<Projectile> toRemove = new ArrayList<>();
                        for (Projectile proj : projectiles) {
                            proj.y += proj.speedY;

                            // Remove if off-screen
                            if (proj.y < 0) {
                                toRemove.add(proj);
                            }
                        }

                        // Add projectiles hit by enemies to removal list
                        toRemove.addAll(projectilesToRemove);
                        toRemove.addAll(projectilesToRemove2);

                        // Add enemies that should explode to removal list
                        enemiesToRemove.addAll(enemiesToExplode);

                        // Increment kill count and reward time for each enemy destroyed
                        enemyKillCount += enemiesToExplode.size();
                        remainingTimeMillis += TIME_BONUS_PER_ENEMY_MILLIS * enemiesToExplode.size();
                        remainingTimeMillis = Math.min(remainingTimeMillis, INITIAL_TIME_MILLIS);

                        // Check if boss should spawn (50 enemies killed)
                        if (enemyKillCount % 50 == 0 && enemyKillCount > 0 && !bossSpawned && !screenFlashing) {
                            bossSpawned = true;
                            screenFlashing = true;
                            screenFlashCounter = 0;
                        }

                        // Check if boss should respawn (50 more kills after boss death)
                        if (!bossSpawned && boss == null && enemyKillCount >= killsWhenBossDied + 50
                                && !screenFlashing) {
                            screenFlashing = true;
                            screenFlashCounter = 0;
                        }

                        // Handle screen flash effect for boss spawn
                        if (screenFlashing) {
                            screenFlashCounter++;
                            if (screenFlashCounter < 100) { // Flash for about 2 seconds
                                // Alternate between black and white
                                double opacity = (screenFlashCounter % 20) < 10 ? 0.7 : 0.2;
                                javafx.application.Platform.runLater(() -> {
                                    flashOverlay.setOpacity(opacity);
                                });
                            } else {
                                // Finished flashing, spawn boss
                                screenFlashing = false;
                                javafx.application.Platform.runLater(() -> {
                                    flashOverlay.setOpacity(0);

                                    // Spawn boss
                                    if (boss == null) {
                                        ImageView bossView = new ImageView(bossImage);
                                        bossView.setFitWidth(80);
                                        bossView.setFitHeight(80);
                                        bossView.setPreserveRatio(true);

                                        boss = new Boss(bossView, 450, 20, bossHitImage); // Stay at top
                                        boss.originalImage = bossImage;
                                        enemies.add(boss);
                                        gameArea.getChildren().add(bossView);
                                        bossSpawned = true; // Set to true AFTER boss is spawned
                                        bossHealthLabel.setVisible(true); // Show health label when boss spawns
                                    }
                                });
                            }
                        }

                        // Update explosion animations
                        List<Explosion> explosionsToRemove = new ArrayList<>();
                        for (Explosion explosion : explosions) {
                            if (explosion.updateFrame()) {
                                explosionsToRemove.add(explosion);
                            }
                        }

                        // Create explosions for enemies hit twice
                        for (Enemy enemy : enemiesToExplode) {
                            Explosion explosion = new Explosion(explosionFrames, enemy.x, enemy.y);
                            explosions.add(explosion);

                            javafx.application.Platform.runLater(() -> {
                                gameArea.getChildren().add(explosion.view);
                            });
                        }

                        // Update UI
                        String timeText = formatTime(remainingTimeMillis);
                        javafx.application.Platform.runLater(() -> {
                            timerLabel.setText(timeText);
                            spaceship.setLayoutX(shipX);
                            spaceship.setLayoutY(shipY);

                            for (Projectile proj : projectiles) {
                                proj.view.setLayoutX(proj.x);
                                proj.view.setLayoutY(proj.y);
                            }

                            for (Enemy enemy : enemies) {
                                enemy.view.setLayoutX(enemy.x);
                                enemy.view.setLayoutY(enemy.y);

                                // Update boss health label
                                if (boss != null && enemy instanceof Boss) {
                                    bossHealthLabel.setText("Boss Health: " + boss.health);
                                }
                            }

                            for (Minion minion : minions) {
                                minion.view.setLayoutX(minion.x);
                                minion.view.setLayoutY(minion.y);
                            }

                            // Remove off-screen projectiles
                            for (Projectile proj : toRemove) {
                                gameArea.getChildren().remove(proj.view);
                                projectiles.remove(proj);
                            }

                            // Remove off-screen enemies
                            for (Enemy enemy : enemiesToRemove) {
                                gameArea.getChildren().remove(enemy.view);
                                enemies.remove(enemy);
                                // If boss is removed, reset for respawn
                                if (enemy instanceof Boss && enemy == boss) {
                                    boss = null;
                                    bossSpawned = false; // Re-enable regular enemy spawning
                                    killsWhenBossDied = enemyKillCount; // Store kill count for respawn tracking
                                    bossHealthLabel.setVisible(false); // Hide health label when boss dies
                                }
                            }

                            // Remove completed explosions
                            for (Explosion explosion : explosionsToRemove) {
                                gameArea.getChildren().remove(explosion.view);
                                explosions.remove(explosion);
                            }

                            // Remove off-screen minions
                            for (Minion minion : minionsToRemove) {
                                gameArea.getChildren().remove(minion.view);
                                minions.remove(minion);
                            }
                        });

                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            });
            gameLoop.setDaemon(true);
            remainingTimeMillis = INITIAL_TIME_MILLIS;
            lastUpdateTimeMillis = System.currentTimeMillis();
            gameLoop.start();

            // Back button action (go back to menu)
            backButton.setOnAction(e -> {
                App menu = new App();
                try {
                    menu.start(stage); // reload original menu
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Set new scene to stage
            stage.setScene(gameScene);
            stage.setTitle("Game Stage");
            stage.show();

            // Request focus for keyboard input
            gameArea.requestFocus();

        } catch (Exception ex) {
            System.err.println("Failed to load spaceship image: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String formatTime(long timeMillis) {
        long safeTimeMillis = Math.max(0, timeMillis);
        int totalSeconds = (int) (safeTimeMillis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}