import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;


/**
 * Simple sprite for the bullet class.
 */

public class Bullet {
    private ImageView bullet;
    public final double WIDTH = 13;
    public final double HEIGHT = 44;

    Bullet(Image image) {
        this.bullet = new ImageView(image);
    }

    public void setPosition(double x, double y) {
        this.bullet.setX(x);
        this.bullet.setY(y);
    }

    public void setX(double pos) {
        this.bullet.setX(pos);
    }

    public void setY(double pos) {
        this.bullet.setY(pos);
    }

    public double getX() {
        return this.bullet.getX();
    }

    public double getY() {
        return this.bullet.getY();
    }

    public void render(Pane gameCanvas) {
        gameCanvas.getChildren().add(this.bullet);
    }

    public void clearFromCanvas(Pane gameCanvas) {
        gameCanvas.getChildren().remove(this.bullet);
    }

    public Rectangle getBoundary() {
        return new Rectangle(this.bullet.getX(), this.bullet.getY(), WIDTH, HEIGHT);
    }
}
