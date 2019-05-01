package entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import collision.AABB;
import models.TexturedModel;
import render.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	private static final float RUN_ACCELERATION = 60; //units per second
	private static final float TURN_SPEED = 150; //degrees per second
	private static final float GRAVITY = -20;
	private static final float JUMP_POWER = 10;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0; //how much the position.y value will increase per second

	private boolean inAir = false;
	
	public Player(TexturedModel model, AABB hitbox, Vector3f position, float rX, float rY, float rZ, float scale) {
		super(model, hitbox, position, rX, rY, rZ, scale);
	}
	
	public void move(List<Terrain> world) {
		checkInputs();
		
		//multiply by getFrameTimeSecond to make sure player doesn't move faster or slower than game
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		//woah dude trigonometry
		//calculate how much to move the camera and player based on currentSpeed and rotation
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
		for(Terrain terrain : world) {
			if(terrain.getX() <= this.getPosition().x && terrain.getX() + terrain.getSize() > this.getPosition().x) {
				if(terrain.getZ() <= this.getPosition().z && terrain.getZ() + terrain.getSize() > this.getPosition().z) {
					return terrain.getHeightOfTerrain(this.getPosition().x, this.getPosition().z);
				}
			}
		}
		
		return 0;
	}
	
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			//if player reaches max speed, keep them that way
			if(currentSpeed > RUN_ACCELERATION) {
				this.currentSpeed = RUN_ACCELERATION;
			}else {
				//otherwise increase speed
				this.currentSpeed += RUN_ACCELERATION * DisplayManager.getFrameTimeSeconds();
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if(currentSpeed < -RUN_ACCELERATION) {
				this.currentSpeed = -RUN_ACCELERATION;
			}else {
				this.currentSpeed -= RUN_ACCELERATION * DisplayManager.getFrameTimeSeconds();
			}
		}else {
			//release keys stop em cold
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
