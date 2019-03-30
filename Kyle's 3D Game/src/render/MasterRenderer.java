package render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;

public class MasterRenderer {
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000; //how far you can see things
	
	//color of fog
	public static final float RED = 0.544f;
	public static final float GREEN = 0.6f;
	public static final float BLUE = 0.6f;
	
	//create once and pass to entity renderer
	private Matrix4f projectionMatrix;
	
	//this class handles all the rendering code in the game
	//reason for creation of class is so that we don't bind and unbind models with the same texture
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private NormalMappingRenderer normalMapRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
	}
	
	public void renderScene(List<Entity> entities, List<Entity> normalMapEntities, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane) {
		for(Terrain terrain : terrains) {
			processTerrain(terrain);
		}
		
		for(Entity entity : entities) {
			processEntity(entity);
		}
		
		for(Entity entity : normalMapEntities) {
			processNormalMapEntity(entity);
		}
		
		render(lights, camera, clipPlane);
	}
	
	public static void enableCulling() {
		//don't render triangles that are outside the camera's fov
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		//choose which faces to cull, which are the back
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		prepare();
		
		shader.start();
			shader.loadClipPlane(clipPlane);
			shader.loadSkyColor(RED, GREEN, BLUE);
			shader.loadLights(lights);
			shader.loadViewMatrix(camera);
			renderer.render(entities);
		shader.stop();
		
		//after shader stop because dont want other shader program running, it mess with lighting
		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
		
		terrainShader.start();
			terrainShader.loadClipPlane(clipPlane);
			terrainShader.loadSkyColor(RED, GREEN, BLUE);
			terrainShader.loadLights(lights);
			terrainShader.loadViewMatrix(camera);
			terrainRenderer.render(terrains);
		terrainShader.stop();
		
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		
		//clear stuff from last frame
		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		
		if(batch != null) {
			//if there is already a batch for that entity add to it
			batch.add(entity);
		}else {
			//if there isn't then make a new one
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		
		if(batch != null) {
			//if there is already a batch for that entity add to it
			batch.add(entity);
		}else {
			//if there isn't then make a new one
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
	}
	
	public void prepare() {
		//enable checking of which triangle is in front of each other so they don't render in random order
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		//clear sky color
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		//background color
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	
	private void createProjectionMatrix() {
		float aspectRatio = (float)Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
}
