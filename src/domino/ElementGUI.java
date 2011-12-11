package domino;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

/**
 * This class is an extension to Element2D that allows
 * for user interaction and predefined actions. Much of the
 * functionality is still missing (low priority).
 * 
 * @author pc1
 *
 */
public abstract class ElementGUI extends Element2D{
	public static final int NORMAL = 1;
	public static final int HOVER = 2;
	public static final int PRESSED = 3;
	public static final int DRAGGED = 4;
	
	protected boolean mouseOver = false;
	protected boolean mousePressed = false;
	public int currentStyle = NORMAL;
	
	public static final Font DefaultFont = new Font("LucidaBrightDemiBold", Font.BOLD, 14);
	
	public ElementGUI(String iden, Element2D parent, int width, int height, int x, int y, GL gl){
		super(iden, parent, width, height, x, y, gl);
	}
	
	public abstract void action();

	public void clear(){
		mouseOver = false;
		mousePressed = false;
		
		currentStyle = 1;
	}
	
	public void setStyle(int style){

	}
}

// Hard coded message box element
class MessageBox extends ElementGUI{
	
	private static final int mBoxWidth = 250;
	private static final int mBoxHeight = 150;
	private static final int shadowHeight = 3;
	
	private String title;
	private String message;
	
	public MessageBox(String iden, Element2D parent, String boxTitle, 
			String boxMessage, int width, int height, GL gl){
		super(iden, parent, mBoxWidth + shadowHeight, mBoxHeight + shadowHeight, 0, 0, gl);

		this.title 		= boxTitle;
		this.message 	= boxMessage;
		
		this.x 			= (width/2) - (mBoxWidth/2);
		this.y 			= (mBoxHeight/2) + (mBoxHeight/2);
		this.width 		= mBoxWidth + shadowHeight;
		this.height 	= mBoxHeight + shadowHeight;

		Graphics2D g = getGraphicsWithAlpha();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw black box at the back
		g.setColor(Color.black);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
		g.fillRoundRect(shadowHeight, shadowHeight, mBoxWidth, mBoxHeight, 20, 20);		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
		g.fillRoundRect(0, 0, mBoxWidth, mBoxHeight, 20, 20);		
		g.fillRoundRect(0, 0, mBoxWidth, 30, 20, 20);		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		
		// Setup type
		g.setFont(ElementGUI.DefaultFont);

		// Draw title
		Rectangle2D textBounds = g.getFontMetrics().getStringBounds(title,g);
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(title, (mBoxWidth / 2) - (int)textBounds.getCenterX(), 20);
		
		// Draw message
		textBounds = g.getFontMetrics().getStringBounds(message,g);
		g.setColor(Color.white);
		g.drawString(message, (mBoxWidth / 2) - (int)textBounds.getCenterX(), 60);

		this.add(new Button ("OK", this, 110, 110, gl));
		this.add(new Button ("Cancel", this, 180, 110, gl));
		
		this.redrawTexture();
	}
	
	public void action(){
		// Default for now...
		this.visible = false;
	}
}

//Hard coded button element
class Button extends ElementGUI{

	private static final int buttonWidth = 60;
	private static final int buttonHeight = 30;
	
	private String label;
	
	public Button(String blabel, Element2D parent, int x, int y, GL gl){
		super(parent.id+"-"+blabel, parent, buttonWidth, buttonHeight , x, y, gl);
		this.label = blabel;
		
		drawNormal();
	}
	
	public void drawNormal(){
		Graphics2D g = getGraphicsWithAlpha();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw the button's background
		g.setColor(new Color(0.2f,0.2f,0.2f));
		g.fillRoundRect(0, 0, buttonWidth, buttonHeight, 9, 9);
		g.setColor(new Color(0.05f,0.05f,0.05f));
		g.fillRoundRect(1, 1, buttonWidth-2, buttonHeight-2, 9, 9);
		GradientPaint gradient = new GradientPaint(0, 0, 
									new Color(0.3f,0.3f,0.3f), 0, buttonHeight, 
									new Color(0.2f,0.2f,0.2f));
		g.setPaint(gradient);
		g.fillRoundRect(2, 2, buttonWidth-4, buttonHeight-4, 9, 9);
		g.setPaint(null);
		g.setColor(Color.white);

		// Draw the button's lablel
		g.setFont(ElementGUI.DefaultFont);

		Rectangle2D textBounds = g.getFontMetrics().getStringBounds(label,g);
		g.drawString(label, (buttonWidth / 2) - (int)textBounds.getCenterX(), 
				(buttonHeight/2) - (int)(textBounds.getY()/2) - 2);
		
		this.redrawTexture();
	}
	
	public void drawHover(){
		Graphics2D g = getGraphicsWithAlpha();
		g.setFont(ElementGUI.DefaultFont);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(0.2f,0.2f,0.2f));
		g.fillRoundRect(0, 0, buttonWidth, buttonHeight, 9, 9);
		g.setColor(new Color(0.05f,0.05f,0.05f));
		g.fillRoundRect(1, 1, buttonWidth-2, buttonHeight-2, 9, 9);
		GradientPaint gradient = new GradientPaint(0, 0, 
									new Color(0.5f,0.5f,0.5f), 0, buttonHeight, 
									new Color(0.4f,0.4f,0.4f));
		g.setPaint(gradient);
		g.fillRoundRect(2, 2, buttonWidth-4, buttonHeight-4, 9, 9);
		g.setPaint(null);
		g.setColor(Color.white);
		Rectangle2D textBounds = g.getFontMetrics().getStringBounds(label,g);
		g.drawString(label, (buttonWidth / 2) - (int)textBounds.getCenterX(), 
				(buttonHeight/2) - (int)(textBounds.getY()/2) - 2);
		
		this.redrawTexture();
	}
	
	public void drawPressed(){
		Graphics2D g = getGraphicsWithAlpha();
		g.setFont(ElementGUI.DefaultFont);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(0.2f,0.2f,0.2f));
		g.fillRoundRect(0, 0, buttonWidth, buttonHeight, 9, 9);
		g.setColor(new Color(0.05f,0.05f,0.05f));
		g.fillRoundRect(1, 1, buttonWidth-2, buttonHeight-2, 9, 9);
		GradientPaint gradient = new GradientPaint(0, 0, 
									new Color(0.3f,0.3f,0.3f), 0, buttonHeight, 
									new Color(0.4f,0.4f,0.4f));
		g.setPaint(gradient);
		g.fillRoundRect(2, 2, buttonWidth-4, buttonHeight-4, 9, 9);
		g.setPaint(null);
		g.setColor(Color.white);
		Rectangle2D textBounds = g.getFontMetrics().getStringBounds(label,g);
		g.drawString(label, 1+(buttonWidth / 2) - (int)textBounds.getCenterX(), 
				1+(buttonHeight/2) - (int)(textBounds.getY()/2) - 2);
		
		this.redrawTexture();
	}
	
	public void action(){
		// default for now...
		parent.visible = false;
	}
	
	public void setStyle(int style){
		switch (style){
		case NORMAL:
			if (currentStyle != Button.NORMAL)
				drawNormal();
			break;
		case HOVER:
			if (currentStyle != Button.HOVER)
				drawHover();
			break;
		case PRESSED:
			if (currentStyle != Button.PRESSED)
				drawPressed();
			break;
		}
		currentStyle = style;
	}
}
