package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private float distanceFromPlayer = 50; //zoom
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20; //how high or low
	private float yaw; //how left or right
	private float roll; //how much tilt

	private final static float MAXZOOM = 160;
	private final static float MINZOOM = 10;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		
		calculateCameraPosition(horizontalDistance, verticalDistance);
		
		this.yaw = 180 - (player.getrY() + angleAroundPlayer);
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.getrY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.08f; //0.1 to slow it down
		distanceFromPlayer -= zoomLevel;
		
		if(distanceFromPlayer > MAXZOOM) distanceFromPlayer = MAXZOOM;
		if(distanceFromPlayer < MINZOOM) distanceFromPlayer = MINZOOM;
	}
	
	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * 0.13f;
		pitch -= pitchChange;
		
		if(pitch > 85) pitch = 85;
		if(pitch < 1) pitch = 1;
	}
	
	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * 0.13f;
		angleAroundPlayer -= angleChange;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	public void invertPitch() {
		//used in calculating the reflection texture for water
		this.pitch = -pitch;
	}
	
}
