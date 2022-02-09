package collision;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
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
	
	public void translate(Entity ent) {
		Matrix4f entityMat = Maths.createTransformationMatrix(ent.getPosition(), ent.getrX(), ent.getrY(), ent.getrZ(), ent.getScale());
		min_extents = Maths.mulVecAndMat(entityMat, min_extents);
		max_extents = Maths.mulVecAndMat(entityMat, max_extents);
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
