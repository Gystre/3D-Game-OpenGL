package fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}
	
	//take in all the texts on the screen
	public void render(Map<FontType, List<GUIText>> texts) {
		//enable alpha blending and disable depth test
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		shader.start();
			//first render font for each set of text
			//second render text with the font
			for(FontType font : texts.keySet()) {
				//bind texture atlas
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
				
				for(GUIText text : texts.get(font)) {
					renderText(text);
				}
			}
		shader.stop();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		//load up them uniform variables
		shader.loadColor(text.getColor());
		shader.loadTranslation(text.getPosition());
		shader.loadBorderInfo(text.getBorderInfo());
		shader.loadDropInfo(text.getDropInfo());
		shader.loadOutlineColor(text.getOutlineColor());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount()); //draw trianges, start at first vertext, and draw vertex count number of vertices 
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

}
