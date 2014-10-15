package Command;

public enum PartTypeT {
	PartTypeUnresolved(0),	// We have not yet tried to figure out what we have.
			PartTypeNotFound(1),		// We went looking and the part is missing.  This keeps us from retrying on every query until someone tells us to try again.
			PartTypeLibrary(2),		// Part is in the library.
			PartTypeSubmodel(3),		// Part is an MPD submodel from our parent LDrawFile
			PartTypePeerFile(4);		// Part is the first model in another file in the same directory as us.
	
	/**
	 * @uml.property  name="value"
	 */
	private int value;
	
	private PartTypeT(int value){
		this.value = value;
	}
}
