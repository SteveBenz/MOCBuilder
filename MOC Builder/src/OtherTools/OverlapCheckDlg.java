package OtherTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import Builder.BrickSelectionManager;
import Command.LDrawColorT;
import Command.LDrawPart;
import Connectivity.GlobalConnectivityManager;
import Exports.CompatiblePartManager;
import LDraw.Support.LDrawUtilities;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Window.MOCBuilder;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.sun.nio.sctp.Notification;

public class OverlapCheckDlg extends Dialog implements ILDrawSubscriber {

	protected Object result;
	protected Shell shlOverlapCheck;
	private Table table;
	private Button btnCheck_onlyShowOverlapped;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public OverlapCheckDlg(Shell parent, int style) {
		super(parent, style);
		setText("Overlab Check");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlOverlapCheck.open();
		shlOverlapCheck.layout();
		Display display = getParent().getDisplay();
		while (!shlOverlapCheck.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.LDrawPartAdded);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.LDrawPartRemoved);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.LDrawPartTransformed);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChange);
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlOverlapCheck = new Shell(getParent(), getStyle());
		shlOverlapCheck.setSize(400, 400);
		shlOverlapCheck.setText("Overlap Check");

		table = new Table(shlOverlapCheck, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 33, 374, 329);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		btnCheck_onlyShowOverlapped = new Button(shlOverlapCheck, SWT.CHECK);
		btnCheck_onlyShowOverlapped
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						createTable();
					}
				});
		btnCheck_onlyShowOverlapped.setBounds(235, 10, 149, 20);
		btnCheck_onlyShowOverlapped.setText("List Up Only Overlapped");
		btnCheck_onlyShowOverlapped.setSelection(true);

		btnNewButton = new Button(shlOverlapCheck, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (LDrawPart part : partList) {
					boolean isOverlapped = false;

					if (part.getCollisionBoxList() == null
							|| part.getCollisionBoxList().size() == 0)
						isOverlapped = true;
					else
						isOverlapped = GlobalConnectivityManager.getInstance()
								.CheckCollisionBox(part,
										part.transformationMatrix());
					if (isOverlapped)
						part.setHidden(false);
					else
						part.setHidden(true);
				}
				NotificationCenter.getInstance().postNotification(NotificationMessageT.NeedReDraw);
			}
		});
		btnNewButton.setBounds(10, 2, 133, 28);
		btnNewButton.setText("Show Only Overlapped");
		
		btnShowAll = new Button(shlOverlapCheck, SWT.NONE);
		btnShowAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				MOCBuilder.getInstance().showAllStep();
			}
		});
		btnShowAll.setText("Show All");
		btnShowAll.setBounds(149, 2, 73, 28);

		updatePartList();
		createTable();

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawPartAdded);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawPartRemoved);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawPartTransformed);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChange);

		addSelectionListener();
	}

	private ArrayList<LDrawPart> partList;
	private Button btnNewButton;
	private Button btnShowAll;

	private void createTable() {
		if (partList == null)
			return;

		table.setVisible(false);
		table.removeAll();
		String[] titles = { "Index", "PartName", "Color", "isOverlapped" };
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[loopIndex]);
		}

		int index = 0;
		LDrawPart part = null;
		for (index = 0; index < partList.size(); index++) {
			part = partList.get(index);
			String partName = LDrawUtilities.excludeExtensionFromPartName(
					part.displayName()).toLowerCase();
			LDrawColorT colorCode = part.getLDrawColor().getColorCode();
			Boolean isOverlapped=false;
			if (part.getCollisionBoxList() == null
					|| part.getCollisionBoxList().size() == 0)
				isOverlapped = null;
			else
				isOverlapped = GlobalConnectivityManager.getInstance()
						.CheckCollisionBox(part, part.transformationMatrix());

			if (btnCheck_onlyShowOverlapped.getSelection()
					&& (isOverlapped !=null && isOverlapped==false))
				continue;

			final TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "" + index);
			item.setText(1, partName);
			item.setText(2, colorCode.toString() + "(" + colorCode.getValue()
					+ ")");
			item.setText(3, "" + (isOverlapped == null ? "UnKnown" : isOverlapped));
		}
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
		table.setVisible(true);
	}

	private void updatePartList() {
		partList = MOCBuilder.getInstance().getAllPartInFile();
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
	}

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		updatePartList();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				createTable();
			}
		});
	}

	private void addSelectionListener() {
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = table.getItem(pt);
				if (item != null) {
					if (partList == null)
						return;
					LDrawPart part = partList.get(Integer.parseInt(item
							.getText(0)));
					part.setHidden(false);
					BrickSelectionManager.getInstance().clearSelection();
					MOCBuilder.getInstance().getCamera()
							.moveTo(part.position());
					BrickSelectionManager.getInstance()
							.updateScreenProjectionVerticesMapAll();

					BrickSelectionManager.getInstance()
							.addPartToSelection(part);
				}
				NotificationCenter.getInstance().postNotification(NotificationMessageT.NeedReDraw);
			}
		});
	}
}
