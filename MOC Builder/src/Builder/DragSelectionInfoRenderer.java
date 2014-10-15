package Builder;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import Command.LDrawPart;
import Common.Box2;
import Common.Size2;
import Common.Vector2f;
import Common.Vector3f;
import Window.MOCBuilder;

public class DragSelectionInfoRenderer {
	private GLU glu;
	private boolean isVisible = false;
	private Vector2f origin;
	private Vector2f end;
	private Size2 canvasSize;

	private LDrawPart part = null;

	public DragSelectionInfoRenderer() {
		glu = new GLU();
		origin = new Vector2f();
		end = new Vector2f();
		canvasSize = new Size2();
	}

	public void setPart(LDrawPart part) {
		this.part = part;
	}

	public void setCanvasSize(int width, int height) {
		this.canvasSize.setWidth(width);
		this.canvasSize.setHeight(height);
	}

	public Size2 getCanvasSize() {
		return canvasSize;
	}

	public void isVisible(boolean flag) {
		this.isVisible = flag;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void setOrigin(Vector2f origin) {
		this.origin = origin;
		this.end = origin;
	}

	public void setEnd(Vector2f end) {
		this.end = end;
	}

	public Vector2f getOrigin() {
		return this.origin;
	}

	public Vector2f getEnd() {
		return this.end;
	}

	public void draw(GL2 gl2) {
		if (isVisible == false)
			return;
		gl2.glUseProgram(0);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluOrtho2D(0, canvasSize.getWidth(), 0, canvasSize.getHeight());
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		gl2.glColor3f(0.5f, 0.5f, 0.5f);

		gl2.glBegin(GL2.GL_LINES);
		gl2.glVertex2f(origin.getX(), canvasSize.getHeight() - origin.getY());
		gl2.glVertex2f(origin.getX(), canvasSize.getHeight() - end.getY());

		gl2.glVertex2f(origin.getX(), canvasSize.getHeight() - end.getY());
		gl2.glVertex2f(end.getX(), canvasSize.getHeight() - end.getY());

		gl2.glVertex2f(end.getX(), canvasSize.getHeight() - end.getY());
		gl2.glVertex2f(end.getX(), canvasSize.getHeight() - origin.getY());

		gl2.glVertex2f(end.getX(), canvasSize.getHeight() - origin.getY());
		gl2.glVertex2f(origin.getX(), canvasSize.getHeight() - origin.getY());

		gl2.glEnd();
		gl2.glFlush();

		drawTestPart(gl2);
	}

	private void drawTestPart(GL2 gl2) {
		if (part == null)
			return;
		
		Vector3f[] vertices = part.getCachedOOB();
		Vector2f[] vertices2f = new Vector2f[8];
		
		for (int i = 0; i < 8; i++) {
			vertices2f[i] = MOCBuilder.getInstance().getCamera()
					.getWorldToScreenPos(vertices[i]);
		}
		drawBoundingBox(gl2, vertices2f);
	}

	private void drawBoundingBox(GL2 gl2, Vector2f[] pos) {
		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor4d(1, 0, 0, 1.0f);
		for (int x = 0; x < 7; x++)
			for (int y = x; y < 8; y++) {
				gl2.glVertex2f(canvasSize.getWidth() / 2 + pos[x].getX(),
						canvasSize.getHeight() / 2 + pos[x].getY());
				gl2.glVertex2f(canvasSize.getWidth() / 2 + pos[y].getX(),
						canvasSize.getHeight() / 2 + pos[y].getY());
			}
		gl2.glEnd();
	}

	public Box2 getBounds() {
		Box2 bounds = new Box2();
		bounds.origin = new Vector2f(Math.min(origin.getX(), end.getX())
				- canvasSize.getWidth() / 2, Math.min(canvasSize.getHeight()
				/ 2 - origin.getY(), canvasSize.getHeight() / 2 - end.getY()));
		bounds.size = new Size2(Math.abs(origin.getX() - end.getX()),
				Math.abs(origin.getY() - end.getY()));

		return bounds;
	}
}
