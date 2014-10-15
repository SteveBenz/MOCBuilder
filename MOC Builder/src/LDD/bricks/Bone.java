package LDD.bricks;

import org.w3c.dom.Element;

import Common.Matrix4;

public class Bone {
	public	int	 		refID;
	public	Matrix4		transformation;
	
	
	public	Bone( int rID, float tx, float ty, float tz )
	{
		refID = rID;
		transformation = new Matrix4();
		transformation.setIdentity();
		transformation.setElement(3, 0, tx);
		transformation.setElement(3, 1, ty);
		transformation.setElement(3, 2, tz);
	}
	public	Bone( Element boneElem )
	{
		refID = Integer.parseInt(boneElem.getAttribute("refID"));
		String	strTrans = boneElem.getAttribute("transformation");
		
		String[]	mat = strTrans.split(",");
		transformation = new Matrix4(
				Float.parseFloat(mat[0]), Float.parseFloat(mat[3]), Float.parseFloat(mat[6]), Float.parseFloat(mat[9]),
				Float.parseFloat(mat[1]), Float.parseFloat(mat[4]), Float.parseFloat(mat[7]), Float.parseFloat(mat[10]),
				Float.parseFloat(mat[2]), Float.parseFloat(mat[5]), Float.parseFloat(mat[8]), Float.parseFloat(mat[11]),
				0, 0, 0, 1 );
	}
}
