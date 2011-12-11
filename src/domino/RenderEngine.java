package domino;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import static javax.media.opengl.GL.*;

/**
 * The RenderEngine class will render all elements within the World
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
	public Vertex defaultLightPos = new Vertex(5, 5, 15);
	
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
	
	
	// init all GL functions, set cameras, init 2d UI, and load world
	public void init(GLAutoDrawable gLDrawable){
		gl = gLDrawable.getGL();
        
        // bgColor contains the background color
        gl.glClearColor(0.15f, 0.15f, 0.15f, 0.0f);

        // Set the OpenGL depth functions
        gl.glClearDepth(1.0f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        
        // Set default render settings
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        // Set the default camera
        currentCamera = new Camera(gl, glu, width, height);
        currentCamera.lookFrom(new Vertex(17.67767f,17.67767f,20));
        currentCamera.lookAt(new Vertex(0,0,0));
        
        // Create the user interface manager
        ui = new UI (width, height, gl, glu, this.world);

        // Initialize FPS counter
		fpsEnd = System.currentTimeMillis();
        fpsCounter = 0;
        
        // Load the world's objects
        world.loadWorld(gLDrawable);
	}
	
	// render scene
	public void display(GLAutoDrawable drawable){
        gl = drawable.getGL();
      
        // clear buffers
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        mouseMoveCamera();
        currentCamera.set(gl);
        
        //gl.glRotatef(rotateT, 0.0f, 0.0f, 1.0f);
        //rotateT+= 0.01f; 
        
        Light point1 = new Light(2);
        point1.turnOff(gl);
        //Light.ambientLight(gl);

        
        // choose between normal or shadowed rendering pipeline
        if (world.shadowOn){
        	/*
        		Stencil Shadow Volume
        		http://www.codesampler.com/oglsrc/oglsrc_8.htm
        	*/	
            gl.glClear(GL_STENCIL_BUFFER_BIT);
            
            // disable writing of frame buffer color components
            gl.glColorMask( false, false, false, false );
            
            // Initialize the depth buffer
            world.superObject.renderAll();
            
            // Set the appropriate states for creating a stencil for shadowing.
            gl.glEnable( GL_CULL_FACE );
            gl.glEnable( GL_STENCIL_TEST );
            gl.glDepthMask( false );
            gl.glStencilFunc( GL_ALWAYS, 0, 0 );
            
            // Render the shadow volume and increment the stencil every where a front
            // facing polygon is rendered.
            gl.glStencilOp( GL_KEEP, GL_KEEP, GL_INCR );
            gl.glCullFace( GL_BACK );
            world.superObject.renderAllShadow();
    
            gl.glStencilOp( GL_KEEP, GL_KEEP, GL_DECR );
            gl.glCullFace( GL_FRONT );
            world.superObject.renderAllShadow();
            
            // When done, set the states back to something more typical.
            gl.glDepthMask( true );
            gl.glDepthFunc( GL_LEQUAL );
            gl.glColorMask( true, true, true, true );
            gl.glStencilOp( GL_KEEP, GL_KEEP, GL_KEEP );
            gl.glCullFace( GL_BACK );
            gl.glDisable( GL_CULL_FACE );
            
            // Render the shadowed part...
            gl.glStencilFunc( GL_EQUAL, 1, 1 );
            point1.turnOff(gl);
            gl.glEnable(GL_LIGHTING);
            world.render(gl);

            // Render the lit part...
            gl.glStencilFunc( GL_EQUAL, 0, 1 );
            point1.pointLight(gl, defaultLightPos);
            world.render(gl);
            
            Element3D.renderDummyBox(defaultLightPos, gl);

            // When done, set the states back to something more typical.
            gl.glDepthFunc( GL_LEQUAL );
            gl.glDisable( GL_STENCIL_TEST);
            
        }else{
        	// Render all objects
        	point1.pointLight(gl, defaultLightPos);
        	world.render(gl);
        }
 
        // Calculate Frames Per Second
        calcFPS();
        
        // User interface is rendered last
        ui.render();
	}
	
	// move the mouse to the edge of the window to move camera
	public void mouseMoveCamera(){
		int edge = 20;
		float speed = 0.06f;
		
		if (!rotating){
		if (world.input.x > (world.renderer.width - edge * 2))
			world.renderer.moveCamera(-speed, speed);
		
		if (world.input.x < edge)
			world.renderer.moveCamera(speed, -speed);
		
		if (world.input.y > (world.renderer.height - edge * 3) - 20)
			world.renderer.moveCamera(speed, speed);
		
		if (world.input.y < edge)
			world.renderer.moveCamera(-speed, -speed);
		
		}else{	
			rotSpeed *= defaultRotSpeed;
			
			if (rotSpeed < 1)
				rotSpeed = 1;
			
			if (rotDirection == 1){
				rotAngle += rotSpeed;
			}else{
				rotAngle -= rotSpeed;
			}
			
			if (rotAngle > 225){
				rotSpeed = defaultRotSpeed;
				rotDirection = -1;
				rotatingBack = true;
			}
			
			if (rotAngle < -135){
				rotSpeed = defaultRotSpeed;
				rotDirection = 1;
				
				rotatingBack = true;
			}
			
			if (rotatingBack){
				if (rotDirection == 1 && rotAngle > 45){
					rotating = false;
					rotAngle = 45;
				}
				
				if (rotDirection == -1 && rotAngle < 45){
					rotating = false;
					rotAngle = 45;
				}
			}
			
			int r = 25;
			
			float x = (float) Math.cos(Math.toRadians(rotAngle)); 
			float y = (float) Math.sin(Math.toRadians(rotAngle));
			
			x *= r;
			y *= r;
			
			currentCamera.lookFrom.x = x + currentCamera.lookAt.x;
			currentCamera.lookFrom.y = y + currentCamera.lookAt.y;
		}
	}
	
	// move camera to x, y coords
	public void moveCamera(float x, float y){
		currentCamera.lookAt.moveX(x);
		currentCamera.lookAt.moveY(y);
		
		currentCamera.lookFrom.moveX(x);
		currentCamera.lookFrom.moveY(y);
	}
	
	public void displayChanged(GLAutoDrawable drawable, 
			boolean modeChanged, boolean deviceChanged){
	}
	
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
