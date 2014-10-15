package LDD.bricks;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Part {
	public	int	 	refID;
	public	String	designID;
	public	int[]	materials;
	public	int[]	decoration;

	public	Bone	bone;
	
	public	Part( int rID, String dID, int material, float tx, float ty, float tz )
	{
		refID = 0;
		designID = dID;
		materials = new int[1];
		materials[0] = material;
		decoration = new int[1];
		decoration[0] = 0;
		
		bone = new Bone( rID, tx, ty, tz );
	}
	
	public	Part( Element partElem )
	{
		refID = Integer.parseInt(partElem.getAttribute("refID"));
		designID = partElem.getAttribute("designID");
		
		String		str = partElem.getAttribute("materials");
		String[]	strMaterials = str.split(",");
		materials = new int[strMaterials.length];
		for(int i=0; i<strMaterials.length; i++ )
		{
			materials[i] = Integer.parseInt(strMaterials[i]);
		}
		
		String		strDeco = partElem.getAttribute("decoration");
		if( strDeco != null && strDeco.length() > 0 )
		{
			String[]	strDecos = strDeco.split(",");
			decoration = new int[strDecos.length];
			for(int i=0; i<strDecos.length; i++ )
			{
				decoration[i] = Integer.parseInt(strDecos[i]);
			}
		}
		
		NodeList	boneList = partElem.getElementsByTagName("Bone");
		Node		boneNode = boneList.item(0);
		Element		boneElem = (Element)boneNode;
	
		bone = new Bone(boneElem);
		
	}
}
