package domino;

import com.sun.opengl.util.*;

import java.io.*;

import javax.media.opengl.*;
import javax.swing.*;
import java.util.Vector;

/**
 * �The most incomprehensible thing about the world is 
 * that it is at all comprehensible.�
 * 
 * This class represent everything including the render 
 * engine and the physics engine.
 * 
 * @author pc1
 *
 */

public class World implements Runnable {

	public GLCanvas canvas;
	public RenderEngine renderer;
	public PhysicsEngine physics;
	public InputEngine input;
	private JFrame window;

	public GL gl;
	public GLAutoDrawable gLDrawable;

	public Element3D superObject;
        public static final String FILE = "domino.txt";  
        
	public int fpsCap = 80;

	public Vector<Element3D> dominoes = new Vector<Element3D>();
	public static final int NORTH = 0;
	public static final int NW = 45;
	public static final int WEST = 90;
	public static final int SW = 135;
	public static final int SOUTH = 180;
	public static final int SE = 225;
	public static final int EAST = 270;
	public static final int NE = 335;

	public int startX = 0;
	public int startY = 0;

	public boolean contextReady = false;

	public boolean shadowOn = false;
	
	// Sounds
	//Vector<AudioClip> clickSounds = new Vector<AudioClip>();
	//AudioClip createSound;

        
        // Animator z run
        private FPSAnimator animator;
        
	// World constructor will hold renderer, physics, canvas, and all listeners
	public World(JFrame w, int width, int height) {
		renderer = new RenderEngine(width, height, this);
		input = new InputEngine(this);
		physics = new PhysicsEngine(this);

		canvas = new GLCanvas();

		this.window = w;

		canvas.addGLEventListener(renderer);

		
		canvas.addKeyListener(input.keyboard);

		window.add(canvas);
		canvas.requestFocus();
		window.setVisible(true);
	}

	public void run() {
		animator = new FPSAnimator(canvas, fpsCap);
		animator.setRunAsFastAsPossible(true);
		animator.start();
	}

        public void stop() {
            animator.stop();
            window.removeAll();
            window.dispose();
        }
        
	public void render(GL gl) {
		//Setup shadow option
		superObject.setLightPos(this.renderer.defaultLightPos);

		superObject.renderAll();
		physics.run();
	}

	
	// builds the world. superObject holds all dominoes 
	public void loadWorld(GLAutoDrawable gld) {
		gLDrawable = gld;
		gl = gld.getGL();

		superObject = new Element3D("SuperObject", null, renderer.gl);

		Element3D e;

		//addLineDominoes(10, WEST);
                loadDominoFromFile(FILE);
                
		e = Element3D.loadObj("media/objects/floor.obj",
				"media/textures/floor.jpg", "Floor", 1.5f, renderer.gl);
		e.moveTo(new Vertex(0, 0, 0));
		add(e);

		e = Element3D.loadObj("media/objects/rim.obj",
				"media/textures/rim.jpg", "Rim", 1.5f, renderer.gl);
		e.moveTo(new Vertex(0, 0, 0));
		add(e);
		
		
		
		
	}
	
	

	// adds element to superObject. most likely a domino
	public void add(Element3D e) {
		superObject.add(e);
	}

	// returns Element3D object
	public Element3D get(String id) {
		return (Element3D) superObject.getChild(id);
	}

