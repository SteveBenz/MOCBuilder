package BrickControlGuide;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import Builder.MainCamera;
import Common.Vector3f;
import Connectivity.IConnectivity;
import LDraw.Support.MatrixMath;

public class DefaultGuideRenderer implements IGuideRenderer {
	protected AxisGuideTypeT axisGuideT;
	protected float r, g, b;
	protected GLU glu;
	protected Vector3f directionVector;
	protected IConnectivity centerConnector;
	protected float lastHittedDistance;

	@Override
	public void draw(GL2 gl2, MainCamera camera, Vector3f orientation) {
	}

	@Override
	public void setColor3f(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public boolean isHitted(MainCamera camera, Vector3f orientation, float x,
			float y, FloatBuffer distance) {
		return false;
	}
	
	@Override
	public float getLastHittedDistance() {
		return -1;
	}

	@Override
	public void setAxisGuideType(AxisGuideTypeT axisGuideType) {
		this.axisGuideT = axisGuideType;
		if (axisGuideType != null && axisGuideType != AxisGuideTypeT.Custom)
			setAxisDirectionVector(axisGuideT.getDirectionVector());
	}

	@Override
	public AxisGuideTypeT getAxisGuideType() {
		return this.axisGuideT;
	}

	@Override
	public void setAxisDirectionVector(Vector3f directionVector) {
		this.directionVector = directionVector;
	}

	@Override
	public Vector3f getAxisDirectionVector() {
		if (centerConnector != null)
			return MatrixMath.V3RotateByTransformMatrix(directionVector,
					centerConnector.transformationMatrixOfPart());
		return this.directionVector;
	}

	@Override
	public void setConnectivity(IConnectivity centerConn) {
		this.centerConnector = centerConn;
	}

	@Override
	public IConnectivity getCenterConnectivity() {
		return this.centerConnector;
	}

	@Override
	public Vector3f getCenterPos() {
		if (this.centerConnector == null)
			return null;

		return this.centerConnector.getCurrentPos(centerConnector
				.transformationMatrixOfPart());
	}
}
