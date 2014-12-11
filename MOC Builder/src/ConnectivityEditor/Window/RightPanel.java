package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.CollisionCylinder;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.Slider;
import Connectivity.Stud;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class RightPanel extends Composite implements ILDrawSubscriber {

	private Composite detailComposite;
	private SashForm sashForm;
	public static boolean isShowCollision = true;

	public RightPanel(Composite arg0, int arg1) {
		super(arg0, arg1);
		generateView();

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.ConnectivityDidSelected);
	}

	private void generateView() {
		setLayout(new GridLayout(1, false));
		Composite composite = new Composite(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.heightHint = 20;
		composite.setLayoutData(gridData);
		composite.setLayout(new RowLayout());
		final Button btnShowCollision = new Button(composite, SWT.CHECK);
		btnShowCollision.setText("Show CollisionObject");
		btnShowCollision.setSelection(true);
		btnShowCollision.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				isShowCollision = btnShowCollision.getSelection();
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.ConnectivityDidChanged);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		sashForm = new SashForm(this, SWT.NONE);
		sashForm.setOrientation(SWT.VERTICAL);
		gridData = new GridData(GridData.FILL_BOTH);
		sashForm.setLayoutData(gridData);

		// FileInfoTreeView
		ConnectivityFileInfoWindow connectivityFileInfoWindow = new ConnectivityFileInfoWindow(
				sashForm);

		detailComposite = new Composite(sashForm, SWT.NONE);
		updateDetailView();

		sashForm.setWeights(new int[] { 2, 1 });
	}

	public void connectivitySelected() {
		if (detailComposite != null && detailComposite.isDisposed() == false) {
			for (Control control : detailComposite.getChildren())
				control.dispose();
			detailComposite.dispose();
		}
		detailComposite = new Composite(this, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		detailComposite.setLayoutData(gridData);
		detailComposite.setLayout(new GridLayout());

		updateDetailView();

		sashForm.layout(true);
		sashForm.getParent().layout(true);
	}

	private void updateDetailView() {
		detailComposite.setVisible(false);

		Connectivity conn = ConnectivitySelectionManager.getInstance()
				.getSelectedLastConnectivity();
		if (conn instanceof Stud)
			new StudEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Hole)
			new HoleEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Axle)
			new AxleEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Slider)
			new SliderEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Hinge)
			new HingeEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Ball)
			new BallEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof Fixed)
			new FixedEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof CollisionBox)
			new CollisionBoxEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof CollisionSphere)
			new CollisionSphereEditorComposite(detailComposite, SWT.NONE, conn);
		else if (conn instanceof CollisionCylinder)
			new CollisionCylinderEditorComposite(detailComposite, SWT.NONE,
					conn);

		detailComposite.setVisible(true);
	}

	private boolean isUpdateCompleted = true;
	private boolean needOneMoreUpdate = false;

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		if (isUpdateCompleted) {
			isUpdateCompleted = false;
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					connectivitySelected();
					while (needOneMoreUpdate) {
						needOneMoreUpdate = false;
						connectivitySelected();
					}
					isUpdateCompleted = true;
				}
			});
		} else
			needOneMoreUpdate = true;
	}

}
