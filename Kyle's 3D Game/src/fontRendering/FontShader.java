package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/fontRendering/fontFragment.txt";
	
	private int location_color;
	private int location_translation;
	private int location_borderInfo;
	private int location_dropInfo;
	private int location_outlineColor;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
		location_borderInfo = super.getUniformLocation("borderInfo");
		location_dropInfo = super.getUniformLocation("dropInfo");
		location_outlineColor = super.getUniformLocation("outlineColor");
	}

	//bind attributes of vao defined in loader
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	protected void loadBorderInfo(Vector2f borderInfo) {
		super.load2DVector(location_borderInfo, borderInfo);
	}
	
	protected void loadDropInfo(Vector2f dropInfo) {
		super.load2DVector(location_dropInfo, dropInfo);
	}
	
	protected void loadOutlineColor(Vector3f outlineColor) {
		super.loadVector(location_outlineColor, outlineColor);
	}
	
	protected void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}
	
	protected void loadTranslation(Vector2f translation) {
		super.load2DVector(location_translation, translation);
	}

}
