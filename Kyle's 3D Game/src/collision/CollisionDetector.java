package collision;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

public class CollisionDetector {
	/*
	 	Let's face it. This math is beyond me. 
	 	Got it from a really cool YouTube video: https://www.youtube.com/watch?v=6Nkn1x9DSSE
	 
	 	This be AP Geometry bruh
	 */
	
	private Vector3f ellipsoidPosRS; // real space IR
	private Vector3f ellipsoidPosES; // eye space IE
	
	private Vector3f triangleP1R; //real space triangle points
	private Vector3f triangleP2R;
	private Vector3f triangleP3R;
	
	private Vector3f triangleP1E; //eye space triangle points
	private Vector3f triangleP2E;
	private Vector3f triangleP3E;
	
	private Vector3f P2P1E; //use these to calculate normal vector to plane
	private Vector3f P2P3E;
	
	private Vector3f planeIntersectionPointR;
	private Vector3f planeIntersectionPointE;
	
	private Vector3f planeNormal;
	private Vector3f planeUnitNormal; //plane normal that been normalised and has a magnitude of 1

	private float planeConstantD;
	private float planeConstantP;
	
	private float distance;
	
	private float t0; //time at which the sphere is intersecting the plane
	private float t1;
	
	private Vector3f velocityR; //vel in real space
	private Vector3f velocityE; //vel in eye space
	
	private Matrix3f toESpaceMatrix = new Matrix3f(); //take vectors from real space to eye space
	private Matrix3f fromESpaceMatrix;
	
	private CollisionPacket packetVertex;
	private CollisionPacket packetSurface;
	private CollisionPacket packetEdge;
	
	public CollisionDetector(Vector3f pos, Vector3f vel, Vector3f P1, Vector3f P2, Vector3f P3, float ellipsoidMaxX, float ellipsoidMaxY, float ellipsoidMaxZ) {
		this.ellipsoidPosRS = pos; //player.getPosition
		this.velocityR = vel; //player.getCurrentSpeed
		this.triangleP1R = P1;
		this.triangleP2R = P2;
		this.triangleP3R = P3;
		
		this.toESpaceMatrix.m00 = (1/ellipsoidMaxX);
		this.toESpaceMatrix.m11 = (1/ellipsoidMaxY);
		this.toESpaceMatrix.m22 = (1/ellipsoidMaxZ);
	}
	
	public void sendValuesToESpace() {
		//transform real space values into eye space values
		Matrix3f.transform(toESpaceMatrix, triangleP1R, triangleP1E);
		Matrix3f.transform(toESpaceMatrix, triangleP2R, triangleP2E);
		Matrix3f.transform(toESpaceMatrix, triangleP2R, triangleP2E);
		
		Matrix3f.transform(toESpaceMatrix, velocityR, velocityE);
		
		Matrix3f.transform(toESpaceMatrix, ellipsoidPosRS, ellipsoidPosES);
	}
	
	public void constructCollisionValues() {
		P2P1E = Vector3f.sub(triangleP1E, triangleP2E, P2P1E);
		P2P3E = Vector3f.sub(triangleP3E, triangleP2E, P2P3E);
		
		planeNormal = Vector3f.cross(P2P3E, P2P1E, planeNormal);
		planeUnitNormal = planeNormal.normalise(planeUnitNormal);
		
		planeConstantD = (planeNormal.x * triangleP2E.x * -1) + (planeNormal.y * triangleP2E.y * -1) + (planeNormal.z * triangleP2E.z * -1);
		planeConstantP = (float)(planeConstantD / (Math.sqrt((planeNormal.x * planeNormal.x) + (planeNormal.y * planeNormal.y) + (planeNormal.z * planeNormal.z))));
		
		distance = Vector3f.dot(planeUnitNormal, ellipsoidPosES) + planeConstantP; //signed distance
		
		//find where distance is 0	
		
		if(Vector3f.dot(planeUnitNormal, velocityE) == 0.0) {
			t0 = 0;
			t1 = 1;
			System.err.println("object is moving parallel to plane");
		}else {
			try {
				t0 = (1-distance) / (Vector3f.dot(planeUnitNormal, velocityE));
			}catch(IllegalArgumentException e) {
				t0 = 0;
				t1 = 1;
				System.err.println("objet is moving parallel to plane");
			}
			
			try {
				t1 = (-1-distance) / (Vector3f.dot(planeUnitNormal, velocityE));
			}catch(IllegalArgumentException e) {
				t0 = 0;
				t1 = 1;
				System.err.println("object is moving parallel to plane");
			}
		}
		
		if(t1 < t0) {
			float temp = t1;
			t1 = t0;
			t0 = temp;
		}
	}
	
