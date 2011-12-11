package domino;

import javax.media.opengl.GL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.io.*;

import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.sun.opengl.util.j2d.TextureRenderer;
import com.sun.opengl.util.texture.*;
import static javax.media.opengl.GL.*;

/**
 * This class represent all visible 3D objects in the world.
 * @author pc1
 *
 */
public class Element3D extends Element {
	private Vector<Vertex> vertices = new Vector<Vertex>();
	private Vector<Vertex> texCoordinates = new Vector<Vertex>();
	private Vector<Vertex> normals = new Vector<Vertex>();

	private Vector<Face> sourceFaces = new Vector<Face>();
	private Vector<Face> faces = new Vector<Face>();

	public Vertex center;
	public Vertex rotate;
	public float scale = 1;

	public float length;
	public float width;
	public float height;

	// Default values for 3D objects
	private int polyType = GL_QUADS;
	private int shadeMode = GL_SMOOTH;
	private boolean wireFrame = false;
	private Texture texture = null;

	// Used for simulation, if its alive it will move
	public boolean alive = true;
	private int direction;

	// Shadow settings
	public Vertex lightPos = null;
	public boolean castShadow = true;

	public Element3D(String iden, GL gl) {
		this(iden, null, gl);
	}

	public Element3D(String iden, Element3D parent, GL gl) {
		super(iden, parent, gl);

		// Position in the center of the world
		center = new Vertex(0, 0, 0);
		rotate = new Vertex(0, 0, 0);
	}

	public void moveTo(Vertex v) {
		center = v;
	}

	public void rotateTo(Vertex v) {
		rotate = v;
	}

	public void scaleTo(float s) {
		scale = s;
	}

	public void moveX(float x) {
		center.x += x;
	}

	public void moveY(float y) {
		center.y += y;
	}

	public void moveZ(float z) {
		center.z += z;
	}

	public void rotateX(float angle) {
		rotate.x += angle;
	}

	public void rotateY(float angle) {
		rotate.y += angle;
	}

	public void rotateZ(float angle) {
		rotate.z += angle;
	}

	public void setPolyType(int pType) {
		polyType = pType;
	}

	public void setShadeMode(int mode) {
		shadeMode = mode;
	}

	public void setWireframe(boolean b) {
		wireFrame = b;
	}

	public int getNumVertices() {
		return vertices.size();
	}

	public Vertex getVertexIndex(int i) {
		return vertices.get(i);
	}

	public void setDirection(int dir) {
		direction = dir;
	}

	public int getDirection() {
		return direction;
	}

	public void placeElement() {
		float parentX, parentY, parentZ;

		parentX = parentY = parentZ = 0;

		if (parent != null) {
			parentX = ((Element3D) parent).center.x;
			parentY = ((Element3D) parent).center.y;
			parentZ = ((Element3D) parent).center.z;
		}

		gl.glTranslatef(center.x + parentX, center.y + parentY, center.z+ parentZ);

		gl.glRotatef(rotate.x, 1, 0, 0);
		gl.glRotatef(rotate.y, 0, 1, 0);
		gl.glRotatef(rotate.z, 0, 0, 1);

		gl.glScalef(scale, scale, scale);
	}

	public void render() {
		if (!visible || vertices.size() == 0)
			return;

		if (wireFrame) {
			renderWireframe();
			return;
		}

		if (texture != null) {
			texture.enable();
			texture.bind();
		}

		gl.glPushMatrix();
		placeElement();

		gl.glColor4f(1.0f, 1.0f, 1.0f, transperncy);
		gl.glShadeModel(shadeMode);
		
		// Render vertices
		gl.glBegin(GL_QUADS);
			renderAllVertices();
		gl.glEnd();
		
		gl.glPopMatrix();

		if (texture != null) {
			texture.disable();
		}
	}

