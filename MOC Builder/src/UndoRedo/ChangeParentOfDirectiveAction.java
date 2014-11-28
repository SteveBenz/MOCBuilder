package UndoRedo;

import java.util.ArrayList;

import LDraw.Files.LDrawContainer;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class ChangeParentOfDirectiveAction implements IAction {
	private ArrayList<LDrawContainer> oldParentList;
	private ArrayList<LDrawContainer> newParentList;
	private ArrayList<Integer> oldIndexList;
	private ArrayList<LDrawDirective> directiveList;

	public ChangeParentOfDirectiveAction() {
		oldParentList = new ArrayList<LDrawContainer>();
		newParentList = new ArrayList<LDrawContainer>();
		directiveList = new ArrayList<LDrawDirective>();
		oldIndexList = new ArrayList<Integer>();
	}

	public void add(LDrawContainer newStep, LDrawDirective directive, int oldIndex) {
		this.newParentList.add(newStep);
		this.oldParentList.add(directive.enclosingStep());
		this.directiveList.add(directive);
		this.oldIndexList.add(oldIndex);
	}

	@Override
	public void undoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directiveList.size(); i++) {
			builder.ChangeParentOfDirectiveAction(directiveList.get(i),
					newParentList.get(i), oldParentList.get(i),
					oldIndexList.get(i));
		}
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directiveList.size(); i++)
			builder.ChangeParentOfDirectiveAction(directiveList.get(i),
					oldParentList.get(i), newParentList.get(i));
	}
}
