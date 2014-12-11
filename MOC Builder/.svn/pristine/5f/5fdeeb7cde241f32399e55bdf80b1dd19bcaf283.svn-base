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

import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.CollisionCylinder;
import Connectivity.Connectivity;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.UndoRedo.ConnectivityEditorUndoWrapper;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class CollisionCylinderEditorComposite extends
		ConnectivityEditorComposite {
	private Combo combo_X;
	private Label lblSizey;
	private Combo combo_Y;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CollisionCylinderEditorComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public CollisionCylinderEditorComposite(Composite parent, int style,
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
		group.setLayoutData(new RowData(161, 93));

		combo_X = new Combo(group, SWT.READ_ONLY);
		combo_X.setBounds(69, 22, 88, 23);

		combo_Y = new Combo(group, SWT.READ_ONLY);
		combo_Y.setBounds(69, 52, 88, 23);

		for (int i = 1; i <= 50; i++) {
			combo_X.add("" + i);
			combo_Y.add("" + i);
		}
		combo_X.select(9);
		combo_Y.select(11);

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(8, 22, 55, 15);
		lblNewLabel_1.setText("Size_X");

		lblSizey = new Label(group, SWT.NONE);
		lblSizey.setText("Size_Y");
		lblSizey.setBounds(8, 52, 55, 15);

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
			CollisionCylinder cb = (CollisionCylinder) conn;
			combo_X.select((int) cb.getsX() - 1);
			combo_Y.select((int) cb.getsY() - 1);
			btnGenerate.setText("Apply");
		}

		this.pack();
		this.layout();
	}

	protected void handleGenerate() {
		Vector3f size = new Vector3f(Float.parseFloat(combo_X.getText()),
				Float.parseFloat(combo_Y.getText()), 0);

		CollisionCylinder newItem = ConnectivityGenerator.getInstance()
				.generateCollisionCylinder(size);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		if (conn == null)
			ConnectivityEditorUndoWrapper.getInstance()
					.addConnectivity(newItem);
		else {
			CollisionCylinder cb = (CollisionCylinder) conn;

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
