package collision;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import toolbox.Maths;

public class AABB {
	/*
	 * this AABB class is based on https://github.com/chrisdickinson/aabb-3d/blob/master/index.js
	 * adapted to java of course
	 */
	
	/*
		   x0/y1/z1---x1/y1/z1
	depth->  /           /|
	        /           / |
	    x0/y1/z0 -- x1/y1/z0
	      |           |   |
	      |           | <-- height
	      |           |  /
	      |           | /
	   x0/y0/z0 ----- x1/y0/z0
	            ^
	            |
	          width
	 */
	
	private Vector3f min_extents;
	private Vector3f max_extents;
	
	private Vector3f pos; //pos is front bottom left corner
	private Vector3f whd;
	
	private Vector3f pos2; //???
	
	public AABB(Vector3f pos, Vector3f whd) {
		this.pos = pos;
		this.whd = whd;
		
		Vector3f.add(this.pos, this.whd, pos2);
		
		this.min_extents = Maths.minVec(whd, pos2);
		this.max_extents = Maths.maxVec(whd, pos2);
	}
	
	public boolean isIntersects(AABB box2) {
		if(box2.getMin_extents().x > min_extents.x) return false;
		if(box2.getMin_extents().y > min_extents.y) return false;
		if(box2.getMin_extents().z > min_extents.z) return false;
		
		if(box2.getMax_extents().x < max_extents.x) return false;
		if(box2.getMax_extents().y < max_extents.y) return false;
		if(box2.getMax_extents().z < max_extents.z) return false;
		
		return true;
	}
	
	
	public void translate(Entity entity) { 
		min_extents = Maths.addVec(pos, entity.getPosition()); 
		max_extents = Maths.addVec(pos, entity.getPosition()); }
	 	
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


	@Override
	public String toString() {
		return this.getMin_extents() + " and " + this.getMax_extents();
	}
}
