package domino;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import static javax.media.opengl.GL.*;

/**
 * The RenderEngine class renderuje vsetky objekty sveta
 * 
 * @author pc1
 *
 */

public class RenderEngine implements GLEventListener{
	
	public GL gl;
	private static final GLU glu = new GLU();
	
	private World world;
	public int width;
	public int height;
	
	public Camera currentCamera;
	public Vertex defaultLightPos = new Vertex(5, 5, 25);
	
	public UI ui;
	
	public int fps;
	public int fpsCounter;
	private long fpsEnd;
	
	float rotateT = 0.0f;
	
	public boolean rotating = false;
	public boolean rotatingBack = false;
	public float rotAngle = 45.0f;
	public float rotSpeed = 1.01f;
	public float rotDirection = -1;
	
	public float defaultRotSpeed = 1.01f;

	
	public RenderEngine(int width, int height, World world){
		this.width = width;
		this.height = height;
		
		this.world = world;
	}
	
	
	// inicializacia prostredia
	public void init(GLAutoDrawable gLDrawable){
		gl = gLDrawable.getGL();
        
        // bgColor obsahuje background color
        gl.glClearColor(0.15f, 0.15f, 0.15f, 0.0f);

        // Nastavi opnegl funkcie
        gl.glClearDepth(1.0f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        
        // Nastavi defaultne renderovacie funkcie
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        // defaultna kamera
        currentCamera = new Camera(gl, glu, width, height);
        currentCamera.lookFrom(new Vertex(17.67767f,37,30));
        currentCamera.lookAt(new Vertex(0,0,0));
        
        // Pouzivatelske rozhranie
        ui = new UI (width, height, gl, glu, this.world);

        // Inicializuje FST pocitadlo
		fpsEnd = System.currentTimeMillis();
        fpsCounter = 0;
        
        // Nacita objekty sveta
        world.loadWorld(gLDrawable);
	}
	
	// renderovanie sceny
	public void display(GLAutoDrawable drawable){
        gl = drawable.getGL();
      
        
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
     
        currentCamera.set(gl);
        
        
        Light point1 = new Light(2);
        point1.turnOff(gl);
        
        	// Render all objects
        	point1.pointLight(gl, defaultLightPos);
        	world.render(gl);
       
 
        // Vypocita framy za sekundu (FPS)
        calcFPS();
        
	}
	
//	
	
	// posunutie kamery to x, y 
	public void moveCamera(float x, float y){
		currentCamera.lookAt.moveX(x);
		currentCamera.lookAt.moveY(y);
		
		currentCamera.lookFrom.moveX(x);
		currentCamera.lookFrom.moveY(y);
	}
	
    @Override
	public void displayChanged(GLAutoDrawable drawable, 
			boolean modeChanged, boolean deviceChanged){
	}
	
    @Override
	public void reshape(GLAutoDrawable drawable, 
			int x, int y, int width, int height){
	}
	
	
	// fps for display
	public void calcFPS(){
		fpsCounter++;
		
		long currTime = System.currentTimeMillis();
		
		if (currTime > fpsEnd){
			fps = fpsCounter;
			fpsEnd = currTime + 1000;
	        fpsCounter = 0;
		}
	}
}
