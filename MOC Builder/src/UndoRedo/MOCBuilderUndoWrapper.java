package UndoRedo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Builder.DirectiveSelectionManager;
import Builder.MainCamera;
import Command.CameraTransformCommand;
import Command.LDrawColor;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import Command.LDrawLSynthDirective;
import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import Connectivity.Direction6T;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawModel;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawMetaCommand;
import Notification.LDrawDirectiveDidChanged;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Window.MOCBuilder;

public class MOCBuilderUndoWrapper {
	private static MOCBuilderUndoWrapper _instance = null;

	private MOCBuilder builder = null;

	private MOCBuilderUndoWrapper() {
		builder = MOCBuilder.getInstance();
	}

	public synchronized static MOCBuilderUndoWrapper getInstance() {
		if (_instance == null)
			_instance = new MOCBuilderUndoWrapper();
		return _instance;
	}

	public void makeASubmodelFromSelection() {
		if (DirectiveSelectionManager.getInstance().getSelectedDirectiveSize() == 0)
			return;

		MakeASubModelAction action = new MakeASubModelAction();
		for (LDrawDirective directive : DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveList()) {
			if (directive instanceof LDrawLSynthDirective) {
				if (directive.enclosingDirective().isSelected() == false) {
					DirectiveSelectionManager.getInstance()
							.addDirectiveToSelection(
									directive.enclosingDirective());
				}
				DirectiveSelectionManager.getInstance()
						.removeDirectiveFromSelection(directive);
			}
		}

		for (LDrawDirective directive : DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveList())
			action.add(directive);
		LDrawPart part = builder.makeASubmodelFromSelection();
		if (part != null) {
			action.setPart(part);
			action.setModel(part.getCacheModel());
		}
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void extractDirectivesFromASubmodel(LDrawPart part) {
		if (part == null)
			return;

		ExtractDirectivesFromASubModelAction action = new ExtractDirectivesFromASubModelAction();
		action.setPart(part);

		ArrayList<LDrawDirective> extractedPartList = builder
				.extractDirectivesFromASubmodel(part);
		if (extractedPartList == null)
			return;
		for (LDrawDirective directive : extractedPartList)
			action.add(directive);

		action.setModel(part.getCacheModel());

		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void removeModel(LDrawModel model) {
		if (model == null)
			return;
		RemoveModelAction action = new RemoveModelAction();
		if (model instanceof LDrawMPDModel)
			action.setModel((LDrawMPDModel) model);
		builder.removeModel(model);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void hideSelectedDirectives() {
		boolean flag = false;
		HideShowLDrawElementAction action = new HideShowLDrawElementAction();
		for (LDrawDrawableElement element : builder.hideSelectedDirectives()) {
			action.addPartForHide(element);
			flag = true;
		}
		if (flag)
			LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void hideAllStep() {
		boolean flag = false;
		HideShowLDrawElementAction action = new HideShowLDrawElementAction();
		for (LDrawDrawableElement element : builder.hideAllStep()) {
			action.addPartForHide(element);
			flag = true;
		}
		if (flag)
			LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void showAllStep() {
		boolean flag = false;
		HideShowLDrawElementAction action = new HideShowLDrawElementAction();
		for (LDrawDrawableElement element : builder.showAllStep()) {
			action.addPartForShow(element);
			flag = true;
		}
		if (flag)
			LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void showSelectedDirectives() {
		boolean flag = false;
		HideShowLDrawElementAction action = new HideShowLDrawElementAction();
		for (LDrawDrawableElement element : builder.showSelectedDirectives()) {
			action.addPartForShow(element);
			flag = true;
		}
		if (flag)
			LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void removeSelectedDirective() {
		boolean flag = false;
		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		for (LDrawDirective directive : DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveList()) {
			action.removeDirective(directive);
			flag = true;
		}
		if (flag) {
			LDrawUndoRedoManager.getInstance().pushUndoAction(action);
			builder.removeSelectedDirective();
		}
	}

	public void addNewGroupToWorkingFile() {
		LDrawStep step = builder.newStepToWorkingFile();
		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.addDirective(step);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void addDirectiveToWorkingFile(LDrawDirective directive) {
		builder.addDirectiveToWorkingFile(directive);

		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.addDirective(directive);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void addDirectiveToWorkingFile(LDrawStep step,
			LDrawDirective directive) {
		builder.addDirectiveToWorkingFile(step, directive);

		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.addDirective(directive);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);

	}

	public void insertDirectiveToWorkingFile(int index, LDrawStep step,
			LDrawDirective directive) {
		builder.insertDirectiveToWorkingFile(index, step, directive);

		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.addDirective(directive);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);

	}

	public void addNewGroupToWorkingFile(int indexOfDirective) {
		LDrawStep step = builder.addStepToWorkingFileAt(indexOfDirective);
		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.addDirective(step);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void makeNewStepFromSeletion() {
		boolean flag = false;

		MakeAGroupAction action = new MakeAGroupAction();
		for (LDrawDirective directive : DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveList()) {
			if (directive instanceof LDrawLSynthDirective) {
				if (directive.enclosingDirective().isSelected() == false)
					DirectiveSelectionManager.getInstance()
							.addDirectiveToSelection(
									directive.enclosingDirective());
				DirectiveSelectionManager.getInstance()
						.removeDirectiveFromSelection(directive);
			}
		}
		for (LDrawDirective directive : DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveList()) {
			if (directive instanceof LDrawStep == false) {
				action.add(directive);
				flag = true;
			}
		}

		if (flag == false)
			return;

		LDrawStep newStep = builder.makeNewStepFromSeletion();

		action.setStep(newStep);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void replaceDirective(LDrawDirective from, LDrawDirective to) {
		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		action.removeDirective(from);
		builder.replaceDirective(from, to);
		action.addDirective(to);

		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void replaceDirectives(
			HashMap<LDrawDirective, LDrawDirective> fromToMap) {
		if (fromToMap == null)
			return;

		AddNRemoveDirectiveAction action = new AddNRemoveDirectiveAction();
		for (Entry<LDrawDirective, LDrawDirective> entry : fromToMap.entrySet()) {
			action.removeDirective(entry.getKey());
			builder.replaceDirective(entry.getKey(), entry.getValue());
			action.addDirective(entry.getValue());
		}

		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void changeDirectiveIndex(LDrawContainer container,
			HashMap<LDrawDirective, Integer> newIndexMap) {
		ChangeDirectivesIndexAction action = new ChangeDirectivesIndexAction();
		HashMap<LDrawDirective, Integer> oldIndexMap = new HashMap<LDrawDirective, Integer>();
		for (Entry<LDrawDirective, Integer> entry : newIndexMap.entrySet())
			oldIndexMap.put(entry.getKey(),
					container.indexOfDirective(entry.getKey()));
		for (Entry<LDrawDirective, Integer> entry : newIndexMap.entrySet()) {
			action.add(container, entry.getKey(),
					oldIndexMap.get(entry.getKey()), entry.getValue());
			builder.changeDirectiveIndex(container, entry.getKey(),
					entry.getValue());
		}
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void changeParentOfDirectiveAction(LDrawContainer newParent,
			HashMap<LDrawDirective, LDrawContainer> oldParentMap) {
		ChangeParentOfDirectiveAction action = new ChangeParentOfDirectiveAction();
		for (Entry<LDrawDirective, LDrawContainer> entry : oldParentMap
				.entrySet())
			action.add(newParent, entry.getKey(), entry.getValue()
					.indexOfDirective(entry.getKey()));
		for (Entry<LDrawDirective, LDrawContainer> entry : oldParentMap
				.entrySet())
			builder.ChangeParentOfDirectiveAction(entry.getKey(),
					entry.getValue(), newParent);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void changeColor(HashMap<LDrawDirective, LDrawColor> newColorMap) {
		if (newColorMap.size() == 0)
			return;
		ChangeColorAction colorChangeAction = new ChangeColorAction(newColorMap);
		for (Entry<LDrawDirective, LDrawColor> entry : newColorMap.entrySet()) {
			builder.changeColor(entry.getKey(), entry.getValue());
		}
		LDrawUndoRedoManager.getInstance().pushUndoAction(colorChangeAction);
	}

	public void handleTransformSelectedDirective() {
		MoveDirectivesAction action = new MoveDirectivesAction();
		Matrix4 originalMatrix;
		ArrayList<LDrawDirective> directiveList = DirectiveSelectionManager
				.getInstance().getSelectedDirectiveList();
		for (LDrawDirective directive : directiveList) {
			if (directive instanceof LDrawDrawableElement == false)
				continue;
			LDrawDrawableElement element = (LDrawDrawableElement) directive;
			originalMatrix = DirectiveSelectionManager.getInstance()
					.getInitialMoveTransformMatrix(element);
			if (originalMatrix == null)
				continue;
			action.addElement(element, originalMatrix,
					element.transformationMatrix());
		}

		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void rotateSelectedDirectiveBy(Vector3f degree) {
		builder.rotateSelectedDirectiveBy(degree);
		handleTransformSelectedDirective();
	}

	public void snapToGrid() {
		builder.snapToGrid();
		handleTransformSelectedDirective();
	}

	public void adjustRotationMatrixForSelectedDirective() {
		builder.adjustRotationMatrixForSelectedDirective();
		handleTransformSelectedDirective();
	}

	public void alignSelectedDirective(Direction6T direction) {
		builder.alignSelectedDirective(direction);
		handleTransformSelectedDirective();
	}

	public void renameGroup(LDrawDirective directive, String newName) {
		if (directive == null)
			return;
		if (directive instanceof LDrawStep == false)
			return;
		RenameGroupAction action = new RenameGroupAction((LDrawStep) directive,
				newName);
		((LDrawStep) directive).setStepName(newName);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void renameModel(LDrawDirective directive, String newName) {
		if (directive == null)
			return;
		if (directive instanceof LDrawMPDModel == false)
			return;
		RenameModelAction action = new RenameModelAction(
				(LDrawMPDModel) directive, newName);
		builder.renameModel((LDrawMPDModel) directive, newName);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void modifyComment(LDrawDirective directive, String newString) {
		if (directive == null)
			return;
		if (directive instanceof LDrawMetaCommand == false)
			return;
		ModifyCommentAction action = new ModifyCommentAction(
				(LDrawMetaCommand) directive, newString);
		((LDrawMetaCommand) directive).setStringValue(newString);
		LDrawUndoRedoManager.getInstance().pushUndoAction(action);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(directive));
	}

	public void updateCameraTransform(CameraTransformCommand cCommand) {
		MainCamera camera = builder.getCamera();
		cCommand.setDistanceToObject(camera.getDistanceBetweenObjectToCamera());
		cCommand.setLookAtPos(new Vector3f(camera.getLookAtPos()));
		cCommand.setRotation(camera.getCurrentRotation());

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(cCommand));
	}

	public void insertANewCameraTransform(LDrawStep step, int index,
			String description) {
		CameraTransformCommand cCommand = new CameraTransformCommand();
		cCommand.setDesciprion(description);
		MainCamera camera = builder.getCamera();
		cCommand.setDistanceToObject(camera.getDistanceBetweenObjectToCamera());
		cCommand.setLookAtPos(new Vector3f(camera.getLookAtPos()));
		cCommand.setRotation(camera.getCurrentRotation());

		if (step == null)
			addDirectiveToWorkingFile(cCommand);
		else
			insertDirectiveToWorkingFile(index, step, cCommand);
	}

	public void addANewCameraTransform(LDrawStep step, String description) {
		CameraTransformCommand cCommand = new CameraTransformCommand();
		cCommand.setDesciprion(description);
		MainCamera camera = builder.getCamera();
		cCommand.setDistanceToObject(camera.getDistanceBetweenObjectToCamera());
		cCommand.setLookAtPos(new Vector3f(camera.getLookAtPos()));
		cCommand.setRotation(camera.getCurrentRotation());

		if (step == null)
			addDirectiveToWorkingFile(cCommand);
		else
			addDirectiveToWorkingFile(step, cCommand);
	}
}
