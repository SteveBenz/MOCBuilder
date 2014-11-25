package UndoRedo;

import LDraw.Files.LDrawMPDModel;
import Window.MOCBuilder;

public class RenameModelAction implements IAction {
	private LDrawMPDModel model;
	private String newName;
	private String oldName;

	public RenameModelAction(LDrawMPDModel model, String newName) {
		this.model = model;
		this.oldName = model.modelName();
		this.newName = newName;
	}

	@Override
	public void undoAction() {
		MOCBuilder.getInstance().renameModel(model, oldName);
	}

	@Override
	public void redoAction() {
		MOCBuilder.getInstance().renameModel(model, newName);
	}
}
