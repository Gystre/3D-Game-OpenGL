package skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import render.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram{
    private static final String VERTEX_FILE = "/skybox/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "/skybox/skyboxFragmentShader.txt";
     
    private static final float ROTATE_SPEED = 1f; //1 degrees per second
    
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColor;
    private int location_cubeMap1;
    private int location_cubeMap2;
    private int location_blendFactor;
    
    private float rotation = 0;
     
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0; // 3rd column 0-2 are where translation of skybox is stored
        matrix.m31 = 0;
        matrix.m32 = 0;
        
        rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix); //0,1,0 is the along the y-axis
        super.loadMatrix(location_viewMatrix, matrix);
    }
    
    public void connectTextureUnits() {
    	super.loadInt(location_cubeMap1, 0);
    	super.loadInt(location_cubeMap2, 1);
    }
    
    public void loadFogColor(float r, float g, float b) {
    	super.loadVector(location_fogColor, new Vector3f(r, g, b));
    }
    
    public void loadBlendFactor(float blend) {
    	super.loadFloat(location_blendFactor, blend);
    }
     
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColor = super.getUniformLocation("fogColor");
        location_cubeMap1 = super.getUniformLocation("cubeMap1");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
