import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;


/**
 * Simple sprite for the alien class.
 */

public class Alien {

    public static final double ALIEN_SPEED = 0.5;
    public static final double ALIEN_VERTICAL_SPEED = 5;
    public static final double ALIEN_BULLET_SPEED = 1.5;
    public static final double SCORE_PER_KILL = 50;

    private ImageView alien;
    private ALIEN_TYPE alienType;
    public static final double WIDTH = 62;
    public static final double HEIGHT = 37;

    public static final int BULLET_WIDTH = 26;
    public static final int BULLET_HEIGHT = 50;
    public static final Image BULLET_ONE = new Image("images/bullet1.png", BULLET_WIDTH, BULLET_HEIGHT, true, true);
    public static final Image BULLET_TWO = new Image("images/bullet2.png", BULLET_WIDTH, BULLET_HEIGHT, true, true);
    public static final Image BULLET_THREE = new Image("images/bullet3.png", BULLET_WIDTH, BULLET_HEIGHT, true, true);

    private AudioClip deathSound;
    private AudioClip fireSound;

    Alien(Image image, ALIEN_TYPE alienType) {
        this.alien = new ImageView(image);
        this.alienType = alienType;
        String sound = getClass().getClassLoader().getResource("sounds/invaderkilled.wav").toString();
        deathSound = new AudioClip(sound);
        String fireSoundSource = "sounds/fastinvader4.wav";
        if (alienType == ALIEN_TYPE.ONE) {
            fireSoundSource = "sounds/fastinvader1.wav";
        } else if (alienType == ALIEN_TYPE.TWO) {
            fireSoundSource = "sounds/fastinvader2.wav";
        } else if (alienType == ALIEN_TYPE.THREE) {
            fireSoundSource = "sounds/fastinvader2.wav";
        }
        fireSoundSource = getClass().getClassLoader().getResource(fireSoundSource).toString();
        fireSound = new AudioClip(fireSoundSource);
    }

    public void setPosition(double x, double y) {
        this.alien.setX(x);
        this.alien.setY(y);
    }

    public void setX(double pos) {
        this.alien.setX(pos);
    }

    public void setY(double pos) {
        this.alien.setY(pos);
    }

    public double getX() {
        return this.alien.getX();
    }

    public double getY() {
        return this.alien.getY();
    }

    public void render(Pane gameCanvas) {
        gameCanvas.getChildren().add(this.alien);
    }

    public void clearFromCanvas(Pane gameCanvas) {
        gameCanvas.getChildren().remove(this.alien);
        deathSound.play();
    }

    public Rectangle getBoundary() {
        return new Rectangle(this.alien.getX(), this.alien.getY(), WIDTH, HEIGHT);
    }

    public Bullet fire() {
        Bullet alienBullet;
        if (this.alienType == ALIEN_TYPE.ONE) {
            alienBullet = new Bullet(BULLET_ONE);
        } else if (this.alienType == ALIEN_TYPE.TWO) {
            alienBullet = new Bullet(BULLET_TWO);
        } else {
            alienBullet = new Bullet(BULLET_THREE);
        }
        alienBullet.setX(this.alien.getX() + WIDTH / 2);
        alienBullet.setY(this.alien.getY());
        fireSound.play();
        return alienBullet;
    }
}
