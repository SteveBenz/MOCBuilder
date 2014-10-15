package UndoRedo;

import java.util.ArrayList;

import Builder.BrickSelectionManager;
import Connectivity.GlobalConnectivityManager;
import LDraw.Files.LDrawContainer;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class DirectiveAction implements IAction {
	private ArrayList<Integer> indexList;
	private ArrayList<LDrawContainer> parentList;
	private ArrayList<LDrawDirective> directiveList;
	private ArrayList<Boolean> flagList;

	public DirectiveAction() {
		indexList = new ArrayList<Integer>();
		parentList = new ArrayList<LDrawContainer>();
		directiveList = new ArrayList<LDrawDirective>();
		flagList = new ArrayList<Boolean>();
	}

	private void add(LDrawDirective directive,boolean flag) {
		indexList.add(directive.enclosingDirective()
				.indexOfDirective(directive));
		parentList.add(directive.enclosingDirective());
		directiveList.add(directive);
		flagList.add(flag);
	}

	public void addDirective(LDrawDirective directive) {
		add(directive,true);
	}

	public void removeDirective(LDrawDirective directive) {
		add(directive,false);

	}

	public void removeDirectives(ArrayList<LDrawDirective> directives) {
		for (LDrawDirective directive : directives) {
			add(directive,false);
		}
	}

	@Override
	public void undoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directiveList.size(); i++) {
			if (flagList.get(i)) {
				builder.removeDirectiveFromWorkingFile(directiveList.get(i));
			} else {
				builder.insertDirectiveToWorkingFile(indexList.get(i),
						parentList.get(i), directiveList.get(i), true);
			}
		}
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		BrickSelectionManager.getInstance()
				.updateScreenProjectionVerticesMapAll();
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (int i = 0; i < directiveList.size(); i++) {
			if (flagList.get(i)) {
				builder.insertDirectiveToWorkingFile(indexList.get(i),
						parentList.get(i), directiveList.get(i), true);
			} else {
				builder.removeDirectiveFromWorkingFile(directiveList.get(i));
			}
		}
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		BrickSelectionManager.getInstance()
				.updateScreenProjectionVerticesMapAll();
	}
}
