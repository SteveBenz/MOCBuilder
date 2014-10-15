package ConnectivityEditor.Window;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.Connectivity;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.AxleT;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;

public class SliderRenderer extends DefaultConnectivityRenderer {
	private Slider slider;

	public SliderRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);

		slider = (Slider) conn;
	}

	@Override
	public void draw(GL2 gl2) {
		if (slider.gettype() % 2 == 0) {// socket
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(1f, 1f, 0.5f);
		} else {
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0f, 0f, 0.5f);
		}

		gl2.glLoadMatrixf(camera.getModelView(), 0);

		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (Vector3f vertex : getSliderVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

		gl2.glBegin(GL2.GL_TRIANGLES);
		for (Vector3f vertex : getCapVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();
	}

	private Vector3f[] getCapVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float diskRadius = LDrawGridTypeT.Medium.getXZValue()/2;

		Vector3f vertex = null;
		int slice = 24;

		if (slider.getstartCapped() == 1) {
			vertex = new Vector3f();
			for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
				vertices.add(vertex);
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex) * Math.PI
								* 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex) * Math.PI
								* 2 / (slice - 1)))));
				vertices.add(vertex.add(

						(float) (diskRadius * Math.cos((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1)))));
			}
		}
		if (slider.getendCapped() == 1) {
			vertex = new Vector3f(0, -slider.getlength(), 0);
			for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
				vertices.add(vertex);
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex) * Math.PI
								* 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex) * Math.PI
								* 2 / (slice - 1)))));
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1)))));
			}
		}

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = slider.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getSliderVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float width = 2;
		final float length = slider.getlength();
		final float thickness = 2;
		vertices.add(new Vector3f(-width, 0, thickness));
		vertices.add(new Vector3f(-width, 0, -thickness));
		vertices.add(new Vector3f(-width, -length, -thickness));
		vertices.add(new Vector3f(-width, -length, thickness));

		vertices.add(new Vector3f(width, 0, thickness));
		vertices.add(new Vector3f(width, 0, -thickness));
		vertices.add(new Vector3f(width, -length, -thickness));
		vertices.add(new Vector3f(width, -length, thickness));

		vertices.add(new Vector3f(-width, 0, -thickness));
		vertices.add(new Vector3f(width, 0, -thickness));
		vertices.add(new Vector3f(width, -length, -thickness));
		vertices.add(new Vector3f(-width, -length, -thickness));

		vertices.add(new Vector3f(-width, 0, thickness));
		vertices.add(new Vector3f(width, 0, thickness));
		vertices.add(new Vector3f(width, -length, thickness));
		vertices.add(new Vector3f(-width, -length, thickness));

		//
		vertices.add(new Vector3f(thickness, 0, width));
		vertices.add(new Vector3f(thickness, 0, -width));
		vertices.add(new Vector3f(-thickness, 0, -width));
		vertices.add(new Vector3f(-thickness, 0, width));

		vertices.add(new Vector3f(thickness, -length, width));
		vertices.add(new Vector3f(thickness, -length, -width));
		vertices.add(new Vector3f(-thickness, -length, -width));
		vertices.add(new Vector3f(-thickness, -length, width));

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = slider.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		boolean isHitted = false;
		Vector3f[] vertices = getSliderVertices();
		for (int i = 0; i < vertices.length; i += 4) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1)], vertices[(i + 2)], distanceTemp, null)) {

				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i + 2],
					vertices[(i + 3)], vertices[i], distanceTemp, null)) {
				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
		}
		vertices = getCapVertices();
		for (int i = 0; i < vertices.length; i += 3) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1)], vertices[(i + 2)], distanceTemp, null)) {
				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
		}

		lastHittedDistance = distance.get(0);
		return isHitted;
	}
}
