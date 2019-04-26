package collision;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class AABB {
	/*
	so the basic gist of this is BOXES WOOHOO
	cube made of 8 vertices
	ex.
	front top left vertex of the box is (minX, maxY, minZ)
	back right bottom vertex is (maxX, minY, maxZ)
	 */
	
	//the "cold" or non-changing
	private Vector3f sizeMin;
	private Vector3f sizeMax;
	
	//the "hot" or changing
	private Vector3f min_extents;
	private Vector3f max_extents;
	
	public AABB(Vector3f min, Vector3f max) {
		this.min_extents = min;
		this.max_extents = max;
		
		this.sizeMin = min;
		this.sizeMax = max;
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
	
	public boolean isIntersects(AABB box2) {
		if(box2.getMin_extents().x > this.min_extents.x) return false;
		if(box2.getMin_extents().y > this.min_extents.y) return false;
		if(box2.getMin_extents().z > this.min_extents.z) return false;
		
		if(box2.getMax_extents().x > this.max_extents.x) return false;
		if(box2.getMax_extents().y > this.max_extents.y) return false;
		if(box2.getMax_extents().z > this.max_extents.z) return false;
		
		return true;
	}
	
//	public Intersection intersects(AABB box2) {
//		Vector3f distances1 = new Vector3f();
//		Vector3f.sub(box2.getMin_extents(), this.getMax_extents(), distances1);
//		Maths.absVec(distances1);
//		
//		Vector3f distances2 = new Vector3f();
//		Vector3f.sub(this.getMin_extents(), box2.getMax_extents(), distances2);
//		Maths.absVec(distances2);
//		
//		//find the smallest length
//		Vector3f dist = distances1.length() < distances2.length() ? distances1 : distances2;
//		float maxDistance = max(dist);
//		
//		return new Intersection(maxDistance < 0, Math.abs(maxDistance));
//	}
	
	public Vector3f getMin_extents() {
		return min_extents;
	}

	public Vector3f getMax_extents() {
		return max_extents;
	}

	public void setMin_extents(Vector3f min_extents) {
		this.min_extents = min_extents;
	}

	public void setMax_extents(Vector3f max_extents) {
		this.max_extents = max_extents;
	}
	
	public Vector3f getSizeMin() {
		return sizeMin;
	}

	public Vector3f getSizeMax() {
		return sizeMax;
	}


	@Override
	public String toString() {
		return this.getMin_extents() + " and " + this.getMax_extents();
	}
}