	public void renderAllVertices() {
		Iterator<Vertex> i = vertices.iterator();
		Iterator<Vertex> texCoord = texCoordinates.iterator();
		Iterator<Vertex> norm = normals.iterator();

		// Loop through all the vertices
		while (i.hasNext()) {
			Vertex v = i.next();

			// Setup the texture coordinates
			if (texCoord.hasNext() && texture != null) {
				Vertex uv = texCoord.next();

				gl.glTexCoord2f(uv.x, uv.y);
			}

			// Setup the normals
			if (norm.hasNext()) {
				Vertex n = norm.next();
				gl.glNormal3f(n.x, n.y, n.z);
			}
			//gl.glEnable(GL_NORMALIZE);

			// Draw the vertex
			gl.glVertex3f(v.x, v.y, v.z);
		}
	}

	public void renderWireframe() {
		gl.glPushMatrix();
		placeElement();
		gl.glDisable(GL_LIGHTING);
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		gl.glBegin(polyType);
			gl.glColor4f(1.0f, 1.0f, 0.0f, transperncy);
			renderAllVertices();
		gl.glEnd();
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		gl.glEnable(GL_LIGHTING);
		gl.glPopMatrix();
	}

	public void renderFlat() {
		gl.glPushMatrix();
		placeElement();
		gl.glDisable(GL_LIGHTING);
		gl.glBegin(polyType);
		gl.glColor4f(1.0f, 1.0f, 0.0f, transperncy);
			renderAllVertices();
		gl.glEnd();
		gl.glEnable(GL_LIGHTING);
		gl.glPopMatrix();
	}
	
	public static void renderDummyBox(Vertex v, GL gl) {
		Element3D e = createBox("Dummy", 0.25f, 0.25f, 0.25f, gl);
		e.center = v;
		e.renderFlat();
	}

	public void renderShadow() {
		transformFaces();
		calculateLitFaces();

		Vector<Edge> contour = findShadowEdges();
		renderShadowVolume(contour);

		//renderEdgeList(contour);      
	}

	public void renderShadowVolume(Vector<Edge> contour) {
		Iterator<Edge> i = contour.iterator();

		while (i.hasNext()) {
			Edge e1 = i.next();

			Edge e2 = e1.extrudeFromPoint(lightPos, 5);

			gl.glPushMatrix();
			gl.glDisable(GL_LIGHTING);
			gl.glBegin(GL_QUADS);

			gl.glVertex3f(e1.v2.x, e1.v2.y, e1.v2.z);
			gl.glVertex3f(e1.v1.x, e1.v1.y, e1.v1.z);

			gl.glVertex3f(e2.v1.x, e2.v1.y, e2.v1.z);
			gl.glVertex3f(e2.v2.x, e2.v2.y, e2.v2.z);

			gl.glEnd();
			gl.glPopMatrix();
		}
	}

	public float[] getTransformMatrix() {
		float[] matrix = new float[16];

		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glTranslatef(center.x, center.y, center.z);

		gl.glRotatef(rotate.x, 1, 0, 0);
		gl.glRotatef(rotate.y, 0, 1, 0);
		gl.glRotatef(rotate.z, 0, 0, 1);

		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrix, 0);
		gl.glPopMatrix();

