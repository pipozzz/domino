package domino;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * Tato trieda reprezentuje kameru.
 * 
 * @author pc1
 *
 */

public class Camera {
	GLU glu;
	
	private float aspectRatio;
	private float fovAngle;
	private float nearZ;
	private float farZ;
	
	public Vertex lookAt;
	public Vertex lookFrom;
        public float angle = 0.0f ;
	public float distance = 30.0f;
        
	public Camera(GL gl, GLU glu, int width, int height){
        this.glu = glu;
        
        // Defaultne hodnoty
		fovAngle = 60.0f;
        aspectRatio = (float)width / (float)height;
        nearZ = 10.0f;
        farZ = 1000.0f;
        
        // miesta na nastavenie kamery
        lookAt = new Vertex(0,0,0);
        lookFrom = new Vertex(0,20,30);
        distance = lookFrom.distanceTo(lookAt);
	}
	
	public void set(GL gl){
        //set up a perspective projection matrix
	    gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(fovAngle, aspectRatio , nearZ, farZ);
        
        // Pozicia kamery
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(lookFrom.x, lookFrom.y, lookFrom.z, 
        				lookAt.x, lookAt.y, lookAt.z, 
        					0.0, 0.0, 1.0);
	}
	
	public void setFOV(float fov){
		this.fovAngle = fov;
	}
	
	public void setAspectRatio(float ratio){
		this.aspectRatio = ratio;
	}
	
	public void lookAt(Vertex v){
		lookAt.x = v.x;
		lookAt.y = v.y;
		lookAt.z = v.z;
	}
	
	public void lookFrom(Vertex v){
		lookFrom.x = v.x;
		lookFrom.y = v.y;
		lookFrom.z = v.z;
	}
	
	public void lookFromAt(float atX, float atY, float atZ, 
							float fromX, float fromY, float fromZ){
		lookFrom.x = fromX;
		lookFrom.y = fromY;
		lookFrom.z = fromZ;
		
		lookAt.x = atX;
		lookAt.y = atY;
		lookAt.z = atZ;
	}
	
	public void setNear(float near){
		this.nearZ = near;
	}
	
	public void setFar(float far){
		this.farZ = far;
	}
        
        public void rotateLeft(float offset){
        
            System.out.println(distance);
                        angle -= offset;
			float x = (float) Math.cos(Math.toRadians(angle)); 
			float y = (float) Math.sin(Math.toRadians(angle));

			x *= distance;
			y *= distance;

			lookFrom.x = x + lookAt.x;
			lookFrom.y = y + lookAt.y;
        }
        
        public void rotateRight(float offset){
        
        System.out.println(distance);
                        angle += offset;
			float x = (float) Math.cos(Math.toRadians(angle)); 
			float y = (float) Math.sin(Math.toRadians(angle));

			x *= distance;
			y *= distance;

			lookFrom.x = x + lookAt.x;
			lookFrom.y = y + lookAt.y;
        }
        
        public void goNear(float offset){
            if(distance > 5.0) {
            System.out.println(distance);
            distance -= offset;
                        
			float x = (float) Math.cos(Math.toRadians(angle)); 
			float y = (float) Math.sin(Math.toRadians(angle));
            
                        x *= distance;
			y *= distance;

			lookFrom.x = x + lookAt.x;
			lookFrom.y = y + lookAt.y;
            }
        }
      
        public void goFar(float offset){
            
            System.out.println(distance);
            distance += offset;
                        
			float x = (float) Math.cos(Math.toRadians(angle)); 
			float y = (float) Math.sin(Math.toRadians(angle));
            
                        x *= distance;
			y *= distance;

			lookFrom.x = x + lookAt.x;
			lookFrom.y = y + lookAt.y;
        }
}
