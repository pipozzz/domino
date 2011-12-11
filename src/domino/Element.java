package domino;

import java.util.Vector;
import java.util.Iterator;

import javax.media.opengl.GL;

/**
 * Parent class for 2D and 3D elements.
 * 
 * Attributes:
 * 	id			A textual identifier to identify distinct objects
 *  visible		Specify if an element should be rendered
 *  parent		Helps create hierarchical objects
 *  child		Part of the hierarchical model (holds all the children)
 * 
 * @author pc1
 */

public abstract class Element {
	protected String id;
	protected boolean visible;
	protected float transperncy;
	
	protected Element parent;
	protected Vector<Element> child = new Vector<Element>();
	
	protected GL gl;
	
	public Element(String iden, GL gl){
		this(iden, null, gl);
	}
	
	public Element(String iden, Element parent, GL gl){
		this.id 		= iden;
		this.visible 	= true;
		this.transperncy = 1.0f;
		this.parent 	= parent;
		this.gl 		= gl;
	}
	
	public String toString(){
		return id;
	}
	
	public Element getParent(){
		return parent;
	}
	
	public void add(Element e){
		e.parent = this;
		child.add(e);
	}
	
	public void setTransperncy(float t){
		transperncy = t;
	}
	
	public Element getChild(String iden){
		if (this.id.equals(iden))
			return this;
		else{
			Element result = null;
			
			for (int i = 0; i < child.size(); i++){
				result = (Element)child.get(i).getChild(iden);
				
				if (result != null)
					return result;
			}
			
			return result;
		}
	}

	public void setVisible(boolean b){
		this.visible = b;
	}
	
	public void renderAll(){
		if (visible){
			// Render this element
			this.render();
			
			Iterator<Element> i = child.listIterator();
			
			// Render all children elements
			while (i.hasNext()){
				Element e = (Element)i.next();
				e.renderAll();
			}
		}
	}
	
	public void renderAllShadow(){
		if (visible){
			// Render this element
			this.renderShadow();
			
			Iterator<Element> i = child.listIterator();
			
			// Render all children elements
			while (i.hasNext()){
				Element e = (Element)i.next();
				e.renderAllShadow();
			}
		}
	}
	
	// Remove all children with IDs staring with 'childId'
	public void removeAllChildren(String childId){
		for (int i = 0; i < child.size(); i++){
			Element e = child.get(i);
			
			if (e.id.startsWith(childId))
				child.remove(i--);
		}
	}
	
	public abstract void render();
	public abstract void renderShadow();	// Specific to Element3D
}
