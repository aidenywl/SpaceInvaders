import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;


/**
 * Simple sprite for the alien class.
 */

public class Player {
    public static final double WIDTH = 124;
    public static final double HEIGHT = 75;
    public static final double SPEED = 15;
    public static final double STARTX = SpaceInvaders.SCREEN_WIDTH / 2;;
    public static final double STARTY = SpaceInvaders.SCREEN_HEIGHT - HEIGHT;;
    public long lastBulletFiredTime;
    private ImageView player;
    private AudioClip fireSound;

    Player(Image image) {
        this.player = new ImageView(image);
        lastBulletFiredTime = System.currentTimeMillis();
        this.setX(this.STARTX);
        this.setY(this.STARTY);
        String sound = getClass().getClassLoader().getResource("sounds/shoot.wav").toString();
        fireSound = new AudioClip(sound);
    }

    public void handleLeft() {
        if (this.player.getX() < 0) {
            return;
        }
        this.player.setX(player.getX() - SPEED);
    }

    public void handleRight() {
        if (this.player.getX() > SpaceInvaders.SCREEN_WIDTH - WIDTH / 2) {
            return;
        }
        this.player.setX(player.getX() + SPEED);
    }

    public void fire(Bullet bullet) {
        bullet.setX(this.player.getX() + WIDTH / 2);
        bullet.setY(this.player.getY());

        this.fireSound.play();
    }

    public void setX(double pos) {
        this.player.setX(pos);
    }

    public void setY(double pos) {
        this.player.setY(pos);
    }

    public void reset() {
        this.player.setX(STARTX);
        this.player.setY(STARTY);
    }

    public void render(Pane gameCanvas) {
        gameCanvas.getChildren().add(this.player);
    }

    public void clearFromCanvas(Pane gameCanvas) {
        gameCanvas.getChildren().remove(this.player);
    }

    public Rectangle getBoundary() {
        return new Rectangle(this.player.getX(), this.player.getY(), WIDTH, HEIGHT);
    }
}
