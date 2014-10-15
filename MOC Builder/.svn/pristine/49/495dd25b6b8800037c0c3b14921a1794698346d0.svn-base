package Bricklink.ChildDialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
import Exports.CompatiblePartManager;
import Exports.PartDomainT;
import Exports.PartIds;
import LDraw.Support.ColorLibrary;
import Window.BrickViewer;

public class IDMappingFromBricklinkComposite extends Composite {

	private Table table;
	ToolTip tooltip;
	Composite composite_ldrawPreview;
	BrickViewer viewer;
	Canvas canvas_bricklinkPreview;

	boolean showMatchedItem = false;

	private Text text_SearchBricklink;
	private Text text_Search;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IDMappingFromBricklinkComposite(Composite parent, int style) {
		super(parent, style);

		setLocation(0, 0);
		setSize(563, 511);

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
		table.setSize(295, 459);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final Button btnCheck_hideMatchedItem = new Button(this, SWT.CHECK);
		btnCheck_hideMatchedItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showMatchedItem = !btnCheck_hideMatchedItem.getSelection();
				BusyIndicator.showWhile(getParent().getDisplay(), new Thread(
						new Runnable() {
							@Override
							public void run() {
								updateTable(showMatchedItem);
							}
						}));
			}
		});
		btnCheck_hideMatchedItem.setBounds(422, 20, 128, 16);
		btnCheck_hideMatchedItem.setText("Hide matched items");
		btnCheck_hideMatchedItem.setSelection(true);

		canvas_bricklinkPreview = new Canvas(this, SWT.BORDER);
		canvas_bricklinkPreview.setBounds(322, 293, 236, 208);

		text_SearchBricklink = new Text(this, SWT.BORDER);
		text_SearchBricklink.setLocation(381, 270);
		text_SearchBricklink.setSize(91, 21);

		final Label lblLdraw = new Label(this, SWT.NONE);
		lblLdraw.setBounds(322, 20, 55, 20);
		lblLdraw.setText("LDraw");

		Label lblBricklink = new Label(this, SWT.NONE);
		lblBricklink.setText("Bricklink");
		lblBricklink.setBounds(322, 270, 55, 20);

		Button btnBricklinkSearch = new Button(this, SWT.NONE);
		btnBricklinkSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String itemNo = text_SearchBricklink.getText();
				updateBricklinkModelView(itemNo);
			}
		});
		btnBricklinkSearch.setLocation(477, 268);
		btnBricklinkSearch.setSize(75, 25);
		btnBricklinkSearch.setText("Search");

		composite_ldrawPreview = new Composite(this, SWT.BORDER);
		composite_ldrawPreview.setLayout(new GridLayout(1, false));
		composite_ldrawPreview.setBounds(322, 42, 233, 196);

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

		createTable();
		addSelectionListener();
	}

	protected void updateTableWithKeyword(String keyword, boolean showMappedItem) {
		table.setRedraw(false);
		table.removeAll();
		String[] titles = { "Index", "BrickLink PartId", "LDraw PartName" };
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[loopIndex]);
		}

		int index = 0;
		HashMap<String, PartIds> compatiblePartIdMap = CompatiblePartManager
				.getInstance().getAllPartsInDomain(PartDomainT.BRICKLINK);
		for (Entry<String, PartIds> entry : compatiblePartIdMap.entrySet()) {
			String partName = entry.getKey();
			PartIds partId = entry.getValue();
			ArrayList<String> ldrawIds = null;
			String ldrawId = null;
			if (partId != null)
				ldrawIds = partId.getId(PartDomainT.LDRAW);
			if (ldrawIds != null)
				for (String id : ldrawIds)
					if (ldrawId == null)
						ldrawId = id;
					else
						ldrawId += "+" + id;

			if (ldrawId == null || ldrawId.equals(""))
				ldrawId = "UnKnown";
			else if (showMappedItem == false)
				continue;

			if (keyword != null && keyword.equals("") == false) {
				if (partName.toLowerCase().contains(keyword) == false)
					continue;
			}

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, partName);
			item.setText(2, ldrawId);

			if (ldrawId.equals("UnKnown"))
				item.setBackground(2,
						Display.getDefault().getSystemColor(SWT.COLOR_RED));
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
					String ldrawId = item.getText(2) + ".dat";
					String bricklinkId = item.getText(1);
					updateBricklinkModelView(bricklinkId);
					if (ldrawId.equals(".dat") || ldrawId.equals("UnKnown.dat"))
						ldrawId = bricklinkId + ".dat";
					loadLdrawModelView(ldrawId);
					for (int col = 0; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							final int column = col;
							if (column == 2) {
								TableEditor editor = new TableEditor(table);
								Text text = new Text(table, SWT.NONE);
								text.setText(item.getText(2));

								text.addFocusListener(new FocusListener() {
									@Override
									public void focusLost(FocusEvent arg0) {
										Text text = (Text) arg0.widget;
										item.setText(column, text.getText());
										loadLdrawModelView(text.getText()
												+ ".dat");
										CompatiblePartManager
												.getInstance()
												.updateIdMappingInfoFromBricklink(
														item.getText(1),
														item.getText(2));
										text.dispose();
									}

									@Override
									public void focusGained(FocusEvent arg0) {
										// TODO Auto-generated method stub

									}
								});

								editor.grabHorizontal = true;
								editor.setEditor(text, item, column);
								text.forceFocus();
							} else if (event.button == 3) {
								item.setText(2, bricklinkId);
								CompatiblePartManager.getInstance()
										.updateIdMappingInfoFromBricklink(
												bricklinkId, bricklinkId);
							}
						}
					}
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
