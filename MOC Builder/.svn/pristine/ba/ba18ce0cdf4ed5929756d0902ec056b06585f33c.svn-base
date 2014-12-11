package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import Connectivity.CollisionBox;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.UndoRedo.ConnectivityEditorUndoWrapper;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class CollisionSphereEditorComposite extends ConnectivityEditorComposite {
	private Label lblRadius;
	private Combo combo_Radius;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CollisionSphereEditorComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public CollisionSphereEditorComposite(Composite parent, int style,
			Connectivity conn) {
		super(parent, style);
		this.conn = conn;
		init();
	}

	private void init() {
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		setLayout(rowLayout);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new RowData(161, 53));

		lblRadius = new Label(group, SWT.NONE);
		lblRadius.setLocation(8, 22);
		lblRadius.setSize(55, 15);
		lblRadius.setText("Radius");

		combo_Radius = new Combo(group, SWT.READ_ONLY);
		combo_Radius.setBounds(69, 22, 88, 23);

		for (int i = 1; i <= 50; i++) {
			combo_Radius.add("" + i);
		}
		combo_Radius.select(9);

		Button btnGenerate = new Button(this, SWT.NONE);
		btnGenerate.setLayoutData(new RowData(96, 47));
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleGenerate();
			}
		});
		if (this.conn == null) {
			btnGenerate.setText("Generate");
		} else {
			CollisionSphere cb = (CollisionSphere) conn;
			combo_Radius.select((int) cb.getRadius()-1);
			btnGenerate.setText("Apply");
		}

		this.pack();
		this.layout();
	}

	protected void handleGenerate() {
		float radius = Float.parseFloat(combo_Radius.getText());

		CollisionSphere newItem = ConnectivityGenerator.getInstance()
				.generateCollisionSphere(radius);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		if (conn == null)
			ConnectivityEditorUndoWrapper.getInstance()
					.addConnectivity(newItem);
		else {
			CollisionSphere cb = (CollisionSphere) conn;

			cb.apply(newItem);

			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.ConnectivityDidChanged);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
