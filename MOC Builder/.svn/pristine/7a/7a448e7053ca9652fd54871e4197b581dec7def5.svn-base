package BrickControlGuide;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import Builder.MainCamera;
import Command.LDrawPart;
import Common.Ray3;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

import com.jogamp.opengl.util.gl2.GLUT;

public class oldBrickMovementGuideRenderer {
	private static oldBrickMovementGuideRenderer _instance = null;

	private MainCamera camera;
	private boolean isVisible = true;
	private GLU glu;
	private GLUT glut;
	private LDrawPart part;
	private AxisGuideTypeT axisSelectedType = AxisGuideTypeT.None;
	
	public static oldBrickMovementGuideRenderer getInstance(){
		return _instance;
	}
	
	public synchronized static oldBrickMovementGuideRenderer getInstance(MainCamera cam){
		if(_instance==null)
			_instance = new oldBrickMovementGuideRenderer(cam);
		return _instance;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		part = null;
	}

	private oldBrickMovementGuideRenderer(MainCamera cam) {
		camera = cam;
		part = null;
		glu = new GLU(); // get GL Utilities
		glut = new GLUT();
	}

	public void draw(GL2 gl2) {
		if(camera==null)
			return;
		if (isVisible == false)
			return;
		if (part == null)
			return;
		if (part.isSelected() == false)
			return;
		gl2.glDisable(GL2.GL_LIGHTING);

		gl2.glPushMatrix();

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadMatrixf(camera.getModelView(), 0);

//		drawGuide(gl2);
		MovementGuide movementGuide = new MovementGuide(glu);
		movementGuide.setAxisGuideType(AxisGuideTypeT.Z_Movement);
		movementGuide.draw(gl2,  camera, part.position());

		gl2.glPopMatrix();

		gl2.glEnable(GL2.GL_LIGHTING);
	}

