package collision;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class BSphere extends BObject{
	private Vector3f center;
	private float radius;
	
	public BSphere(Vector3f center, float radius) {
		this.center = center;
		this.radius = radius;
	}
	
	@Override
	public Intersection intersects(BSphere sphere) {
		float radiusDistance = radius + sphere.radius;
		float centerDistance = Maths.subVec(sphere.getCenter(), center).length();
		
		return new Intersection(centerDistance < radiusDistance, centerDistance - radiusDistance);
	}
	
	@Override
	public void translate(Vector3f by, float height) {
		center = new Vector3f(by.x, by.y + height, by.z);
	}

	public Vector3f getCenter() {
		return center;
	}

	public void setCenter(Vector3f center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	
}
