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
import Common.Box3;
import Common.Vector3f;
import Connectivity.CollisionBox;
import ConnectivityEditor.Connectivity.CollisionBoxT;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.Connectivity.FixedT;

public class CollisionEditorComposite extends Composite {
	private Label lblNewLabel;
	private Combo combo_Type;
	private Combo combo_X;
	private Label lblSizey;
	private Combo combo_Y;
	private Label lblSizez;
	private Combo combo_Z;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CollisionEditorComposite(Composite parent, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		setLayout(rowLayout);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new RowData(161, 173));

		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLocation(8, 20);
		lblNewLabel.setSize(55, 15);
		lblNewLabel.setText("Type");

		combo_Type = new Combo(group, SWT.READ_ONLY);
		combo_Type.setBounds(69, 17, 88, 23);

		for (CollisionBoxT type : CollisionBoxT.values())
			combo_Type.add(type.toString());

		combo_Type.select(0);
		
		combo_X = new Combo(group, SWT.READ_ONLY);
		combo_X.setBounds(69, 52, 88, 23);
		
		combo_Y = new Combo(group, SWT.READ_ONLY);
		combo_Y.setBounds(69, 81, 88, 23);
		
		combo_Z = new Combo(group, SWT.READ_ONLY);
		combo_Z.setBounds(69, 108, 88, 23);
		
		for(int i=1; i <= 50; i++){
			combo_X.add(""+i);
			combo_Y.add(""+i);
			combo_Z.add(""+i);
		}		
		combo_X.select(9);
		combo_Y.select(11);
		combo_Z.select(9);
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(8, 55, 55, 15);
		lblNewLabel_1.setText("Size_X");
		
		lblSizey = new Label(group, SWT.NONE);
		lblSizey.setText("Size_Y");
		lblSizey.setBounds(8, 84, 55, 15);
		
		
		
		lblSizez = new Label(group, SWT.NONE);
		lblSizez.setText("Size_Z");
		lblSizez.setBounds(8, 111, 55, 15);
		
		

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
		Vector3f size = new Vector3f(Float.parseFloat(combo_X.getText()), Float.parseFloat(combo_Y.getText()), Float.parseFloat(combo_Z.getText()));		
		CollisionBoxT type;
		type = CollisionBoxT.valueOf(combo_Type.getText());

		CollisionBox newItem = ConnectivityGenerator.getInstance()
				.generateCollisionBox(type, size);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		ConnectivityEditor.getInstance().addConnectivity(newItem);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
