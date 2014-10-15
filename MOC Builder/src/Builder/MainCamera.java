package Builder;

import java.awt.event.MouseWheelEvent;

import org.eclipse.swt.events.MouseEvent;

import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Support.LDrawGLCamera;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.ProjectionModeT;

public class MainCamera extends LDrawGLCamera {

	private int mouseXStart;
	private int mouseYStart;
	private float currentCameraRotateX;
	private float currentCameraRotateY;
	private float startCameraRotateX;
	private float startCameraRotateY;
	private float distanceBetweenObjectToCamera;

	private float screenWidth = 1.0f;
	private float screenHeight = 1.0f;

	private Matrix4 modelViewMatrix;
	private Matrix4 projectionMatrix;

	private Vector3f lookAtPos = new Vector3f(0, 0, 0);

	public MainCamera() {
		super();
		setDefault();
	}

	public Vector3f getLookAtPos() {
		return new Vector3f(lookAtPos);
	}

	public Matrix4 getModelViewMatrix() {
		return modelViewMatrix;
	}

	public Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4 getTransformMatrix() {
		return Matrix4.multiply(modelViewMatrix, projectionMatrix);
	}

	public void setDefault() {
		currentCameraRotateX = (float) (Math.PI);
		currentCameraRotateY = -0.52f;
		startCameraRotateX = 0.0f;
		startCameraRotateY = 0.0f;
		distanceBetweenObjectToCamera = 850.0f;
	}

	public void moveTo(Vector3f lookat) {
		lookAtPos = new Vector3f(lookat);
		makeModelView();
	}

	public Vector3f screenToWorldXZ(float mouse_x, float mouse_y, float planeY) {
		float normalised_x = -(2 * mouse_x / screenWidth - 1);
		float normalised_y = -(1 - 2 * mouse_y / screenHeight);

		normalised_x /= projectionMatrix.getElement(0, 0);
		normalised_y /= projectionMatrix.getElement(1, 1);
		Matrix4 invView = null;
		invView = Matrix4.inverse(modelViewMatrix);
		Vector3f dir = new Vector3f(-(normalised_x * invView.getElement(0, 0)
				+ normalised_y * invView.getElement(1, 0) + invView.getElement(
				2, 0)), -(normalised_x * invView.getElement(0, 1)
				+ normalised_y * invView.getElement(1, 1) + invView.getElement(
				2, 1)), -(normalised_x * invView.getElement(0, 2)
				+ normalised_y * invView.getElement(1, 2) + invView.getElement(
				2, 2)));
		Vector3f cameraPos = new Vector3f(invView.getElement(3, 0),
				invView.getElement(3, 1), invView.getElement(3, 2));

		Vector3f pickPos = Vector3f.intersectRayPlane(cameraPos, dir,
				new Vector3f(1, planeY, 0), new Vector3f(0, planeY, 0),
				new Vector3f(0, planeY, 1));

		return pickPos;
	}

	public Vector3f screenToWorldXY(float mouse_x, float mouse_y, float planeZ) {
		float normalised_x = -(2 * mouse_x / screenWidth - 1);
		float normalised_y = -(1 - 2 * mouse_y / screenHeight);

		normalised_x /= projectionMatrix.getElement(0, 0);
		normalised_y /= projectionMatrix.getElement(1, 1);
		Matrix4 invView = null;
		invView = Matrix4.inverse(modelViewMatrix);
		Vector3f dir = new Vector3f(-(normalised_x * invView.getElement(0, 0)
				+ normalised_y * invView.getElement(1, 0) + invView.getElement(
				2, 0)), -(normalised_x * invView.getElement(0, 1)
				+ normalised_y * invView.getElement(1, 1) + invView.getElement(
				2, 1)), -(normalised_x * invView.getElement(0, 2)
				+ normalised_y * invView.getElement(1, 2) + invView.getElement(
				2, 2)));
		Vector3f cameraPos = new Vector3f(invView.getElement(3, 0),
				invView.getElement(3, 1), invView.getElement(3, 2));

		Vector3f pickPos = Vector3f.intersectRayPlane(cameraPos, dir,
				new Vector3f(1, 0, planeZ), new Vector3f(0, 1, planeZ),
				new Vector3f(-1, 0, planeZ));

		return pickPos;
	}

