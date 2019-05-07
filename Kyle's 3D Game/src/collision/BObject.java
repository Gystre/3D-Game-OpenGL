package collision;

import org.lwjgl.util.vector.Vector3f;

public abstract class BObject {
	public abstract Intersection intersects(BSphere bObject);

	//somehow move this thing to only in bounding sphere
	public abstract void translate(Vector3f position, float height);
}
