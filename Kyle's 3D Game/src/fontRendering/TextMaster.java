package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import render.Loader;

public class TextMaster {
	//this class in charge of all text related things
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static void init(Loader theLoader) {
		renderer = new FontRenderer();
		loader = theLoader;
	}
	
	//render all the texts!
	public static void render() {
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text) {
		FontType font = text.getFont(); //find the font
		TextMeshData data = font.loadText(text); //create the quads for each character
		
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		
		List<GUIText> textBatch = texts.get(font); //get list of texts that are associated with this font
		if(textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch); //add the batch and associate it with the current font the text uses
		}
		
		textBatch.add(text); // add the text
	}
	
	//NOTE: good idea to delete text's vao and related vbos from memory if it's never used in memory again
	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		
		if(textBatch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
}
