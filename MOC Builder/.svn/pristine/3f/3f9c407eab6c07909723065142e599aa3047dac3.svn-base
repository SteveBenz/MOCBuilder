package Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import Builder.MouseControlMode;
import Builder.MouseControlMode.MouseControlModeT;
import ConnectivityEditor.Window.ConnectivityEditor;
import ConnectivityEditor.Window.ConnectivityEditorUI;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Resource.ResourceManager;

public class MouseControlPanelDlg extends Dialog implements ILDrawSubscriber {
	private Display display;
	private Shell shell;
	private static MouseControlPanelDlg _instance = null;

	public synchronized static MouseControlPanelDlg getInstance(Shell parent) {
		if (_instance == null) {
			_instance = new MouseControlPanelDlg(parent);
			_instance.open();
		}
		return _instance;
	}

	private MouseControlPanelDlg(Shell shell) {
		super(shell, SWT.DIALOG_TRIM | SWT.ON_TOP);
		// shell.setBounds(0, 0, 30, 30);
		display = shell.getDisplay();
	}

	private boolean isForConnectivityEditor = false;

	public void setforConnectivityEditor(boolean flag) {
		this.isForConnectivityEditor = flag;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (shell.getParent().isDisposed())
				break;
			if (shell.getDisplay().isDisposed())
				break;
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		_instance = null;

		return null;
	}

	Image selectImage = ResourceManager.getInstance().getImage(display,
			"/Resource/Image/arrow.png");
	Image moveCameraImage = ResourceManager.getInstance().getImage(display,
			"/Resource/Image/cameraMove.png");
	Image rotateCameraImage = ResourceManager.getInstance().getImage(display,
			"/Resource/Image/cameraRotate.png");
	Image zoominCameraImage = ResourceManager.getInstance().getImage(display,
			"/Resource/Image/zoomin_mag.png");
	Image zoomoutCameraImage = ResourceManager.getInstance().getImage(display,
			"/Resource/Image/zoomout_mag.png");
	private Button selectionBtn;
	private Button moveCameraBtn;
	private Button rotateCameraBtn;

	private void createContents() {
		shell = new Shell(getParent(), getStyle() & (~SWT.CLOSE));
		shell.setMinimumSize(0, 0);
		Rectangle parentBounds = getParent().getBounds();
		shell.setBounds(0, parentBounds.y, 0, 0);
		shell.setSize(40, 207);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginTop = -3;
		gridLayout.marginLeft = -3;
		gridLayout.marginRight = -3;
		gridLayout.marginBottom = -3;
		shell.setLayout(gridLayout);

		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});

		Composite composite = new Composite(shell, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		gridLayout = new GridLayout(1, false);
		gridLayout.marginTop = -7;
		gridLayout.marginLeft = -6;
		gridLayout.marginRight = -6;
		gridLayout.marginBottom = -6;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		selectionBtn = new Button(composite, SWT.TOGGLE | SWT.FLAT);
		selectionBtn.setImage(selectImage);
		gridData = new GridData();
		gridData.heightHint = 37;
		gridData.widthHint = 37;
		selectionBtn.setLayoutData(gridData);
		selectionBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				MouseControlMode.setCurrentMode(MouseControlModeT.Seletion);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		selectionBtn.setSelection(true);

		moveCameraBtn = new Button(composite, SWT.TOGGLE | SWT.FLAT);
		moveCameraBtn.setImage(moveCameraImage);
		gridData = new GridData();
		gridData.heightHint = 37;
		gridData.widthHint = 37;
		moveCameraBtn.setLayoutData(gridData);
		moveCameraBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				MouseControlMode.setCurrentMode(MouseControlModeT.MoveCamera);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		rotateCameraBtn = new Button(composite, SWT.TOGGLE | SWT.FLAT);
		rotateCameraBtn.setImage(rotateCameraImage);
		gridData = new GridData();
		gridData.heightHint = 37;
		gridData.widthHint = 37;
		rotateCameraBtn.setLayoutData(gridData);
		rotateCameraBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				MouseControlMode.setCurrentMode(MouseControlModeT.RotateCamera);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Button zoominCameraBtn = new Button(composite, SWT.PUSH | SWT.FLAT);
		zoominCameraBtn.setImage(zoominCameraImage);
		gridData = new GridData();
		gridData.heightHint = 37;
		gridData.widthHint = 37;
		zoominCameraBtn.setLayoutData(gridData);
		zoominCameraBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isForConnectivityEditor) {
					ConnectivityEditor
							.getInstance()
							.getCamera()
							.setDistanceBetweenObjectToCamera(
									ConnectivityEditor.getInstance()
											.getCamera()
											.getDistanceBetweenObjectToCamera() * 0.9f);
				} else {
					MOCBuilder
							.getInstance()
							.getCamera()
							.setDistanceBetweenObjectToCamera(
									MOCBuilder.getInstance().getCamera()
											.getDistanceBetweenObjectToCamera() * 0.9f);
					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.NeedRedraw);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Button zoomoutCameraBtn = new Button(composite, SWT.PUSH | SWT.FLAT);
		zoomoutCameraBtn.setImage(zoomoutCameraImage);
		gridData = new GridData();
		gridData.heightHint = 37;
		gridData.widthHint = 37;
		zoomoutCameraBtn.setLayoutData(gridData);
		zoomoutCameraBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (isForConnectivityEditor) {
					ConnectivityEditor
							.getInstance()
							.getCamera()
							.setDistanceBetweenObjectToCamera(
									ConnectivityEditor.getInstance()
											.getCamera()
											.getDistanceBetweenObjectToCamera() * 1.1f);
				} else {
					MOCBuilder
							.getInstance()
							.getCamera()
							.setDistanceBetweenObjectToCamera(
									MOCBuilder.getInstance().getCamera()
											.getDistanceBetweenObjectToCamera() * 1.1f);
					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.NeedRedraw);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.MouseControlModeChanged);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.EscKeyPressed);
	}

	private boolean isUpdateCompleted = true;
	private boolean needOneMoreUpdate = false;

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		if (messageType == NotificationMessageT.EscKeyPressed) {
			if (MouseControlMode.getCurrentMode() != MouseControlModeT.Seletion) {
				MouseControlMode.setCurrentMode(MouseControlModeT.Seletion);
			}
			return;
		}
		if (isUpdateCompleted) {
			isUpdateCompleted = false;
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					updatePanel();
					while (needOneMoreUpdate) {
						needOneMoreUpdate = false;
						updatePanel();
					}
					isUpdateCompleted = true;
				}
			});
		} else
			needOneMoreUpdate = true;
	}

	protected void updatePanel() {
		switch (MouseControlMode.getCurrentMode()) {
		case Seletion:
			selectionBtn.setSelection(true);
			moveCameraBtn.setSelection(false);
			rotateCameraBtn.setSelection(false);
			break;
		case MoveCamera:
			selectionBtn.setSelection(false);
			moveCameraBtn.setSelection(true);
			rotateCameraBtn.setSelection(false);
			break;
		case RotateCamera:
			selectionBtn.setSelection(false);
			moveCameraBtn.setSelection(false);
			rotateCameraBtn.setSelection(true);
			break;
		default:
			break;
		}
	}

	public static MouseControlPanelDlg getInstance() {
		return _instance;
	}

}
