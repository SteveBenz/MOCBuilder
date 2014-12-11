package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import Connectivity.Connectivity;
import Connectivity.Hinge;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.UndoRedo.ConnectivityEditorUndoWrapper;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class HingeEditorComposite extends ConnectivityEditorComposite {
	private Label lblNewLabel;
	private Combo combo_Type;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HingeEditorComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public HingeEditorComposite(Composite parent, int style, Connectivity conn) {
		super(parent, style);
		this.conn = conn;
		init();
	}

	private void init() {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		this.setLayoutData(gridData);
		setLayout(new GridLayout());

		Group group = new Group(this, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));

		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setText("Type");
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
				false);
		lblNewLabel.setLayoutData(gridData);

		combo_Type = new Combo(group, SWT.READ_ONLY);

		for (int i = 1; i < 201; i++) {
			combo_Type.add("Hinge_" + i + "_f");
			combo_Type.setData("Hinge_" + i + "_f", i * 2);
			combo_Type.add("Hinge_" + i + "_m");
			combo_Type.setData("Hinge_" + i + "_m", i * 2 + 1);
		}

		combo_Type.select(0);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		combo_Type.setLayoutData(gridData);

		Button btnGenerate = new Button(this, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleGenerate();
			}
		});
		if (this.conn == null) {
			btnGenerate.setText("Generate");
		} else {
			Hinge hinge = (Hinge) conn;
			combo_Type.select(hinge.gettype() - 2);
			btnGenerate.setText("Apply");
		}
		gridData = new GridData(GridData.CENTER, GridData.BEGINNING, true,
				false);
		gridData.widthHint = 96;
		gridData.heightHint = 47;
		btnGenerate.setLayoutData(gridData);
	}

	protected void handleGenerate() {
		int type;
		type = (Integer) (combo_Type.getData(combo_Type.getText()));

		Connectivity newItem = ConnectivityGenerator.getInstance()
				.generateHinge(type);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		if (conn == null)
			ConnectivityEditorUndoWrapper.getInstance()
					.addConnectivity(newItem);
		else {
			Hinge newConn = (Hinge) newItem;
			Hinge thisConn = (Hinge) conn;

			thisConn.apply(newConn);

			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.ConnectivityDidChanged);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
