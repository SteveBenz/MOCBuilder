package Command;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import Common.Matrix4;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Support.DispatchGroup;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;
import LDraw.Support.Range;

public class CameraTransformCommand extends LDrawDirective {
	public static final String HEADER = "0 MOCBUILDER_CAMERA_TRANSFORM";

	private String description;
	Vector3f lookAtPos = new Vector3f();
	Vector2f rotation = new Vector2f();
	float distanceToObject = 0;

	public CameraTransformCommand() {
	}

	public void finishParsing(StringTokenizer strTokenizer) {
	}

	public CameraTransformCommand initWithLines(ArrayList<String> lines,
			Range range, DispatchGroup parentGroup) throws Exception {
		String workingLine = lines.get(range.getLocation());
		String parsedField = null;

		Vector3f lookAtPos = new Vector3f();
		Vector2f rotation = new Vector2f();
		float distanceToObject = 0;

		try {
			super.initWithLines(lines, range, parentGroup);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			// Read in the line code and advance past it.
			StringTokenizer strTokenizer = new StringTokenizer(workingLine);
			parsedField = strTokenizer.nextToken();
			// Only attempt to create the part if this is a valid line.
			if (Integer.parseInt(parsedField) == 0) {
				parsedField = strTokenizer.nextToken();
				// Read Lookat position.
				// (x)
				parsedField = strTokenizer.nextToken();
				lookAtPos.setX(MatrixMath.round(Float.parseFloat(parsedField)));

				// (y)
				parsedField = strTokenizer.nextToken();
				lookAtPos.setY(MatrixMath.round(Float.parseFloat(parsedField)));

				// (z)
				parsedField = strTokenizer.nextToken();
				lookAtPos.setZ(MatrixMath.round(Float.parseFloat(parsedField)));

				// Read Rotation.
				// (X)

				parsedField = strTokenizer.nextToken();
				rotation.setX(MatrixMath.round(Float.parseFloat(parsedField)));
				// (y)

				parsedField = strTokenizer.nextToken();
				rotation.setY(MatrixMath.round(Float.parseFloat(parsedField)));

				// Read Distance.

				parsedField = strTokenizer.nextToken();
				distanceToObject = MatrixMath.round(Float
						.parseFloat(parsedField));

				// Read Description

				if (strTokenizer.hasMoreTokens()) {
					String description = strTokenizer.nextToken();
					while (strTokenizer.hasMoreTokens()) {
						description += " " + strTokenizer.nextToken();
					}

					this.description = description;
				}
				this.lookAtPos = lookAtPos;
				this.rotation = rotation;
				this.distanceToObject = distanceToObject;

			} else
				throw new Exception("BricksmithParseException: "
						+ "Bad CameraTransformCommand syntax");
		} catch (Exception e) {
			System.out.println(String.format("the part %s was fatally invalid",
					lines.get(range.getLocation())));

			System.out.println(String.format(" raised exception %s",
					e.getMessage()));
		}
		return this;

	}// end initWithLines:inRange:

	@Override
	public String description() {
		return this.description;
	}

	public String write() {
		return String.format(Locale.US,
				"0 MOCBUILDER_CAMERA_TRANSFORM %s %s %s %s %s %s %s",

				LDrawUtilities.outputStringForFloat(this.lookAtPos.x), // lookAt
																		// position.x,
																		// (x)
				LDrawUtilities.outputStringForFloat(this.lookAtPos.y), // lookAt
																		// position.y,
																		// (y)
				LDrawUtilities.outputStringForFloat(this.lookAtPos.z), // lookAt
																		// position.z,
																		// (z)
				LDrawUtilities.outputStringForFloat(this.rotation.getX()), // RotationX.,
				LDrawUtilities.outputStringForFloat(this.rotation.getY()), // RotationY,

				LDrawUtilities.outputStringForFloat(distanceToObject), // Distance
																		// To
																		// Object

				description);
	}// end write

	public static boolean lineIsCameraTransformBeginning(String line) {
		return line.trim().toUpperCase().startsWith(HEADER);
	}

	public void setDesciprion(String description2) {
		this.description = description2;
		// TODO Auto-generated method stub

	}

	public Vector3f getLookAt() {
		return this.lookAtPos;
	}

	public Vector2f getRotation() {
		return this.rotation;
	}

	public float getDistanceToObject() {
		return this.distanceToObject;
	}

	public Vector3f getLookAtPos() {
		return lookAtPos;
	}

	public void setLookAtPos(Vector3f lookAtPos) {
		this.lookAtPos = lookAtPos;
	}

	public void setRotation(Vector2f rotation) {
		this.rotation = rotation;
	}

	public void setDistanceToObject(float distanceToObject) {
		this.distanceToObject = distanceToObject;
	}
	
	
}
