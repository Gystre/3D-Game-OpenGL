package collision;

public class Intersection {
	private boolean intersects;
	private float distance;
	
	public Intersection(boolean intersects, float distance) {
		this.intersects = intersects;
		this.distance = distance;
	}

	public boolean isIntersects() {
		return intersects;
	}

	public float getDistance() {
		return distance;
	}
}