	public Ray3 getRay(float mousex, float mousey) {
		float normalised_x = -(2 * mousex / screenWidth - 1);
		float normalised_y = -(1 - 2 * mousey / screenHeight);

		normalised_x /= projection[0];
		normalised_y /= projection[5];
		Matrix4 invView = MatrixMath.Matrix4CreateFromGLMatrix4(modelView);
		invView.invert();

		Ray3 retRay = new Ray3();

		retRay.setDirection(new Vector3f(-(normalised_x
				* invView.getElement(0, 0) + normalised_y
				* invView.getElement(1, 0) + invView.getElement(2, 0)),
				-(normalised_x * invView.getElement(0, 1) + normalised_y
						* invView.getElement(1, 1) + invView.getElement(2, 1)),
				-(normalised_x * invView.getElement(0, 2) + normalised_y
						* invView.getElement(1, 2) + invView.getElement(2, 2))));
		retRay.setOrigin(new Vector3f(invView.getElement(3, 0), invView
				.getElement(3, 1), invView.getElement(3, 2)));

		float magnitude = (float) Math
				.sqrt(Math.pow(retRay.getDirection().x, 2)
						+ Math.pow(retRay.getDirection().y, 2)
						+ Math.pow(retRay.getDirection().z, 2));
		retRay.getDirection().setX(retRay.getDirection().x / magnitude);
		retRay.getDirection().setY(retRay.getDirection().y / magnitude);
		retRay.getDirection().setZ(retRay.getDirection().z / magnitude);

		return retRay;
	}

	public void setDistanceBetweenObjectToCamera(float distance) {
		this.distanceBetweenObjectToCamera = distance;
		makeModelView();
	}

	public float getDistanceBetweenObjectToCamera() {
		return this.distanceBetweenObjectToCamera;
	}

	public void setCurrentCameraRotation(Vector3f rotation) {
		this.currentCameraRotateX = rotation.getX();
		this.currentCameraRotateY = rotation.getY();
		makeModelView();
	}

	public void setScreenSize(final float width, final float height) {
		screenWidth = width;
		screenHeight = height;
	}

	public void makeModelView() {
		Matrix4 m = new Matrix4();
		m.setIdentity();
		m.rotateY(currentCameraRotateX);
		m.rotateX(currentCameraRotateY);

		Vector3f veye = new Vector3f(0, 0, distanceBetweenObjectToCamera);
		Vector3f vup = new Vector3f(0, -1, 0);
		Vector3f vcenter = new Vector3f(0, 0, 0);
		veye = m.multiply(veye);
		vup = m.multiply(vup);

		veye = lookAtPos.add(veye);
		vcenter = lookAtPos.add(vcenter);
		Matrix4 temp = new Matrix4();
		temp.makeLookAtMatrix(veye, vcenter, vup);
		if (modelViewMatrix != null)
			synchronized (modelViewMatrix) {
				modelViewMatrix = temp;
			}
		else {
			modelViewMatrix = temp;
		}

		for (int i = 0; i < 16; i++) {
			modelView[i] = modelViewMatrix.getElement(i / 4, i % 4);
		}
	}

	public void makeProjection() {
		if (projectionMode == ProjectionModeT.ProjectionModePerspective) {
			projectionMatrix = new Matrix4();

			float aspect = screenWidth / screenHeight;

			projectionMatrix.makePerspectiveMatrix(45, aspect, 20, 20000);

			for (int i = 0; i < 16; i++) {
				projection[i] = projectionMatrix.getElement(i / 4, i % 4);
			}
		}
	}

	public void startRotate(int x, int y) {
		// TODO Auto-generated method stub
		mouseXStart = x;
		mouseYStart = y;
		startCameraRotateX = currentCameraRotateX;
		startCameraRotateY = currentCameraRotateY;
	}

	public void rotate(int x, int y) {
		// TODO Auto-generated method stub
		int mouseX = mouseXStart - x;
		int mouseY = mouseYStart - y;

		currentCameraRotateX = startCameraRotateX + (((float) mouseX) / 100.0f);
		currentCameraRotateY = startCameraRotateY + (((float) mouseY) / 100.0f);

		// System.out.println("mouseDragged "+currentCameraRotateX +" " +
		// currentCameraRotateY );
	}

	public void zoom(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.count > 0) {
			distanceBetweenObjectToCamera -= 50.0;

			if (distanceBetweenObjectToCamera < 1.0f)
				distanceBetweenObjectToCamera = 1.0f;
		} else {
			distanceBetweenObjectToCamera += 50.0;

			if (distanceBetweenObjectToCamera > 4000.0f)
				distanceBetweenObjectToCamera = 4000.0f;
		}

		if (Float.isNaN(distanceBetweenObjectToCamera))
			distanceBetweenObjectToCamera = 0;
	}

	public Vector2f getWorldToScreenPos(Vector3f worldPos) {
		return getWorldToScreenPos(worldPos, true);
	}

	public Vector2f getWorldToScreenPos(Vector3f worldPos, boolean depthTest) {
		Vector3f pos = null;
		pos = modelViewMatrix.transformPoint(worldPos);
		if (depthTest && pos.z > 0f)
			return null;
		else {
			final float value = 1.2f * screenHeight / -pos.z;
			pos.x = pos.x * value;
			pos.y = pos.y * value;
		}
		return new Vector2f(pos.x, pos.y);

		// System.out.println(pos);
		// screenRatio width/height

	}
	
	public float getScreenToWorldDistance(float distance){
		Vector3f pos = null;
		pos = modelViewMatrix.getDefaultTransformPos();
		if (pos.z > 0f)
			return 0;
		return distance/1.2f / screenHeight*-pos.z; 
	}

}
