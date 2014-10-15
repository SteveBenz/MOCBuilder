package Connectivity;

import java.util.ArrayList;

import Common.Matrix4;
import Common.Vector3f;

public interface IConnectivity {
	public Direction6T getDirection();
	public Vector3f getCurrentPos();
	public Vector3f getDirectionVector();
	
	public Direction6T getDirection(Matrix4 partTransformMatrix);
	public Vector3f getCurrentPos(Matrix4 partTransformMatrix);
	public Vector3f getDirectionVector(Matrix4 partTransformMatrix);
	
	public float distance(Vector3f testingPos);
	
	public void updateConnectivityOrientationInfo();
	
	public ConnectivityTestResultT isConnectable(ArrayList<IConnectivity> connectors);
	public ConnectivityTestResultT isConnectable(ArrayList<IConnectivity> connectors, Matrix4 partTransformMatrix);
	public ConnectivityTestResultT isConnectable(IConnectivity connector);
	public ConnectivityTestResultT isConnectable(IConnectivity connector, Matrix4 partTransformMatrix);
	
	public Matrix4 transformationMatrixOfPart();
	
	public void moveTo(Vector3f moveByInWorld);
	public void moveBy(Vector3f moveByInWorld);
	public void rotateBy(float angle, Vector3f roationVector);
	
	public Connectivity getConnectivity();
}
