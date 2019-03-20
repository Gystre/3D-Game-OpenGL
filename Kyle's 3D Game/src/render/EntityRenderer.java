package render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for(TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			
			for(Entity entity : batch) {
				prepareInstance(entity);
				//indices are all positive so unsigned int
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		//from VAO
		GL20.glEnableVertexAttribArray(0); //enable positions
		GL20.glEnableVertexAttribArray(1); //enable texture coords
		GL20.glEnableVertexAttribArray(2); //enable normals
		
		//update shine and damper variables
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		
		if(texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShineVariable(texture.getShineDamper(), texture.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		//tell opengl which texture to use
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		
	}
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		//unbind it
		GL30.glBindVertexArray(0);
	}
	
	//per entity
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getrX(), entity.getrY(), entity.getrZ(), entity.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
		
		//load location of texture from atlas
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
}
