import java.awt.*;
import java.awt.image.BufferedImage;

public class Moon implements Collideable {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float scale;

    private boolean destroyed = false; // true if the moon should be removed
    private BufferedImage texture;

    public Moon(BufferedImage moons, float x, float y, int nMoons, int sx, int sy, int cols) {
        int moon = (int)(Math.random()*nMoons); // generate random moon image from spritesheet
        int col = moon % cols;
        int row = moon/cols;

        this.x = x;
        this.y = y;
        this.vx = (float)Math.random()*2 - 1;
        this.vy = (float)Math.random()*1 - 0.5f;
        this.scale = (float)Math.random()+0.75f;
        this.texture = moons.getSubimage(col*sx, row*sy, sx, sy);
    }

    public boolean isDestroyed() { return destroyed; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVX() { return vx; }
    public float getVY() { return vy; }
    public void setVelocity(float vx, float vy) { this.vx = vx; this.vy = vy; }
    public float getWidth() { return (float)texture.getWidth(); };
    public float getHeight() { return (float)texture.getHeight(); };

    public void teleport(float x, float y) { this.x = x; this.y = y; }
    public void bounce() { vx = -vx; vy = -vy; }
    public void destroy() { destroyed = true; }

    public void update() {
        x += vx;
        y += vy;

        Collideable.wrap(this);
    }

    public void render(Graphics2D g) {
        g.drawImage(texture.getScaledInstance((int)(getWidth()*scale), (int)(getHeight()*scale),0),
                (int)x, (int)y, null);
    }
}
