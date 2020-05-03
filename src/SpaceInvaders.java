import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;


public class SpaceInvaders extends Application {

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 800;
    public static final String FONT = "Avenir";
    public static final int FONT_SIZE = 20;
    public static boolean gameOn = false;
    public static boolean messageOn = false;
    private AnimationTimer timer;
    private static final int HIGHSCORE_DISPLAY_HEIGHT = 50;

    private static final int NUM_ALIEN_ROWS = 5;
    private static final int NUM_ALIEN_COLS = 10;
    private Alien[][] aliens = new Alien[NUM_ALIEN_ROWS][NUM_ALIEN_COLS];
    private static final double ALIEN_FIRING_PROB = 0.003;
    // Ship


    // Speeds
    private static int ALIEN_DIRECTION = 1; // start by moving to the right.

    // Bullets
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Bullet> alienBullets = new ArrayList<>();
    private static final double BULLET_WIDTH = 13;
    private static final double BULLET_HEIGHT = 44;
    private static final double BULLET_SPEED = 4;
    private static final int TIME_BETWEEN_SHOTS_MS = 1;
    public static Image bulletImage = new Image("images/player_bullet.png", BULLET_WIDTH, BULLET_HEIGHT, true, true);

    // player
    private Player player;
    private int livesRemaining = 3;
    private int score = 0;
    private int level = 1;

    // Current game canvas
    private Pane gameCanvas;
    private Stage stage;
    private long lastBulletFiredTime;

    // messages
    private static final double MESSAGE_WIDTH = SCREEN_WIDTH / 2;
    private static final double MESSAGE_HEIGHT = SCREEN_HEIGHT / 2;
    private Text scoreboardText;
    private AudioClip gameOverSound;

    @Override
    public void start(Stage stage) {

        lastBulletFiredTime = System.currentTimeMillis();
        this.stage = stage;
        String gameOverSource = getClass().getClassLoader().getResource("sounds/explosion.wav").toString();
        gameOverSound = new AudioClip(gameOverSource);
        stage.setTitle("Space Invaders");
        stage.setScene(createOpeningScene());
        stage.show();
    }

    private String createScoreText(int score, int lives, int level) {
        return String.format("Score: %d, Lives: %d, Level: %d", score, lives, level);
    }

