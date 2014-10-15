package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

import Window.DNDTransfer;

public class ConnectivityBrowserUI implements DragSourceListener {
	Tree tree;
	Display display;
	Shell shell;
	Composite parent;

	public ConnectivityBrowserUI(Composite parent, int style) {
		display = parent.getDisplay();
		shell = parent.getShell();

		setMainView(parent);
		setData();
	}

	public void close() {
	}

	private void setData() {
	}

	private void setMainView(Composite composite) {
		parent = composite;
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayout(new RowLayout(SWT.HORIZONTAL));

		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 0);
		fd_group.left = new FormAttachment(0, 0);
		fd_group.bottom = new FormAttachment(0, 210);
		fd_group.right = new FormAttachment(0, 600);
		tabFolder.setLayoutData(fd_group);

//		String[] category = new String[] { "Stud", "Hole", "Axle", "Slider", "Hinge", "Ball", "Fixed", "Gear", "Rail" };

		Composite unit = new StudEditorComposite(tabFolder, SWT.NONE);
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Stud");
		unit.pack();
		
		unit = new HoleEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Hole");
		unit.pack();
		
		unit = new AxleEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Axle");
		unit.pack();
		
		unit = new BallEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Ball");
		unit.pack();
				
		unit = new FixedEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Fixed");
		unit.pack();
		
		unit = new HingeEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Hinge");
		unit.pack();
		
		unit = new SliderEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("Slider");
		unit.pack();
		
		unit = new CollisionEditorComposite(tabFolder, SWT.NONE);
		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(unit);
		tbtmNewItem.setText("CollisionBox");
		unit.pack();

	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.image = null;

		Object object = null;
		Control control = ((DragSource) event.getSource()).getControl();
		if (control.equals(tree)) {
			object = tree.getSelection()[0].getData();
		}
		if (object == null) {
			event.doit = false;
		} else {
			DNDTransfer.getInstance().setData(object);
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = DNDTransfer.getInstance().getData();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		DNDTransfer.getInstance().end();
	}
}
