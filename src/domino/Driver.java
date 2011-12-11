package domino;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * This class is used to start an instance of our World.
 * 
 * @author pc1
 *
 */
public class Driver {

    public static Window w = new Window();
    public static World myWorld = new World(w, 800, 600);

    public static void main(String[] args) {


        Thread world = new Thread(myWorld);
        world.start();
    }
}

@SuppressWarnings("serial")
class Window extends JFrame {

    public Window() {
        this.setSize(800, 600);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                Driver.myWorld.stop();
            }
        });
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
