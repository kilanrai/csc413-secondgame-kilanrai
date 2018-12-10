import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles user input.
 */
public class GameController implements KeyListener {
    private Game state;

    public GameController(Game state) {
        this.state = state;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            state.getPlayer().rotate(-5);
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            state.getPlayer().rotate(5);
        else if (e.getKeyCode() == KeyEvent.VK_SPACE)
            state.getPlayer().takeoff();
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        else if (e.getKeyCode() == KeyEvent.VK_ENTER && Game.initialize == false)
            Game.initialize = true;
        else if (e.getKeyCode() == KeyEvent.VK_P) // for testing, allows full rotation of the rocket
            state.getPlayer().toggleDebug();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}
