package Bricklink.ChildDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import Window.BackgroundThreadManager;
import Exports.CompatiblePartManager;
import Exports.UpdateManager;

public class MappingDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private TabFolder tabFolder;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public MappingDialog(Shell parent, int style) {
		super(parent, style);
		setText("Mapping Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		Cursor waitCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
		getParent().setCursor(waitCursor);
		createContents();
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		getParent().setCursor(null);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent());
		shell.setSize(599, 620);
		shell.setText(getText());

		Button btnWriteToMappingFile = new Button(shell, SWT.NONE);
		btnWriteToMappingFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleApplyToMappingList();
			}
		});
		btnWriteToMappingFile.setBounds(158, 543, 235, 45);
		btnWriteToMappingFile.setText("Write to Mapping File");

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 10, 573, 531);

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("ID Mapping From LDraw");

		Composite composite = new IDMappingFromLDrawComposite(tabFolder,
				SWT.EMBEDDED);
		tbtmNewItem.setControl(composite);

		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("ID Mapping From Bricklink");

		composite = new IDMappingFromBricklinkComposite(tabFolder, SWT.EMBEDDED);
		tbtmNewItem.setControl(composite);

		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Color Mapping From LDraw");

		composite = new ColorMappingFromLDrawComposite(tabFolder, SWT.EMBEDDED);
		tbtmNewItem.setControl(composite);

		tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Color Mapping From Bricklink");

		composite = new ColorMappingFromBricklinkComposite(tabFolder,
				SWT.EMBEDDED);
		tbtmNewItem.setControl(composite);
	}

	protected void handleApplyToMappingList() {
		CompatiblePartManager.getInstance().writeMappingListToFileCache();

		BackgroundThreadManager.getInstance().add(new Runnable() {

			@Override
			public void run() {
				UpdateManager.getInstance().uploadIdMappingList();
			}
		});

		BackgroundThreadManager.getInstance().add(new Runnable() {

			@Override
			public void run() {
				UpdateManager.getInstance().uploadColorMappingList();
			}
		});
	}
}
