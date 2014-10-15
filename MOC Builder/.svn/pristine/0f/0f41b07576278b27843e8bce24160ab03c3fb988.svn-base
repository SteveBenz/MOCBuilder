package Exports;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import Bricklink.BricklinkAPI;
import Bricklink.org.kleini.bricklink.api.BrickLinkClient;
import Bricklink.org.kleini.bricklink.api.Catalog.ItemRequest;
import Bricklink.org.kleini.bricklink.api.Catalog.ItemResponse;
import Bricklink.org.kleini.bricklink.data.ItemDT;
import Bricklink.org.kleini.bricklink.data.ItemType;
import Command.LDrawColorT;
import LDraw.Support.ColorLibrary;
import LDraw.Support.ConnectivityLibrary;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.PartCache;
import Window.BackgroundThreadManager;
import Window.MOCBuilder;
import Window.BrickViewer;
import Window.ProgressDlg;

public class PartInfoComposite extends Composite {

	public static void main(String args[]) {
		Display display = Display.getDefault();
		new UpdateManagerDlg(new Shell(display), SWT.NO_TRIM).open();
	}

	private Table table;
	Composite composite_ldrawPreview;
	BrickViewer viewer;
	private boolean showMatchedItem = false;
	private Text text_Search;
	private ToolTip tooltip;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PartInfoComposite(Composite parent, int style) {
		super(parent, style);
		setLocation(0, 0);
		setSize(780, 560);

		createContents();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void createContents() {
		tooltip = new ToolTip(getShell(), SWT.NONE);
		tooltip.setAutoHide(true);

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.MULTI);
		table.setLocation(10, 42);
		table.setSize(495, 459);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final Button btnCheck_HideMatchedItem = new Button(this, SWT.CHECK);
		btnCheck_HideMatchedItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showMatchedItem = !btnCheck_HideMatchedItem.getSelection();
				BusyIndicator.showWhile(getParent().getDisplay(), new Thread(
						new Runnable() {
							@Override
							public void run() {
								updateTable(showMatchedItem);
							}
						}));
			}
		});
		btnCheck_HideMatchedItem.setBounds(622, 20, 128, 16);
		btnCheck_HideMatchedItem.setText("Hide Updated items");
		btnCheck_HideMatchedItem.setSelection(true);

		final Label lblLdraw = new Label(this, SWT.NONE);
		lblLdraw.setBounds(522, 20, 55, 20);
		lblLdraw.setText("LDraw");

		composite_ldrawPreview = new Composite(this, SWT.BORDER);
		composite_ldrawPreview.setLayout(new GridLayout(1, false));
		composite_ldrawPreview.setBounds(522, 42, 233, 196);

		createBrickViewer(composite_ldrawPreview);

		text_Search = new Text(this, SWT.BORDER);
		text_Search.setBounds(10, 15, 105, 21);

		text_Search.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				String keyword = text_Search.getText();
				if (keyword == null)
					return;
				System.out.println(keyword);
				updateTableWithKeyword(keyword, showMatchedItem);
			}
		});

		Button btnUploadAll = new Button(this, SWT.NONE);
		btnUploadAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleUploadAll();
			}
		});
		btnUploadAll.setBounds(328, 523, 200, 45);
		btnUploadAll.setText("Upload All Unregisted Info");

		Button btnUpdateAll = new Button(this, SWT.NONE);
		btnUpdateAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleUpdateAll();
			}
		});
		btnUpdateAll.setBounds(188, 523, 100, 45);
		btnUpdateAll.setText("Download All");

		createTable();
		addSelectionListener();
	}

	protected void handleUploadAll() {
		System.out.println("handleUploadAll");
		UpdateManager.getInstance().uploadCustomParts();
		BackgroundThreadManager.getInstance().add(new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						createTable();
					}
				});
			}
		});
		if(BackgroundThreadManager.getInstance().sizeOfThread()!=0)
			new ProgressDlg(getShell(), SWT.NONE).open();

	}

	protected void handleUpdateAll() {
		UpdateManager.getInstance().downloadNewParts();
		BackgroundThreadManager.getInstance().add(new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						createTable();
					}
				});
			}
		});
		if(BackgroundThreadManager.getInstance().sizeOfThread()!=0)
			new ProgressDlg(getShell(), SWT.NONE).open();
	}

	protected void updateTableWithKeyword(String keyword, boolean showMappedItem) {
		table.setRedraw(false);
		table.removeAll();
		String[] titles = { "Index", "PartId", "State" };
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[loopIndex]);
		}

		ArrayList<String> partInfoList_server = UpdateManager.getInstance()
				.getPartList();
		ArrayList<String> partfoList_local = PartCache.getInstance()
				.getAllParts();

		int index = 0;
		String state = "New";
		for (String partId : partInfoList_server) {
			if (keyword != null && keyword != "")
				if (partId.contains(keyword) == false)
					continue;

			boolean isAlreadyExist = false;
			for (String partId_local : partfoList_local) {
				if (partId.equals(partId_local)) {
					isAlreadyExist = true;
					break;
				}
			}
			if (isAlreadyExist) {
				state = "Updated";
				if (showMappedItem == false)
					continue;
			} else
				state = "New";

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, partId);
			item.setText(2, state);
			if (state.equals("New"))
				item.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_CYAN));
			index++;
		}

		for (String partId : partfoList_local) {
			if (keyword != null && keyword != "")
				if (partId.contains(keyword) == false)
					continue;

			boolean isAlreadyExist = false;
			for (String partId_server : partInfoList_server)
				if (partId.equals(partId_server)) {
					isAlreadyExist = true;
					break;
				}
			if (isAlreadyExist == false)
				state = "Custom";
			else
				continue;

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, partId);
			item.setText(2, state);
			if (state.equals("Custom"))
				item.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_MAGENTA));
			index++;
		}

		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
		table.setRedraw(true);
		table.setVisible(true);
	}

	private void createBrickViewer(Composite parent) {
		viewer = new BrickViewer(parent, tooltip);
		viewer.setVisible(true);
	}

	private void addSelectionListener() {
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = table.getItem(pt);
				if (item != null) {
					String ldrawId = item.getText(1) + ".dat";
					loadLdrawModelView(ldrawId);
				}
			}
		});
	}

	protected void loadLdrawModelView(String ldrawId) {
		viewer.setDirectiveToWorkingFile(
				ldrawId,
				ColorLibrary.sharedColorLibrary().colorForCode(
						LDrawColorT.LDrawCurrentColor));
	}

	private void updateTable(boolean showMappedItem) {
		updateTableWithKeyword(null, showMappedItem);
	}

	private void createTable() {
		updateTable(showMatchedItem);
	}
}
