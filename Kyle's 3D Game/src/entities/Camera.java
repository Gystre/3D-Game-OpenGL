package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private float distanceFromPlayer = 50; //zoom
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20; //how high or low
	private float yaw; //how left or right
	private float roll; //how much tilt, think of the camera in the middle of a circle

	private final static float MAXZOOM = 160;
	private final static float MINZOOM = 15;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		
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
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.08f; //0.1 to slow it down
		distanceFromPlayer -= zoomLevel;
		
		if(distanceFromPlayer > MAXZOOM) distanceFromPlayer = MAXZOOM;
		if(distanceFromPlayer < MINZOOM) distanceFromPlayer = MINZOOM;
	}
	
	private void calculatePitch() {
		if(Mouse.isButtonDown(0)) {
			float pitchChange = Mouse.getDY() * 0.08f;
			pitch -= pitchChange;
			
			if(pitch > 85) pitch = 85;
			if(pitch < 1) pitch = 1;
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.08f;
			angleAroundPlayer -= angleChange;
		}
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
