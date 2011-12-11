package domino;

import javax.swing.*;

/**
 * This class is used to start an instance of our World.
 * 
 * @author pc1
 *
 */

public class Driver {

	public static void main(String[] args)
	{
		Window w = new Window();
	
		Thread world = new Thread(new World(w, 800, 600));
		
		world.start();
	}
	
}

@SuppressWarnings("serial")
class Window extends JFrame {
	
	public Window()
	{
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}
