import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Asteroids are collideable object that kill the player (end the game) if a collision occurs.
 */
public class Asteroid implements Collideable {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private BufferedImage texture;

    public Asteroid(BufferedImage texture, float x, float y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.vx = (float)Math.random()*4 - 2;
        this.vy = (float)Math.random()*2 - 1;
        this.angle = (float)Math.random()*360.0f;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getVX() { return vx; }
    public float getVY() { return vy; }
    public void setVelocity(float vx, float vy) { this.vx = vx; this.vy = vy; }
    public float getWidth() { return texture.getWidth(); }
    public float getHeight() { return texture.getHeight(); }

    public void teleport(float x, float y) { this.x = x; this.y = y; }
    public void bounce() { vx = -vx; vy = -vy; }

    public void update() {
        x += vx;
        y += vy;

        Collideable.wrap(this);
    }

    public void render(Graphics2D g) {
        AffineTransform t = AffineTransform.getTranslateInstance(x, y);
        t.rotate(angle, getWidth()/2, getHeight()/2);
        g.drawImage(texture, t, null);
    }
}
