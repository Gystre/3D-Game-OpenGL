package entities;

import org.lwjgl.util.vector.Vector3f;

import collision.AABB;
import models.TexturedModel;
import toolbox.Maths;

public class Entity {
	//instance of textured model
	private TexturedModel model;
	private Vector3f position;
	private float rX, rY, rZ;
	private float scale;
	
	//index of texture in atlas
	private int textureIndex = 0;
	
	private AABB hitbox;
	
	public Entity(TexturedModel model, AABB hitbox, Vector3f position, float rX, float rY, float rZ, float scale) {
		this.model = model;
		this.position = position;
		this.rX = rX;
		this.rY = rY;
		this.rZ = rZ;
		this.scale = scale;
		this.hitbox = hitbox;
		
		this.hitbox.setMin_extents(Maths.mulVec(hitbox.getSizeMin(), position));
		this.hitbox.setMax_extents(Maths.mulVec(hitbox.getSizeMax(), position));
	}
	
	public Entity(TexturedModel model, int index, Vector3f position, float rX, float rY, float rZ, float scale) {
		this.model = model;
		this.position = position;
		this.rX = rX;
		this.rY = rY;
		this.rZ = rZ;
		this.scale = scale;
		this.textureIndex = index;
	}
	
	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float)column / (float)model.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float)row / (float)model.getTexture().getNumberOfRows();
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		
		//update the hitbox's postion
		this.hitbox.setMin_extents(Maths.mulVec(hitbox.getSizeMin(), position));
		this.hitbox.setMax_extents(Maths.mulVec(hitbox.getSizeMax(), position));
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rX += dx;
		this.rY += dy;
		this.rZ += dz;
		
		this.hitbox.setMin_extents(Maths.mulVec(hitbox.getSizeMin(), position));
		this.hitbox.setMax_extents(Maths.mulVec(hitbox.getSizeMax(), position));
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getrX() {
		return rX;
	}

	public void setrX(float rX) {
		this.rX = rX;
	}

	public float getrY() {
		return rY;
	}

	public void setrY(float rY) {
		this.rY = rY;
	}

	public float getrZ() {
		return rZ;
	}

	public void setrZ(float rZ) {
		this.rZ = rZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public AABB getHitbox() {
		return hitbox;
	}
	
}
