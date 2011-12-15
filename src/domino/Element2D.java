package domino;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;
import com.sun.opengl.util.texture.*;
import com.sun.opengl.util.j2d.TextureRenderer;
import static javax.media.opengl.GL.*;

/**
 * Element2D
 * 
 * @author pc1
 */

public class Element2D extends Element {
	
	public int x, y;
	public int width, height, zIndex;
		
	private Vertex[] corner = new Vertex[4];

	// Pouzite na renderovanie textur pomocou Graphics2D
	private TextureRenderer texRenderer;

	public Element2D(String iden, int width, int height, int x, int y, GL gl){
		this(iden, null, width, height, x, y, gl);
	}

	public Element2D(String iden, Element2D parent, GL gl){
		super(iden, parent, gl);
	}
	
	public Element2D(String iden, Element2D parent, int width, int height, int x, int y, GL gl){
		this(iden, parent, gl);
		
		this.width = width;
		this.height = height;
		
		this.x = x;
		this.y = y;
		
		// Vytvorenie obdliznika
		this.corner[0] = new Vertex(0,0,0.0f);
		this.corner[1] = new Vertex(0,height,0.0f);
		this.corner[2] = new Vertex(width,height,0.0f);
		this.corner[3] = new Vertex(width,0,0.0f);
		
        texRenderer = new TextureRenderer(width, height, true);
   
	}

	public void render(){
		int parentX, parentY;
        parentX = parentY = 0;
        
        if (parent != null){
        	parentX = ((Element2D)parent).x;
        	parentY = ((Element2D)parent).y;
        }
        
        Texture tex = texRenderer.getTexture();   
        
        gl.glDisable(GL_LIGHTING);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glColor4f(1.0f, 1.0f, 1.0f, transperncy);
        
        tex.bind();
        tex.enable();
        
        gl.glLoadIdentity();
   
        texRenderer.drawOrthoRect(x + parentX, y + parentY, 0, 0, width, height);

        tex.disable();
        gl.glDisable(GL_BLEND);
    }
	
	public Graphics2D getGraphicsWithAlpha(){
		Graphics2D g = texRenderer.createGraphics();
		
        // Vycistenie s backgroundom
		g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.Src);
        
		AffineTransform t = g.getTransform();
		
		// This is necessary since OpenGL uses a different x,y origin
		t.translate(0 , height);
		t.scale(1.0, -1.0);
		
		g.setTransform(t);
		
		return g;
	}
	
	public Graphics2D getGraphics(){
		return texRenderer.createGraphics();
	}
	
	public void redrawTexture(){
		texRenderer.markDirty(0, 0, width, height);
	}
	
	// Vracia Element2D ktory je na bode obrazovky
	public Element2D isInside(int pointX, int pointY){
		for (int i = child.size() - 1; i >= 0; i--){
			Element2D e = (Element2D) child.get(i);
			e = e.isInside(pointX, pointY);
			
			if (e != null)
				return e;
		}
		
		int parentX, parentY;
        parentX = parentY = 0;
        
        if (parent != null){
        	parentX = ((Element2D)parent).x;
        	parentY = ((Element2D)parent).y;
        }
		
		if ((pointX > x+parentX && pointX < (x+width+parentX)) 
				&& (pointY > y+parentY && pointY < (y+height+parentY)))
			return this;
		
		return null;
	}
	
	// Kontroluje ci je bod v elemente
	public boolean inside(int pointX, int pointY){
		int parentX, parentY;
        parentX = parentY = 0;
        
        if (parent != null){
        	parentX = ((Element2D)parent).x;
        	parentY = ((Element2D)parent).y;
        }
		
		if ((pointX > x+parentX && pointX < (x+width+parentX)) 
				&& (pointY > y+parentY && pointY < (y+height+parentY)))
			return true;
		
		return false;
	}
	
	
	public void renderShadow(){
	}
}
