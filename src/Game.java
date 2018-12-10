import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public final class Game extends JPanel {
    public static final int WIDTH = 640*2;
    public static final int HEIGHT = 480*2;

    private boolean win = false;
    private boolean running = true;
    private JFrame frame;
    private Rocket player;
    private Image background;
    private ArrayList<Moon> moons;
    private ArrayList<Asteroid> asteroids;
    private int level;

    public Game(int level, int score) {
        frame = new JFrame();
        moons = new ArrayList<>();
        asteroids = new ArrayList<>();
        this.level = level;

        BufferedImage moonTexture = null;
        BufferedImage asteroidTexture = null;

        // load textures and initialize player
        try {
            BufferedImage backgroundTexture = ImageIO.read(new File("Resources/Background.png"));
            BufferedImage flyingTexture = ImageIO.read(new File("Resources/Flying.png"));
            BufferedImage landedTexture = ImageIO.read(new File("Resources/Landed.png"));
            moonTexture = ImageIO.read(new File("Resources/Moon.png"));
            asteroidTexture = ImageIO.read(new File("Resources/Asteroid.png"));

            background = backgroundTexture.getScaledInstance(WIDTH, HEIGHT, 0);
            player = new Rocket(flyingTexture, landedTexture, score);
            player.teleport(WIDTH/2, HEIGHT/2);
        } catch (Exception e) {
            System.err.println("*** could not load resources! ***");
            e.printStackTrace();
            System.exit(1);
        }

        // Randomly generate moons and asteroids
        {
            int max_moons = 5 + level;
            int max_asteroids = 10 + 4*level;

            for (int x = 50; x <= WIDTH-50; x += 100*2) {
                for (int y = 100; y <= HEIGHT-100; y += 100*2) {
                    if (moons.size() >= max_moons) break;
                    if (Math.random() < 0.5) moons.add(new Moon(moonTexture, x, y, 8, 64, 64, 8));
                }
            }

            for (int x = 50; x <= WIDTH-50; x += 100*2) {
                for (int y = 100; y <= HEIGHT-100; y += 100*2) {
                    if (asteroids.size() >= max_asteroids) break;
                    if (Math.random() < 0.5) asteroids.add(new Asteroid(asteroidTexture, x, y));
                }
            }
        }

        // initialize the JFrame
        frame.setResizable(false);
        frame.setLocation(50, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(new GameController(this));
        frame.setUndecorated(true);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
    }

    public Rocket getPlayer() { return player; }

    public boolean isRunning() { return running; } // game is still active and rendering

    // called every frame before rendering
    public void update() {
        if (moons.size() == 0) { // win state: no more moons
            win = true;
            running = false;
            return;
        }

        // update moon positions and remove destroyed moons
        // iterate backwards to allow for arraylist removal while iterating
        for (int i = moons.size() - 1; i >= 0; i--) {
            Moon moon = moons.get(i);

            if (moon.isDestroyed())
                moons.remove(i);
            else
                moon.update();
        }

        // update asteroid positions
        for (Asteroid asteroid : asteroids)
            asteroid.update();

        if (!player.isGrounded()) {
            for (Moon moon : moons) {
                if (Collideable.collide(player, moon)) {
                    player.land(moon);

                    if (moons.size() == 1) {  // early exit (right when they touch last moon they win)
                        player.takeoff();
                        return;
                    }

                    break;
                }
            }

            for (Asteroid asteroid : asteroids) {
                if (Collideable.collide(player, asteroid)) { // on collision with an asteroid, lose state
                    new Audio("Resources/Explosion.mp3", false).start();
                    win = false;
                    running = false;
                    return;
                }
            }
        }

        // bounce moons on collision with other moons
        for (int i = 0; i < moons.size() - 1; i++) {
            for (int j = i + 1; j < moons.size(); j++) {
                Collideable a = moons.get(i);
                Collideable b = moons.get(j);

                if (Collideable.collide(a, b))
                    Collideable.bounce(a, b);
            }
        }

        // bounce asteroids on collision with other asteroids
        for (int i = 0; i < asteroids.size() - 1; i++) {
            for (int j = i + 1; j < asteroids.size(); j++) {
                Collideable a = asteroids.get(i);
                Collideable b = asteroids.get(j);

                if (Collideable.collide(a, b))
                    Collideable.bounce(a, b);
            }
        }


        // update player position
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        super.paintComponent(g);

        g2.setColor(Color.BLACK);
        g2.drawImage(background, 0, 0, null);

        if (running) { // render all asteroids moons and the player if the game is active
            for (Asteroid asteroid : asteroids) // asteroids rendered behind moons
                asteroid.render(g2);

            for (Moon moon : moons)
                moon.render(g2);

            player.render(g2);
        } else { // render win/lose message
            String message = win ? "You win!" : "You lose.";
            String message2 = "Press Enter to " + (win ? "continue to the next level." : "restart.");
            g2.setColor(new Color(0, 0, 0, 127));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("TimesRoman", Font.BOLD, 100));
            g2.drawString(message, WIDTH/2 - g2.getFontMetrics().stringWidth(message)/2, HEIGHT/2);
            g2.setFont(new Font("TimesRoman", Font.BOLD, 50));
            g2.drawString(message2, WIDTH/2 - g2.getFontMetrics().stringWidth(message2)/2, HEIGHT/2 + g2.getFontMetrics().getHeight() + 5);
        }

        g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g2.setColor(Color.WHITE);

        // render level
        String level = "Level: " + this.level;
        g2.drawString(level, WIDTH - g2.getFontMetrics().stringWidth(level) - 10, g2.getFontMetrics().getHeight() + 10);

        // render score
        String score = "Score: " + player.getScore();
        g2.drawString(score, WIDTH - g2.getFontMetrics().stringWidth(score) - 10, 2*g2.getFontMetrics().getHeight() + 10);

        // render title
        String title = "Welcome to Galactic Mail";
        g2.drawString(title, WIDTH/2 - g2.getFontMetrics().stringWidth(title)/2, g2.getFontMetrics().getHeight() + 10);
    }

    public static boolean initialize = true;

    public static void main(String[] args) {
        new Audio("Resources/Music.mp3", true ).start();
        try {
            int level = 1;
            int score = 0;
            while (true) {
                    Game game = new Game(level, score);

                    while (game.isRunning()) {
                        game.frame.revalidate();
                        game.update();
                        game.frame.repaint();
                        Thread.sleep(1000 / 30);
                    }
                game.frame.revalidate();
                game.frame.repaint();
                initialize = false;

                while (!initialize)
                    Thread.sleep(100);

                if (game.win) {
                    level += 1;
                    score += game.player.getScore();
                } else {
                    level = 1;
                    score = 0;
                }
            }
        } catch (InterruptedException e) {
        }
    }
}