	public CollisionPacket isCollides() {
		sendValuesToESpace();
		constructCollisionValues();
		
		//if true, sphere is traveling parallel to plane
		if(Vector3f.dot(planeUnitNormal, velocityE) == 0) {
			//no collision
			if(Math.abs(distance) > 1) return null;
		}else if(((t0 < 0) || (t0 > 1)) && ((t1 < 0) || (t1 > 1))){
			return null;
		}
		
		planeIntersectionPointE = Vector3f.sub(ellipsoidPosES, planeUnitNormal, planeIntersectionPointE);
	
		Vector3f velExt0 = new Vector3f(velocityE.x * t0, velocityE.y * t0, velocityE.z * t0);
		planeIntersectionPointE = Vector3f.add(planeIntersectionPointE, velExt0, planeIntersectionPointE);
	
		if(checkPositionWithTriangle(planeIntersectionPointE, triangleP1E, triangleP2E, triangleP3E)) {
			return packetSurface = new CollisionPacket(planeIntersectionPointE, distance, 0);
		}else {
			Vector3f ellipsoidVertexDistance = new Vector3f();
			Vector3f vertexEllipsoidDistance = new Vector3f();
			
			float vertexTime1;
			float vertexTime2;
			float smallestSolutionVertex = 10005;
			
			Vector3f collisionPoint = new Vector3f();
			
			float a = velocityE.lengthSquared();
			
			//first vertex
			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPosES, triangleP1E, ellipsoidVertexDistance);
			
			float b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);
			
			vertexEllipsoidDistance = Vector3f.sub(triangleP1E, ellipsoidPosES, vertexEllipsoidDistance);
		
			float c = vertexEllipsoidDistance.lengthSquared()-1;
			
