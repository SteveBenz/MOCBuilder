package Connectivity;

public class Rail extends Connectivity {
	float length;

	public float getlength() {
		return length;
	}

	public void setlength(String length) {
		this.length = Float.parseFloat(length);
	}
	
	@Override
	public String toString() {
		return super.toString(String.valueOf(length));
	}
	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setlength(line[size+1]);
		return 0;
	}

	@Override
	public String getName() {
		return "Rail";
	}
}
