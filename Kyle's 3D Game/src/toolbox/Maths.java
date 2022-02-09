package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

public class Maths {
	//misc math functions
	
	//transformation matrix
	//used for guis
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		
		return matrix;
	}
	
	//takes in 3d position of triangle and position of player and find the center of it
	//used for calculating current polygon height of terrain
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	//transformation matrix: translation rotation, and scale
	//used for entities
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity(); //resets matrix
		Matrix4f.translate(translation, matrix, matrix); //store in matrix
		//1,0,0 = x-axis, 0,1,0 = y-axis, 0,0,1 = z-axis
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		
		return matrix;
	}
	
	//view matrix: pitch, yaw, roll
	//used for camera
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		
		Matrix4f.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		
		Vector3f cameraPos = camera.getPosition();
		//make position negative so move world in opposite direction of player
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		
		return viewMatrix;
	}
	
	//would put these vec related functions in the source file but not really programming it now am I :P
	public static Vector3f addVec(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
	}
	
	public static Vector3f subVec(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z);
	}
	
	public static Vector3f minVec(Vector3f vec1, Vector3f vec2) {
		Vector3f out = new Vector3f();
		out.x = Math.min(vec1.x, vec2.x);
		out.y = Math.min(vec1.y, vec2.y);
		out.z = Math.min(vec1.z, vec2.z);
		
		return out;
	}
	
	public static Vector3f maxVec(Vector3f vec1, Vector3f vec2) {
		Vector3f out = new Vector3f();
		out.x = Math.max(vec1.x, vec2.x);
		out.y = Math.max(vec1.y, vec2.y);
		out.z = Math.max(vec1.z, vec2.z);
		return out;
	}
	
	public static Vector3f mulVecAndMat(Matrix4f mat, Vector3f vec) {
		Vector3f ret = new Vector3f();
		
		ret.x = vec.x * mat.m00 + mat.m01 * vec.y + mat.m02 * vec.z + mat.m03 * 1;
		ret.y = vec.x * mat.m10 + mat.m11 * vec.y + mat.m12 * vec.z + mat.m13 * 1;
		ret.z = vec.x * mat.m20 + mat.m21 * vec.y + mat.m22 * vec.z + mat.m02 * 1;
		
		return ret;
	}
}
