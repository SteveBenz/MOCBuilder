package UndoRedo;

import java.util.ArrayList;
import java.util.HashMap;

import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import Notification.LDrawDirectiveDidAdded;
import Notification.LDrawDirectiveDidRemoved;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Window.MOCBuilder;

public class MakeAGroupAction implements IAction {
	private HashMap<LDrawDirective, LDrawContainer> parentMap;
	private HashMap<LDrawDirective, Integer> indexMap;
	private ArrayList<LDrawDirective> directives;

	private LDrawStep step;

	public MakeAGroupAction() {
		parentMap = new HashMap<LDrawDirective, LDrawContainer>();
		indexMap = new HashMap<LDrawDirective, Integer>();
		directives = new ArrayList<LDrawDirective>();
	}

	public void setStep(LDrawStep step) {
		this.step = step;
	}

	public void add(LDrawDirective directive) {
		directives.add(directive);
		parentMap.put(directive, directive.enclosingDirective());
		indexMap.put(directive, directive.enclosingDirective().subdirectives()
				.indexOf(directive));
	}

	@Override
	public void undoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		if (step != null)
			builder.removeDirectiveFromWorkingFile(step);

		for (LDrawDirective directive : directives) {
			builder.insertDirectiveToWorkingFile(indexMap.get(directive),
					parentMap.get(directive), directive);
			
		}
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		for (LDrawDirective directive : directives){
			builder.removeDirectiveFromWorkingFile(directive);			
		}

		builder.addDirectiveToWorkingFile(step);
	}
}
