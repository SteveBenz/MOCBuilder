package BrickControlGuide;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Vector3f;
import Connectivity.Connectivity;
import Connectivity.IConnectivity;

public interface IGuideRenderer {
	void draw(GL2 gl2, MainCamera camera, Vector3f orientation);

	void setAxisDirectionVector(Vector3f directionVector);
	Vector3f getAxisDirectionVector();
	void setAxisGuideType(AxisGuideTypeT axisGuideType);
	void setColor3f(float r, float g, float b);
	boolean isHitted(MainCamera camera, Vector3f orientation, float x, float y, FloatBuffer distance);
	float getLastHittedDistance();
	
	void setConnectivity(IConnectivity centerConn);
	IConnectivity getCenterConnectivity();
	Vector3f getCenterPos();

	AxisGuideTypeT getAxisGuideType();
}
