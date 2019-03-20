package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {
	private int texture;
	private Vector2f position; //center of quad in opengl coordinate system
	private Vector2f scale; //size of quad in relation to size of screen
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getScale() {
		return scale;
	}
	
	
}
