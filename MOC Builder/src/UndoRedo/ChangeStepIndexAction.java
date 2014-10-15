package UndoRedo;

import LDraw.Files.LDrawStep;
import Window.MOCBuilder;

public class ChangeStepIndexAction implements IAction {
	private int oldIndex;
	private int newIndex;
	private LDrawStep step;
	private MOCBuilder builder;

	private ChangeStepIndexAction() {
	}

	public ChangeStepIndexAction(MOCBuilder builder, LDrawStep step,
			int oldIndex, int newIndex) {
		this();
		this.builder = builder;
		this.step = step;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	public void undoAction() {
		builder.changeStepIndex(step,  oldIndex);		
	}

	@Override
	public void redoAction() {
		builder.changeStepIndex(step,  newIndex);
	}
}
