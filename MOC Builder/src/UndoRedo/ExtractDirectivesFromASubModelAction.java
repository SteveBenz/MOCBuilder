package UndoRedo;

import java.util.ArrayList;
import java.util.HashMap;

import Command.LDrawPart;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawModel;
import LDraw.Support.LDrawDirective;
import Window.MOCBuilder;

public class ExtractDirectivesFromASubModelAction implements IAction {
	private HashMap<LDrawDirective, LDrawContainer> parentMap;
	private HashMap<LDrawDirective, Integer> indexMap;
	private ArrayList<LDrawDirective> directives;

	private LDrawModel model;
	private LDrawPart part;

	public ExtractDirectivesFromASubModelAction() {
		parentMap = new HashMap<LDrawDirective, LDrawContainer>();
		indexMap = new HashMap<LDrawDirective, Integer>();
		directives = new ArrayList<LDrawDirective>();
		
	}

	public void setModel(LDrawModel model) {
		this.model = model;
	}

	public void setPart(LDrawPart part) {
		this.part = part;
		indexMap.put(part, part.enclosingDirective().subdirectives().indexOf(part));
		parentMap.put(part, part.enclosingDirective());
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
		for (LDrawDirective directive : directives)
			builder.removeDirectiveFromWorkingFile(directive);		
		builder.insertDirectiveToWorkingFile(indexMap.get(part), parentMap.get(part), part);
	}

	@Override
	public void redoAction() {
		MOCBuilder builder = MOCBuilder.getInstance();
		builder.removeDirectiveFromWorkingFile(part);
		for (LDrawDirective directive : directives)
			builder.insertDirectiveToWorkingFile(indexMap.get(directive),
					parentMap.get(directive), directive);
	}
}
