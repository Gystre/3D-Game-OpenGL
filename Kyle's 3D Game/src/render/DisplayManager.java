package render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	private static final int width = 1280;
	private static final int height = 720;
	private static final int fps_cap = 60;
	
	private static long lastFrameTime;
	private static float delta; //time taken to render last frame
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3, 2) //opengl ver3.2
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(new PixelFormat().withDepthBits(24), attribs); //with depth bits to prevent water looking jagged and glitchy when zoomed out
			Display.setTitle("ultra good");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, width, height);
		
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		Display.sync(fps_cap);
		Display.update();
		
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f; //convert from milliseconds to seconds
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	private static long getCurrentTime() {
		//multiply by 1000 to get milliseconds
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
