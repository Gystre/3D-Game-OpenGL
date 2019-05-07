package entities;

import org.lwjgl.util.vector.Vector3f;

import collision.BObject;
import models.TexturedModel;
import render.DisplayManager;

public class Projectile extends Entity{
	private float distanceTraveled = 0;
	private float currentSpeed = 350;

	public Projectile(TexturedModel model, Vector3f position, BObject hitbox, float h, float rX, float rY, float rZ, float scale) {
		super(model, position, hitbox, h, rX, rY, rZ, scale);
	}
	
	public void move() {
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		distanceTraveled += distance;
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getrY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getrY())));
		super.increasePosition(dx, 0, dz);
	}

	public float getDistanceTraveled() {
		return distanceTraveled;
	}
}
