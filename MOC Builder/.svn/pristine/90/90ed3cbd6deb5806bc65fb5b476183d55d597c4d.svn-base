package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import Command.LDrawPart;
import Connectivity.Connectivity;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import ConnectivityEditor.Connectivity.AxleT;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.RowData;

public class SliderEditorComposite extends Composite {
	private Combo combo_Length;
	private Button btnCheck_StartCapped;
	private Button btnCheck_EndCapped;
	private Button btnCheck_Cylindrial;
	private Label lblNewLabel;
	private Combo combo_Type;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SliderEditorComposite(Composite parent, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		setLayout(rowLayout);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new RowData(161, 64));

		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLocation(10, 18);
		lblNewLabel.setSize(55, 15);
		lblNewLabel.setText("Type");

		Label lblLength = new Label(group, SWT.NONE);
		lblLength.setBounds(10, 44, 55, 15);
		lblLength.setText("Length");

		combo_Length = new Combo(group, SWT.READ_ONLY);
		combo_Length.setBounds(67, 41, 90, 23);

		for (int i = 0; i < 30; i++)
			combo_Length.add("" + i);
		combo_Length.select(0);

		combo_Type = new Combo(group, SWT.READ_ONLY);
		combo_Type.setBounds(69, 10, 88, 23);
		
		for(int i=1; i < 20; i++){
			combo_Type.add("Slider_"+i+"_f");
			combo_Type.setData("Slider_"+i+"_f", i*2);
			combo_Type.add("Slider_"+i+"_m");
			combo_Type.setData("Slider_"+i+"_m", i*2+1);			
		}		
		
		combo_Type.select(0);

		btnCheck_StartCapped = new Button(this, SWT.CHECK);
		btnCheck_StartCapped.setLayoutData(new RowData(120, SWT.DEFAULT));
		btnCheck_StartCapped.setText("Start Capped");

		btnCheck_EndCapped = new Button(this, SWT.CHECK);
		btnCheck_EndCapped.setLayoutData(new RowData(120, SWT.DEFAULT));
		btnCheck_EndCapped.setText("End Capped");

		btnCheck_Cylindrial = new Button(this, SWT.CHECK);
		btnCheck_Cylindrial.setLayoutData(new RowData(120, SWT.DEFAULT));
		btnCheck_Cylindrial.setText("Cylindrical");


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
		boolean isStartCapped;
		boolean isEndCapped;
		boolean isCylindrical;
		boolean isRequireGrabbing;
		int length;
		int type;

		isStartCapped = btnCheck_StartCapped.getSelection();
		isEndCapped = btnCheck_EndCapped.getSelection();
		isCylindrical = btnCheck_Cylindrial.getSelection();
		length = combo_Length.getSelectionIndex();
		type = (Integer)(combo_Type.getData(combo_Type.getText()));

		Connectivity newItem = ConnectivityGenerator.getInstance()
				.generateSlider(type, length, isStartCapped, isEndCapped,
						isCylindrical);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		ConnectivityEditor.getInstance().addConnectivity(newItem);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