	private void drawGuide(GL2 gl2) {

		float line_length = camera.getDistanceBetweenObjectToCamera() / 5;

		Vector3f orientation = part.position();
		// draw guide line for x, y, z axis
		gl2.glBegin(GL2.GL_LINES); // draw using triangles

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.X_Movement
				|| axisSelectedType == AxisGuideTypeT.X_Rotate) {
			gl2.glColor3d(1, 0, 0);
			gl2.glVertex3f(orientation.getX(), orientation.getY(),
					orientation.getZ());
			gl2.glVertex3f(orientation.getX() + line_length,
					orientation.getY(), orientation.getZ());
		}
		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Y_Movement
				|| axisSelectedType == AxisGuideTypeT.Y_Rotate) {
			gl2.glColor3d(0, 1, 0);
			gl2.glVertex3f(orientation.getX(), orientation.getY(),
					orientation.getZ());
			gl2.glVertex3f(orientation.getX(),
					orientation.getY() - line_length, orientation.getZ());
		}

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Z_Movement
				|| axisSelectedType == AxisGuideTypeT.Z_Rotate) {
			gl2.glColor3d(0, 0, 1);
			gl2.glVertex3f(orientation.getX(), orientation.getY(),
					orientation.getZ());
			gl2.glVertex3f(orientation.getX(), orientation.getY(),
					orientation.getZ() + line_length);
		}
		gl2.glEnd();

		// draw guide arrow for x, y, z axis movement
		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 40f;
		final float cylinderHeight = camera.getDistanceBetweenObjectToCamera() / 20;
		GLUquadric body = glu.gluNewQuadric();
		int slices = 20;
		int stacks = 10;
		glu.gluQuadricTexture(body, false);
		glu.gluQuadricDrawStyle(body, GLU.GLU_FILL);
		glu.gluQuadricNormals(body, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(body, GLU.GLU_OUTSIDE);

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.X_Movement) {
			gl2.glColor3d(1, 0, 0);
			gl2.glTranslatef(orientation.getX() + line_length,
					orientation.getY(), orientation.getZ());
			gl2.glRotatef(90f, 0, 1, 0);
			glu.gluDisk(body, 0, cylinderRadius, slices, 2);
			glu.gluCylinder(body, cylinderRadius, 0, cylinderHeight, slices,
					stacks);
		}

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Y_Movement) {
			gl2.glColor3d(0, 1, 0);
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(orientation.getX(), orientation.getY()
					- line_length, orientation.getZ());
			gl2.glRotatef(90f, 1, 0, 0);
			glu.gluDisk(body, 0, cylinderRadius, slices, 2);
			glu.gluCylinder(body, cylinderRadius, 0, cylinderHeight, slices,
					stacks);
		}
		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Z_Movement) {
			gl2.glColor3d(0, 0, 1);
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(orientation.getX(), orientation.getY(),
					orientation.getZ() + line_length);
			glu.gluDisk(body, 0, cylinderRadius, slices, 2);
			glu.gluCylinder(body, cylinderRadius, 0, cylinderHeight, slices,
					stacks);
		}

		// draw guide arrow for x, y, z axis rotation
		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.X_Rotate) {
			gl2.glColor3d(1, 0, 0);
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(orientation.getX() + line_length - cylinderRadius
					* 2, orientation.getY(), orientation.getZ());
			gl2.glRotatef(90f, 0, 1, 0);
			glut.glutSolidTorus(cylinderRadius / 2, cylinderRadius, slices,
					stacks);
		}

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Y_Rotate) {
			gl2.glColor3d(0, 1, 0);
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(orientation.getX(), orientation.getY()
					- line_length + cylinderRadius * 2, orientation.getZ());
			gl2.glRotatef(90f, 1, 0, 0);
			glut.glutSolidTorus(cylinderRadius / 2, cylinderRadius, slices,
					stacks);
		}

		if (axisSelectedType == AxisGuideTypeT.None
				|| axisSelectedType == AxisGuideTypeT.Z_Rotate) {
			gl2.glColor3d(0, 0, 1);
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(orientation.getX(), orientation.getY(),
					orientation.getZ() + line_length - cylinderRadius * 2);
			gl2.glRotatef(90f, 0, 0, 1);
			glut.glutSolidTorus(cylinderRadius / 2, cylinderRadius, slices,
					stacks);
		}
	}

	public void setLDrawPart(LDrawPart part) {
		this.part = part;
	}
	
	public LDrawPart getLDrawPart(){
		return this.part;
	}

	public AxisGuideTypeT getHittedAxisArrow(float screenX, float screenY) {
		
		if(part==null)
			return AxisGuideTypeT.None;
		
		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 20f;
		float centerOfCylinder = camera.getDistanceBetweenObjectToCamera() / 5f
				+ cylinderRadius / 2;

		Vector3f orientation = part.position();
		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distance = FloatBuffer.allocate(1);
		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x
				+ centerOfCylinder, orientation.y, orientation.z),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.X_Movement;

		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x,
				orientation.y - centerOfCylinder, orientation.z),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.Y_Movement;

		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x,
				orientation.y, orientation.z + centerOfCylinder),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.Z_Movement;

		float centerOfTorus = camera.getDistanceBetweenObjectToCamera() / 5f
				- cylinderRadius;

		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x
				+ centerOfTorus, orientation.y, orientation.z),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.X_Rotate;

		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x,
				orientation.y - centerOfTorus, orientation.z),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.Y_Rotate;

		if (MatrixMath.V3RayIntersectsSphere(ray, new Vector3f(orientation.x,
				orientation.y, orientation.z + centerOfTorus),
				cylinderRadius / 2, distance))
			return AxisGuideTypeT.Z_Rotate;

		return AxisGuideTypeT.None;
	}

	public void axisSelectedType(AxisGuideTypeT type) {
		this.axisSelectedType = type;
	}

	public AxisGuideTypeT axisSelectedType() {
		return this.axisSelectedType;
	}
}
