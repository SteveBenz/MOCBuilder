package Bricklink.ChildDialog;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import Bricklink.BricklinkAPI;
import Bricklink.org.kleini.bricklink.api.BrickLinkClient;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.api.Catalog.PriceGuideRequest;
import Bricklink.org.kleini.bricklink.api.Catalog.PriceGuideResponse;
import Bricklink.org.kleini.bricklink.data.ConditionT;
import Bricklink.org.kleini.bricklink.data.GuideTypeDT;
import Bricklink.org.kleini.bricklink.data.ItemType;
import Bricklink.org.kleini.bricklink.data.PriceGuideDT;
import Color.BricklinkColorT;
import Command.LDrawColorT;
import Command.LDrawLSynth;
import Command.LDrawPart;
import Exports.CompatiblePartManager;
import Exports.PartColors;
import Exports.PartDomainT;
import Exports.PartIds;
import LDraw.Files.LDrawMPDModel;
import LDraw.Support.LDrawUtilities;
import Window.BackgroundThreadManager;
import Window.MOCBuilder;
import Window.ProgressDlg;

public class UploadBricklinkWantedListDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table table;

	private HashMap<String, Integer> numOfSamePartMap;
	private ArrayList<String> partNameList;
	private ArrayList<String> bricklinkPartIdList;
	private ArrayList<LDrawColorT> partColorList;
	private ArrayList<Integer> bricklinkPartColorValueList;

	private HashMap<Integer, PriceGuideDT> bricklinkPriceGuideMap;
	private Button btnOpenBricklinkUpload;
	private Button btnObtainPriceInfomation;

	private HashMap<String, String> tempBricklinkColorMap;
	private HashMap<String, String> tempBricklinkPartNameMap;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public UploadBricklinkWantedListDialog(Shell parent, int style) {
		super(parent, style);
		setText("Exporting Dialog");

		bricklinkPartIdList = new ArrayList<String>();
		bricklinkPartColorValueList = new ArrayList<Integer>();
		bricklinkPriceGuideMap = new HashMap<Integer, PriceGuideDT>();
		tempBricklinkColorMap = new HashMap<String, String>();
		tempBricklinkPartNameMap = new HashMap<String, String>();
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(800, 600);
		shell.setText(getText());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.MULTI);
		table.setBounds(10, 10, 774, 501);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleClikcCopyToClipboard();
			}
		});
		btnNewButton.setBounds(20, 517, 235, 45);
		btnNewButton.setText("Copy to Clipboard");

		btnOpenBricklinkUpload = new Button(shell, SWT.NONE);
		btnOpenBricklinkUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Program.launch("http://www.bricklink.com/wantedXML.asp");
			}
		});
		btnOpenBricklinkUpload.setText("Open Bricklink Upload Page");
		btnOpenBricklinkUpload.setBounds(261, 517, 235, 45);

		btnObtainPriceInfomation = new Button(shell, SWT.NONE);
		btnObtainPriceInfomation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				createTableWithPrice();
			}
		});
		btnObtainPriceInfomation.setText("Obtain Prices From Bricklink");
		btnObtainPriceInfomation.setBounds(583, 517, 179, 45);

		createTable();
		addSelectionListener();

	}

	private void addSelectionListener() {
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = table.getItem(pt);
				if (item != null) {
					for (int col = 0; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							System.out.println("item clicked.");
							System.out.println("column is " + col);
							final int column = col;
							int index = -1;
							try {
								index = Integer.parseInt(item.getText(0));
							} catch (Exception e) {
							}
							if (index == -1)
								continue;
							if (column == 3) {
								PartIds partIds = CompatiblePartManager
										.getInstance().getPartIds(
												PartDomainT.LDRAW,
												partNameList.get(index));
								boolean isUnKnown = false;
								if (partIds == null)
									isUnKnown = true;
								else if (partIds.getId(PartDomainT.BRICKLINK) == null)
									isUnKnown = true;

								if (isUnKnown) {
									TableEditor editor = new TableEditor(table);
									Text text = new Text(table, SWT.NONE);
									text.setText("");
									// text.addModifyListener(new
									// ModifyListener() {
									// @Override
									// public void modifyText(ModifyEvent arg0)
									// {
									// Text text = (Text) arg0.widget;
									// item.setText(column, text.getText());
									// }
									// });

									text.addFocusListener(new FocusListener() {
										@Override
										public void focusLost(FocusEvent arg0) {
											Text text = (Text) arg0.widget;
											item.setText(column, text.getText());

											tempBricklinkPartNameMap.put(
													item.getText(1),
													text.getText());
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
								}
							}
							if (column == 4) {
								PartColors partColors = CompatiblePartManager
										.getInstance().getPartColors(
												PartDomainT.LDRAW,
												partColorList.get(index)
														.getValue());
								boolean isUnKnown = false;
								if (partColors == null)
									isUnKnown = true;
								else if (partColors
										.getColorId(PartDomainT.BRICKLINK) == null)
									isUnKnown = true;

								if (isUnKnown) {
									TableEditor editor = new TableEditor(table);
									CCombo combo = new CCombo(table, SWT.NONE);
									combo.setText("UnKnown");
									combo.setBackground(Display.getDefault()
											.getSystemColor(SWT.COLOR_RED));

									for (BricklinkColorT bricklinkColorT : BricklinkColorT
											.values())
										combo.add("" + bricklinkColorT + "("
												+ bricklinkColorT.getValue()
												+ ")");
									// combo.addModifyListener(new
									// ModifyListener() {
									// @Override
									// public void modifyText(ModifyEvent arg0)
									// {
									// CCombo combo = (CCombo) arg0.widget;
									// item.setText(column,
									// combo.getText());
									//
									// combo.dispose();
									// }
									// });
									combo.addFocusListener(new FocusListener() {
										@Override
										public void focusLost(FocusEvent arg0) {
											CCombo combo = (CCombo) arg0.widget;
											item.setText(column,
													combo.getText());
											String key = item.getText(1) + "_"
													+ item.getText(2);
											tempBricklinkColorMap.put(key,
													combo.getText());
											combo.dispose();
										}

										@Override
										public void focusGained(FocusEvent arg0) {
											// TODO Auto-generated method stub

										}
									});
									// combo.forceFocus();
									editor.grabHorizontal = true;
									editor.setEditor(combo, item, column);
								}
							}
						}
					}
				}
			}
		});
	}

	protected void handleClikcCopyToClipboard() {
		ArrayList<String> exportPartIdList = new ArrayList<String>();
		ArrayList<Integer> exportPartColorValueList = new ArrayList<Integer>();

		boolean isAllCompatible = true;
		int counter = 0;

		for (TableItem item : table.getItems()) {
			try {
				Integer.parseInt(item.getText(1));
			} catch (Exception e) {
				continue;
			}

			exportPartIdList.add(item.getText(3));
			if (item.getText(3).equals("UnKnown")) {
				isAllCompatible = false;
				counter++;
			}
			String colorItemText = item.getText(4);
			if (colorItemText.contains("(")) {
				colorItemText = colorItemText.substring(0,
						colorItemText.indexOf("("));
				BricklinkColorT blColorT = BricklinkColorT
						.valueOf(colorItemText);
				if (blColorT == null) {
					exportPartColorValueList.add(-1);
					isAllCompatible = false;
					counter++;
				} else
					exportPartColorValueList.add(blColorT.getValue());
			} else {
				counter++;
				isAllCompatible = false;
				exportPartColorValueList.add(-1);
			}
		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringBuilder copyString = new StringBuilder();
		copyString.append("<INVENTORY>\r\n");
		for (int i = 0; i < exportPartIdList.size(); i++) {
			copyString.append(" <ITEM>\r\n");
			copyString.append("  <ITEMTYPE>P</ITEMTYPE>\r\n");
			copyString.append("  <ITEMID>");
			copyString.append(exportPartIdList.get(i));
			copyString.append("</ITEMID>\r\n");
			copyString.append("  <COLOR>");
			copyString.append(exportPartColorValueList.get(i));
			copyString.append("</COLOR>\r\n");
			copyString.append(" </ITEM>\r\n");
		}
		copyString.append("</INVENTORY>\r\n");

		StringSelection contents = new StringSelection(copyString.toString());
		clipboard.setContents(contents, null);
		if (isAllCompatible == false) {
			MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING);
			msgBox.setMessage(counter
					+ " items are incompatible. Please check it.");
			msgBox.open();
		}
	}

	private void createTable() {
		createTable(false);
	}

	private void createTableWithPrice() {
		convertToBricklinkInfo();
		if (BackgroundThreadManager.getInstance().isAllFinish() == false)
			new ProgressDlg(shell, SWT.NONE).open();

		createTable(true);
	}

	private void convertToBricklinkInfo() {
		bricklinkPartIdList.clear();
		bricklinkPartColorValueList.clear();

		for (TableItem item : table.getItems()) {
			bricklinkPartIdList.add(item.getText(3));
			String colorItemText = item.getText(4);
			if (colorItemText.contains("(")) {
				colorItemText = colorItemText.substring(0,
						colorItemText.indexOf("("));
				BricklinkColorT blColorT = BricklinkColorT
						.valueOf(colorItemText);
				if (blColorT == null) {
					bricklinkPartColorValueList.add(-1);
				} else
					bricklinkPartColorValueList.add(blColorT.getValue());
			} else {
				bricklinkPartColorValueList.add(-1);
			}
		}

		for (int i = 0; i < bricklinkPartIdList.size(); i++) {
			final String bricklinkId = bricklinkPartIdList.get(i);
			final int bricklinkColorId = bricklinkPartColorValueList.get(i);
			final int index = i;
			BackgroundThreadManager.getInstance().add(new Runnable() {
				@Override
				public void run() {
					if (bricklinkId.equals("UnKnown") == false
							&& bricklinkColorId != -1)
						bricklinkPriceGuideMap.put(
								index,
								getPriceOfBricklinkItem(bricklinkId,
										bricklinkColorId));
					else
						bricklinkPriceGuideMap.put(index, null);
				}
			});
		}
	}

	private void updatePartList() {
		ArrayList<LDrawPart> partList = MOCBuilder.getInstance()
				.getAllPartInActiveModel(true);
		ArrayList<LDrawLSynth> lsynthList = MOCBuilder.getInstance()
				.getAllLSynthInActiveModel(true);

		for (LDrawLSynth lsynth : lsynthList) {

			String partName = null;

			if (lsynth.lsynthType().equals("TECHNIC_CHAIN_LINK")
					|| lsynth.lsynthType().equals("TECHNIC_CHAIN_TREAD")
					|| lsynth.lsynthType().equals("TECHNIC_CHAIN_TREAD_38")) {

				LDrawMPDModel synthsizedModel = lsynth.getSynthesizedModelName();
				if(synthsizedModel==null)continue;
				partList.addAll(LDrawUtilities.extractLDrawPartListModel(synthsizedModel, true));
			} else {
				partName = CompatiblePartManager.getInstance()
						.getCompatiblePartOfLSynth(lsynth);

				LDrawPart part = new LDrawPart();
				part.setDisplayName(partName);
				part.setLDrawColor(lsynth.getLDrawColor());
				partList.add(part);
			}
		}

		Collections.sort(partList, new Comparator<LDrawPart>() {
			@Override
			public int compare(LDrawPart o1, LDrawPart o2) {
				int retValue = o1.displayName().compareTo(o2.displayName());
				if (retValue == 0)
					retValue = o1
							.getLDrawColor()
							.colorCode()
							.toString()
							.compareTo(
									o2.getLDrawColor().colorCode().toString());
				return retValue;
			}
		});
		numOfSamePartMap = new HashMap<String, Integer>();
		partNameList = new ArrayList<String>();
		partColorList = new ArrayList<LDrawColorT>();
		for (LDrawPart part : partList) {
			String partName = LDrawUtilities.excludeExtensionFromPartName(
					part.displayName()).toLowerCase();
			String partColor = part.getLDrawColor().getColorCode().toString();
			String key = partName + partColor;
			if (numOfSamePartMap.containsKey(key)) {
				numOfSamePartMap.put(key, numOfSamePartMap.get(key) + 1);
			} else {
				numOfSamePartMap.put(key, 1);
				partNameList.add(partName);
				partColorList.add(part.getLDrawColor().getColorCode());
			}
		}
	}

	private void createTable(boolean composePrice) {
		try {
			updatePartList();
			table.setVisible(false);
			table.removeAll();
			String[] titles = { "Index", "LDraw PartName", "LDrawColor",
					"BrickLink PartId", "BrickLink Color", "Qty", "Price(unit)" };
			for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
				TableColumn column = new TableColumn(table, SWT.NULL);
				column.setText(titles[loopIndex]);
			}

			float totalAvrPrice = 0;
			int index = 0;
			for (index = 0; index < partNameList.size(); index++) {
				String partName = partNameList.get(index);
				LDrawColorT colorCode = partColorList.get(index);
				int qty = 0;

				PartIds partIds = CompatiblePartManager.getInstance()
						.getPartIds(PartDomainT.LDRAW, partName);

				ArrayList<String> bricklinkIds = null;
				String bricklinkId = null;
				if (partIds != null)
					bricklinkIds = partIds.getId(PartDomainT.BRICKLINK);
				if (bricklinkIds != null)
					for (String id : bricklinkIds)
						if (bricklinkId == null)
							bricklinkId = id;
						else
							bricklinkId += "," + id;

				if (bricklinkId == null) {
					String key = partName;

					if (tempBricklinkPartNameMap.containsKey(key))
						bricklinkId = tempBricklinkPartNameMap.get(key);
					else
						bricklinkId = "UnKnown";
				}

				String brickLinkColorValue = null;
				PartColors partColor = CompatiblePartManager.getInstance()
						.getPartColors(PartDomainT.LDRAW, colorCode.getValue());
				Integer blColorValue = null;
				if (partColor != null) {
					blColorValue = partColor.getColorId(PartDomainT.BRICKLINK);
					if (blColorValue != null)
						brickLinkColorValue = ""
								+ BricklinkColorT.byValue(blColorValue) + "("
								+ blColorValue + ")";
				}

				if (brickLinkColorValue == null
						|| brickLinkColorValue.equals("null")) {
					String key = partName + "_" + colorCode.toString() + "("
							+ colorCode.getValue() + ")";
					if (tempBricklinkColorMap.containsKey(key))
						brickLinkColorValue = ""
								+ tempBricklinkColorMap.get(key);
					else
						brickLinkColorValue = "UnKnown";
				}

				qty = numOfSamePartMap.get(partName + colorCode.toString());

				String price = "UnKnown";
				if (composePrice) {
					PriceGuideDT priceGuide = bricklinkPriceGuideMap.get(index);
					if (priceGuide != null) {
						price = "" + priceGuide.getAveragePrice() + "("
								+ priceGuide.getMinPrice() + " ~ "
								+ priceGuide.getMaxPrice() + ")";
						totalAvrPrice += priceGuide.getAveragePrice()
								.floatValue() * qty;
					}
				}

				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, "" + index);
				item.setText(1, partName);
				item.setText(2,
						colorCode.toString() + "(" + colorCode.getValue() + ")");
				item.setText(3, bricklinkId);
				item.setText(4, brickLinkColorValue);
				item.setText(5, "" + qty);
				item.setText(6, price);

				if (bricklinkId.equals("UnKnown"))
					item.setBackground(3,
							Display.getDefault().getSystemColor(SWT.COLOR_RED));
				if (brickLinkColorValue.equals("UnKnown"))
					item.setBackground(4,
							Display.getDefault().getSystemColor(SWT.COLOR_RED));
			}

			if (composePrice) {
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(0, "Total Average Price");
				item.setText(1, "");
				item.setText(2, "");
				item.setText(3, "");
				item.setText(4, "");
				item.setText(5, "");
				item.setText(6, "" + totalAvrPrice);
			}

			for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
				table.getColumn(loopIndex).pack();
			}
			table.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, PriceGuideDT> priceCacheMap = null;

	private PriceGuideDT getPriceOfBricklinkItem(String brickLinkId,
			int brickLinkColorValue) {

		if (priceCacheMap == null)
			priceCacheMap = new HashMap<String, PriceGuideDT>();
		String key = brickLinkId + brickLinkColorValue;
		if (priceCacheMap.containsKey(key))
			return priceCacheMap.get(key);

		BrickLinkClient client = BricklinkAPI.getInstance()
				.getClientForOpenAPI();
		Request request = null;
		Response response = null;
		try {
			request = new PriceGuideRequest(ItemType.PART, brickLinkId,
					brickLinkColorValue, GuideTypeDT.STOCK, ConditionT.N);
			response = client.execute(request);
			PriceGuideDT priceGuideDT = ((PriceGuideResponse) response)
					.getPriceGuide();

			priceCacheMap.put(key, priceGuideDT);
			return priceGuideDT;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
