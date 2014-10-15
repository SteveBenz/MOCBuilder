package Exports;

import java.net.URL;
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
import org.eclipse.swt.widgets.Canvas;
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
import Window.BackgroundThreadManager;
import Window.MOCBuilder;
import Window.BrickViewer;
import Window.ProgressDlg;

public class IdMappingInfoComposite extends Composite {

	public static void main(String args[]) {
		Display display = Display.getDefault();
		new UpdateManagerDlg(new Shell(display), SWT.NO_TRIM).open();
	}

	private Table table;
	ToolTip tooltip;
	Composite composite_ldrawPreview;
	BrickViewer viewer;
	Canvas canvas_bricklinkPreview;
	private boolean showMatchedItem = false;
	private Text text_Search;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IdMappingInfoComposite(Composite parent, int style) {
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

		final Button btnCheck_HideUpdatedItem = new Button(this, SWT.CHECK);
		btnCheck_HideUpdatedItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showMatchedItem = !btnCheck_HideUpdatedItem.getSelection();
				BusyIndicator.showWhile(getParent().getDisplay(), new Thread(
						new Runnable() {
							@Override
							public void run() {
								updateTable(showMatchedItem);
							}
						}));
			}
		});
		btnCheck_HideUpdatedItem.setBounds(622, 20, 128, 16);
		btnCheck_HideUpdatedItem.setText("Hide Updated items");
		btnCheck_HideUpdatedItem.setSelection(true);

		canvas_bricklinkPreview = new Canvas(this, SWT.BORDER);
		canvas_bricklinkPreview.setBounds(522, 293, 236, 208);

		final Label lblLdraw = new Label(this, SWT.NONE);
		lblLdraw.setBounds(522, 20, 55, 20);
		lblLdraw.setText("LDraw");

		Label lblBricklink = new Label(this, SWT.NONE);
		lblBricklink.setText("Bricklink");
		lblBricklink.setBounds(522, 270, 55, 20);

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

		Button btnUpdateAll = new Button(this, SWT.NONE);
		btnUpdateAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleUpdateAll();
			}
		});
		btnUpdateAll.setBounds(258, 523, 235, 45);
		btnUpdateAll.setText("Update All");

		createTable();
		addSelectionListener();
	}

	protected void handleUpdateAll() {
		CompatiblePartManager.getInstance().loadIdMappingInfo_BrickLink();
		CompatiblePartManager.getInstance().writeMappingListToFileCache();

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
		if (BackgroundThreadManager.getInstance().sizeOfThread() != 0)
			new ProgressDlg(getShell(), SWT.NONE).open();
	}

	protected void updateTableWithKeyword(String keyword, boolean showMappedItem) {
		table.setRedraw(false);
		table.removeAll();
		String[] titles = { "Index", "From PartId", "From Domain", "To PartId",
				"To Domain", "State" };
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[loopIndex]);
		}

		HashMap<String, String> idMappingInfoMapFromLDraw_server = UpdateManager
				.getInstance().getIdMappingInfoMapFromLDraw();
		HashMap<String, String> idMappingInfoMapFromBricklink_server = UpdateManager
				.getInstance().getIdMappingInfoMapFromBricklink();

		HashMap<String, String> idMappingInfoMapFromLDraw_local = new HashMap<String, String>();
		HashMap<String, String> idMappingInfoMapFromBricklink_local = new HashMap<String, String>();

		// obtain all id mapping info from local
		HashMap<String, PartIds> compatiblePartIdMap = CompatiblePartManager
				.getInstance().getAllPartsInDomain(PartDomainT.LDRAW);
		for (Entry<String, PartIds> entry : compatiblePartIdMap.entrySet()) {
			String partName = entry.getKey();
			PartIds partId = entry.getValue();
			String bricklinkId = null;
			if (partId != null && partId.getId(PartDomainT.BRICKLINK) != null)
				for (String id : partId.getId(PartDomainT.BRICKLINK))
					if (bricklinkId == null)
						bricklinkId = id;
					else
						bricklinkId += "+" + id;
			else
				continue;

			if (bricklinkId != null)
				idMappingInfoMapFromLDraw_local.put(partName, bricklinkId);
		}

		compatiblePartIdMap = CompatiblePartManager.getInstance()
				.getAllPartsInDomain(PartDomainT.BRICKLINK);
		for (Entry<String, PartIds> entry : compatiblePartIdMap.entrySet()) {
			String partName = entry.getKey();
			PartIds partId = entry.getValue();
			String ldrawId = null;
			if (partId != null && partId.getId(PartDomainT.LDRAW) != null)

				for (String id : partId.getId(PartDomainT.LDRAW))
					if (ldrawId == null)
						ldrawId = id;
					else
						ldrawId += "," + id;
			else
				continue;
			if (ldrawId != null)
				idMappingInfoMapFromBricklink_local.put(partName, ldrawId);
		}

		int index = 0;
		String state = "New";
		for (Entry<String, String> entry : idMappingInfoMapFromLDraw_server
				.entrySet()) {

			if (keyword != null && keyword != "")
				if (entry.getKey().contains(keyword) == false)
					continue;

			String bricklinkId_local = idMappingInfoMapFromLDraw_local
					.get(entry.getKey());
			if (bricklinkId_local != null) {
				if (bricklinkId_local.equals(entry.getValue())) {
					state = "Updated";
					if (showMatchedItem == false)
						continue;
				} else
					state = "Modified";
			} else
				state = "New";

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, entry.getKey());
			item.setText(2, "Ldraw");
			item.setText(3, entry.getValue());
			item.setText(4, "Bricklink");
			item.setText(5, state);
			if (state.equals("New"))
				item.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_CYAN));
			if (state.equals("Modified"))
				item.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_MAGENTA));
			index++;
		}

		for (Entry<String, String> entry : idMappingInfoMapFromBricklink_server
				.entrySet()) {
			String ldrawId_local = idMappingInfoMapFromBricklink_local
					.get(entry.getKey());
			if (ldrawId_local != null) {
				if (ldrawId_local.equals(entry.getValue())) {
					state = "Updated";
					if (showMatchedItem == false)
						continue;
				} else
					state = "Modified";
			} else
				state = "New";

			if (keyword != null && keyword != "")
				if (entry.getKey().contains(keyword) == false)
					continue;

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, entry.getKey());
			item.setText(2, "Bricklink");
			item.setText(3, entry.getValue());
			item.setText(4, "Ldraw");
			item.setText(5, state);
			if (state.equals("New"))
				item.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_CYAN));
			if (state.equals("Modified"))
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
					String bricklinkId = item.getText(3);
					loadLdrawModelView(ldrawId);
					updateBricklinkModelView(bricklinkId);
				}
			}
		});
	}

	protected void updateBricklinkModelView(String bricklinkId) {
		if (bricklinkId == null || bricklinkId.equals("")
				|| bricklinkId.equals("UnKnown")) {
			canvas_bricklinkPreview.setBackgroundImage(null);
			return;
		}
		canvas_bricklinkPreview.setBackgroundImage(null);
		BrickLinkClient client = BricklinkAPI.getInstance()
				.getClientForOpenAPI();
		ItemRequest request = new ItemRequest(ItemType.PART, bricklinkId);
		try {
			ItemResponse response = client.execute(request);
			ItemDT itemDt = response.getCatalogItem();
			String imageURL = itemDt.getImageUrl();
			if (imageURL == null)
				return;
			URL url = new URL(imageURL);
			Image image = new Image(getDisplay(), url.openConnection()
					.getInputStream());
			image = resize(image, canvas_bricklinkPreview.getSize());
			canvas_bricklinkPreview.setBackgroundImage(image);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private Image resize(Image image, Point size) {
		Image scaled = new Image(Display.getDefault(), size.x, size.y);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, size.x, size.y);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}
}
