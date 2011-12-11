package domino;

import java.awt.Color;
import javax.media.opengl.*;
import static javax.media.opengl.GL.*;

/** Lighting class allows a mixture of ambient and diffuse lighting
 * Light position uses Vertex (x, y, z)
 * 
 * @author pc1
 *
 */

public class Light {

	private int lightNum;
	public Color lightColor;
	private Vertex lightPos;
	
    float lightAmbient[] = {0.0f, 0.0f, 0.0f, 1.0f};
	float lightDiffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float lightPosition[] = {0, 0, 0, 1.0f};
    float lightSpecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    
    boolean isON = true;
    
	// only one light currently implemented: lightAmbient w/ lightDiffuse
	public Light(int num) {
		lightNum = num;

		lightColor = Color.white;
		lightPos = new Vertex(0,0,0);
	}
	
	// single point lighting
	public void pointLight(GL gl, Vertex v){

        int lightNumber = GL_LIGHT0 + lightNum;
        
        lightPos = v;
        lightPosition[0] = lightPos.x;
        lightPosition[1] = lightPos.y;
        lightPosition[2] = lightPos.z;
        
        lightDiffuse[0] = lightColor.getRed()/255;
        lightDiffuse[1] = lightColor.getGreen()/255;
        lightDiffuse[2] = lightColor.getBlue()/255;

        gl.glLightfv(lightNumber, GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(lightNumber, GL_DIFFUSE, lightDiffuse, 0);
        //gl.glLightfv(lightNumber, GL_SPECULAR, lightSpecular, 0);
        gl.glLightfv(lightNumber, GL_POSITION, lightPosition, 0);
		
        gl.glEnable(GL_LIGHTING);
        
        if (isON)
        	gl.glEnable(lightNumber);
        else
        	gl.glDisable(lightNumber);
	}
	
	// set ambient lighting
	public static void ambientLight(GL gl){
	    float lightPosition[] = {0, 0, -10, 1.0f};

        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
        gl.glEnable(GL_LIGHT0);
	}
	
	// turn the light off
	public void turnOff(GL gl){
        int lightNumber = GL_LIGHT0 + lightNum;
        
		gl.glDisable(lightNumber);
	}
}