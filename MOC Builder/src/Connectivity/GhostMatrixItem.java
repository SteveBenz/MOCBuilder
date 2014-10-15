package Connectivity;

import Command.LDrawPart;

public class GhostMatrixItem extends MatrixItem {	
	public GhostMatrixItem() {
		altitude = occupiedArea = shape = 0;
		altitude = 0;
		this.parent = null;		
		LDrawPart newPart = new LDrawPart();
		Stud newStud = new Stud();
		newStud.setParent(newPart);		
		setAltitude(0);
		setShape(1);
		setColumnIndex(0);
		setRowIndex(0);
		setParent(newStud);		
	}

}
