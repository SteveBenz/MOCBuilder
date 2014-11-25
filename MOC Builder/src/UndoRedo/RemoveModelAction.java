package UndoRedo;

import LDraw.Files.LDrawMPDModel;
import Window.MOCBuilder;

public class RemoveModelAction implements IAction {	
	private LDrawMPDModel model;

	public RemoveModelAction() {
	}

	public void setModel(LDrawMPDModel model) {
		this.model = model;
	}
	
	@Override
	public void undoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		builder.addSubModel(model);
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		builder.removeModel(model);
	}
}
