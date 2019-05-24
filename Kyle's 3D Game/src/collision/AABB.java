package collision;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class AABB {
	public Vector3f min_extents;
	public Vector3f max_extents;

	public AABB(Vector3f min, Vector3f max) {
		this.min_extents = min;
		this.max_extents = max;
	}
	
	public boolean isIntersects(AABB box2) {
		  return (this.min_extents.x <= box2.max_extents.x && this.max_extents.x >= box2.min_extents.x) &&
			         (this.min_extents.y <= box2.max_extents.y && this.max_extents.y >= box2.min_extents.y) &&
			         (this.min_extents.z <= box2.max_extents.z && this.max_extents.z >= box2.min_extents.z);
	}
	
	public void translate(Vector3f pos) {
		//min_extents = Maths.addVec(vec1, vec2)
	}
	
	public float getWidth() {
		return max_extents.x - min_extents.x;
	}
	
	public float getHeight() {
		return max_extents.y - min_extents.y;
	}
	
	public float getDepth() {
		return max_extents.z - min_extents.z;
	}
}
