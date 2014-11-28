package OtherTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import Builder.DirectiveSelectionManager;
import Command.LDrawColorT;
import Command.LDrawPart;
import Common.Vector3f;
import Exports.PartDomainT;
import LDraw.Support.ColorLibrary;
import LDraw.Support.LDrawDirective;
import LDraw.Support.PartCache;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import UndoRedo.MOCBuilderUndoWrapper;
import Window.ColorPicker;
import Window.MOCBuilder;

public class PartReplaceDlg extends Dialog implements ILDrawSubscriber {

	private static boolean isAlreadyOpen = false;
	protected Object result;
	protected Shell shell;

	private Combo combo_FindPartName;
	private Combo combo_FindColor;
	private Combo combo_ReplacePartName;
	private ColorPicker colorPicker;
	private Button btn_FindNext;
	private Button btn_Replace;
	private Button btn_ReplaceAll;
	private Button btnCheck_ChangeToLDrawPart;
	private Button btnCheck_ChangeColor;
	private Button btnCheck_MatchColor;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PartReplaceDlg(Shell parent, int style) {
		super(parent, style);
		setText("Find/Replace");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		if (isAlreadyOpen) {
			return null;
		}
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		isAlreadyOpen = true;
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.UndoRedoManagerUpdated);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		isAlreadyOpen = false;

		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.UndoRedoManagerUpdated);
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(374, 251);
		shell.setText(getText());

		Label lblFrom = new Label(shell, SWT.NONE);
		lblFrom.setBounds(10, 10, 55, 20);
		lblFrom.setText("Find");

		combo_FindPartName = new Combo(shell, SWT.READ_ONLY);
		combo_FindPartName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (combo_FindPartName.getSelectionIndex() == -1)
					updateFindPartColorComboBtn(null);
				else
					updateFindPartColorComboBtn(combo_FindPartName.getText());
				updateButton();
			}
		});
		combo_FindPartName.setBounds(10, 31, 118, 23);

		combo_FindColor = new Combo(shell, SWT.READ_ONLY);
		combo_FindColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleFindColorComboChanged();
			}
		});
		combo_FindColor.setBounds(134, 31, 134, 23);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 76, 55, 20);
		lblNewLabel.setText("Replace");

		combo_ReplacePartName = new Combo(shell, SWT.NONE);
		combo_ReplacePartName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				updateReplacePartNameComboBtn();
			}
		});

		combo_ReplacePartName.setBounds(10, 97, 118, 23);

		btn_FindNext = new Button(shell, SWT.NONE);
		btn_FindNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleFindNextClicked();
			}
		});
		btn_FindNext.setEnabled(false);
		btn_FindNext.setBounds(274, 29, 95, 25);
		btn_FindNext.setText("Find Next");

		btn_Replace = new Button(shell, SWT.NONE);
		btn_Replace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleReplaceClicked();
			}
		});
		btn_Replace.setEnabled(false);
		btn_Replace.setBounds(274, 60, 95, 25);
		btn_Replace.setText("Replace");

		btn_ReplaceAll = new Button(shell, SWT.NONE);
		btn_ReplaceAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleReplaceAll();
			}
		});
		btn_ReplaceAll.setEnabled(false);
		btn_ReplaceAll.setBounds(274, 95, 95, 25);
		btn_ReplaceAll.setText("ReplaceAll");

		Button btn_Cancel = new Button(shell, SWT.NONE);
		btn_Cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}
		});
		btn_Cancel.setBounds(274, 126, 95, 25);
		btn_Cancel.setText("Cancel");

		btnCheck_MatchColor = new Button(shell, SWT.CHECK);
		btnCheck_MatchColor.setSelection(false);
		btnCheck_MatchColor.setBounds(10, 145, 133, 16);
		btnCheck_MatchColor.setText("Match color");

		Button btn_ReplaceColor = new Button(shell, SWT.FLAT);
		btn_ReplaceColor.setBounds(134, 90, 45, 35);
		btn_ReplaceColor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false));
		colorPicker = new ColorPicker(btn_ReplaceColor, "Replace Color");
		colorPicker.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button selectedButton = (Button) event.widget;
				LDrawColorT colorT = (LDrawColorT) selectedButton.getData();
				colorPicker.setColor(colorT);
				selectedButton.getShell().setVisible(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		btn_ReplaceColor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				colorPicker.showDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		btnCheck_ChangeColor = new Button(shell, SWT.CHECK);
		btnCheck_ChangeColor.setBounds(10, 167, 133, 16);
		btnCheck_ChangeColor.setText("Change Color");

		btnCheck_ChangeToLDrawPart = new Button(shell, SWT.CHECK);
		btnCheck_ChangeToLDrawPart.setBounds(10, 189, 189, 16);
		btnCheck_ChangeToLDrawPart.setText("Change To LDrawPart");
		btnCheck_ChangeToLDrawPart.setSelection(true);

		updateFindPartNameComboBtn();
	}

	protected void handleReplaceClicked() {
		if (lastFoundPart == null)
			handleFindNextClicked();
		else if (lastFoundPart != null) {
			DirectiveSelectionManager.getInstance().clearSelection();
			replacePart(lastFoundPart);
			handleFindNextClicked();
		}
	}

	private void replacePart(LDrawPart part) {
		boolean isCheckChangeColor = btnCheck_ChangeColor.getSelection();
		String replacePartName = combo_ReplacePartName.getText().toLowerCase();
		if (btnCheck_ChangeToLDrawPart.getSelection()) {
			if (replacePartName.endsWith(".dat") == false) {
				replacePartName += ".dat";
			}
		}
		LDrawColorT replacePartColor = colorPicker.getSelectedColor();

		LDrawPart newPart = new LDrawPart();
		newPart.initWithPartName(replacePartName, new Vector3f());
		newPart.setTransformationMatrix(part.transformationMatrix());
		if (isCheckChangeColor == false)
			newPart.setLDrawColor(part.getLDrawColor());
		else
			newPart.setLDrawColor(ColorLibrary.sharedColorLibrary()
					.colorForCode(replacePartColor));

		MOCBuilderUndoWrapper.getInstance().replaceDirective(part, newPart);
	}

	private int lastFindIndex = -1;
	private LDrawPart lastFoundPart = null;

	protected void handleFindNextClicked() {
		ArrayList<LDrawPart> allPartList = MOCBuilder.getInstance()
				.getAllPartInActiveModel();
		if (allPartList == null || allPartList.size() == 0)
			return;

		String partName = combo_FindPartName.getText();
		LDrawColorT findPartColor = null;
		try {
			findPartColor = LDrawColorT.valueOf(combo_FindColor.getText());
		} catch (Exception e) {
		}

		boolean isCheckMatchColor = btnCheck_MatchColor.getSelection();

		LDrawPart found = null;
		int index = lastFindIndex + 1;
		for (int cnt = 0; cnt < allPartList.size(); cnt++) {
			LDrawPart part = allPartList
					.get((index + cnt) % allPartList.size());
			if (part.displayName().equals(partName))
				if (isCheckMatchColor == false
						|| findPartColor == part.getLDrawColor().getColorCode()) {
					found = part;
					lastFindIndex = (index + cnt) % allPartList.size();
					lastFoundPart = part;
					break;
				}
		}

		if (found != null) {
			DirectiveSelectionManager.getInstance().clearSelection();
			MOCBuilder.getInstance().getCamera().moveTo(found.position());
			DirectiveSelectionManager.getInstance()
					.updateScreenProjectionVerticesMapAll();

			DirectiveSelectionManager.getInstance()
					.removeDirectiveFromSelection(found);
			DirectiveSelectionManager.getInstance().addDirectiveToSelection(
					found);
		}
	}

	protected void handleFindColorComboChanged() {
		if (combo_FindColor.getSelectionIndex() == -1) {
			btnCheck_MatchColor.setSelection(false);
		} else
			btnCheck_MatchColor.setSelection(true);
	}

	protected void handleReplaceAll() {
		boolean isCheckMatchColor = btnCheck_MatchColor.getSelection();

		String findPartName = combo_FindPartName.getText();
		LDrawColorT findPartColor = null;
		try {
			if (combo_FindColor.getText() != null
					&& combo_FindColor.getText() != "")
				findPartColor = LDrawColorT.valueOf(combo_FindColor.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		DirectiveSelectionManager.getInstance().clearSelection();

		HashMap<LDrawDirective, LDrawDirective> fromToMap = new HashMap<LDrawDirective, LDrawDirective>();

		for (LDrawPart part : MOCBuilder.getInstance()
				.getAllPartInActiveModel()) {
			if (part.displayName().equals(findPartName)) {
				if (isCheckMatchColor == false
						|| findPartColor == part.getLDrawColor().getColorCode()) {

					boolean isCheckChangeColor = btnCheck_ChangeColor
							.getSelection();
					String replacePartName = combo_ReplacePartName.getText()
							.toLowerCase();
					if (btnCheck_ChangeToLDrawPart.getSelection()) {
						if (replacePartName.endsWith(".dat") == false) {
							replacePartName += ".dat";
						}
					}
					LDrawColorT replacePartColor = colorPicker
							.getSelectedColor();

					LDrawPart newPart = new LDrawPart();
					newPart.initWithPartName(replacePartName, new Vector3f());
					newPart.setTransformationMatrix(part.transformationMatrix());
					if (isCheckChangeColor == false)
						newPart.setLDrawColor(part.getLDrawColor());
					else
						newPart.setLDrawColor(ColorLibrary.sharedColorLibrary()
								.colorForCode(replacePartColor));
					fromToMap.put(part, newPart);

				}
			}
		}
		if (fromToMap.size() == 0)
			return;
		MOCBuilderUndoWrapper.getInstance().replaceDirectives(fromToMap);

		MessageBox box = new MessageBox(shell);
		box.setMessage("" + fromToMap.size() + " brick(s) are replaced.");
		box.open();
		updateFindPartNameComboBtn();

	}

	protected void updateButton() {
		if (combo_FindPartName.getSelectionIndex() != -1) {
			btn_FindNext.setEnabled(true);
			btn_Replace.setEnabled(true);
			btn_ReplaceAll.setEnabled(true);
		} else {
			btn_FindNext.setEnabled(false);
			btn_Replace.setEnabled(false);
			btn_ReplaceAll.setEnabled(false);
		}
	}

	private void updateFindPartNameComboBtn() {
		combo_FindPartName.setRedraw(false);
		combo_FindPartName.removeAll();
		HashMap<String, Boolean> addedMap = new HashMap<String, Boolean>();
		ArrayList<String> orderedPartNameList = new ArrayList<String>();
		for (LDrawPart part : MOCBuilder.getInstance()
				.getAllPartInActiveModel()) {
			if (addedMap.containsKey(part.displayName()) == false) {
				orderedPartNameList.add(part.displayName());
				addedMap.put(part.displayName(), true);
			}
		}

		Collections.sort(orderedPartNameList);

		for (String partName : orderedPartNameList)
			combo_FindPartName.add(partName);

		combo_FindPartName.setRedraw(true);
		if (combo_FindPartName.getSelectionIndex() == -1)
			updateFindPartColorComboBtn(null);
		else
			updateFindPartColorComboBtn(combo_FindPartName.getText());

		addedMap = null;
		orderedPartNameList = null;
		updateButton();
		lastFoundPart = null;
		lastFindIndex = -1;
	}

	private void updateFindPartColorComboBtn(String partName) {
		combo_FindColor.setRedraw(false);
		combo_FindColor.removeAll();
		HashMap<LDrawColorT, Boolean> addedMap = new HashMap<LDrawColorT, Boolean>();
		ArrayList<String> orderedPartColorList = new ArrayList<String>();
		for (LDrawPart part : MOCBuilder.getInstance()
				.getAllPartInActiveModel()) {
			if (partName != null
					&& part.displayName().equals(partName) == false)
				continue;
			if (addedMap.containsKey(part.getLDrawColor().getColorCode()) == false) {
				orderedPartColorList.add(part.getLDrawColor().getColorCode()
						.toString());
				addedMap.put(part.getLDrawColor().getColorCode(), true);
			}
		}
		Collections.sort(orderedPartColorList);

		for (String partColor : orderedPartColorList) {
			combo_FindColor.add(partColor);
		}
		combo_FindColor.setRedraw(true);

		addedMap = null;
		orderedPartColorList = null;
		handleFindColorComboChanged();
		lastFoundPart = null;
		lastFindIndex = -1;
	}

	private void updateReplacePartNameComboBtn() {
		String keyword = combo_ReplacePartName.getText();
		combo_ReplacePartName.setRedraw(false);
		combo_ReplacePartName.remove(0,
				combo_ReplacePartName.getItemCount() - 1);

		for (String partName : PartCache.getInstance().getPartLists(keyword,
				PartDomainT.LDRAW)) {
			combo_ReplacePartName.add(partName);
		}
		combo_ReplacePartName.setRedraw(true);
	}

	private boolean isUpdateCompleted = true;
	private boolean needOneMoreUpdate = false;

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		if (isUpdateCompleted) {
			isUpdateCompleted = false;
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					updateFindPartNameComboBtn();
					while (needOneMoreUpdate) {
						needOneMoreUpdate = false;
						updateFindPartNameComboBtn();
					}
					isUpdateCompleted = true;

					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.NeedRedraw);
				}
			});
		} else
			needOneMoreUpdate = true;
	}
}
