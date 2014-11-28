package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ConnectivityBrowserUI_1127 {
	List list;
	Display display;
	Shell shell;
	Composite mainComposite;
	Composite detailComposite;

	public ConnectivityBrowserUI_1127(Composite parent, int style) {
		display = parent.getDisplay();
		shell = parent.getShell();
		generateView(parent);
	}

	public void close() {
	}

	private void generateView(Composite parent) {
		parent.setLayout(new GridLayout());
		mainComposite = new Composite(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainComposite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(2, false);
		mainComposite.setLayout(gridLayout);

		list = new List(mainComposite, SWT.BORDER);
		gridData = new GridData(GridData.BEGINNING, GridData.FILL, false, true);
		list.setLayoutData(gridData);
		generateList(list);
		list.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(detailComposite!=null && detailComposite.isDisposed()==false){
					for(Control control :detailComposite.getChildren())
						control.dispose();
					detailComposite.dispose();
				}
				mainComposite.layout();
				detailComposite = new Composite(mainComposite, SWT.BORDER);
				GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
				detailComposite.setLayoutData(gridData);
				detailComposite.setLayout(new GridLayout());
				updateDetailView(detailComposite);
				mainComposite.layout(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		detailComposite = new Composite(mainComposite, SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		detailComposite.setLayoutData(gridData);
		detailComposite.setLayout(new GridLayout());

		updateDetailView(detailComposite);
	}

	private void generateList(List list) {
		String[] category = new String[] { "Stud", "Hole", "Axle", "Slider",
				"Hinge", "Ball", "Fixed"};
		for (String str : category)
			list.add(str);
		list.add("CollisionBox");
		list.select(0);
	}


	private void updateDetailView(Composite parent) {
		Composite unit=null;
		parent.setVisible(false);
		switch (list.getSelectionIndex()) {
		case 0:
			unit = new StudEditorComposite(parent, SWT.NONE);
			break;
		case 1:
			unit = new HoleEditorComposite(parent, SWT.NONE);
			break;
		case 2:
			unit = new AxleEditorComposite(parent, SWT.NONE);
			break;
		case 3:
			unit = new SliderEditorComposite(parent, SWT.NONE);
			break;
		case 4:
			unit = new HingeEditorComposite(parent, SWT.NONE);
			break;
		case 5:
			unit = new BallEditorComposite(parent, SWT.NONE);
			break;
		case 6:
			unit = new FixedEditorComposite(parent, SWT.NONE);
			break;
		case 7:
			unit = new CollisionEditorComposite(parent, SWT.NONE);
			break;
		}
		parent.setVisible(true);
	}

}