		return matrix;
	}

	private Vertex transformedVertex(Vertex v, float[] m) {
		float X = v.x;
		float Y = v.y;
		float Z = v.z;

		Vertex result = new Vertex();

		result.x = X * m[0] + Y * m[4] + Z * m[8] + m[12];
		result.y = X * m[1] + Y * m[5] + Z * m[9] + m[13];
		result.z = X * m[2] + Y * m[6] + Z * m[10] + m[14];

		return result;
	}

	public void setLightPos(Vertex v) {
		Iterator<Element> i = child.listIterator();

		lightPos = v;

		// Set for all children elements
		while (i.hasNext()) {
			Element3D e = (Element3D) i.next();

			e.lightPos = v;
		}
	}

	public void calculateLitFaces() {
		Iterator<Face> f = faces.iterator();

		while (f.hasNext()) {
			Face face = f.next();

			if (lightPos != null)
				face.faces(lightPos);
		}
	}

	public Vector<Edge> findShadowEdges() {
		Vector<Edge> contour = new Vector<Edge>();

		Iterator<Face> i = faces.iterator();

		int tag = 1;

		while (i.hasNext()) {
			Face f1 = i.next();

			Iterator<Face> j = faces.iterator();

			while (j.hasNext()) {
				Face f2 = j.next();

				if (f1.equals(f2))
					break;

				Edge e = Face.sharedEdge(f1, f2);

				if (e != null && e.tag == 0 && f1.visible != f2.visible) {
					e.tag = tag++;
					contour.add(e);
				}
			}
		}

		for (int c = 0; c < contour.size(); c++)
			contour.get(c).tag = 0;

		return contour;
	}

	public void transformFaces() {
		Iterator<Face> f = sourceFaces.iterator();

		float[] m = getTransformMatrix();

		faces.removeAllElements();

		while (f.hasNext()) {
			Face face = f.next();
			Face destFace = new Face();

			// calculate edges
			for (int i = 0; i < 4; i++) {
				destFace.e[i] = new Edge(transformedVertex(face.e[i].v1, m),
						transformedVertex(face.e[i].v2, m));
			}

			// apply to vertices
			destFace.recalcFace();

			faces.add(destFace);
		}
	}

	public static Element3D createGrid(String id, float length, float spacing,
			GL gl) {
		Element3D e = new Element3D(id, gl);

		for (float y = 0; y < length; y += spacing) {
			for (float x = 0; x < length; x += spacing) {
				e.vertices
						.add(new Vertex(-(x * spacing), -(y * spacing), 0.0f));
				e.vertices.add(new Vertex(-(x * spacing), (y * spacing), 0.0f));
				e.vertices.add(new Vertex((x * spacing), (y * spacing), 0.0f));
				e.vertices.add(new Vertex((x * spacing), -(y * spacing), 0.0f));
			}
		}

		return e;
	}

	public static Element3D createDomino(String id, GL gl) {
		Element3D e = Element3D.loadObj("media/objects/dom02.obj", "", id, 1,
				gl);

		createDominoTexture(e);

		return e;
	}

	public static void createDominoTexture(Element3D e) {
		TextureRenderer texRenderer = new TextureRenderer(128, 128, true);
		Graphics2D g = texRenderer.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//Color[] colors = {Color.red, Color.blue, Color.green,
		//              Color.yellow, Color.orange, Color.blue, Color.blue};

		Random r = new Random();
		int number = r.nextInt(6);

		int width, height;
		width = height = 128;

		//g.setColor(colors[number].darker());

		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		AffineTransform t = g.getTransform();

		t.translate(0, height);
		t.scale(2, -1.0);

		g.setTransform(t);
		g.setColor(Color.black);

		number = r.nextInt(7);
		drawPattern(0, 0, number, width / 2, height / 2, g);

		number = r.nextInt(7);
		drawPattern(0, height / 2, number, width / 2, height / 2, g);

		g.translate(0, -height / 2);
		g.setColor(Color.black);
		g.fillRect(0, (height / 2) - 2, width, 4);

		texRenderer.markDirty(0, 0, width, height);
		e.texture = texRenderer.getTexture();

		Vector<Vertex> v = e.texCoordinates;

		// Unused faces texture
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));

		// Front texture
		v.add(new Vertex(1, 0, 0));
		v.add(new Vertex(1, 1, 0));
		v.add(new Vertex(0, 1, 0));
		v.add(new Vertex(0, 0, 0));

		// Not used texture coordinates
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));
		v.add(new Vertex(0, 0, 0));

		// Back texture
		v.add(new Vertex(1, 0, 0));
		v.add(new Vertex(1, 1, 0));
		v.add(new Vertex(0, 1, 0));
		v.add(new Vertex(0, 0, 0));
	}

	private static void drawPattern(int x, int y, int number, int width,
			int height, Graphics2D g) {
		int dotSize = 15;

		int centerX = width / 2;
		int centerY = height / 2;

		int quarterX = width / 4;
		int quarterY = width / 4;

		g.translate(x, y);

		switch (number) {
		case 1:
			drawCircle(centerX, centerY, dotSize, g);
			break;
		case 2:
			drawCircle(quarterX, centerY, dotSize, g);
			drawCircle(quarterX * 3, centerY, dotSize, g);
			break;
		case 3:
			drawCircle(quarterX, quarterY, dotSize, g);
			drawCircle(centerX, centerY, dotSize, g);
			drawCircle(quarterX * 3, quarterY * 3, dotSize, g);
			break;
		case 4:
			drawCircle(quarterX, quarterY, dotSize, g);
			drawCircle(quarterX, quarterX * 3, dotSize, g);
			drawCircle(quarterX * 3, quarterY, dotSize, g);
			drawCircle(quarterX * 3, quarterX * 3, dotSize, g);
			break;
		case 5:
			drawCircle(centerX, centerY, dotSize, g);
			drawCircle(quarterX, quarterY, dotSize, g);
			drawCircle(quarterX, quarterX * 3, dotSize, g);
			drawCircle(quarterX * 3, quarterY, dotSize, g);
			drawCircle(quarterX * 3, quarterX * 3, dotSize, g);
			break;
		case 6:
			drawCircle(quarterX, quarterY, dotSize, g);
			drawCircle(quarterX, centerY, dotSize, g);
			drawCircle(quarterX, quarterX * 3, dotSize, g);
			drawCircle(quarterX * 3, quarterY, dotSize, g);
			drawCircle(quarterX * 3, centerY, dotSize, g);
			drawCircle(quarterX * 3, quarterX * 3, dotSize, g);
			break;
		}
	}

	private static void drawCircle(int centerX, int centerY, int radius,
			Graphics2D g) {
		g.fillOval(centerX - (radius / 2), centerY - (radius / 2), radius,
				radius);
	}
	
	public static Element3D createBox(String id, float width, float length,
			float height, GL gl) {

		Element3D e = new Element3D(id, gl);

		e.vertices = Element3D.box(width, length, height);

		e.width = width;
		e.length = length;
		e.height = height;

		return e;
	}
	
	public static Vector<Vertex> box(float width, float length, float height) {
		Vector<Vertex> v = new Vector<Vertex>();

		width /= 2;
		length /= 2;

		v.add(new Vertex(width, length, 0)); // Bottom
		v.add(new Vertex(-width, length, 0));
		v.add(new Vertex(-width, -length, 0));
		v.add(new Vertex(width, -length, 0));

		v.add(new Vertex(width, length, height)); // Top
		v.add(new Vertex(-width, length, height));
		v.add(new Vertex(-width, -length, height));
		v.add(new Vertex(width, -length, height));

		v.add(new Vertex(width, -length, 0)); // Side1
		v.add(new Vertex(width, -length, height));
		v.add(new Vertex(width, length, height));
		v.add(new Vertex(width, length, 0));

		v.add(new Vertex(width, length, 0)); // Side2
		v.add(new Vertex(width, length, height));
		v.add(new Vertex(-width, length, height));
		v.add(new Vertex(-width, length, 0));

		v.add(new Vertex(-width, -length, 0)); // Side3
		v.add(new Vertex(-width, -length, height));
		v.add(new Vertex(-width, length, height));
		v.add(new Vertex(-width, length, 0));

		v.add(new Vertex(width, -length, 0)); // Side4
		v.add(new Vertex(width, -length, height));
		v.add(new Vertex(-width, -length, height));
		v.add(new Vertex(-width, -length, 0));

		return v;
	}

	public static Element3D createAxis(String id, float length, GL gl) {
		Element3D e = new Element3D(id, gl);

		Element3D x = Element3D.createBox("X-Axis", length, 0.2f, 0.2f, gl);
		Element3D y = Element3D.createBox("Y-Axis", 0.2f, length, 0.2f, gl);
		Element3D z = Element3D.createBox("Z-Axis", 0.2f, 0.2f, length, gl);

		x.moveTo(new Vertex(length / 2, 0, 0));
		x.shadeMode = GL_FLAT;
		e.add(x);

		y.moveTo(new Vertex(0, length / 2, 0));
		y.shadeMode = GL_FLAT;
		e.add(y);

		z.shadeMode = GL_FLAT;
		e.add(z);

		return e;
	}

	public static Element3D loadObj(String fileName, String textureFile,
			String iden, GL gl) {
		return Element3D.loadObj(fileName, textureFile, iden, 1.0f, gl);
	}

	public static Element3D loadObj(String fileName, String textureFile,
			String iden, float size, GL gl) {

		Element3D e = loadObjFile(iden, gl, fileName, textureFile, size);

		e.texture = loadTexture(textureFile);

		return e;
	}

	private static Element3D loadObjFile(String iden, GL gl, String fileName,
			String textureFile, float size) {
		try {
			Element3D e = new Element3D(iden, gl);
			Vector<Vertex> vertices = new Vector<Vertex>();
			Vector<Vertex> texCoord = new Vector<Vertex>();
			Vector<Vertex> normals = new Vector<Vertex>();

			Vector<Vertex> finalVertices = new Vector<Vertex>();
			Vector<Vertex> finalTexCoord = new Vector<Vertex>();
			Vector<Vertex> finalNormals = new Vector<Vertex>();
			Vector<Face> finalFaces = new Vector<Face>();

			StringTokenizer st;
			String type;

			BufferedReader data = new BufferedReader(new FileReader(fileName));

			String line = data.readLine();

			while (line != null) {
				st = new StringTokenizer(line, " ");
				type = "";

				if (st.hasMoreElements())
					type = st.nextToken();

				if (type.equals("v")) {
					float x = Float.valueOf(st.nextToken()) * size;
					float y = Float.valueOf(st.nextToken()) * size;
					float z = Float.valueOf(st.nextToken()) * size;

					vertices.add(new Vertex(x, z, y));
				}

				if (type.equals("vt")) {
					texCoord.add(new Vertex(Float.valueOf(st.nextToken()),
							Float.valueOf(st.nextToken()), 0));
				}

				if (type.equals("vn")) {
					float x = Float.valueOf(st.nextToken()) * size;
					float y = Float.valueOf(st.nextToken()) * size;
					float z = Float.valueOf(st.nextToken()) * size;

					normals.add(new Vertex(x, z, y));
				}

				if (type.equals("f")) {
					int faceType = st.countTokens();

					Vertex faceVerts[] = new Vertex[4];

					for (int i = 0; i < faceType; i++) {
						StringTokenizer num = new StringTokenizer(st
								.nextToken(), "/");

						Vertex currVertex = vertices.get(Integer.valueOf(num
								.nextToken()) - 1);
						faceVerts[i] = new Vertex(currVertex);

						// Vertex locations
						finalVertices.add(currVertex);

						// Texture Coordinates
						if (texCoord.size() > 0)
							finalTexCoord.add(texCoord.get(Integer.valueOf(num
									.nextToken()) - 1));

						// Face normals
						finalNormals.add(normals.get(Integer.valueOf(num
								.nextToken()) - 1));
					}

					if (faceVerts[3] != null)
						finalFaces.add(new Face(faceVerts));
				}

				line = data.readLine();
			}

			data.close();
			e.vertices = finalVertices;
			e.texCoordinates = finalTexCoord;
			e.normals = finalNormals;
			e.sourceFaces = finalFaces;

			return e;
		} catch (Exception e) {
			System.out.println("Cannot load file:" + fileName + "("
					+ e.getMessage() + ")");
			return null;
		}
	}

	public static Texture loadTexture(String textureFile) {
		try {
			if (textureFile.length() == 0)
				return null;

			Texture t = TextureIO.newTexture(new File(textureFile), false);
			t.setTexParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			t.setTexParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			return t;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}