import java.awt.Rectangle;

/**
 * An interface describing a collideable object (something that can be checked for collision).
 * Allows for checking for collisions between objects extending this interface.
 * In addition, also allows for teleporting an object to the other side of the screen when it goes out of bounds.
 * Bouncing handles collisions (if applicable)
 */
public interface Collideable {
    float getX();
    float getY();
    float getVX();
    float getVY();
    float getWidth();
    float getHeight();
    void teleport(float x, float y);
    void setVelocity(float vx, float vy);

    static boolean collide(Collideable a, Collideable b) {
        // less than sum of radii
        float dx = a.getX() - b.getX();
        float dy = a.getY() - b.getY();
        float r1 = Math.max(a.getWidth(), a.getHeight())/2 + 1.0f;
        float r2 = Math.max(b.getHeight(), b.getWidth())/2 + 1.0f;
        float d = r1 + r2;
        return dx*dx + dy*dy <= d*d;
        /*
        Rectangle r1 = new Rectangle((int)a.getX(), (int)a.getY(), (int)a.getWidth(), (int)a.getHeight());
        Rectangle r2 = new Rectangle((int)b.getX(), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight());

        return r1.intersects(r2);
        */
    }

    // Teleport an object that goes off the screen to the other side.
    static void wrap(Collideable a) {
        if (a.getX() < -a.getWidth())
            a.teleport(Game.WIDTH, a.getY());
        else if (a.getX() > Game.WIDTH)
            a.teleport(0, a.getY());

        if (a.getY() < -a.getHeight())
            a.teleport(a.getX(), Game.HEIGHT);
        else if (a.getY() > Game.HEIGHT)
            a.teleport(a.getX(), 0);
    }

    // Swap the velocities (assumes the mass of all collideables is 1)
    // Elastic-collision
    static void bounce(Collideable a, Collideable b) {
        float vx1 = a.getVX();
        float vy1 = a.getVY();
        float vx2 = b.getVX();
        float vy2 = b.getVY();

        a.setVelocity(vx2+0.1f, vy2-0.1f);
        b.setVelocity(vx1-0.1f, vy1+0.1f);
    }
}
