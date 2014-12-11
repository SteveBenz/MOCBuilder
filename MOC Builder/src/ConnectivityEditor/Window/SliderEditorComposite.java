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

import Connectivity.Axle;
import Connectivity.Connectivity;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.AxleT;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.UndoRedo.ConnectivityEditorUndoWrapper;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class SliderEditorComposite extends ConnectivityEditorComposite {
	private Combo combo_Length;
	private Button btnCheck_StartCapped;
	private Button btnCheck_EndCapped;
	private Button btnCheck_Cylindrial;
	private Label lblNewLabel;
	private Combo combo_Type;
	private Label lblLength;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SliderEditorComposite(Composite parent, int style) {
		super(parent, style);
		init();

		// this.pack();
	}

	public SliderEditorComposite(Composite parent, int style, Connectivity conn) {
		super(parent, style);
		this.conn = conn;
		init();

		// this.pack();
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

		for (int i = 1; i < 20; i++) {
			combo_Type.add("Slider_" + i + "_f");
			combo_Type.setData("Slider_" + i + "_f", i * 2);
			combo_Type.add("Slider_" + i + "_m");
			combo_Type.setData("Slider_" + i + "_m", i * 2 + 1);
		}
		combo_Type.select(0);

		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		combo_Type.setLayoutData(gridData);

		lblLength = new Label(group, SWT.NONE);
		lblLength.setText("Length");
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
				false);
		lblLength.setLayoutData(gridData);

		combo_Length = new Combo(group, SWT.READ_ONLY);

		for (int i = 0; i < 30; i++)
			combo_Length.add("" + i);
		combo_Length.select(0);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		combo_Length.setLayoutData(gridData);

		btnCheck_StartCapped = new Button(this, SWT.CHECK);
		btnCheck_StartCapped.setText("Start Capped");
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true,
				false);
		btnCheck_StartCapped.setLayoutData(gridData);

		btnCheck_EndCapped = new Button(this, SWT.CHECK);
		btnCheck_EndCapped.setText("End Capped");
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true,
				false);
		btnCheck_EndCapped.setLayoutData(gridData);

		btnCheck_Cylindrial = new Button(this, SWT.CHECK);
		btnCheck_Cylindrial.setText("Cylindrical");
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true,
				false);
		btnCheck_Cylindrial.setLayoutData(gridData);

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
			Slider slider = (Slider) conn;
			combo_Type.select(slider.gettype() - 2);
			combo_Length.select(combo_Length.indexOf(""
					+ ((int) slider.getlength() / 8)));
			btnCheck_EndCapped.setSelection(slider.getendCapped() == 1);
			btnCheck_StartCapped.setSelection(slider.getstartCapped() == 1);
			btnCheck_Cylindrial.setSelection(slider.getcylindrical() == 1);

			btnGenerate.setText("Apply");
		}
		gridData = new GridData(GridData.CENTER, GridData.BEGINNING, true,
				false);
		gridData.widthHint = 96;
		gridData.heightHint = 47;
		btnGenerate.setLayoutData(gridData);
	}

	protected void handleGenerate() {
		boolean isStartCapped;
		boolean isEndCapped;
		boolean isCylindrical;
		int length;
		int type;

		isStartCapped = btnCheck_StartCapped.getSelection();
		isEndCapped = btnCheck_EndCapped.getSelection();
		isCylindrical = btnCheck_Cylindrial.getSelection();
		length = combo_Length.getSelectionIndex();
		type = (Integer) (combo_Type.getData(combo_Type.getText()));

		Connectivity newItem = ConnectivityGenerator.getInstance()
				.generateSlider(type, length, isStartCapped, isEndCapped,
						isCylindrical);
		newItem.setParent(ConnectivityEditor.getInstance().getWorkingPart());

		if (conn == null)
			ConnectivityEditorUndoWrapper.getInstance()
					.addConnectivity(newItem);
		else {
			Slider newSlider = (Slider) newItem;
			Slider slider = (Slider) conn;

			slider.apply(newSlider);

			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.ConnectivityDidChanged);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
