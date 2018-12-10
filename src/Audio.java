import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Audio extends Thread {
    String resource;
    boolean looped;

    public Audio(String resource, boolean looped) {
        this.resource = resource;
        this.looped = looped;
    }

    @Override
    public void run() {
        try {
            Player plr;
            plr = new Player(new FileInputStream(resource));
            plr.play();

            while (looped) {
                if (plr.isComplete()) {
                    plr = new Player(new FileInputStream(resource));
                    plr.play();
                }
            }
        } catch (Exception e) {
            System.err.println("*** could not load sound " + resource + " ***");
            System.exit(1);
        }
    }
}
