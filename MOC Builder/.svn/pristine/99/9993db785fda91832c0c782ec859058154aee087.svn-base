package Connectivity;

public class Gear extends Connectivity {
	float radius;
	int toothCount;
	public float getradius() {
		return radius;
	}
	public void setradius(String radius) {
		this.radius = Float.parseFloat(radius);
	}
	public int gettoothCount() {
		return toothCount;
	}
	public void settoothCount(String toothCount) {
		this.toothCount = Integer.parseInt(toothCount);
	}
	
	@Override
	public String toString() {
		return super.toString(radius + " " + toothCount);
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setradius(line[size+1]);
		settoothCount(line[size+2]);
		return 0;
	}
	@Override
	public String getName() {
		return "Gear";
	}
}
