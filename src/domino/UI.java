package domino;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Generates and manages UI elements including message boxes, menus
 * buttons, cursor, labels, etc.
 * 
 * @author pc1
 */

public class UI {
	private GL gl;
	private GLU glu;
	private World world;
	
	public int width, height;
	
	private Element2D contentPanel;
	private Element2D currentElement;
	
	private TextRenderer textEngine;
	
	// The console box's stuff
	private Element2D consoleBox;
	Font consoleFont = new Font("SansSerif", Font.PLAIN, 13);
	private int consoleNumLines = 4;
	public String[] console = new String[consoleNumLines];
	
	// Sample items
	private BufferedImage sampleImage;
	private boolean drawOnce = true;
	
	public UI(int width, int height, GL gl, GLU glu, World world){
		this.gl = gl;
		this.glu = glu;
		this.world = world;
		
		this.width = width;
		this.height = height;
		
		contentPanel = new Element2D("contentPanel", 1, 1, -1, -1, gl);	
		
		// Setup console
		textEngine = new TextRenderer(consoleFont, true, true);
		consoleBox = new Element2D("ConsoleBox", width, 60, 0, height - 60, gl);
		setupConsole(consoleBox);
		clearConsole();

		contentPanel.add(new Element2D("ImageBox", 256, 256, width - 256, 0, gl));
		
		/*contentPanel.add(new MessageBox("MsgBox1",contentPanel, "Welcome :)",
				"Press 'Space' to start, 'r' to reset", width, height,gl));
		
                 * 
                 */
        try{
            sampleImage = ImageIO.read(new File("media/logo-fei.png"));
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }
	}
	
	public void render(){
		// Setup 2D projection
		gl.glMatrixMode (GL.GL_PROJECTION);
        gl.glLoadIdentity ();
        glu.gluOrtho2D(0, width, height, 0);
        gl.glMatrixMode (GL.GL_MODELVIEW);
        
		contentPanel.renderAll();
		
	}
	
	public Element2D get(String id){
		return (Element2D)contentPanel.getChild(id);
	}
	
	public void clearConsole(){
		for (int i = 0; i < consoleNumLines; i++)
			console[i] = "";
	}
	
	public void setupConsole(Element2D con){
		Graphics2D g = con.getGraphicsWithAlpha();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.black);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
		g.fillRoundRect(0, 0, width, con.height, 20, 20);		
		
		con.redrawTexture();
	}
	
	public void drawConsole(){
		consoleBox.render();
		
		textEngine.beginRendering(width, height);
		textEngine.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		for(int i = 0; i < consoleNumLines; i++)
			textEngine.draw(console[i], 10,(height-consoleBox.y) - 
					(consoleFont.getSize() * (i) + 15));
		   
		textEngine.endRendering();
	}
	
	public void writeLine(String text){
		for(int i = 0; i < consoleNumLines - 1; i++)
			console[i] = console[i+1];
		
		console[consoleNumLines-1] = text;
	}
	
	public void add(Element2D e){
		contentPanel.add(e);
	}
	
	
}
