package ConnectivityEditor.Window;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import Builder.DirectiveSelectionManager;
import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.AxleT;
import ConnectivityEditor.Connectivity.FixedT;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class ConnectivityFileInfoWindow implements ILDrawSubscriber {
	private Tree fileInfoTreeComponent = null;
	private ConnectivityEditor cEditor = null;
	private Display display;

	public ConnectivityFileInfoWindow(Composite parent) {
		display = parent.getDisplay();
		this.cEditor = ConnectivityEditor.getInstance();

		generateView(parent);
		drawTree();

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.ConnectivityDidAdded);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.ConnectivityDidRemoved);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.ConnectivityDidChanged);
	}

	private void drawTree() {
		fileInfoTreeComponent.setVisible(false);
		fileInfoTreeComponent.removeAll();
		// connectivity
		TreeItem treeItem_Connectivity = new TreeItem(fileInfoTreeComponent,
				SWT.NONE);
		treeItem_Connectivity.setText("Connectivity");
		TreeItem treeItem;
		if (cEditor.getWorkingPart() != null
				&& cEditor.getWorkingPart().getConnectivityList() != null)
			for (Connectivity conn : cEditor.getWorkingPart()
					.getConnectivityList()) {
				treeItem = new TreeItem(treeItem_Connectivity, SWT.NONE);
				treeItem.setText(getDescription(conn));
				treeItem.setData(conn);

			}
		// Collision
		TreeItem treeItem_Collision = new TreeItem(fileInfoTreeComponent,
				SWT.NONE);
		treeItem_Collision.setText("Collision");
		if (cEditor.getWorkingPart() != null
				&& cEditor.getWorkingPart().getCollisionBoxList() != null)
			for (CollisionBox collisionBox : cEditor.getWorkingPart()
					.getCollisionBoxList()) {
				treeItem = new TreeItem(treeItem_Collision, SWT.NONE);
				treeItem.setText(collisionBox.toString());
				treeItem.setData(collisionBox);
			}

		treeItem_Connectivity.setExpanded(true);
		treeItem_Collision.setExpanded(true);
		fileInfoTreeComponent.setVisible(true);

		setSelection();
	}

	private boolean isUpdateCompleted = true;
	private boolean needOneMoreUpdate = false;

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		if (isUpdateCompleted) {
			isUpdateCompleted = false;
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					drawTree();
					while (needOneMoreUpdate) {
						needOneMoreUpdate = false;
						drawTree();
					}
					isUpdateCompleted = true;

					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.NeedRedraw);
				}
			});
		} else
			needOneMoreUpdate = true;
	}

	public void generateView(final Composite parent) {
		fileInfoTreeComponent = new Tree(parent, SWT.MULTI);
		fileInfoTreeComponent.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));

		fileInfoTreeComponent.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				TreeItem item = fileInfoTreeComponent.getItem(new Point(e.x,
						e.y));
				if (item == null) {
					ConnectivitySelectionManager.getInstance().clearSelection();
					fileInfoTreeComponent.setSelection(new TreeItem[0]);
					GlobalFocusManagerForConnectivityEditor.getInstance()
							.forceFocusToMainView();
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		fileInfoTreeComponent.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] selectedItems = fileInfoTreeComponent.getSelection();
				ConnectivitySelectionManager.getInstance().clearSelection();
				Connectivity conn = null;
				for (TreeItem selectedItem : selectedItems) {
					if (selectedItem.getData() instanceof Connectivity) {
						conn = (Connectivity) selectedItem.getData();
						ConnectivitySelectionManager.getInstance()
								.addConnectivityToSelection(conn);
					}
				}

				GlobalFocusManagerForConnectivityEditor.getInstance()
						.forceFocusToMainView();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		fileInfoTreeComponent
				.addKeyListener(new ConnectivityEditorEventHandler(cEditor));
	}

	private void setSelection() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!fileInfoTreeComponent.isDisposed()) {
					ArrayList<TreeItem> list = getSelectedItems(fileInfoTreeComponent
							.getItems());
					final TreeItem[] items = new TreeItem[list.size()];
					list.toArray(items);
					fileInfoTreeComponent.setSelection(items);
				}
			}
		});
	}

	private ArrayList<TreeItem> getSelectedItems(TreeItem[] parentItems) {
		ArrayList<TreeItem> selectedItems = new ArrayList<TreeItem>();
		ArrayList<Connectivity> selected = ConnectivitySelectionManager
				.getInstance().getSelectedConnectivityList();
		Connectivity conn = null;

		for (TreeItem item : parentItems) {
			if (item.getData() == null)
				continue;
			else {
				conn = (Connectivity) item.getData();
				if (selected.contains(conn))
					selectedItems.add(item);
			}
		}

		return selectedItems;
	}

	private String getDescription(Connectivity conn) {
		StringBuilder strBuilder = new StringBuilder();
		if (conn instanceof ICustom2DField) {
			ICustom2DField custom2d = (ICustom2DField) conn;
			if (custom2d instanceof Hole)
				strBuilder.append("Hole: ");
			else
				strBuilder.append("Stud: ");

			strBuilder.append(" Size: ");
			strBuilder.append(custom2d.getheight() / 2);
			strBuilder.append(",");
			strBuilder.append(custom2d.getwidth() / 2);
			strBuilder.append(" MatrixInfo: ");
			MatrixItem[][] matrixItem = custom2d.getMatrixItem();

			for (int column = 0; column < custom2d.getheight() + 1; column++) {
				for (int row = 0; row < custom2d.getwidth() + 1; row++) {
					strBuilder.append(matrixItem[column][row].getAltitude()
							+ ":" + matrixItem[column][row].getOccupiedArea()
							+ ":" + matrixItem[column][row].getShape() + ",");
				}
				strBuilder.append("  ");
			}

		} else if (conn instanceof Axle) {
			strBuilder.append("Axle");
			strBuilder.append(" Type: ");
			strBuilder.append(AxleT.byValue(conn.gettype()));
			strBuilder.append(" Length: ");
			strBuilder.append("" + ((Axle) conn).getlength());
			strBuilder.append(" StartCapped: ");
			strBuilder.append("" + ((Axle) conn).getstartCapped());
			strBuilder.append(" EndCapped: ");
			strBuilder.append("" + ((Axle) conn).getendCapped());
			strBuilder.append(" Grabbing: ");
			strBuilder.append("" + ((Axle) conn).getgrabbing());
			strBuilder.append(" RequireGrabbing: ");
			strBuilder.append("" + ((Axle) conn).getrequireGrabbing());
		} else if (conn instanceof Hinge) {
			strBuilder.append("Hinge");
			strBuilder.append(" Type: ");
			strBuilder.append("Hinge_" + (conn.gettype() % 2 == 0 ? "f" : "m")
					+ "_" + conn.gettype() / 2);
		} else if (conn instanceof Fixed) {
			strBuilder.append("Fixed");
			strBuilder.append(" Type: ");
			strBuilder.append(FixedT.byValue(conn.gettype()));
		} else if (conn instanceof Slider) {
			strBuilder.append("Slider");
			strBuilder.append(" Type: ");
			strBuilder.append("Slider_" + (conn.gettype() % 2 == 0 ? "f" : "m")
					+ "_" + conn.gettype() / 2);
			strBuilder.append(" StartCapped: ");
			strBuilder.append("" + ((Slider) conn).getstartCapped());
			strBuilder.append(" EndCapped: ");
			strBuilder.append("" + ((Slider) conn).getendCapped());
			strBuilder.append(" Cylindrial: ");
			strBuilder.append("" + ((Slider) conn).getcylindrical());

		} else if (conn instanceof Ball) {
			strBuilder.append("Ball");
			strBuilder.append(" Type: ");
			strBuilder.append("Ball_" + (conn.gettype() % 2 == 0 ? "f" : "m")
					+ "_" + conn.gettype() / 2);
		} else {
			strBuilder.append(conn.getName());
			strBuilder.append(" ");
			strBuilder.append(conn.toString());
		}
		return strBuilder.toString();
	}
}
