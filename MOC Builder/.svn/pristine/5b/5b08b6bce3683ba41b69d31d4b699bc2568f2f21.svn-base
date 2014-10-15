package UndoRedo;

import java.util.ArrayList;

import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class ChangeDirectivesIndexAction implements IAction {
	private ArrayList<Integer> oldIndexList;
	private ArrayList<Integer> newIndexList;
	private LDrawStep parent;
	private ArrayList<LDrawDirective> directives;
	private MOCBuilder builder;

	public ChangeDirectivesIndexAction() {
		oldIndexList = new ArrayList<Integer>();
		newIndexList = new ArrayList<Integer>();
		directives = new ArrayList<LDrawDirective>();
	}

	public ChangeDirectivesIndexAction(MOCBuilder builder, LDrawStep parent,
			LDrawDirective directive, int oldIndex, int newIndex) {
		this();
		this.builder = builder;
		this.parent = parent;
		this.directives.add(directive);
		this.oldIndexList.add(oldIndex);
		this.newIndexList.add(newIndex);
	}

	public void add(MOCBuilder builder, LDrawStep parent,
			LDrawDirective directive, int oldIndex, int newIndex) {
		if (this.builder == null)
			this.builder = builder;
		if (this.parent == null)
			this.parent = parent;

		this.directives.add(directive);
		this.oldIndexList.add(oldIndex);
		this.newIndexList.add(newIndex);
	}

	@Override
	public void undoAction() {
		for (int i = 0; i < directives.size(); i++)
			builder.changeDirectiveIndex(parent, directives.get(i),
					oldIndexList.get(i));
	}

	@Override
	public void redoAction() {
		for (int i = 0; i < directives.size(); i++)
			builder.changeDirectiveIndex(parent, directives.get(i),
					newIndexList.get(i));
	}
}
