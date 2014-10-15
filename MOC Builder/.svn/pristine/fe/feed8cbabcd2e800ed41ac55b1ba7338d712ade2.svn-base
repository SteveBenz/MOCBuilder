package UndoRedo;

import java.util.ArrayList;

import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class ChangeDirectivesParentStepAction implements IAction {
	private ArrayList<LDrawStep> oldParentList;
	private ArrayList<LDrawStep> newParentList;
	private ArrayList<Integer> oldIndexList;
	private ArrayList<LDrawDirective> directiveList;
	private MOCBuilder builder;

	public ChangeDirectivesParentStepAction() {
		oldParentList = new ArrayList<LDrawStep>();
		newParentList = new ArrayList<LDrawStep>();
		directiveList = new ArrayList<LDrawDirective>();
		oldIndexList = new ArrayList<Integer>();
	}

	public void add(MOCBuilder builder, LDrawStep newStep,
			LDrawDirective directive, int oldIndex) {
		if (this.builder == null)
			this.builder = builder;
		this.newParentList.add(newStep);
		this.oldParentList.add(directive.enclosingStep());
		this.directiveList.add(directive);
		this.oldIndexList.add(oldIndex);
	}

	@Override
	public void undoAction() {
		for (int i = 0; i < directiveList.size(); i++) {
			builder.ChangeDirectivesParentStepAction(directiveList.get(i),
					newParentList.get(i), oldParentList.get(i),
					oldIndexList.get(i));
		}
	}

	@Override
	public void redoAction() {
		for (int i = 0; i < directiveList.size(); i++)
			builder.ChangeDirectivesParentStepAction(directiveList.get(i),
					oldParentList.get(i), newParentList.get(i));
	}
}
