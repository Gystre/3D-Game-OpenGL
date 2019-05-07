package toolbox;

public class Point3f {
	float x, y, z;
	
	public Point3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float distanceSquared(Point3f p2) {
		float d1 = this.x - p2.x;
		float d2 = this.y - p2.y;
		float d3 = this.z - p2.z;
		return d1 * d1 + d2 * d2 + d3 * d3;
	}
	
	public float distance(Point3f p2) {
		float d1 = this.x - p2.x;
		float d2 = this.y - p2.y;
		float d3 = this.z - p2.z;
		return (float) Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
	}
	
//	public float plus(Point3f p2) {
//		return new Point3f(this.x + p2.x, this.y + p2.y, this.z + p2.z);
//	}
	
	public Point3f minus(Point3f p2) {
		return new Point3f(this.x - p2.x, this.y - p2.y, this.z - p2.z);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	
}
