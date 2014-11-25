package UndoRedo;

import java.util.HashMap;
import java.util.Map.Entry;

import Command.LDrawColor;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import LDraw.Support.LDrawDirective;

public class ChangeColorAction implements IAction {
	private HashMap<LDrawDirective, LDrawColor> originalColorMap;
	private HashMap<LDrawDirective, LDrawColor> newColorMap;

	public ChangeColorAction() {
		originalColorMap = new HashMap<LDrawDirective, LDrawColor>();
		newColorMap = new HashMap<LDrawDirective, LDrawColor>();
	}

	public ChangeColorAction(HashMap<LDrawDirective, LDrawColor> newColorMap) {
		this();
		for (Entry<LDrawDirective, LDrawColor> entry : newColorMap.entrySet()) {
			newColorMap.put(entry.getKey(), entry.getValue());

			if (entry.getKey() instanceof LDrawDrawableElement)
				originalColorMap
						.put(entry.getKey(), ((LDrawDrawableElement) entry
								.getKey()).getLDrawColor());
			else if (entry.getKey() instanceof LDrawLSynth)
				originalColorMap.put(entry.getKey(),
						((LDrawLSynth) entry.getKey()).getLDrawColor());
		}
	}

	public void addElement(LDrawDirective directive, LDrawColor originalColor,
			LDrawColor newColor) {
		originalColorMap.put(directive, originalColor);
		newColorMap.put(directive, newColor);

	}

	@Override
	public void undoAction() {
		for (Entry<LDrawDirective, LDrawColor> entry : originalColorMap
				.entrySet()) {
			LDrawDirective directive = entry.getKey();

			if (directive instanceof LDrawDrawableElement)
				((LDrawDrawableElement) directive).setLDrawColor(entry
						.getValue());
			if (directive instanceof LDrawLSynth)
				((LDrawLSynth) directive).setLDrawColor(entry.getValue());
		}
	}

	@Override
	public void redoAction() {
		for (Entry<LDrawDirective, LDrawColor> entry : newColorMap.entrySet()) {
			LDrawDirective directive = entry.getKey();

			if (directive instanceof LDrawDrawableElement)
				((LDrawDrawableElement) directive).setLDrawColor(entry
						.getValue());
			if (directive instanceof LDrawLSynth)
				((LDrawLSynth) directive).setLDrawColor(entry.getValue());
		}
	}
}
