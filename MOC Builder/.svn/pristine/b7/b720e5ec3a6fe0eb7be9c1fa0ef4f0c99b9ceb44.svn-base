package ConnectivityEditor.Connectivity;

import Common.Matrix4;
import Common.Vector3f;
import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.CollisionCylinder;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.Slider;
import Connectivity.Stud;

public class ConnectivityGenerator {

	public static void main(String args[]) {
		int nColumns = 1;
		int nRows = 1;
		int[][] types = new int[nColumns][nRows];

		for (int row = 0; row < nRows; row++)
			for (int column = 0; column < nColumns; column++)
				types[column][row] = 2;

		// ConnectivityGenerator.getInstance().generateStud(1, 1, types);
		System.out.println(ConnectivityGenerator.getInstance().generateAxle(1,
				1, false, false, false, false));
	}

	private static ConnectivityGenerator _instance = null;

	private ConnectivityGenerator() {
	}

	public synchronized static ConnectivityGenerator getInstance() {
		if (_instance == null)
			_instance = new ConnectivityGenerator();
		return _instance;
	}

	public Connectivity generateStud(int nColumns, int nRows, StudT[][] type) {
		Stud newStud = new Stud();
		newStud.setheight("" + nRows * 2);
		newStud.setwidth("" + nColumns * 2);
		newStud.setTransformMatrix(Matrix4.getIdentityMatrix4());

		StudT matrix[][] = new StudT[nColumns * 2 + 1][nRows * 2 + 1];

		for (int row = 0; row < nRows * 2 + 1; row++)
			for (int column = 0; column < nColumns * 2 + 1; column++)
				matrix[column][row] = StudT.Empty;

		for (int row = 0; row < nRows; row++) {
			for (int column = 0; column < nColumns; column++) {
				switch (type[column][row]) {
				case Empty:
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							if (matrix[column * 2 + i][row * 2 + j] == StudT.Empty)
								matrix[column * 2 + i][row * 2 + j] = StudT.Empty;
					break;
				case Round:
					matrix[column * 2 + 1][row * 2 + 1] = StudT.Round;
					break;
				default:
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							matrix[column * 2 + i][row * 2 + j] = StudT.Basic;
				}
			}
		}

		StringBuilder strBuilder = new StringBuilder();
		for (int row = 0; row < nRows * 2 + 1; row++) {
			for (int column = 0; column < nColumns * 2 + 1; column++) {
				switch (matrix[column][row]) {
				case Empty:
					strBuilder.append("29:0,");
					break;
				case Round:
					if (row % 2 != 0 && column % 2 != 0)
						strBuilder.append("0:4:1,");
					else
						strBuilder.append("29:0,");
					break;
				default:
					if (row % 2 == 0) {
						if (column % 2 == 0) {
							strBuilder.append("18:1:1,");
						} else {
							strBuilder.append("23:4:1,");
						}

					} else {
						if (column % 2 == 0)
							strBuilder.append("23:4:1,");
						else
							strBuilder.append("0:4:1,");

					}
				}
			}
		}
		newStud.setMatrixItem(strBuilder.toString());
		newStud.setFileName("Stud");

		System.out.println(newStud);

		return newStud;
	}

