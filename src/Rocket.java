import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * The player class. A controllable rocket required to make contact with all the moons.
 * A lower score means you're too slow in making contacts with the moons.
 */
public class Rocket implements Collideable {
    private static final float MAX_SPEED = 8.0f; // change this to slow down or speed up rocket
    private static final float ACCEL = 0.4f; // change this to change the rate the rocket speeds up at

    private boolean debug = false;
    private float x = 0;
    private float y = 0;
    private float angle = 0;
    private float speed = 0;
    private long score = 0;
    private long lastDeliveryTime = System.nanoTime();
    private RocketState state = RocketState.STOPPED;
    private Moon moon = null;
    private BufferedImage takeoffTexture;
    private BufferedImage rocketTexture;

    public Rocket(BufferedImage takeoffTexture, BufferedImage rocketTexture) {
        this.takeoffTexture = takeoffTexture;
        this.rocketTexture = rocketTexture;
    }

    public Rocket(BufferedImage takeoffTexture, BufferedImage rocketTexture, int score) {
        this(takeoffTexture, rocketTexture);
        this.score = score;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getVX() { return speed*(float)Math.cos(Math.toRadians(angle)); }
    public float getVY() { return speed*(float)Math.sin(Math.toRadians(angle)); }
    public void setVelocity(float vx, float vy) { this.speed = (float)Math.sqrt(vx*vx + vy*vy); this.angle = (float)Math.toDegrees(Math.atan2(vy, vx)); }
    public float getWidth() { BufferedImage texture = state == RocketState.TAKEOFF ? takeoffTexture : rocketTexture; return (float)texture.getWidth(); }
    public float getHeight() { BufferedImage texture = state == RocketState.TAKEOFF ? takeoffTexture : rocketTexture; return (float)texture.getHeight(); }
    public long getScore() { return score; }

    public void toggleDebug() { debug = !debug; }

    // returns true if the rocket is stopped (on the moon or just starting)
    public boolean isGrounded() { return state != RocketState.TRAVELLING; }

    public void teleport(float x, float y) { this.x = x; this.y = y; }
    public void bounce() { }

    public void takeoff() {
        // can only take off if stopped!
        if (state == RocketState.STOPPED) {
            new Audio("Resources/Launch.mp3", false).start();
            Moon moon = this.moon;

            this.state = RocketState.TAKEOFF;
            this.moon = null;

            if (moon != null)
                moon.destroy(); // if we leave a moon, remove it
        }
    }

    public void land(Moon moon) {
        long now = System.nanoTime();
        long duration = now - lastDeliveryTime; // time (in nanoseconds) since last time we landed

        this.moon = moon;
        this.speed = 0;
        this.lastDeliveryTime = now;
        this.state = RocketState.STOPPED;

        // 0 seconds -> 10 seconds
        // 50000 score -> 0 score
        float secs = duration/1000000000.0f;

        // add to score linearly based on how long it took to land
        this.score += Math.max(0, 50000 - (int)(50000*secs/10));
    }

    public void rotate(float angle) {
        if (state == RocketState.STOPPED || debug)
            this.angle += angle;
        else
            this.angle += angle*(1.1f - speed/MAX_SPEED); // if they're moving, rotate more slowly based on their speed
    }

    public void update() {
        if (state == RocketState.STOPPED) {
            if (moon != null) { // if stopped on moon, update position to stay on moon (because moons move)
                x = moon.getX();
                y = moon.getY();
            }
            return;
        }

        if (state == RocketState.TAKEOFF) {
            speed += ACCEL; // speedup

            if (speed >= MAX_SPEED) {
                speed = MAX_SPEED;
                state = RocketState.TRAVELLING;
            }
        }

        x += (int)(speed * Math.cos(Math.toRadians(angle)));
        y += (int)(speed * Math.sin(Math.toRadians(angle)));

        Collideable.wrap(this); // check if rocket went off screen and move to other side
    }

    // renders the rocket onto the screen
    public void render(Graphics2D g) {
        BufferedImage texture = state == RocketState.TAKEOFF ? takeoffTexture : rocketTexture;

        AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
        transform.rotate(Math.toRadians(angle), texture.getWidth()/2, texture.getHeight()/2);
        g.drawImage(texture, transform, null);
    }
}
