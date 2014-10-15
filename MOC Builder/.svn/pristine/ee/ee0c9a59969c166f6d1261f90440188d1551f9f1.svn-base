package Common;

public class Transformation {
	public		Vector3f	trans;
	public		Vector3f	direction;
	public		float		angle;
	
	public	Transformation( float t_x, float t_y, float t_z, float a_x, float a_y, float a_z, float _angle )
	{
		trans = new Vector3f( t_x, t_y, t_z );
		direction = new Vector3f( a_x, a_y, a_z );
		angle = _angle;
	}
	
	public	Transformation( String t_x, String t_y, String t_z, String a_x, String a_y, String a_z, String _angle )
	{
		trans = new Vector3f( t_x, t_y, t_z );
		direction = new Vector3f( a_x, a_y, a_z );
		
		if( _angle == null || _angle.length() == 0 )
		{
			angle = 0.0f;
		}
		else
		{
			angle = Float.parseFloat(_angle);
		}
	}
	
	public	Matrix4	GetMatrix4()
	{		
		Matrix4	matMap = new Matrix4();
		matMap.rotate(angle, direction);
		matMap.setElement(3, 0, trans.x);
		matMap.setElement(3, 1, trans.y);
		matMap.setElement(3, 2, trans.z);
				
		return matMap;
	}

}