			if((b*b)-(4*a*c) >= 0) {
				vertexTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				vertexTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0) && ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP1E;
				}else if((vertexTime2 < vertexTime1) && (vertexTime2 <= t1) && (vertexTime2 >= t0) && ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP1E;
				}
			}
			
			//second vertex
			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPosES, triangleP2E, ellipsoidVertexDistance);
			
			b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);
			
			vertexEllipsoidDistance = Vector3f.sub(triangleP2E, ellipsoidPosES, vertexEllipsoidDistance);
		
			c = vertexEllipsoidDistance.lengthSquared()-1;
			
			if((b*b)-(4*a*c) >= 0) {
				vertexTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				vertexTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0) && ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP2E;
				}else if((vertexTime2 < vertexTime1) && (vertexTime2 <= t1) && (vertexTime2 >= t0) && ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP2E;
				}
			}
			
			//third vertex
			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPosES, triangleP3E, ellipsoidVertexDistance);
			
			b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);
			
			vertexEllipsoidDistance = Vector3f.sub(triangleP3E, ellipsoidPosES, vertexEllipsoidDistance);
		
			c = vertexEllipsoidDistance.lengthSquared()-1;
			
			if((b*b)-(4*a*c) >= 0) {
				vertexTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				vertexTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0) && ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP3E;
				}else if((vertexTime2 < vertexTime1) && (vertexTime2 <= t1) && (vertexTime2 >= t0) && ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))){
					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP3E;
				}
			}
			
			//has been modified
			if(smallestSolutionVertex != 10005) {
				float vertexDistance = smallestSolutionVertex * velocityE.length();
				packetVertex = new CollisionPacket(collisionPoint, vertexDistance, 2);
			}
			
			Vector3f edge = new Vector3f();
			Vector3f baseToVertex = new Vector3f();
			Vector3f edgeIntersectionPoint = new Vector3f();
			
			float intersectionDistance;
			Vector3f fromEdgePoint = new Vector3f();
			Vector3f smallestEdge = new Vector3f();
			
			float edgeTime1;
			float edgeTime2;
			float smallestSolutionEdge = 10005;
			float smallerSolutionEdge;
			float smallerF = -1; //F be function
			float smallestF = 0;
			
			//check first edge
			edge = Vector3f.sub(triangleP2E, triangleP1E, edge);
			baseToVertex = Vector3f.sub(triangleP1E, ellipsoidPosES, baseToVertex);
			
			a = edge.lengthSquared() * -1 * velocityE.lengthSquared() + (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex) - (2* Vector3f.dot(edge,velocityE) * Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared()) + (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));
			
			if((b*b)-(4*a*c) >= 0) {
				edgeTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				edgeTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if(edgeTime1 <= edgeTime2) {
					smallerSolutionEdge = edgeTime1;
				}else {
					smallerSolutionEdge = edgeTime2;
				}
				
				if(smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {
					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge) - Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
				}
				
				if(smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {
					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP1E;
					smallestEdge = edge;
				}
			}
			
			//may or may not need this not sure
			smallerF = -1;
			
			//check second edge
			edge = Vector3f.sub(triangleP3E, triangleP2E, edge);
			baseToVertex = Vector3f.sub(triangleP1E, ellipsoidPosES, baseToVertex);
			
			a = edge.lengthSquared() * -1 * velocityE.lengthSquared() + (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex) - (2* Vector3f.dot(edge,velocityE) * Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared()) + (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));
			if((b*b)-(4*a*c) >= 0) {
				edgeTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				edgeTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if(edgeTime1 <= edgeTime2) {
					smallerSolutionEdge = edgeTime1;
				}else {
					smallerSolutionEdge = edgeTime2;
				}
				
				if(smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {
					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge) - Vector3f.dot(edge, baseToVertex));
				}
				
				if(smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {
					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP2E;
					smallestEdge = edge;
				}
			}
			
			//may or may not need this not sure
			smallerF = -1;
			
			//check third edge
			edge = Vector3f.sub(triangleP1E, triangleP3E, edge);
			baseToVertex = Vector3f.sub(triangleP3E, ellipsoidPosES, baseToVertex);
			
			a = edge.lengthSquared() * -1 * velocityE.lengthSquared() + (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex) - (2* Vector3f.dot(edge,velocityE) * Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared()) + (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));
			
			if((b*b)-(4*a*c) >= 0) {
				edgeTime1 = (-b + (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				edgeTime2 = (-b - (float)Math.sqrt((double)((b*b)-(4*a*c)))/(2*a));
				
				if(edgeTime1 <= edgeTime2) {
					smallerSolutionEdge = edgeTime1;
				}else {
					smallerSolutionEdge = edgeTime2;
				}
				
				if(smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {
					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge) - Vector3f.dot(edge, baseToVertex));
				}
				
				if(smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {
					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP3E;
					smallestEdge = edge;
				}
			}
			
			if(smallestSolutionEdge != 10005) {
				edgeIntersectionPoint = Vector3f.add(fromEdgePoint, (Vector3f)edge.scale(smallestF), edgeIntersectionPoint);
				intersectionDistance = smallestSolutionEdge * velocityE.length();
				
				packetEdge = new CollisionPacket(edgeIntersectionPoint, intersectionDistance, 3);
			}
		} //end of the edge/vertex portion
		
		if(packetEdge != null || packetEdge != null){
			try {
				if(packetEdge.getDistance() < packetVertex.getDistance()) {
					return packetEdge;
				}
			}catch(NullPointerException e) { }
			
			try {
				if(packetVertex.getDistance() < packetEdge.getDistance()) {
					return packetVertex;
				}
			}catch(NullPointerException e) { }
			
			if(packetEdge != null) {
				return packetEdge;
			}
			
			if(packetVertex != null) {
				return packetVertex;
			}
		}
		
		return null;
	}
	
	//check if position in triangle
	public boolean checkPositionWithTriangle(Vector3f pos, Vector3f P1, Vector3f P2, Vector3f P3) {
		float angles = 0;
		
		Vector3f v1 = new Vector3f();
		Vector3f.sub(pos, P1, v1);
		Vector3f v2 = new Vector3f();
		Vector3f.sub(pos, P2, v2);
		Vector3f v3 = new Vector3f();
		Vector3f.sub(pos, P3, v3);
		v1.normalise();
		v2.normalise();
		v3.normalise();
		
		angles += Math.acos(Vector3f.dot(v1, v2));
		angles += Math.acos(Vector3f.dot(v1, v3));
		angles += Math.acos(Vector3f.dot(v3, v2));
		
		//check if angles add up to 2pi
		return (Math.abs(angles - 2 * Math.PI) <= 0.005);
	}
}
