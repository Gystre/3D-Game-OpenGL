package collision;

import org.lwjgl.util.vector.Vector3f;

public class CollisionPacket {
	private Vector3f pos;
	private float distance;
	private int type; //0 = null, 1 = surface, 2 = vertex, 3 = edge
	public CollisionPacket(Vector3f pos, float distance, int type) {
		this.pos = pos;
		this.distance = distance;
		this.type = type;
	}
	public Vector3f getPos() {
		return pos;
	}
	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
