package LDraw.Support;

public class DispatchGroup {
	private boolean isCCW = true;
	private boolean isInvertedNext = false;
	private boolean isInverted = false;
	private boolean isReversed = false;

	public void extendsFromParent(DispatchGroup parent) {
		isCCW = parent.isCCW();
		if (parent.isInvertedNext) {
			isInverted = true;
			parent.setInvertedNext(false);
		}
	}

	public void _release() {
		// TODO Auto-generated method stub

	}

	public void _wait() {
		// TODO Auto-generated method stub

	}

	public boolean isCCW() {
		boolean ret = false;
		if (isInverted)
			ret = !isCCW;
		else
			ret = isCCW;

		if (isReversed)
			ret = !ret;
		return ret;
	}

	public boolean setCCW(boolean isCCW) {
		this.isCCW = isCCW;
		return this.isCCW;
	}

	public boolean isInvertedNext() {
		return isInvertedNext;
	}

	public void setInvertedNext(boolean isInvertedNext) {
		this.isInvertedNext = isInvertedNext;
	}

	public void setReversed() {
		this.isReversed = true;
	}

}
