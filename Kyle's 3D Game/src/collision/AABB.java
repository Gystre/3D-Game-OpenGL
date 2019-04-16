package collision;

import org.lwjgl.util.vector.Vector3f;

public class AABB {
	/*
	so the basic gist of this is BOXES WOOHOO
	cube made of 8 vertices
	ex.
	front top left vertex of the box is (minX, maxY, minZ)
	back right bottom vertex is (maxX, minY, maxZ)
	 */
	
	private Vector3f min_extents;
	private Vector3f max_extents;
	
	public AABB(Vector3f min, Vector3f max) {
		this.min_extents = min;
		this.max_extents = max;
	}
	
	private float max(Vector3f vec) {
		float big = vec.getX();
		
		if(vec.getY() > big) {
			big = vec.getY();
		}
		
		if(vec.getZ() > big) {
			big = vec.getZ();
		}
		
		return big;
	}
	
	public Vector3f compare(Vector3f vec1, Vector3f vec2) {
		if(vec1.length() < vec2.length()) {
			return vec1;
		}
		return vec2;
		
	}
	
	public Intersection intersects(AABB box2) {
		Vector3f distances1 = new Vector3f();
		Vector3f.sub(box2.getMin_extents(), this.getMax_extents(), distances1);
		
		Vector3f distances2 = new Vector3f();
		Vector3f.sub(this.getMin_extents(), box2.getMax_extents(), distances2);
		
		//find the smallest length
		Vector3f dist = compare(distances1, distances2);
		float maxDistance = max(dist);
		
		return new Intersection(maxDistance < 0, Math.abs(maxDistance));
	}
	
	public Vector3f getMin_extents() {
		return min_extents;
	}

	public Vector3f getMax_extents() {
		return max_extents;
	}
}