	// builds a line of "number" dominoes in a certain direction
	// when changing directions, a 2 domino cap (corner) is also added
	public void addLineDominoes(int number, int direction) {
		Element3D e;

		Vertex v;

		int dirFrom = 0;

		boolean capFlag;

		float x = 0;
		float y = 0;

		// if there is more than one domino, get the direction of the last set domino for capping
		if (dominoes.size() > 1) {
			x = dominoes.get(dominoes.size() - 1).center.x;
			y = dominoes.get(dominoes.size() - 1).center.y;
			dirFrom = dominoes.get(dominoes.size() - 1).getDirection();

			capFlag = true;
		} else {
			x = startX;
			y = startY;
			capFlag = false;
		}
		
    	// avoid trivial collisions
    	if (dirFrom == NORTH && direction == SOUTH ||
    			dirFrom == SOUTH && direction == NORTH ||
    			dirFrom == EAST && direction == WEST ||
    			dirFrom == WEST && direction == EAST)
    		return;
    	
    	// build line
		for (int i = 0; i < number; i++) {
			e = Element3D.createDomino("Domino" + dominoes.size(), renderer.gl);

			switch (direction) {
			case NORTH:

				// set direction of domino
				e.setDirection(NORTH);

				// build cap if needed
				if (capFlag == true) {
					capFlag = false;

					v = addDominoCap(dirFrom, NORTH, x, y);
					
					// get the position of the ending cap piece
					x = v.x;
					y = v.y;

					// move new domino to location and rotate to respected direction
					e.moveTo(new Vertex(x, ((i + 1) * -1.5f) + y, 0));
					e.rotate.z = NORTH;

				} else {

					e.moveTo(new Vertex(x, ((i + 1) * -1.5f) + y, 0));
					e.rotate.z = NORTH;

				}

				break;

			case SOUTH:

				e.setDirection(SOUTH);

				if (capFlag == true) {
					capFlag = false;

					v = addDominoCap(dirFrom, SOUTH, x, y);
					
					x = v.x;
					y = v.y;

					e.moveTo(new Vertex(x, ((i + 1) * 1.5f) + y, 0));
					e.rotate.z = SOUTH;

				}

				e.moveTo(new Vertex(x, ((i + 1) * 1.5f) + y, 0));
				e.rotate.z = SOUTH;

				break;

			case EAST:

				e.setDirection(EAST);

				if (capFlag == true) {
					capFlag = false;

					v = addDominoCap(dirFrom, EAST, x, y);

					x = v.x;
					y = v.y;

					e.moveTo(new Vertex(x + ((i + 1) * -1.5f), y, 0));
					e.rotate.z = EAST;

				}

				e.moveTo(new Vertex(x + ((i + 1) * -1.5f), y, 0));
				e.rotate.z = EAST;

				break;

			case WEST:

				e.setDirection(WEST);

				if (capFlag == true) {
					capFlag = false;

					v = addDominoCap(dirFrom, WEST, x, y);

					x = v.x;
					y = v.y;

					e.moveTo(new Vertex(x + ((i + 1) * 1.5f), y, 0));
					e.rotate.z = WEST;

				}

				e.moveTo(new Vertex(x + ((i + 1) * 1.5f), y, 0));
				e.rotate.z = WEST;

				break;
			}

			dominoes.add(e);
			superObject.add(e);
			
			
		}
	}

	// add the two domino cap (corner) based on the dir coming from, and going to.
	//return the center vertex of the second domino piece so a future line can be built
	public Vertex addDominoCap(int dirFrom, int dirTo, float x, float y) {
		if (dirFrom == dirTo)
			return new Vertex(x, y, 0);
		
		Element3D e1, e2;

		e1 = Element3D.createDomino("Domino" + dominoes.size(), renderer.gl);
		e2 = Element3D.createDomino("Domino" + dominoes.size(), renderer.gl);

		renderer.ui.writeLine("dirFr: " + dirFrom + " dirTo: " + dirTo);

		if (dirFrom == WEST && dirTo == NORTH) {

			e1.moveTo(new Vertex(x + 1.0f, y - 0.5f, 0));
			e1.rotateZ(NW);

			e2.moveTo(new Vertex(x + 1.5f, y - 1.5f, 0));
			e2.rotateZ(NW - 20);

		} else if (dirFrom == EAST && dirTo == NORTH) {

			e1.moveTo(new Vertex(x - 1.0f, y - 0.5f, 0));
			e1.rotateZ(NE - 20);

			e2.moveTo(new Vertex(x - 1.5f, y - 1.5f, 0));
			e2.rotateZ(NE);

		} else if (dirFrom == EAST && dirTo == SOUTH) {

			e1.moveTo(new Vertex(x - 1.0f, y + 0.5f, 0));
			e1.rotateZ(SE);

			e2.moveTo(new Vertex(x - 1.5f, y + 1.5f, 0));
			e2.rotateZ(SE - 20);

		} else if (dirFrom == WEST && dirTo == SOUTH) {

			e1.moveTo(new Vertex(x + 1.0f, y + 0.5f, 0));
			e1.rotateZ(SW);

			e2.moveTo(new Vertex(x + 1.5f, y + 1.5f, 0));
			e2.rotateZ(SW + 20);

		} else if (dirFrom == NORTH && dirTo == EAST) {

			e1.moveTo(new Vertex(x - 0.5f, y - 1.0f, 0));
			e1.rotateZ(NE - 20);

			e2.moveTo(new Vertex(x - 1.5f, y - 1.5f, 0));
			e2.rotateZ(NE - 40);

		} else if (dirFrom == NORTH && dirTo == WEST) {

			e1.moveTo(new Vertex(x + 0.5f, y - 1.0f, 0));
			e1.rotateZ(NW);

			e2.moveTo(new Vertex(x + 1.5f, y - 1.5f, 0));
			e2.rotateZ(NW + 20);

		} else if (dirFrom == SOUTH && dirTo == WEST) {

			e1.moveTo(new Vertex(x + 0.5f, y + 1.5f, 0));
			e1.rotateZ(SW + 30);

			e2.moveTo(new Vertex(x + 1.5f, y + 2.5f, 0));
			e2.rotateZ(SW - 20);

		} else if (dirFrom == SOUTH && dirTo == EAST) {

			e1.moveTo(new Vertex(x - 0.5f, y + 1.5f, 0));
			e1.rotateZ(SE - 20);

			e2.moveTo(new Vertex(x - 1.5f, y + 2.5f, 0));
			e2.rotateZ(SE + 20);

		}

		dominoes.add(e1);
		superObject.add(e1);

		dominoes.add(e2);
		superObject.add(e2);

		return e2.center;
	}