    private Scene createOpeningScene() {
        // Opening screen
        Text name = new Text("Aiden Yew Woei Low\n20869748\n");
        name.setFont(Font.font(FONT, FONT_SIZE));
        name.setTextAlignment(TextAlignment.CENTER);

        Text instHeader = new Text("Instructions");
        instHeader.setFont(Font.font(FONT, FontWeight.BOLD, FONT_SIZE * 2));
        instHeader.setTextAlignment(TextAlignment.CENTER);

        Text instructions = new Text("ENTER – Start Game\n A/D – Move ship Left/Right\n" +
                "SPACE –Fire!\nQ – Quit Game\n 1 or 2 or 3 – Start Game at a specific level.\n");
        instructions.setFont(Font.font(FONT, FONT_SIZE));
        instructions.setTextAlignment(TextAlignment.CENTER);

        Image image = new Image("images/logo.png", 491, 211, true, true);
        ImageView imageView = new ImageView(image);
        VBox box = new VBox(imageView, name, instHeader, instructions);
        box.setAlignment(Pos.CENTER);
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene openingScene = new Scene(box, SCREEN_WIDTH, SCREEN_HEIGHT);

        openingScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if (gameOn == false && keyCode.equals(KeyCode.ENTER)) {
                stage.setScene(createGameLevel(1));
            } else if (keyCode.equals(KeyCode.Q)) {
                System.exit(0);
                return;
            } else if (keyCode.equals(KeyCode.DIGIT1)) {
                stage.setScene(createGameLevel(1));
                resetGame();
            } else if (keyCode.equals(keyCode.DIGIT2)) {
                stage.setScene(createGameLevel(2));
                resetGame();
            } else if (keyCode.equals(keyCode.DIGIT3)) {
                stage.setScene(createGameLevel(3));
                resetGame();
            }
        });
        return openingScene;
    }

    private void setGameOverScene(boolean hasWon) {
        gameOverSound.play();
        this.timer.stop();
        this.messageOn = true;
        Rectangle messageRect = new Rectangle(SCREEN_WIDTH / 2 - MESSAGE_WIDTH / 2, SCREEN_HEIGHT / 2 - MESSAGE_HEIGHT / 2, MESSAGE_WIDTH, MESSAGE_HEIGHT);
        messageRect.setFill(Color.LIGHTGREY);
        String message;
        if (hasWon) {
            message = "You Won!\n";
        } else {
            message = "You Lost!\n";
        }
        message = message + "Your final score is: " + score + "\nENTER - Start New Game\n" +
                "I - Back to Instructions\nQ - Quit Game\n1 or 2 or 3 - Start New Game at a Specific Level\n";

        Text text = new Text(SCREEN_WIDTH / 2 - MESSAGE_WIDTH / 3, SCREEN_HEIGHT / 2 - MESSAGE_HEIGHT / 3, message);
        text.setFont(Font.font(FONT, FONT_SIZE));
        text.setFill(Color.BLACK);
        this.gameCanvas.getChildren().addAll(messageRect, text);
    }

    private void resetGame() {
        this.livesRemaining = 3;
        this.score = 0;
        this.alienBullets.clear();
    }

    private Scene createGameLevel(int level) {
        Pane gameCanvas = new Pane();
        if (this.timer != null) {
            this.timer.stop();
        }
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOn == true) {
                    handleAlienAnimation();
                    handleBulletAnimation();
                    handlePlayerCollision();
                    handleScoreboardUpdate();
                }
            }
        };

        timer.start();
        this.gameCanvas = gameCanvas;
        this.level = level;
        gameCanvas.setStyle("-fx-background-color: black;");
        Text scoreboard = new Text(0, HIGHSCORE_DISPLAY_HEIGHT, createScoreText(this.score, this.livesRemaining, this.level));
        scoreboard.setFont(Font.font(FONT, FONT_SIZE));
        scoreboard.setFill(Color.WHITE);
        this.scoreboardText = scoreboard;
        this.gameCanvas.getChildren().add(scoreboard);
        // Initialize aliens.
        Image alienImage1 = new Image("images/enemy1.png", Alien.WIDTH, Alien.HEIGHT, true, true);
        Image alienImage2 = new Image("images/enemy2.png", Alien.WIDTH, Alien.HEIGHT, true, true);
        Image alienImage3 = new Image("images/enemy3.png", Alien.WIDTH, Alien.HEIGHT, true, true);
        for (int row = 0; row < NUM_ALIEN_ROWS; row++) {
            for (int col = 0; col < NUM_ALIEN_COLS; col++) {
                Alien alien;
                if (row == 0) {
                    alien = new Alien(alienImage1, ALIEN_TYPE.ONE);
                } else if ( row <= 2) {
                    alien = new Alien(alienImage2, ALIEN_TYPE.TWO);
                } else {
                    alien = new Alien(alienImage3, ALIEN_TYPE.THREE);
                }
                alien.setPosition(col * Alien.WIDTH + 20, row * Alien.HEIGHT + 20 + SpaceInvaders.HIGHSCORE_DISPLAY_HEIGHT);
                aliens[row][col] = alien;
                alien.render(gameCanvas);
            }
        }

        // initialize player.
        Image playerImage = new Image("images/player.png", Player.WIDTH, Player.HEIGHT, true, true);
        Player player = new Player(playerImage);

        this.player = player;
        this.player.render(gameCanvas);

        Scene gameLevel = new Scene(gameCanvas, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameLevel.setOnKeyPressed(e -> {
            KeyCode kc = e.getCode();
            if (kc.equals(KeyCode.Q)) {
                System.exit(0);
            } else if (kc.equals(KeyCode.A)) {
                // move left;
                player.handleLeft();

            } else if (kc.equals(KeyCode.D)) {
                // move right;
                player.handleRight();
            } else if (kc.equals(KeyCode.SPACE) && canFireBullet()) {
                this.lastBulletFiredTime = System.currentTimeMillis();
                Bullet bullet = new Bullet(bulletImage);
                player.fire(bullet);
                this.bullets.add(bullet);
                bullet.render(gameCanvas);
            } else if (messageOn == true && (kc.equals(KeyCode.DIGIT1) || kc.equals(KeyCode.ENTER))) {
                this.stage.setScene(createGameLevel(1));
                resetGame();
            } else if (messageOn == true && kc.equals(KeyCode.DIGIT2)) {
                this.stage.setScene(createGameLevel(2));
                resetGame();
            } else if (messageOn == true && kc.equals(KeyCode.DIGIT3)) {
                this.stage.setScene(createGameLevel(3));
                resetGame();
            } else if (messageOn == true && kc.equals(KeyCode.I)) {
                this.stage.setScene(createOpeningScene());
                resetGame();
            }
        });
        this.gameOn = true;
        return gameLevel;
    }

    private boolean canFireBullet() {
        return (System.currentTimeMillis() - this.lastBulletFiredTime) > TIME_BETWEEN_SHOTS_MS;
    }


    private Alien getLeftMostAlien() {
        for (int col = 0; col < NUM_ALIEN_COLS; col++) {
            for (int row = 0; row < NUM_ALIEN_ROWS; row++) {
                Alien alien = this.aliens[row][col];
                if (alien == null) {
                    continue;
                }
                return alien;
            }
        }
        return null;
    }

    private Alien getRightMostAlien() {
        for (int col = NUM_ALIEN_COLS - 1; col >= 0; col--) {
            for (int row = 0; row < NUM_ALIEN_ROWS; row++) {
                Alien alien = this.aliens[row][col];
                if (alien == null) {
                    continue;
                }
                return alien;
            }
        }
        return null;
    }

    private void handleScoreboardUpdate() {
        this.scoreboardText.setText(createScoreText(this.score, this.livesRemaining, this.level));
    }

    private void handleBulletAnimation() {
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        ArrayList<Bullet> alienBulletsToRemove = new ArrayList<>();

        for (Bullet bullet : this.bullets) {
            if (bullet.getY() < 0) {
                bulletsToRemove.add(bullet);
                continue;
            }
            bullet.setY(bullet.getY() - BULLET_SPEED * this.level);
        }

        for (Bullet bullet : this.alienBullets) {
            if (bullet.getY() > SCREEN_HEIGHT) {
                alienBulletsToRemove.add(bullet);
                continue;
            }
            bullet.setY(bullet.getY() + Alien.ALIEN_BULLET_SPEED * this.level);
        }

        for (Bullet bullet : bulletsToRemove) {
            bullets.remove(bullet);
            bullet.clearFromCanvas(gameCanvas);
        }

        for (Bullet bullet : alienBulletsToRemove) {
            this.alienBullets.remove(bullet);
            bullet.clearFromCanvas(gameCanvas);
        }

    }

    private void handlePlayerCollision() {
        Bullet bulletToRemove = null;
        for (Bullet bullet : alienBullets) {
            if (hasCollided(bullet.getBoundary(), this.player.getBoundary())) {
                // player dies.
                this.livesRemaining--;
                System.out.println("this is happening");
                bulletToRemove = bullet;
                if (this.livesRemaining > 0) {
                    this.player.reset();
                } else {
                    // game over.
                    setGameOverScene(false);
                }
                break;
            }
        }
        if (bulletToRemove != null) {
            bulletToRemove.clearFromCanvas(this.gameCanvas);
        }
    }

    private void handleAlienAnimation() {
        // Check if direction has to change.
        Alien rightMostAlien = getRightMostAlien();
        Alien leftMostAlien = getLeftMostAlien();

        if (rightMostAlien == null && this.level == 3) {
            // no remaining aliens, won. Go to next round.
            setGameOverScene(true);
            return;
        } else if (rightMostAlien == null) {
            // go to the next round.
            this.stage.setScene(createGameLevel(this.level + 1));
            return;
        }

        boolean moveVert = false;
        boolean willFire = false;

        if (rightMostAlien.getX() > SCREEN_WIDTH - Alien.WIDTH ||
                leftMostAlien.getX() < 0) {
            ALIEN_DIRECTION = ALIEN_DIRECTION * -1;
            moveVert = true;
            willFire = true;

        }
        ArrayList<Alien> aliensToRemove = new ArrayList<>();

        for (int row = 0; row < NUM_ALIEN_ROWS; row++) {
            for (int col = 0; col < NUM_ALIEN_COLS; col++) {
                Alien alien = aliens[row][col];
                if (alien == null) {
                    continue;
                }

                // Check if alien collided with a bullet.
                ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
                for (Bullet bullet : this.bullets) {
                    if (hasCollided(bullet.getBoundary(), alien.getBoundary())) {
                        // The bullet intersected with the alien. Remove both.
                        aliensToRemove.add(alien);
                        bulletsToRemove.add(bullet);
                        aliens[row][col] = null;
                        continue;
                    }
                }

                for (Bullet bullet : bulletsToRemove) {
                    bullet.clearFromCanvas(this.gameCanvas);
                    this.bullets.remove(bullet);
                }
                alien.setX(alien.getX() + Alien.ALIEN_SPEED * ALIEN_DIRECTION * this.level);
                if (moveVert == true) {
                    alien.setY(alien.getY() + Alien.ALIEN_VERTICAL_SPEED * this.level);
                }
                // Check if collided with a player.
                if (hasCollided(alien.getBoundary(), player.getBoundary())) {
                    setGameOverScene(false);
                }
            }
        }

        if (willFire == true || Math.random() < ALIEN_FIRING_PROB * this.level) {
            Alien randomAlien = getRandomAlien();
            Bullet bullet = randomAlien.fire();
            bullet.render(gameCanvas);
            alienBullets.add(bullet);
        }

        for (Alien alien : aliensToRemove) {
            this.score += Alien.SCORE_PER_KILL;
            alien.clearFromCanvas(this.gameCanvas);
        }
    }

    private boolean hasCollided(Shape s1, Shape s2) {
        Shape intersect = Shape.intersect(s1, s2);
        if (intersect.getBoundsInParent().getWidth() <= 0) {
            return false;
        }
        return true;
    }

    private Alien getRandomAlien() {
        ArrayList<Alien> notNullAliens = new ArrayList<>();
        for (int row = 0; row < NUM_ALIEN_ROWS; row++) {
            for (int col = 0; col < NUM_ALIEN_COLS; col++) {
                Alien alien = aliens[row][col];
                if (alien == null) {
                    continue;
                }
                notNullAliens.add(alien);
            }
        }
        int randInt = (int) (Math.random() * notNullAliens.size());
        return notNullAliens.get(randInt);
    }

}
