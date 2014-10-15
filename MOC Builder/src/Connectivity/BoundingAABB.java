package Connectivity;


public class BoundingAABB extends Connectivity {
	float	minX, minY, minZ;
	float	maxX, maxY, maxZ;
	
	public float getMinX() {
		return minX;
	}
	public void setminX(String minX) {
		this.minX = Float.parseFloat(minX);
	}
	public float getMinY() {
		return minY;
	}
	public void setminY(String minY) {
		this.minY = Float.parseFloat(minY);
	}
	public float getminZ() {
		return minZ;
	}
	public void setminZ(String minZ) {
		this.minZ = Float.parseFloat(minZ);
	}
	public float getmaxX() {
		return maxX;
	}
	public void setmaxX(String maxX) {
		this.maxX = Float.parseFloat(maxX);
	}
	public float getmaxY() {
		return maxY;
	}
	public void setmaxY(String maxY) {
		this.maxY = Float.parseFloat(maxY);
	}
	public float getmaxZ() {
		return maxZ;
	}
	public void setmaxZ(String maxZ) {
		this.maxZ = Float.parseFloat(maxZ);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "BoundingAABB";
	}
	
	@Override
	public int parseString(String[] line) {
		settype(line[1]);
		setminX(line[2]);
		setminY(line[3]);
		setminZ(line[4]);
		setmaxX(line[5]);
		setmaxY(line[6]);
		setmaxZ(line[7]);
		fileName = line[line.length - 1];
		return 0;
	}
	@Override
	public String toString() {
		String	detail = String.format("%.6f %.6f %.6f %.6f %.6f %.6f", minX*25.0f, minY*25.0f, minZ*25.0f, maxX*25.0f, maxY*25.0f, maxZ*25.0f);
		return String.format("%d %d %s %s",
				TYPE.valueOf(getClass().getName().substring(13)).ordinal(),
				type, detail, fileName);
		
	}
}
