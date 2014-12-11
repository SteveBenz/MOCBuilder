package Connectivity;

import java.util.ArrayList;
import java.util.HashMap;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import Renderer.ILDrawCollector;
import Renderer.LDrawTextureSpec;

public class LDrawVertexCollector implements ILDrawCollector {

	private Matrix4 transform;

	private static HashMap<LDrawPart, ArrayList<Vector3f>> quadMap;
	private static HashMap<LDrawPart, ArrayList<Vector3f>> triMap;

	public LDrawVertexCollector(LDrawPart part, Matrix4 transform) {

		if (quadMap == null)
			quadMap = new HashMap<LDrawPart, ArrayList<Vector3f>>();
		if (triMap == null)
			triMap = new HashMap<LDrawPart, ArrayList<Vector3f>>();

		boolean needCollect = false;
		if (quadMap.containsKey(part))
			quad = quadMap.get(part);
		else {
			quad = new ArrayList<Vector3f>();
			needCollect = true;
		}
		if (triMap.containsKey(part))
			tri = triMap.get(part);
		else {
			tri = new ArrayList<Vector3f>();
			needCollect = true;
		}

		this.transform = transform;

		if (part.getCacheModel() != null && needCollect == true) {
			part.getCacheModel().collectSelf(this);
			quadMap.put(part, quad);
			triMap.put(part, tri);
		}

	}

	private ArrayList<Vector3f> quad = null;
	private ArrayList<Vector3f> tri = null;

	public ArrayList<Vector3f> getQuadList() {
		ArrayList<Vector3f> ret = new ArrayList<Vector3f>();
		for (Vector3f vector : quad)
			ret.add(transform.transformPoint(vector));
		return ret;
	}

	public ArrayList<Vector3f> getTriList() {
		ArrayList<Vector3f> ret = new ArrayList<Vector3f>();
		for (Vector3f vector : tri)
			ret.add(transform.transformPoint(vector));

		return ret;
	}

	@Override
	public void pushTexture(LDrawTextureSpec tex_spec) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popTexture() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawQuad(float[] vertices, float[] normal, float[] color) {
		Vector3f normalVector = new Vector3f(normal[0], normal[1], normal[2]);
		normalVector.normalize();
		for (int i = 0; i < vertices.length; i += 3)
			for (int j = 0; j < 3; j++)
				vertices[i + j] -= normalVector.getArray()[j];

		float[] center = new float[3];
		for (int i = 0; i < 3; i++)
			center[i] = (vertices[i] + vertices[6 + i]) / 2;

		for (int i = 0; i < vertices.length; i += 3)
			for (int j = 0; j < 3; j++)
				vertices[i + j] = (vertices[i + j] - center[j]) * 0.9f
						+ center[j];

		for (int i = 0; i < vertices.length; i += 3) {
			quad.add(new Vector3f(vertices[i], vertices[i + 1],
					vertices[i + 2]));
		}
		
	}

	@Override
	public void drawTri(float[] vertices, float[] normal, float[] color) {
		Vector3f normalVector = new Vector3f(normal[0], normal[1], normal[2]);
		normalVector.normalize();
		for (int i = 0; i < vertices.length; i += 3)
			for (int j = 0; j < 3; j++)
				vertices[i + j] -= normalVector.getArray()[j];

		float[] center = new float[3];
		for (int i = 0; i < 3; i++)
			center[i] = (vertices[i] + vertices[3 + i] + vertices[6 + i]) / 3;

		for (int i = 0; i < vertices.length; i += 3)
			for (int j = 0; j < 3; j++)
				vertices[i + j] = (vertices[i + j] - center[j]) * 0.9f
						+ center[j];

		for (int i = 0; i < vertices.length; i += 3) {
			tri.add(new Vector3f(vertices[i], vertices[i + 1],
					vertices[i + 2]));
		}
	}

	public static boolean isTooSmall(Vector3f[] vertices) {
		Vector3f edge[] = new Vector3f[4];
		float[] edgeLength = new float[4];
		for (int k = 0; k < 4; k++) {
			edge[k] = vertices[(k + 1) % 4].sub(vertices[k]);
			edgeLength[k] = edge[k].length();
		}

		boolean isTooSmall = true;
		for (int k = 0; k < 2; k++)
			if (edgeLength[k] >= 5)
				isTooSmall = false;

		return isTooSmall;
	}

	@Override
	public void drawLine(float[] vertices, float[] normal, float[] color) {
		// TODO Auto-generated method stub
	}
}
