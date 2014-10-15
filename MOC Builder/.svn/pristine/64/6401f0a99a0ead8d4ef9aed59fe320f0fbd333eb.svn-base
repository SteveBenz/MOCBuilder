package Builder;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import Common.Vector3f;
import LDraw.Support.type.LDrawGridTypeT;

public class Baseplate {
	private float startx;
	private float finishx;
	private float starty;
	private float finishy;
	private float height;
	private MainCamera camera;
	private int slice = 12;

	private float adjustedStartx;
	private float adjustedFinishx;
	private float adjustedStarty;
	private float adjustedFinishy;

	public Baseplate(MainCamera camera) {
		height = 0;
		startx = -240;
		finishx = 240;
		starty = -240;
		finishy = 240;

		adjustedStartx = startx;
		adjustedFinishx = finishx;
		adjustedStarty = starty;
		adjustedFinishy = finishy;

		this.camera = camera;
	}

	private final int unitSize = LDrawGridTypeT.Coarse.getXZValue();

	public void draw(GL2 gl2) {

		slice = (int) (12 * 300.0f / camera.getDistanceBetweenObjectToCamera());

		if (slice > 24)
			slice = 24;
		if (slice < 4)
			slice = 4;

		// slice = (4, 23)
		// 4일땐 원래 값 그대로 사용.

		adjustedStartx = startx;
		adjustedFinishx = finishx;
		adjustedStarty = starty;
		adjustedFinishy = finishy;

		if (adjustedStartx < -120 * (26 - slice) + camera.getLookAtPos().x)
			adjustedStartx = -120 * (26 - slice) + camera.getLookAtPos().x;
		if (adjustedFinishx > 120 * (26 - slice) + camera.getLookAtPos().x)
			adjustedFinishx = 120 * (26 - slice) + camera.getLookAtPos().x;

		if (adjustedStarty < -120 * (26 - slice) + camera.getLookAtPos().z)
			adjustedStarty = -120 * (26 - slice) + camera.getLookAtPos().z;
		if (adjustedFinishy > 120 * (26 - slice) + camera.getLookAtPos().z)
			adjustedFinishy = 120 * (26 - slice) + camera.getLookAtPos().z;

		if (BuilderConfigurationManager.getInstance().isUseConnectivity())
			gl2.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
		else
			gl2.glColor4f(0.5f, 0.5f, 0.5f, 0.05f);
		// Draw Stud Disk (possible styles: FILL, LINE, POINT).
		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (Vector3f vertex : getCylinderVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

		gl2.glBegin(GL2.GL_TRIANGLES);
		for (Vector3f vertex : getDiskVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();
	}

	private Vector3f[] getDiskVertices() {
		int width = (int) (adjustedFinishx - adjustedStartx);
		int height = (int) (adjustedFinishy - adjustedStarty);

		height /= unitSize;
		width /= unitSize;

		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float diskRadius = LDrawGridTypeT.Medium.getXZValue() / 1.6f;
		final float diskHeight = -LDrawGridTypeT.Fine.getYValue() * 2;

		Vector3f vertex;
		for (int column = 0; column < height; column += 1) {
			for (int row = 0; row < width; row += 1) {
				vertex = new Vector3f(LDrawGridTypeT.Coarse.getXZValue()
						+ LDrawGridTypeT.Coarse.getXZValue() * row, diskHeight,
						-LDrawGridTypeT.Coarse.getXZValue() / 2
								- LDrawGridTypeT.Coarse.getXZValue() * (column));

				for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
					vertices.add(vertex);
					vertices.add(vertex.add(
							(float) (diskRadius * Math.cos((sliceIndex)
									* Math.PI * 2 / (slice - 1))),
							0,
							(float) (diskRadius * Math.sin((sliceIndex)
									* Math.PI * 2 / (slice - 1)))));
					vertices.add(vertex.add(
							(float) (diskRadius * Math.cos((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1))),
							0,
							(float) (diskRadius * Math.sin((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1)))));
				}
			}
		}
		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = vertices.get(i).add(adjustedStartx, 0,
					adjustedFinishy);
		}
		return retArray;
	}

	private Vector3f[] getCylinderVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		int width = (int) (adjustedFinishx - adjustedStartx);
		int height = (int) (adjustedFinishy - adjustedStarty);

		height /= unitSize;
		width /= unitSize;

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 1.6f;
		final float cylinderHeight = -LDrawGridTypeT.Fine.getYValue() * 2;

		Vector3f vertex;
		for (int column = 0; column < height; column++) {
			for (int row = 0; row < width; row++) {
				vertex = new Vector3f(LDrawGridTypeT.Coarse.getXZValue()
						+ LDrawGridTypeT.Coarse.getXZValue() * (row), 0,
						-LDrawGridTypeT.Coarse.getXZValue() / 2
								- LDrawGridTypeT.Coarse.getXZValue() * (column));

				for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
					vertices.add(vertex.add(
							(float) (cylinderRadius * Math.cos(sliceIndex
									* Math.PI * 2 / (slice - 1))),
							0,
							(float) (cylinderRadius * Math.sin(sliceIndex
									* Math.PI * 2 / (slice - 1)))));
					vertices.add(vertex.add(
							(float) (cylinderRadius * Math.cos((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1))),
							0,
							(float) (cylinderRadius * Math.sin((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1)))));
					vertices.add(vertex.add(
							(float) (cylinderRadius * Math.cos((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1))),
							cylinderHeight,
							(float) (cylinderRadius * Math.sin((sliceIndex + 1)
									* Math.PI * 2 / (slice - 1)))));
					vertices.add(vertex.add(
							(float) (cylinderRadius * Math.cos(sliceIndex
									* Math.PI * 2 / (slice - 1))),
							cylinderHeight,
							(float) (cylinderRadius * Math.sin(sliceIndex
									* Math.PI * 2 / (slice - 1)))));
				}
			}
		}
		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = vertices.get(i).add(adjustedStartx, 0,
					adjustedFinishy);
		}
		return retArray;
	}

	public void setRange(float[] range) {
		if (range[0] > -240)
			range[0] = -240;
		if (range[1] < 240)
			range[1] = 240;
		if (range[2] > -240)
			range[2] = -240;
		if (range[3] < 240)
			range[3] = 240;
		if (range[4] >= 3.40282347E+38f)
			range[4] = 0;
		if (range[5] <= -3.40282347E+38f)
			range[5] = 0;

		for (int i = 0; i < 4; i++) {
			int r = Math.round(range[i] / unitSize);
			if (r < 0)
				r -= 2;
			else
				r += 1;
			range[i] = (float) r * unitSize;
		}
		startx = range[0];
		finishx = range[1];
		starty = range[2];
		finishy = range[3];
		height = range[5];
	}

	public float getStartx() {
		return startx;
	}

	public void setStartx(float startx) {
		this.startx = startx;
	}

	public float getFinishx() {
		return finishx;
	}

	public void setFinishx(float finishx) {
		this.finishx = finishx;
	}

	public float getStarty() {
		return starty;
	}

	public void setStarty(float starty) {
		this.starty = starty;
	}

	public float getFinishy() {
		return finishy;
	}

	public void setFinishy(float finishy) {
		this.finishy = finishy;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
