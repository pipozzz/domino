package domino;
/**
 * 
 * Utiltily class to manage coordinates in 2D and 3D.
 * @author pc1
 *
 */
public class Vertex{
	public float x;
	public float y;
	public float z;
	
	// default all new vertex objects to (0,0,0)
	public Vertex(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	// overload default constructor
	public Vertex(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// copy constructor
	public Vertex(Vertex v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	// update vertex
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// move x component of vertex
	public void moveX(float offset){
		x += offset;
	}
	
	// move y component of vertex
	public void moveY(float offset){
		y += offset;
	}
	
	// move z component of vertex
	public void moveZ(float offset){
		z += offset;
	}
	
	// distance calculations
	public float distanceTo(Vertex v){
		return (float) Math.sqrt(((x - v.x) * (x - v.x))+
								 ((y - v.y) * (y - v.y))+
								 ((z - v.z) * (z - v.z)));
	}
	
	// output to string
	public String toString(){
		return "x: " + x + "\t, y: " + y +  "\t, z: " + z;
	}
	
	// dot product
	public final float dot(Vertex v1){
		return (this.x*v1.x + this.y*v1.y + this.z*v1.z);
	}
	
	// test if vertex is equal
	public boolean equals(Vertex v){
		if (v.x == x && v.y == y && v.z == z)
			return true;
		
		return false;
	}
}


// an edge is two vertices 
class Edge{
	public Vertex v1;
	public Vertex v2;
	
	public int tag = 0;
	
	public Edge(){	}
	
	public Edge(Vertex v1, Vertex v2){
		this.v1 = v1;
		this.v2 = v2;
	}
	
	// test edges for equality
	public boolean equals(Edge e){
		if (v1.equals(e.v1) && v2.equals(e.v2)
			|| v1.equals(e.v2) && v2.equals(e.v1))
			return true;
		
		return false;
	}
	
	// return an edge from a specific vertex. used mainly for shadows. 
	public Edge extrudeFromPoint(Vertex p, float amount){
		Edge result = new Edge();
		
		result.v1 = new Vertex(
				( v1.x - p.x ) * amount,
				( v1.y - p.y  ) * amount,
				( v1.z - p.z  ) * amount);
		
		result.v2 = new Vertex(
				( v2.x - p.x ) * amount,
				( v2.y - p.y  ) * amount,
				( v2.z - p.z  ) * amount);
		
		return result;
	}
}

// a face is a collection of edges
class Face{
	public Edge[] e = new Edge[4];
	public Vertex[] v = new Vertex [4];
	public boolean visible = false;
	
	float a,b,c,d;
	
	public Face(){	}
	
	public Face(Vertex[] v){
		this(	new Edge(v[0], v[1]), 
				new Edge(v[1], v[2]), 
				new Edge(v[2], v[3]),
				new Edge(v[3], v[0]));
	}
	
	public Face(Edge e0, Edge e1, Edge e2, Edge e3){
		e = new Edge[4];
		
		e[0] = e0;
		e[1] = e1;
		e[2] = e2;
		e[3] = e3;
		
		v[0] = e[0].v1;
		v[1] = e[1].v1;
		v[2] = e[2].v1;
		v[3] = e[3].v1;		
	}

	public boolean faces(Vertex v){	
		calculatePlane();
		
		Float side = a*v.x + b*v.y + c*v.z + d;
		
		if (side > 0)
			return visible = true;
		else
			return visible = false;
	}
	
	void calculatePlane(){
		// Get Shortened Names For The Vertices Of The Face
		Vertex v1 = v[0];
		Vertex v2 = v[1];
		Vertex v3 = v[2];

		a = v1.y*(v2.z-v3.z) + v2.y*(v3.z-v1.z) + v3.y*(v1.z-v2.z);
		b = v1.z*(v2.x-v3.x) + v2.z*(v3.x-v1.x) + v3.z*(v1.x-v2.x);
		c = v1.x*(v2.y-v3.y) + v2.x*(v3.y-v1.y) + v3.x*(v1.y-v2.y);
		d = -( v1.x*( v2.y*v3.z - v3.y*v2.z ) 
				+ v2.x*(v3.y*v1.z - v1.y*v3.z) 
					+ v3.x*(v1.y*v2.z - v2.y*v1.z) );
	}
	
	public void recalcFace(){
		v[0] = e[0].v1;
		v[1] = e[1].v1;
		v[2] = e[2].v1;
		v[3] = e[3].v1;	
	}
	
	public static Face createFace(Edge e1, Edge e2){
		Face result = new Face(e1,
							new Edge(e1.v2, e2.v2),
							new Edge(e2.v2, e2.v1),
							new Edge(e2.v1, e1.v1));

		return result;
	}
	
	public static Edge sharedEdge(Face f1, Face f2){
		Edge result = null;
		
		for (int i = 0 ; i < 4; i++){
			for (int j = 0 ; j < 4 ; j++){
				if (f1.e[i].equals(f2.e[j])){
					return f1.e[i];
				}
			}
		}
		
		return result;
	}
}