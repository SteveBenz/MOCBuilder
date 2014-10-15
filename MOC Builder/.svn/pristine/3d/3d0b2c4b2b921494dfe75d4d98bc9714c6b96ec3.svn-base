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

import Command.LDrawPart;
import Connectivity.Connectivity;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.Connectivity.FixedT;

public class FixedEditorComposite extends Composite {
	private Label lblNewLabel;
	private Combo combo_Type;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FixedEditorComposite(Composite parent, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		setLayout(rowLayout);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new RowData(161, 32));

		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLocation(8, 20);
		lblNewLabel.setSize(55, 15);
		lblNewLabel.setText("Type");

		combo_Type = new Combo(group, SWT.READ_ONLY);
		combo_Type.setBounds(69, 17, 88, 23);
		
		for (FixedT type : FixedT.values())
			combo_Type.add(type.toString());
		
		combo_Type.select(0);

		Button btnGenerate = new Button(this, SWT.NONE);
		btnGenerate.setLayoutData(new RowData(122, 48));
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleGenerate();
			}
		});
		btnGenerate.setText("Generate");

		this.pack();
		this.layout();
	}

	protected void handleGenerate() {		
		int type;
		type = FixedT.valueOf(combo_Type.getText()).getValue();

		Connectivity newItem = ConnectivityGenerator.getInstance()
				.generateFixed(type);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		ConnectivityEditor.getInstance().addConnectivity(newItem);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
