package entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import render.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	private static final float RUN_ACCELERATION = 50; //units per second
	private static final float TURN_SPEED = 150; //degrees per second
	private static final float GRAVITY = -20;
	private static final float JUMP_POWER = 10;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0; //how much the position.y value will increase per second

	private boolean inAir = false;
	
	public Player(TexturedModel model, Vector3f position, float rX, float rY, float rZ, float scale) {
		super(model, position, rX, rY, rZ, scale);
	}
	
	public void move(List<Terrain> world) {
		checkInputs();
		
		//multiply by getFrameTimeSecond to make sure player doesn't move faster or slower than game
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		//woah dude trigonometry
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getrY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getrY())));
		super.increasePosition(dx, 0, dz);
		
		//falling
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
	
		//detection of height in terrain chunk
		float terrainheight = getTerrainHeight(world);
		if(super.getPosition().y < terrainheight) {
			upwardsSpeed = 0;
			inAir = false; //set to false because we have landed
			super.getPosition().y = terrainheight;
		}
	}
	
	private float getTerrainHeight(List<Terrain> world) {
		float height = 0;
		
		for(Terrain terrain : world) {
			if(terrain.getX() <= this.getPosition().x) {
				if(terrain.getX() + terrain.getSize() > this.getPosition().x) {
					if(terrain.getZ() <= this.getPosition().z) {
						if(terrain.getZ() + terrain.getSize() > this.getPosition().z) {
							height = terrain.getHeightOfTerrain(this.getPosition().x, this.getPosition().z);
						}
					}
				}
			}
		}
		
		return height;
	}
	
	private void checkInputs() {		
		//try currentspeed = velocity + (player acceleration * delta time)
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if(currentSpeed > RUN_ACCELERATION) {
				this.currentSpeed = RUN_ACCELERATION;
			}else {
				this.currentSpeed += RUN_ACCELERATION * DisplayManager.getFrameTimeSeconds();
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if(currentSpeed < -RUN_ACCELERATION) {
				this.currentSpeed = -RUN_ACCELERATION;
			}else {
				this.currentSpeed -= RUN_ACCELERATION * DisplayManager.getFrameTimeSeconds();
			}
		}else {
			currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(!inAir) {
				this.upwardsSpeed = JUMP_POWER;
				inAir = true;
			}
		}
	}
}