	public Connectivity generateHole(int nColumns, int nRows, HoleT[][] type) {
		Hole newHole = new Hole();
		newHole.setheight("" + nRows * 2);
		newHole.setwidth("" + nColumns * 2);
		newHole.setTransformMatrix(Matrix4.getIdentityMatrix4());

		HoleT matrix[][] = new HoleT[nColumns * 2 + 1][nRows * 2 + 1];

		for (int row = 0; row < nRows * 2 + 1; row++)
			for (int column = 0; column < nColumns * 2 + 1; column++)
				matrix[column][row] = HoleT.Empty;

		for (int row = 0; row < nRows; row++) {
			for (int column = 0; column < nColumns; column++) {
				switch (type[column][row]) {
				case Empty:
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							if (matrix[column * 2 + i][row * 2 + j] == HoleT.Empty)
								matrix[column * 2 + i][row * 2 + j] = HoleT.Empty;
					break;
				case Round:
					matrix[column * 2 + 1][row * 2 + 1] = HoleT.Round;
					break;
				default:
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							matrix[column * 2 + i][row * 2 + j] = HoleT.Basic;
				}
			}
		}

		StringBuilder strBuilder = new StringBuilder();
		for (int row = 0; row < nRows * 2 + 1; row++) {
			for (int column = 0; column < nColumns * 2 + 1; column++) {
				switch (matrix[column][row]) {
				case Empty:
					strBuilder.append("29:0,");
					break;
				case Round:
					if (row % 2 != 0 && column % 2 != 0)
						strBuilder.append("17:4:1,");
					else
						strBuilder.append("29:0,");
					break;
				default:
					if (row % 2 == 0) {
						if (column % 2 == 0) {
							strBuilder.append("22:2:1,");
						} else {
							strBuilder.append("22:1:1,");
						}
					} else {
						if (column % 2 == 0)
							strBuilder.append("22:2:1,");
						else
							strBuilder.append("17:4:1,");

					}
				}
			}
		}
		newHole.setMatrixItem(strBuilder.toString());
		newHole.setFileName("Hole");

		System.out.println(newHole);

		return newHole;
	}

	public Connectivity generateAxle(int type, int length,
			boolean isStartCapped, boolean isEndCapped, boolean isGrabbing,
			boolean isRequireGrabbing) {
		Axle newAxle = new Axle();
		newAxle.settype("" + type);
		newAxle.setstartCapped("" + (isStartCapped ? 1 : 0));
		newAxle.setendCapped("" + (isEndCapped ? 1 : 0));
		newAxle.setstartCapped("" + (isStartCapped ? 1 : 0));
		newAxle.setgrabbing("" + (isGrabbing ? 1 : 0));
		newAxle.setrequireGrabbing("" + (isRequireGrabbing ? 1 : 0));
		newAxle.setlength("" + (length / 25.0f));
		newAxle.setFileName("Axle");

		System.out.println(newAxle);
		return newAxle;
	}

	public Connectivity generateFixed(int type) {
		// TODO Auto-generated method stub
		Fixed newFixed = new Fixed();
		newFixed.settype("" + type);
		newFixed.setFileName("Fixed");
		System.out.println(newFixed);
		return newFixed;
	}

	public Connectivity generateHinge(int type) {
		Hinge newHinge = new Hinge();
		newHinge.settype("" + type);
		newHinge.setFileName("Hinge");
		System.out.println(newHinge);
		return newHinge;
	}

	public CollisionBox generateCollisionBox(Vector3f size) {

		CollisionBox newBox = new CollisionBox();
		newBox.setSize(size.x, size.y, size.z);
		newBox.setFileName("Collision Box");
		return newBox;
	}

	public CollisionSphere generateCollisionSphere(float radius) {

		CollisionSphere newObj = new CollisionSphere();
		newObj.setRadius(radius);
		newObj.setFileName("Collision Sphere");
		return newObj;
	}

	public CollisionCylinder generateCollisionCylinder(Vector3f size) {

		CollisionCylinder newObj = new CollisionCylinder();
		newObj.setSize(size.getX(), size.getY(), size.getZ());
		newObj.setFileName("Collision Cylinder");
		return newObj;
	}

	public Connectivity generateBall(int type) {
		Ball newBall = new Ball();
		newBall.settype("" + type);
		newBall.setFileName("Ball");
		System.out.println(newBall);
		return newBall;
	}

	public Connectivity generateSlider(int type, int length,
			boolean isStartCapped, boolean isEndCapped, boolean isCylindrical) {
		Slider newSlider = new Slider();
		newSlider.settype("" + type);
		newSlider.setstartCapped("" + (isStartCapped ? 1 : 0));
		newSlider.setendCapped("" + (isEndCapped ? 1 : 0));
		newSlider.setstartCapped("" + (isStartCapped ? 1 : 0));
		newSlider.setcylindrical("" + (isCylindrical ? 1 : 0));
		newSlider.setlength("" + (length * 8 / 25.0f));
		newSlider.setFileName("Slider");

		System.out.println(newSlider);
		return newSlider;
	}
}
