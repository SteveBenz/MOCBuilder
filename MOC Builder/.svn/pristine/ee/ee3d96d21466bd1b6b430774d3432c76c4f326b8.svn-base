package UndoRedo;

import java.util.ArrayList;

import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class CopyDirectivesAction implements IAction {
	private ArrayList<LDrawDirective> directiveList;
	private MOCBuilder builder;

	private CopyDirectivesAction() {
		directiveList = new ArrayList<LDrawDirective>();
	}

	public CopyDirectivesAction(MOCBuilder builder,
			ArrayList<LDrawDirective> directives) {
		this();
		this.builder = builder;
		for (LDrawDirective directive : directives)
			directiveList.add(directive);
	}

	@Override
	public void undoAction() {
		for (LDrawDirective directive : directiveList)
			builder.removeDirectiveFromWorkingFile(directive);
	}

	@Override
	public void redoAction() {
		for (LDrawDirective directive : directiveList)
			builder.addDirectiveToWorkingFile(directive);
	}
}
