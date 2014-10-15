package Connectivity;

import Common.Matrix4;
import Common.Vector3f;
 
public class ConnectivityTestResult {
	private String msg;
	private ConnectivityTestResultT resultType;
	private Vector3f testPos;
	private Matrix4 transformMatrix;
	
	public ConnectivityTestResult(){
		this.resultType = ConnectivityTestResultT.None;
	}
	
	public ConnectivityTestResult(ConnectivityTestResultT type){
		this.resultType = type;
	}
	
	public void setResultType(ConnectivityTestResultT resultType){
		this.resultType = resultType;
	}
	
	public void setTestPos(Vector3f pos){
		this.testPos = new Vector3f(pos);
	}
	
	public void setTransformMatrix(Matrix4 transformMatrix){
		this.transformMatrix = new Matrix4(transformMatrix);
	}

	public Matrix4 getTransformMatrix() {		
		return transformMatrix;
	}

	
	public ConnectivityTestResultT getResultType() {
		return resultType;
	}

	public Vector3f getTestPos() {
		return testPos;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	
	
}
