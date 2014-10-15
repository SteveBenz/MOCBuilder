package LDraw.Support.type;

public enum MessageT {

	// The reference name of the MPD model has changed and observers should 
	// update their string references.
	MessageNameChanged(0),
	
	// The MPD's parent has changed, and thus its scope may have changed
	MessageScopeChanged(1),

    // The observed have changed in a way that may require the observer to
    // update its representation (e.g. an LSynth constraint has moved and
    // requires resynthesis)
    MessageObservedChanged(2);
    
    /**
	 * @uml.property  name="value"
	 */
    private int value;
	private MessageT(int value){
		this.value = value;
	}
}
