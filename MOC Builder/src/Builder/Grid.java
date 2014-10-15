package Builder;

import javax.media.opengl.GL2;

import LDraw.Support.type.LDrawGridTypeT;

public class Grid {
	private float xGridSpace;
	private float yGridSpace;

	private float startx;
	private float finishx;
	private float starty;
	private float finishy;
	private float height;
	private MainCamera camera;

	public Grid(MainCamera camera) {
		// TODO Auto-generated constructor stub
		height = 0;
		xGridSpace = 40;
		yGridSpace = 40;
		startx = -240;
		finishx = 240;
		starty = -240;
		finishy = 240;
		this.camera = camera;
	}

	public void draw(GL2 gl2) {
		gl2.glBegin(GL2.GL_LINES); // draw using triangles

		xGridSpace = yGridSpace = BuilderConfigurationManager
				.getInstance().getGridUnit().getXZValue();
		int zoom = (int) (12 * 300.0f / camera
				.getDistanceBetweenObjectToCamera());
		float adjustedStartx = startx;
		float adjustedFinishx = finishx;
		float adjustedStarty = starty;
		float adjustedFinishy = finishy;

		if (zoom >= 24)
			zoom = 24;
		if (zoom < 4)
			zoom = 4;

		if (zoom < 8) {
			xGridSpace = yGridSpace = LDrawGridTypeT.Medium.getXZValue();
		} else if (zoom <= 4)
			xGridSpace = yGridSpace = LDrawGridTypeT.Coarse.getXZValue();

		if (adjustedStartx < -120 * (25 - zoom) + camera.getLookAtPos().x)
			adjustedStartx = -120 * (25 - zoom) + camera.getLookAtPos().x;
		if (adjustedFinishx > 120 * (25 - zoom) + camera.getLookAtPos().x)
			adjustedFinishx = 120 * (25 - zoom) + camera.getLookAtPos().x;

		if (adjustedStarty < -120 * (25 - zoom) + camera.getLookAtPos().z)
			adjustedStarty = -120 * (25 - zoom) + camera.getLookAtPos().z;
		if (adjustedFinishy > 120 * (25 - zoom) + camera.getLookAtPos().z)
			adjustedFinishy = 120 * (25 - zoom) + camera.getLookAtPos().z;

		for (float y = adjustedStarty; y <= adjustedFinishy; y += yGridSpace) {
			gl2.glColor4d(1.0, 0.7, 0.7, 0.5);
			gl2.glVertex3f(adjustedStartx, height, y);
			gl2.glVertex3f(adjustedFinishx, height, y);

		}
		for (float x = adjustedStartx; x <= adjustedFinishx; x += xGridSpace) {
			gl2.glColor4d(1.0, 0.7, 0.7, 0.5);
			gl2.glVertex3f(x, height, adjustedStarty);
			gl2.glVertex3f(x, height, adjustedFinishy);
		}
		gl2.glEnd();
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
			int r = (int) (range[i] / xGridSpace);
			range[i] = (float) r * xGridSpace;
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
