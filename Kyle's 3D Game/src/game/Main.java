package game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collision.BSphere;
import collision.Intersection;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Projectile;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJLoader;
import render.DisplayManager;
import render.Loader;
import render.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class Main {
	
	public static void main(String[] args) {
		final float MAX_DISTANCE = 1000;
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		
		//EVERY FONT I MAKE IN HIERO NO WORK :(
		//some notes on fonts:
		//hard border: higher width, lower edge
		//glow: lower width, higher edge
		//keep border edge non zero
		//drop shadow creates artifacts but can be fixed by using smaller offset or more padding in hiero
		FontType font = new FontType(loader.loadFontAtlasTexture("candara"), new File("assets/candara.fnt"));
		GUIText text = new GUIText("test", 3, font , new Vector2f(0f, 0.4f), 1f, true, new Vector2f(0.1f, 0.5f), new Vector2f(0.006f, 0.006f), new Vector3f(1f, 0, 0f));
		text.setColor(0, 1, 0);
		
		List<Projectile> bulletList = new ArrayList<Projectile>();
		
		Random random = new Random(System.nanoTime());
		
		//**********TERRAIN TEXTURE STUFF**********
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassyFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		//*****************************************
		
	    TexturedModel lampModel = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("lamp/lamp")), new ModelTexture(loader.loadTexture("models/lamp/lamp")));
	    
	    //diffuse lighting is where brightness of object's surface depends on it faces the light
	    List<Light> lights = new ArrayList<Light>();
	    Light sun = new Light(new Vector3f(0, 1000, 1000), new Vector3f(255f, 255f, 255f), new Vector3f(1, 0.1f, 0.1f));
	    lights.add(sun); //sun
	    Light redLight = new Light(new Vector3f(185, 3f, 293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f));
	    lights.add(redLight);
	    lights.add(new Light(new Vector3f(370, 17, 293), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
	    lights.add(new Light(new Vector3f(293, 7, 293), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
	    
	    List<Terrain> world = new ArrayList<Terrain>();
	    Terrain chunk00 = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
	    Terrain chunk10 = new Terrain(1, 0, loader, texturePack, blendMap, "heightmap");
	    Terrain chunk01 = new Terrain(0, 1, loader, texturePack, blendMap, "heightmap");
	    Terrain chunk11 = new Terrain(1, 1, loader, texturePack, blendMap, "heightmap");
	    world.add(chunk00);
	    world.add(chunk01);
	    world.add(chunk10);
	    world.add(chunk11);
	    
	    List<Entity> entities = new ArrayList<Entity>();
	    List<Entity> normalMapEntities = new ArrayList<Entity>();
	    
	    //ENTITIES
	    BSphere test = new BSphere(new Vector3f(0,0,0), 1);
	    
	    TexturedModel asuka = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("asuka_maid/asuka_maid")), new ModelTexture(loader.loadTexture("models/asuka_maid/test")));
	    asuka.getTexture().setShineDamper(10);
	    asuka.getTexture().setReflectivity(1);
	    
	    Player player = new Player(asuka, new Vector3f(185, 0, 293), new BSphere(new Vector3f(0,0,0), 1),2,0,180,0,1);
	    Camera camera = new Camera(player);
	    entities.add(player);
	    
	    Entity lampEntity = new Entity(lampModel, new Vector3f(185, chunk00.getHeightOfTerrain(185, 293), 293), new BSphere(new Vector3f(0,0,0), 1),2,0,0,0,1);
	    entities.add(lampEntity);
	    entities.add(new Entity(lampModel, new Vector3f(370, chunk00.getHeightOfTerrain(370, 293), 293), test,0,0,0,0,1));
	    
	    ModelTexture standing_grass = new ModelTexture(loader.loadTexture("models/standing_grass/standing_grass"));
	    TexturedModel grass = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("standing_grass/grassModel")), standing_grass);
	    
	    for(int i = 0; i < 100; i++) {
	    	float x = (float)(Math.random() * 1600);
	    	float z = (float)(Math.random() * 1600);
	    	float y = chunk00.getHeightOfTerrain(x, z);
	    	
		    grass.getTexture().setHasTransparency(true);
		    grass.getTexture().setUseFakeLighting(true);
	    	
	    	entities.add(new Entity(grass, new Vector3f(x, y, z), test,0,0,0,0,2.5f));
	    }
	    
	    ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("models/fern/fern"));
	    fernAtlas.setNumberOfRows(2);
	    TexturedModel fern = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("fern/fern")), fernAtlas);
	    
	    for(int i = 0; i < 50; i++) {
	    	float x = (float)(Math.random() * 400);
    		float z = (float)(Math.random() * 400);
	    	float y = chunk00.getHeightOfTerrain(x, z);
	    	
	    	entities.add(new Entity(fern, random.nextInt(4),new Vector3f(x, y, z),0,0,0,1.5f));
	    	
	    }
	    
	    //NORMALMAPED ENTITIES
	    //load with normalmappedobjloader because it calculates tangents of normal vectors, i think
	    TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel/barrel", loader), new ModelTexture(loader.loadTexture("models/barrel/barrel")));
	    barrelModel.getTexture().setShineDamper(10);
	    barrelModel.getTexture().setReflectivity(0.5f);
	    barrelModel.getTexture().setNormalMap(loader.loadTexture("models/barrel/barrelNormal"));
	    normalMapEntities.add(new Entity(barrelModel,  new Vector3f(205, 5, 293), test,0,0,0,0,1f));
	    
	    MasterRenderer renderer = new MasterRenderer(loader);
	    
	    //MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), chunk00);
	    
	    GuiRenderer guiRenderer = new GuiRenderer(loader);
	    List<GuiTexture> guis = new ArrayList<GuiTexture>();
	    
	    WaterFrameBuffers buffers = new WaterFrameBuffers();
	    WaterShader waterShader = new WaterShader();
	    WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
	    
	    List<WaterTile> waters = new ArrayList<WaterTile>();
	    WaterTile waterTile = new WaterTile(0, 0, -5);
	    waters.add(waterTile);
	    
