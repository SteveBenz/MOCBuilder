package Bricklink.ChildDialog;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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

import Color.BricklinkColorT;
import Command.LDrawColor;
import Command.LDrawColorT;
import Exports.CompatiblePartManager;
import Exports.PartColors;
import Exports.PartDomainT;
import LDraw.Support.ColorLibrary;

public class ColorMappingFromBricklinkComposite extends Composite {
	private Table table;
	private Text text_Search;
	private Canvas canvas_Ldraw;
	private Canvas canvas_Bricklink;

	boolean showMatchedItem = false;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorMappingFromBricklinkComposite(Composite parent, int style) {
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
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.MULTI);
		table.setLocation(10, 42);
		table.setSize(405, 459);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final Button btnCheckButton = new Button(this, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showMatchedItem = !btnCheckButton.getSelection();
				BusyIndicator.showWhile(getParent().getDisplay(), new Thread(new Runnable() {					
					@Override
					public void run() {
						updateTable(showMatchedItem);						
					}
				}));
			}
		});
		btnCheckButton.setBounds(422, 20, 128, 16);
		btnCheckButton.setText("Hide matched items");
		btnCheckButton.setSelection(true);
		
		canvas_Ldraw = new Canvas(this, SWT.NONE);
		canvas_Ldraw.setBounds(422, 74, 128, 97);
		
		Label lblLdraw = new Label(this, SWT.NONE);
		lblLdraw.setBounds(421, 53, 55, 15);
		lblLdraw.setText("LDraw");
		
		Label lblBricklink = new Label(this, SWT.NONE);
		lblBricklink.setText("Bricklink");
		lblBricklink.setBounds(421, 192, 55, 15);
		
		canvas_Bricklink = new Canvas(this, SWT.NONE);
		canvas_Bricklink.setBounds(422, 213, 128, 97);
		
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
		String[] titles = { "Index", "BrickLink Color", "LDraw Color" };
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[loopIndex]);
		}

		int index = 0;
		HashMap<Integer, PartColors> compatiblePartColorMap = CompatiblePartManager
				.getInstance().getAllColorsInDomain(PartDomainT.BRICKLINK);

		for (Entry<Integer, PartColors> entry : compatiblePartColorMap
				.entrySet()) {
			Integer bricklinkId = entry.getKey();
			PartColors partColors = entry.getValue();
			Integer ldrawId = null;
			if (partColors != null)
				ldrawId = partColors.getColorId(PartDomainT.LDRAW);

			if (ldrawId != null && showMappedItem == false)
				continue;

			if (keyword != null && keyword.equals("") == false) {
				if (new String("" + bricklinkId).contains(keyword) == false && BricklinkColorT.byValue(bricklinkId).toString().toLowerCase().contains(keyword)==false)
					continue;
			}

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, "" + BricklinkColorT.byValue(bricklinkId));
			if (ldrawId != null)
				item.setText(2, "" + LDrawColorT.byValue(ldrawId));
			else
				item.setText(2, "UnKnown");
			item.setData(partColors);

			if (ldrawId == null) {
				item.setBackground(2,
						Display.getDefault().getSystemColor(SWT.COLOR_RED));
			}
			index++;
		}

		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
		table.setRedraw(true);
		table.setVisible(true);

	}

	private void addSelectionListener() {
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = table.getItem(pt);
				if (item != null) {
					final PartColors colors = (PartColors) item.getData();
					
					updateLDrawColor(colors.getColorId(PartDomainT.LDRAW));
					updateBricklinkColor(colors.getColorId(PartDomainT.BRICKLINK));

					final CCombo combo = new CCombo(table, SWT.NONE);
					combo.setEditable(false);
					combo.setText("");
					combo.add("UnKnown");
					for (LDrawColorT ldrawColorT : LDrawColorT
							.values()) {
						combo.add(ldrawColorT.toString());
					}
					combo.forceFocus();

					combo.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetSelected(
								SelectionEvent arg0) {
							String text = combo.getText();
							Integer color = null;
							if (!text.equals("UnKnown")) {
								color = LDrawColorT.valueOf(text).getValue();
							}
							item.setText(2, text);
							CompatiblePartManager
									.getInstance()
									.updateColorMappingInfoFromBricklink(
											colors.getColorId(PartDomainT.BRICKLINK),color);
							updateLDrawColor(colors.getColorId(PartDomainT.LDRAW));
						}

						@Override
						public void widgetDefaultSelected(
								SelectionEvent arg0) {
							// TODO Auto-generated method stub

						}
					});
					combo.addFocusListener(new FocusListener() {
						@Override
						public void focusLost(FocusEvent arg0) {
							CCombo combo = (CCombo) arg0.widget;
							combo.dispose();
						}

						@Override
						public void focusGained(FocusEvent arg0) {
						}
					});
					for (int col = 0; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							final int column = col;
							if (column == 2) {
								TableEditor editor = new TableEditor(table);

								editor.grabHorizontal = true;
								editor.setEditor(combo, item, column);
							}
						}
					}
				}
			}
		});
	}

	private void updateTable(boolean showMappedItem) {
		updateTableWithKeyword(null, showMappedItem);
	}

	private void createTable() {
		updateTable(showMatchedItem);
	}
	
	private void updateLDrawColor(Integer colorId) {
		canvas_Ldraw.setBackground(null);
		if (colorId == null)
			return;

		LDrawColor ldrawColor = ColorLibrary.sharedColorLibrary().colorForCode(
				LDrawColorT.byValue(colorId));
		float rgba[] = new float[4];
		ldrawColor.getColorRGBA(rgba);
		Color color = new Color(Display.getCurrent(), (int) (255 * rgba[0]),
				(int) (255 * rgba[1]), (int) (255 * rgba[2]));
		canvas_Ldraw.setBackground(color);
	}

	private void updateBricklinkColor(Integer colorId) {
		canvas_Bricklink.setBackground(null);
		if (colorId == null)
			return;
		BricklinkColorT bricklinkColor = BricklinkColorT.byValue(colorId);
		if (bricklinkColor == null)
			return;

		int rgb[] = new int[3];
		String colorCode = bricklinkColor.getColorCode();
		if(colorCode==null || colorCode.length()!=6)return;
		
		rgb[0] = Integer.parseInt(colorCode.substring(0, 2), 16);
		rgb[1] = Integer.parseInt(colorCode.substring(2, 4), 16);
		rgb[2] = Integer.parseInt(colorCode.substring(4), 16);
		Color color = new Color(Display.getCurrent(), rgb[0], rgb[1], rgb[2]);
		canvas_Bricklink.setBackground(color);
	}
}
