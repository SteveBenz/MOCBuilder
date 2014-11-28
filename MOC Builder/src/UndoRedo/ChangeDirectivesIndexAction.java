package UndoRedo;

import java.util.ArrayList;

import LDraw.Files.LDrawContainer;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class ChangeDirectivesIndexAction implements IAction {
	private ArrayList<Integer> oldIndexList;
	private ArrayList<Integer> newIndexList;
	private LDrawContainer parent;
	private ArrayList<LDrawDirective> directives;

	public ChangeDirectivesIndexAction() {
		oldIndexList = new ArrayList<Integer>();
		newIndexList = new ArrayList<Integer>();
		directives = new ArrayList<LDrawDirective>();
	}

	public ChangeDirectivesIndexAction(LDrawContainer parent,
			LDrawDirective directive, int oldIndex, int newIndex) {
		this();
		this.parent = parent;
		this.directives.add(directive);
		this.oldIndexList.add(oldIndex);
		this.newIndexList.add(newIndex);
	}

	public void add(LDrawContainer parent, LDrawDirective directive, int oldIndex,
			int newIndex) {
		if (this.parent == null)
			this.parent = parent;

		this.directives.add(directive);
		this.oldIndexList.add(oldIndex);
		this.newIndexList.add(newIndex);
	}

	@Override
	public void undoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directives.size(); i++)
			builder.changeDirectiveIndex(parent, directives.get(i),
					oldIndexList.get(i));
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directives.size(); i++)
			builder.changeDirectiveIndex(parent, directives.get(i),
					newIndexList.get(i));
	}
}