	// add a curved set of dominoes. very sophisticated - aka: buggy. 
	// need to base curve on the direction of the last set domino
	public void addCurveDominoes(float radius) {
		Element3D e;

		float x = 0;
		float y = 0;

		int number = (int) radius;
		float dirFrom = 0;

		if (dominoes.size() > 1) {
			x = dominoes.get(dominoes.size() - 1).center.x;
			y = dominoes.get(dominoes.size() - 1).center.y;

			dirFrom = dominoes.get(dominoes.size() - 1).rotate.z;
		}

		float angle = dirFrom % 90;

		float angleZ = 90 + angle;
		float angleStep = (angle + 90) / number;

		for (int i = 1; i < (number + 1) / 2; i++) {
			angle += angleStep;
			angleZ += angleStep;

			e = Element3D.createDomino("Domino" + String.valueOf(i), renderer.gl);

			x += Math.cos(Math.toRadians(angle));
			y += Math.sin(Math.toRadians(angle));

			e.moveTo(new Vertex(x, y, 0));

			e.rotate.z = angleZ;

			dominoes.add(e);
			superObject.add(e);
		}
	}
        
        public void loadDominoFromFile(String file){
        FileInputStream fis = null;
        int count;
        String[] line;
        float x, y, z;
        int direction = 0;
        
        try {
            fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            count = Integer.parseInt(br.readLine());
            System.out.println(count);
            for(int iterator = 0 ; iterator < count; iterator++){
                line = br.readLine().split("\\s");
                if(line.length != 4) System.out.println("Bad format of input file");
                
                x = Float.parseFloat(line[0]);
                y = Float.parseFloat(line[1]);
                z = Float.parseFloat(line[2]);
                if(line[3].length() == 1) {
                switch(line[3].charAt(0)){
                    case 'N': direction = NORTH; break;
                    case 'S': direction = SOUTH; break;
                    case 'W': direction = WEST; break;
                    case 'E': direction = EAST; break;
                    
                            
                }
                } else {
                if (line [3].equals("NE")) direction = NE;
                if (line [3].equals("NW")) direction = NW;
                if (line [3].equals("SE")) direction = SE;
                if (line [3].equals("SW")) direction = SW;
                
                }
                
               addDomino(iterator, x, y, z, direction); 
            }
         fis.close();           
        } 
        catch (IOException ex) {
            System.out.println("Cannot read from input file");
        } 
        
        }
        
        
        
        private void addDomino(float id, float x, float y, float z, int direction){
            Element3D e;		

		e = Element3D.createDomino("Domino" + String.valueOf(id), renderer.gl);
                e.setDirection(direction);
		e.moveTo(new Vertex(x, y, z));
		e.rotate.z = direction;	

		dominoes.add(e);
		superObject.add(e);
			
			
		
            
        }
        
}
