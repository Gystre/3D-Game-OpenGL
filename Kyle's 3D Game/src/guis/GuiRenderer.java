package guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import render.Loader;
import toolbox.Maths;

public class GuiRenderer {
	//only need one quad to store all guis because use transformation matrix to move them around
	private final RawModel quad;
	private GuiShader shader;
	
	public GuiRenderer(Loader loader) {
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1}; //positions of triangle vertices
		
		quad = loader.loadToVAO(positions, 2);
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis) {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND); //enable blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //enable alpha blending
		GL11.glDisable(GL11.GL_DEPTH_TEST); //disable depth test so guis can be on top of one another
		
		//render
		for(GuiTexture gui : guis) {
			//bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			
			//create matrix
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
