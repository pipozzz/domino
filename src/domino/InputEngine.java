package domino;

import com.sun.org.apache.bcel.internal.generic.LoadClass;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.*;

/**
 * This class handles user inputs from both the 
 * mouse and the keyboard.
 * @author pc1
 *
 */
public class InputEngine{
	//public Mouse mouse;
	public Keyboard keyboard;
	
	public int x = 0;
	public int y = 0;
	
	public boolean MouseButtonPressed;
	public int MouseButtonNumber;
	public boolean MouseDragged;
	public boolean MouseReleased;
	
	// take in input from mouse or keyboard
	public InputEngine(World world){
		
		keyboard = new Keyboard(world);
	}
}



// Keyboard class takes in all keyboard input 
class Keyboard extends KeyAdapter{
	private World world;
	
	public Keyboard(World world){
		this.world = world;
	}
	
	public void keyPressed(KeyEvent e) {
		if (!world.contextReady){
			world.gLDrawable.getContext().makeCurrent();
		}
			
		
		switch (e.getKeyCode()) {
	        
		// zacne simulaciu
		case KeyEvent.VK_SPACE:
                            
                        if(!world.dominoes.isEmpty())
			world.physics.simulationRunning = !world.physics.simulationRunning;	break;
			
		// Resetuje simulaciu
		case KeyEvent.VK_R:
			for (int i = 0; i < world.dominoes.size(); i++){
				Element3D ele = world.dominoes.get(i);
				ele.rotate.x = 0;
				ele.rotate.y = 0;
				ele.alive = true;
			}
		break;
		
                case KeyEvent.VK_X:
                    if(world.dominoes.isEmpty())
                    world.loadDominoFromFile(world.FILE);
				break;			
		
		// Vymazanie domina
		case KeyEvent.VK_C:
			if(!world.physics.simulationRunning){
				world.dominoes.removeAllElements();
				world.superObject.removeAllChildren("Domino");
			}
			break;
                // Vypnutie 
                case KeyEvent.VK_Q:
                        world.stop();
		}
	}
}
