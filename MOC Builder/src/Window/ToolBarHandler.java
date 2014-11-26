package Window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import Builder.BuilderConfigurationManager;
import Builder.DirectiveSelectionManager;
import Command.LDrawColor;
import Command.LDrawColorT;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import Common.Vector3f;
import Connectivity.Direction6T;
import LDraw.Support.ColorLibrary;
import LDraw.Support.LDrawDirective;
import LDraw.Support.type.LDrawGridTypeT;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.LDrawColorSelected;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import OtherTools.Syringe;
import Resource.ResourceManager;
import UndoRedo.MOCBuilderUndoWrapper;

public class ToolBarHandler extends SelectionAdapter implements KeyListener,
		ILDrawSubscriber {
	private static final String DefaultResourcePath = "/Resource/Image/toolbar/";
	MOCBuilder brickBuilder = null;
	Shell shell = null;

	ToolItem item_newFile;
	ToolItem item_openFile;
	ToolItem item_save;
	ToolItem item_showStudHoleMatrix;
	ToolItem item_connectivityCheck;
	ToolItem item_gridCoarse;
	ToolItem item_gridMedium;
	ToolItem item_gridFine;
	ToolItem item_remove;
	ToolItem item_snapToGrid;
	ToolItem item_collision;

	ToolItem item_rotateXClockwise;
	ToolItem item_rotateXCClockwise;

	ToolItem item_rotateYClockwise;
	ToolItem item_rotateYCClockwise;

	ToolItem item_rotateZClockwise;
	ToolItem item_rotateZCClockwise;

	ToolItem item_align_X_L;
	ToolItem item_align_X_R;
	ToolItem item_align_Y_T;
	ToolItem item_align_Y_B;
	ToolItem item_align_Z_L;
	ToolItem item_align_Z_R;

	ToolItem item_align_Rotation;

	ToolBar toolBar;

	ToolItem item_colorPicker;
	ColorPicker colorPicker;

	ToolItem item_syringe;

	BuilderConfigurationManager configurationManager;

	public ToolBarHandler() {
		configurationManager = BuilderConfigurationManager.getInstance();
	}

	public ToolBarHandler(MOCBuilder builder, Shell shell) {
		this();
		this.brickBuilder = builder;
		this.shell = shell;

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.BrickbuilderConfigurationChanged);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidSelected);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		try {
			ToolItem eventSrc = (ToolItem) event.getSource();
			if (eventSrc == item_newFile) {
				brickBuilder.newLDrawFile();
			}
			if (eventSrc == item_openFile) {
				if (brickBuilder.checkChanged(shell)) {
					final String path;
					FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
					fileDialog.setFilterExtensions(new String[] {
							"*.ldr;*.mpd;*.dat", "*.*" });
					path = fileDialog.open();
					if (path == null)
						return;

					brickBuilder.openFile(path);
				}
			} else if (eventSrc == item_save) {
				if (brickBuilder.getWorkingLDrawFile().path() == null)
					brickBuilder.saveAs(shell, null);
				else
					brickBuilder.saveFile();
			} else if (eventSrc == item_showStudHoleMatrix) {
				boolean flag = eventSrc.getSelection();
				brickBuilder.isVisibleStudHoleMatrix(flag);
			} else if (eventSrc == item_gridCoarse) {
				// if (item_gridCoarse.getSelection())
				BuilderConfigurationManager.getInstance().setGridUnit(
						LDrawGridTypeT.Coarse);
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.BrickbuilderConfigurationChanged);
			} else if (eventSrc == item_gridMedium) {
				// if (item_gridMedium.getSelection())
				BuilderConfigurationManager.getInstance().setGridUnit(
						LDrawGridTypeT.Medium);
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.BrickbuilderConfigurationChanged);
			} else if (eventSrc == item_gridFine) {
				// if (item_gridFine.getSelection())
				BuilderConfigurationManager.getInstance().setGridUnit(
						LDrawGridTypeT.Fine);
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.BrickbuilderConfigurationChanged);
			} else if (eventSrc == item_connectivityCheck) {
				boolean flag = eventSrc.getSelection();
				BuilderConfigurationManager.getInstance().setUseConnectivity(
						flag);
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.NeedRedraw);
			} else if (eventSrc == item_collision) {
				boolean flag = eventSrc.getSelection();
				BuilderConfigurationManager.getInstance().setUseCollision(flag);
			} else if (eventSrc == item_remove) {
				MOCBuilderUndoWrapper.getInstance().removeSelectedDirective();
				GlobalFocusManager.getInstance().forceFocusToMainView();
			} else if (eventSrc == item_snapToGrid) {
				MOCBuilderUndoWrapper.getInstance().snapToGrid();
			} else if (eventSrc == item_rotateXClockwise) {
				handleRoateSelectedParts(new Vector3f(
						BuilderConfigurationManager.getInstance().getGridUnit()
								.getRotationValue(), 0, 0));

			} else if (eventSrc == item_rotateXCClockwise) {
				Vector3f degree = new Vector3f(-BuilderConfigurationManager
						.getInstance().getGridUnit().getRotationValue(), 0, 0);
				handleRoateSelectedParts(degree);
			} else if (eventSrc == item_rotateYClockwise) {
				Vector3f degree = new Vector3f(0, BuilderConfigurationManager
						.getInstance().getGridUnit().getRotationValue(), 0);
				handleRoateSelectedParts(degree);
			} else if (eventSrc == item_rotateYCClockwise) {
				Vector3f degree = new Vector3f(0, -BuilderConfigurationManager
						.getInstance().getGridUnit().getRotationValue(), 0);
				handleRoateSelectedParts(degree);
			} else if (eventSrc == item_rotateZClockwise) {
				Vector3f degree = new Vector3f(0, 0,
						-BuilderConfigurationManager.getInstance()
								.getGridUnit().getRotationValue());
				handleRoateSelectedParts(degree);
			} else if (eventSrc == item_rotateZCClockwise) {
				Vector3f degree = new Vector3f(0, 0,
						BuilderConfigurationManager.getInstance().getGridUnit()
								.getRotationValue());
				handleRoateSelectedParts(degree);
			} else if (eventSrc.equals(item_colorPicker)) {
				colorPicker.showDialog();
			} else if (eventSrc.equals(item_syringe)) {
				handleSyringe();
			} else if (eventSrc == item_align_X_L) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.X_Minus);
			} else if (eventSrc == item_align_X_R) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.X_Plus);
			} else if (eventSrc == item_align_Y_T) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.Y_Plus);
			} else if (eventSrc == item_align_Y_B) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.Y_Minus);
			} else if (eventSrc == item_align_Z_L) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.Z_Plus);
			} else if (eventSrc == item_align_Z_R) {
				MOCBuilderUndoWrapper.getInstance().alignSelectedDirective(
						Direction6T.Z_Minus);
			} else if (eventSrc == item_align_Rotation) {
				MOCBuilderUndoWrapper.getInstance()
						.adjustRotationMatrixForSelectedDirective();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleSyringe() {
		if (item_syringe.getSelection()) {
			Syringe.getInstance().activate();
		} else {
			Syringe.getInstance().clear();
		}
	}

	private void handleRoateSelectedParts(Vector3f degree) {
		MOCBuilderUndoWrapper.getInstance().rotateSelectedDirectiveBy(degree);
	}

	public void generateToolbar() {
		shell.setLayout(new GridLayout());
		toolBar = new ToolBar(shell, SWT.WRAP );
		final GridData toolbarData = new GridData();
//		toolbarData.verticalSpan = 2;
		toolbarData.verticalAlignment = GridData.CENTER;
		toolbarData.horizontalAlignment = GridData.FILL;
		toolbarData.widthHint = 700;
		toolBar.setLayoutData(toolbarData);

		GridLayout toolBarLayout = new GridLayout(2, true);
		toolBarLayout.verticalSpacing = 15;
		toolBar.setLayout(toolBarLayout);
		
		Display display = shell.getDisplay();

		item_newFile = new ToolItem(toolBar, SWT.None);
		item_newFile.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_new.png"));
		item_newFile.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_new.png"));
		item_newFile.addSelectionListener(this);
		item_newFile.setToolTipText("NEW");

		item_openFile = new ToolItem(toolBar, SWT.None);
		item_openFile.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_open.png"));
		item_openFile.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_open.png"));
		item_openFile.addSelectionListener(this);
		item_openFile.setToolTipText("OPEN");

		item_save = new ToolItem(toolBar, SWT.None);
		item_save.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_save.png"));
		item_save.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_save.png"));
		item_save.addSelectionListener(this);
		item_save.setToolTipText("SAVE");

		ToolItem separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_connectivityCheck = new ToolItem(toolBar, SWT.CHECK);
		item_connectivityCheck.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_checkcntn.png"));
		item_connectivityCheck.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_checkcntn.png"));
		item_connectivityCheck.addSelectionListener(this);
		item_connectivityCheck.setSelection(configurationManager
				.isUseConnectivity());
		item_connectivityCheck.setToolTipText("Check Connectivity");

		item_collision = new ToolItem(toolBar, SWT.CHECK);
		item_collision.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_checkclsn.png"));
		item_collision.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_checkclsn.png"));
		item_collision.addSelectionListener(this);
		item_collision.setSelection(configurationManager.isUseCollision());
		item_collision.setToolTipText("Check Collision");

		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_gridCoarse = new ToolItem(toolBar, SWT.RADIO);
		item_gridCoarse.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_grid-coarse.png"));
		item_gridCoarse.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_grid-coarse.png"));
		item_gridCoarse.addSelectionListener(this);
		item_gridCoarse.setSelection(false);
		item_gridCoarse.setToolTipText("Set Grid Unit: Coarse");

		item_gridMedium = new ToolItem(toolBar, SWT.RADIO);
		item_gridMedium.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_grid-medium.png"));
		item_gridMedium.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_grid-medium.png"));
		item_gridMedium.addSelectionListener(this);
		item_gridMedium.setSelection(false);
		item_gridMedium.setToolTipText("Set Grid Unit: Medium");

		item_gridFine = new ToolItem(toolBar, SWT.RADIO);
		item_gridFine.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_grid-fine.png"));
		item_gridMedium.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_grid-fine.png"));
		item_gridFine.addSelectionListener(this);
		item_gridFine.setSelection(false);
		item_gridFine.setToolTipText("Set Grid Unit: Fine");
		switch (configurationManager.getGridUnit()) {
		case Coarse:
			item_gridCoarse.setSelection(true);
			break;
		case Fine:
			item_gridFine.setSelection(true);
			break;
		default:
			item_gridMedium.setSelection(true);
			break;
		}
		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_colorPicker = new ToolItem(toolBar, SWT.None);
		item_colorPicker.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_color.png"));
		item_colorPicker.setDisabledImage(ResourceManager.getInstance()
				.getImage(display, DefaultResourcePath + "inactive_color.png"));
		item_colorPicker.addSelectionListener(this);
		colorPicker = new ColorPicker(item_colorPicker,
				"Set Color of Selected Brick(s)");
		colorPicker.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button selectedButton = (Button) event.widget;
				LDrawColorT colorT = (LDrawColorT) selectedButton.getData();
				colorPicker.setColor(colorT);
				LDrawColor color = ColorLibrary.sharedColorLibrary()
						.colorForCode(colorT);

				HashMap<LDrawDirective, LDrawColor> newColorMap = new HashMap<LDrawDirective, LDrawColor>();
				for (LDrawDirective directive : DirectiveSelectionManager
						.getInstance().getSelectedDirectiveList())
					if (directive instanceof LDrawDrawableElement
							|| directive instanceof LDrawLSynth)
						newColorMap.put(directive, color);

				MOCBuilderUndoWrapper.getInstance().changeColor(newColorMap);
				selectedButton.getShell().setVisible(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

			}
		});

		NotificationCenter.getInstance().addSubscriber(new ILDrawSubscriber() {
			@Override
			public void receiveNotification(NotificationMessageT messageType,
					INotificationMessage msg) {
				colorPicker.setColor(((LDrawColorSelected) msg).getColorCode());
			}
		}, NotificationMessageT.LDrawSyringeColorSelected);

		item_syringe = new ToolItem(toolBar, SWT.CHECK);
		item_syringe.addSelectionListener(this);
		item_syringe.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_colordropper.png"));
		item_syringe.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_colordropper.png"));

		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		// item_showStudHoleMatrix = new ToolItem(toolBar, SWT.CHECK);
		// item_showStudHoleMatrix.setImage(ResourceManager.getInstance()
		// .getImage(display,
		// DefaultResourcePath + "active_showmatrix.png"));
		// item_showStudHoleMatrix.setDisabledImage(ResourceManager.getInstance()
		// .getImage(display,
		// DefaultResourcePath + "inactive_showmatrix.png"));
		// item_showStudHoleMatrix.addSelectionListener(this);
		// item_showStudHoleMatrix.setSelection(false);
		// item_showStudHoleMatrix.setToolTipText("Show Connectivity Matrix");
		//
		// separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_remove = new ToolItem(toolBar, SWT.None);
		item_remove.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_delete.png"));
		item_remove.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_delete.png"));
		item_remove.addSelectionListener(this);
		item_remove.setToolTipText("Delete Selected Brick");
		item_remove.setEnabled(false);

		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_snapToGrid = new ToolItem(toolBar, SWT.None);
		item_snapToGrid.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_snaptogrid.png"));
		item_snapToGrid.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_snaptogrid.png"));
		item_snapToGrid.addSelectionListener(this);
		item_snapToGrid.setToolTipText("Snap To Grid");
		item_snapToGrid.setEnabled(false);

		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_rotateXCClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateXCClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatex-ccw.png"));
		item_rotateXCClockwise.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_rotatex-ccw.png"));
		item_rotateXCClockwise.addSelectionListener(this);
		item_rotateXCClockwise.setToolTipText("X CCW");
		item_rotateXCClockwise.setEnabled(false);

		item_rotateXClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateXClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatex.png"));
		item_rotateXClockwise
				.setDisabledImage(ResourceManager.getInstance().getImage(
						display, DefaultResourcePath + "inactive_rotatex.png"));
		item_rotateXClockwise.addSelectionListener(this);
		item_rotateXClockwise.setToolTipText("X CW");
		item_rotateXClockwise.setEnabled(false);

		item_rotateYCClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateYCClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatey-ccw.png"));
		item_rotateYCClockwise.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_rotatey-ccw.png"));
		item_rotateYCClockwise.addSelectionListener(this);
		item_rotateYCClockwise.setToolTipText("Y CW");
		item_rotateYCClockwise.setEnabled(false);

		item_rotateYClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateYClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatey.png"));
		item_rotateYClockwise
				.setDisabledImage(ResourceManager.getInstance().getImage(
						display, DefaultResourcePath + "inactive_rotatey.png"));
		item_rotateYClockwise.addSelectionListener(this);
		item_rotateYClockwise.setToolTipText("Y CCW");
		item_rotateYClockwise.setEnabled(false);

		item_rotateZCClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateZCClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatez-ccw.png"));
		item_rotateZCClockwise.setDisabledImage(ResourceManager.getInstance()
				.getImage(display,
						DefaultResourcePath + "inactive_rotatez-ccw.png"));
		item_rotateZCClockwise.addSelectionListener(this);
		item_rotateZCClockwise.setToolTipText("Z CW");
		item_rotateZCClockwise.setEnabled(false);

		item_rotateZClockwise = new ToolItem(toolBar, SWT.None);
		item_rotateZClockwise.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_rotatez.png"));
		item_rotateZClockwise
				.setDisabledImage(ResourceManager.getInstance().getImage(
						display, DefaultResourcePath + "inactive_rotatez.png"));
		item_rotateZClockwise.addSelectionListener(this);
		item_rotateZClockwise.setToolTipText("Z CCW");
		item_rotateZClockwise.setEnabled(false);

		separator = new ToolItem(toolBar, SWT.SEPARATOR);

		item_align_X_L = new ToolItem(toolBar, SWT.None);
		item_align_X_L.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_alignx.png"));
		item_align_X_L.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_alignx.png"));
		item_align_X_L.addSelectionListener(this);
		item_align_X_L.setToolTipText("Align to X");
		item_align_X_L.setEnabled(false);

		item_align_X_R = new ToolItem(toolBar, SWT.None);
		item_align_X_R.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_alignx-ccw.png"));
		item_align_X_R.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_alignx-ccw.png"));
		item_align_X_R.addSelectionListener(this);
		item_align_X_R.setToolTipText("Align to X");
		item_align_X_R.setEnabled(false);

		item_align_Y_T = new ToolItem(toolBar, SWT.None);
		item_align_Y_T.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_aligny.png"));
		item_align_Y_T.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_aligny.png"));
		item_align_Y_T.addSelectionListener(this);
		item_align_Y_T.setToolTipText("Align to Y");
		item_align_Y_T.setEnabled(false);

		item_align_Y_B = new ToolItem(toolBar, SWT.None);
		item_align_Y_B.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_aligny-ccw.png"));
		item_align_Y_B.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_aligny-ccw.png"));
		item_align_Y_B.addSelectionListener(this);
		item_align_Y_B.setToolTipText("Align to Y");
		item_align_Y_B.setEnabled(false);

		item_align_Z_L = new ToolItem(toolBar, SWT.None);
		item_align_Z_L.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_alignz.png"));
		item_align_Z_L.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_alignz.png"));
		item_align_Z_L.addSelectionListener(this);
		item_align_Z_L.setToolTipText("Align to Z");
		item_align_Z_L.setEnabled(false);

		item_align_Z_R = new ToolItem(toolBar, SWT.None);
		item_align_Z_R.setImage(ResourceManager.getInstance().getImage(display,
				DefaultResourcePath + "active_alignz-ccw.png"));
		item_align_Z_R.setDisabledImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "inactive_alignz-ccw.png"));
		item_align_Z_R.addSelectionListener(this);
		item_align_Z_R.setToolTipText("Align to Z");
		item_align_Z_R.setEnabled(false);

		item_align_Rotation = new ToolItem(toolBar, SWT.None);
		item_align_Rotation.setImage(ResourceManager.getInstance().getImage(
				display, DefaultResourcePath + "active_samertn.png"));
		item_align_Rotation
				.setDisabledImage(ResourceManager.getInstance().getImage(
						display, DefaultResourcePath + "inactive_samertn.png"));
		item_align_Rotation.addSelectionListener(this);
		item_align_Rotation.setToolTipText("Align Rotation");
		item_align_Rotation.setEnabled(false);

		toolBar.pack();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// System.out.println("keyPressed: " + event.keyCode);
		switch (event.keyCode) {
		case SWT.ESC:
			if (item_syringe.getSelection()) {
				item_syringe.setSelection(false);
				Syringe.getInstance().clear();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	private HashMap<NotificationMessageT, Boolean> isUpdateCompletedMap = new HashMap<NotificationMessageT, Boolean>();
	private HashMap<NotificationMessageT, Boolean> needOneMoreUpdate = new HashMap<NotificationMessageT, Boolean>();

	@Override
	public void receiveNotification(final NotificationMessageT messageType,
			INotificationMessage msg) {
		if (isUpdateCompletedMap.containsKey(messageType) == true
				&& isUpdateCompletedMap.get(messageType) == false) {
			needOneMoreUpdate.put(messageType, true);
			return;
		}
		isUpdateCompletedMap.put(messageType, false);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case BrickbuilderConfigurationChanged:
					handleBrickbuilderConfigurationChanged();
					break;
				case LDrawDirectiveDidSelected:
					handleLDrawDirectiveDidSelected();
					break;
				default:
					break;
				}
				while (needOneMoreUpdate.containsKey(messageType) == true
						&& needOneMoreUpdate.get(messageType)) {
					needOneMoreUpdate.put(messageType, false);
					switch (messageType) {
					case BrickbuilderConfigurationChanged:
						handleBrickbuilderConfigurationChanged();
						break;
					case LDrawDirectiveDidSelected:
						handleLDrawDirectiveDidSelected();
						break;
					default:
						break;
					}
				}
				isUpdateCompletedMap.put(messageType, true);
			}
		});
	}

	private void handleBrickbuilderConfigurationChanged() {
		BuilderConfigurationManager configuration = BuilderConfigurationManager
				.getInstance();

		switch (configuration.getGridUnit()) {
		case Coarse:
			item_gridCoarse.setSelection(true);
			item_gridMedium.setSelection(false);
			item_gridFine.setSelection(false);
			break;
		case Medium:
			item_gridCoarse.setSelection(false);
			item_gridMedium.setSelection(true);
			item_gridFine.setSelection(false);
			break;
		case Fine:
			item_gridCoarse.setSelection(false);
			item_gridMedium.setSelection(false);
			item_gridFine.setSelection(true);
			break;
		default:
			break;
		}
	}

	private void handleLDrawDirectiveDidSelected() {
		boolean isSelect = !DirectiveSelectionManager.getInstance().isEmpty();

		item_remove.setEnabled(isSelect);
		item_rotateXCClockwise.setEnabled(isSelect);
		item_rotateXClockwise.setEnabled(isSelect);
		item_rotateYCClockwise.setEnabled(isSelect);
		item_rotateYClockwise.setEnabled(isSelect);
		item_rotateZCClockwise.setEnabled(isSelect);
		item_rotateZClockwise.setEnabled(isSelect);
		item_snapToGrid.setEnabled(isSelect);

		boolean isSelectSingleBrick = (DirectiveSelectionManager.getInstance()
				.getSelectedDirectiveSize() == 1);
		boolean isSelectMoreThanOne = false;
		if (isSelect && isSelectSingleBrick == false)
			isSelectMoreThanOne = true;

		item_align_Rotation.setEnabled(isSelectMoreThanOne);
		item_align_X_L.setEnabled(isSelectMoreThanOne);
		item_align_X_R.setEnabled(isSelectMoreThanOne);
		item_align_Y_B.setEnabled(isSelectMoreThanOne);
		item_align_Y_T.setEnabled(isSelectMoreThanOne);
		item_align_Z_L.setEnabled(isSelectMoreThanOne);
		item_align_Z_R.setEnabled(isSelectMoreThanOne);
	}
}
