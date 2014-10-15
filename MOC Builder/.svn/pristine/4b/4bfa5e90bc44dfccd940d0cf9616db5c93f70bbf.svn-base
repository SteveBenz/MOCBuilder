package UndoRedo;

import java.util.ArrayList;

import Command.LDrawColor;
import Command.LDrawPart;

public class ColorChangeAction implements IAction {
	private ArrayList<LDrawPart> parts;
	private ArrayList<LDrawColor> originalColors;
	private ArrayList<LDrawColor> newColors;

	public ColorChangeAction() {
		parts = new ArrayList<LDrawPart>();
		originalColors = new ArrayList<LDrawColor>();
		newColors = new ArrayList<LDrawColor>();
	}

	public void addColorChangePart(LDrawPart part, LDrawColor originalColor,
			LDrawColor newColor) {
		parts.add(part);
		originalColors.add(originalColor);
		newColors.add(newColor);
	}

	@Override
	public void undoAction() {
		for (int i = 0; i < parts.size(); i++)
			parts.get(i).setLDrawColor(originalColors.get(i));
	}

	@Override
	public void redoAction() {
		for (int i = 0; i < parts.size(); i++)
			parts.get(i).setLDrawColor(newColors.get(i));
	}
}