//		GuiTexture gui = new GuiTexture(loader.loadTexture("supersaist"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
	    
	    //CHECK TEXTMASTER IN FONTRENDERING IF YOU HAVE NOTHING TO DO
	    
	    //REFRACTION: below the water
	    //REFLECTION: above the water, camera needs to be moved under the water to create this effect
	    
//	    CollisionPacket packet;
//	    
//	    //plane at z = 5, vel = 5, ellipsoid maxs of 5
//	    CollisionDetector detector1 = new CollisionDetector(new Vector3f(0,0,0), new Vector3f(0,0,5), new Vector3f(-500,500,5), new Vector3f(500, 500,5), new Vector3f(0,-10000,-5),5,5,5);
//	    packet = detector1.isCollides();
	    
//	    try {
//	    	System.out.println("collision at: " + packet.getDistance() + "\n type: " + packet.getType());
//	    }catch(NullPointerException e) {
//	    	System.out.println("no collision");
//	    }
	    
		//NOTE: remember to render objects three times because of water (damned water)
	    while(!Display.isCloseRequested()) {
	    	//take in keyboard inputs
	    	player.move(world);
			camera.move();
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				System.exit(0);
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				Projectile bullet = new Projectile(lampModel, new Vector3f(player.getPosition()), test,0, 0, player.getrY(), 0, 1f);
				bulletList.add(bullet);
			}
			
			for(int i = bulletList.size()-1; i >= 0; i--) {
				bulletList.get(i).move();
				
				if(bulletList.get(i).getDistanceTraveled() > MAX_DISTANCE) {
					bulletList.remove(i);
				}
			}
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
//			picker.update();
			
//			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//			
//			if(terrainPoint != null) {
//				lampEntity.setPosition(terrainPoint);
//				redLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
//			}
			
			//render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distanceToMoveCamera = 2 * (camera.getPosition().y - waterTile.getHeight());
			camera.getPosition().y -= distanceToMoveCamera;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, world, lights, camera, new Vector4f(0, 1, 0, -waterTile.getHeight() + 1f)); //0,1,0 horizontal plane pointing upwards
			for(Projectile bullet : bulletList) {
				renderer.processEntity(bullet);
			}
			camera.getPosition().y += distanceToMoveCamera;
			camera.invertPitch();
			
			//render refraction texture
			buffers.bindRefractionFrameBuffer();
			//-1 is horizontal plane pointing downwards and 15 is everything above that number will get culled
			renderer.renderScene(entities, normalMapEntities, world, lights, camera, new Vector4f(0, -1, 0, waterTile.getHeight()));
			for(Projectile bullet : bulletList) {
				renderer.processEntity(bullet);
			}
			
			//switch back to main framebuffer
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, world, lights, camera, new Vector4f(0, 0, 0, 0)); //made all zero to make dot product zero thus nothing gets culled
			for(Projectile bullet : bulletList) {
				renderer.processEntity(bullet);
			}
			
	    	//render the water tiles
	    	waterRenderer.render(waters, camera, sun);
	    	
			guiRenderer.render(guis);
			
			normalMapEntities.get(0).increaseRotation(0, 1, 0);
			
			//text render goes last because want to render on top of everything else
			TextMaster.render();
			
			//clear the framebuffer's color buffer and depth buffer
			DisplayManager.updateDisplay();
		}
		
	    TextMaster.cleanUp();
	    buffers.cleanUp();
	    waterShader.cleanUp();
	    guiRenderer.cleanUp();
	    renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
