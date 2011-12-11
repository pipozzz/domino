package domino;

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
	public Mouse mouse;
	public Keyboard keyboard;
	
	public int x = 0;
	public int y = 0;
	
	public boolean MouseButtonPressed;
	public int MouseButtonNumber;
	public boolean MouseDragged;
	public boolean MouseReleased;
	
	// take in input from mouse or keyboard
	public InputEngine(World world){
		mouse = new Mouse(world);
		keyboard = new Keyboard(world);
	}
}


// Mouse handles all mouse input (movement and button)
class Mouse extends MouseInputAdapter{
	private World world;
	
	public Mouse(World world){
		this.world = world;
	}
	
	public void mouseMoved(MouseEvent e) {
		world.input.x = e.getX();
		world.input.y = e.getY();
	}
	
	public void mousePressed(MouseEvent e){
		world.input.MouseButtonPressed = true;
		world.input.MouseButtonNumber = e.getButton();
		world.renderer.ui.manage();
		
		world.renderer.ui.writeLine("Button pressed="+ e.getButton());
	}
	
	public void mouseReleased(MouseEvent e){
		world.input.MouseButtonPressed = false;
		world.input.MouseDragged = false;
		world.input.MouseReleased = true;
		
		world.renderer.ui.manage();
		
		world.renderer.ui.writeLine("Button Released.");
	}
	
	public void mouseDragged(MouseEvent e){
		world.input.MouseButtonPressed = true;
		world.input.MouseDragged = true;
		world.renderer.ui.manage();
		
		world.renderer.ui.writeLine("Mouse Dragged.");
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
			
		//world.renderer.ui.writeLine("Key pressed="+ e.getKeyChar());
		
		switch (e.getKeyCode()) {

		// Move light
		case KeyEvent.VK_F:
			world.renderer.defaultLightPos.moveX(1);	break;
		case KeyEvent.VK_H:
			world.renderer.defaultLightPos.moveX(-1);   break;        
		case KeyEvent.VK_T:
			world.renderer.defaultLightPos.moveY(-1);   break;
		case KeyEvent.VK_G:
			world.renderer.defaultLightPos.moveY(1);    break;
		case KeyEvent.VK_PAGE_UP:
			world.renderer.defaultLightPos.moveZ(1);	break;
		case KeyEvent.VK_PAGE_DOWN:
			world.renderer.defaultLightPos.moveZ(1);	break;
	        
		// Simulation key
		case KeyEvent.VK_SPACE:
			world.physics.simulationRunning = !world.physics.simulationRunning;	break;
			
		// Reset simulation
		case KeyEvent.VK_R:
			for (int i = 0; i < world.dominoes.size(); i++){
				Element3D ele = world.dominoes.get(i);
				ele.rotate.x = 0;
				ele.rotate.y = 0;
				ele.alive = true;
			}
		break;
		
		// Camera work
		case KeyEvent.VK_V:
			world.renderer.currentCamera.lookFrom(new Vertex(20,20,20));
			world.renderer.currentCamera.lookAt(new Vertex(0,0,0));
		break;
		case KeyEvent.VK_Z:
			world.renderer.rotatingBack = false;
			world.renderer.rotating = true;
			world.renderer.rotDirection = -1;
			world.renderer.rotSpeed = world.renderer.defaultRotSpeed;
		break;
		case KeyEvent.VK_X:
			world.renderer.rotatingBack = false;
			world.renderer.rotating = true;
			world.renderer.rotDirection = 1;
			world.renderer.rotSpeed = world.renderer.defaultRotSpeed;
		break;
				
		// Turn on/off shadows
		case KeyEvent.VK_S:
			world.shadowOn = !(world.shadowOn);				break;
			
		// Create dominoes
		case KeyEvent.VK_UP:
			world.addLineDominoes(3, World.NORTH);			break;
		case KeyEvent.VK_DOWN:
			world.addLineDominoes(3, World.SOUTH);			break;
		case KeyEvent.VK_RIGHT:
			world.addLineDominoes(3, World.EAST);			break;
		case KeyEvent.VK_LEFT:
			world.addLineDominoes(3, World.WEST);			break;
		case KeyEvent.VK_ENTER:
			world.addCurveDominoes(10);						break;
		
		// Clear all dominoes
		case KeyEvent.VK_C:
			if(!world.physics.simulationRunning){
				world.dominoes.removeAllElements();
				world.superObject.removeAllChildren("Domino");
			}
			break;
		}
	}
}
