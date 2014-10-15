package Window;

import javax.media.opengl.GL2;

import Builder.BrickSelectionManager;
import Builder.MainCamera;
import Command.LDrawPart;
import Common.Box3;
import Common.Vector3f;

public class GlobalBoundingBoxRenderer {
	private static GlobalBoundingBoxRenderer _instance = null;

	public synchronized static GlobalBoundingBoxRenderer getInstance() {
		return _instance;
	}

	public synchronized static GlobalBoundingBoxRenderer getInstance(
			MainCamera camera) {
		if (_instance == null)
			_instance = new GlobalBoundingBoxRenderer(camera);
		return _instance;
	}

	private MainCamera camera;

	private LDrawPart pointingPart = null;

	private GlobalBoundingBoxRenderer(MainCamera cam) {
		camera = cam;
	}

	public void setPointingPart(LDrawPart part) {
		this.pointingPart = part;
	}

	public void draw(GL2 gl2) {
		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glUseProgram(0);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(camera.getProjection(), 0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(camera.getModelView(), 0);

		drawBoundingBoxes(gl2);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPopMatrix();

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPopMatrix();

		gl2.glEnable(GL2.GL_LIGHTING);
	}

	private void drawBoundingBox(GL2 gl2, Vector3f[] pos) {

		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor4d(1, 0, 0, 1.0f);

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);

		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);
		
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		
		gl2.glEnd();
	}

	private void drawBoundingBoxes(GL2 gl2) {
		gl2.glLineWidth(1.45f);
		if (pointingPart != null) {
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			drawBoundingBox(gl2, pointingPart.getCachedOOB());
		}
		
		for (LDrawPart part : BrickSelectionManager.getInstance()
				.getSelectedPartList()) {
			drawBoundingBox(gl2, part.getCachedOOB());
		}
	}

}
