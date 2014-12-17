package ConnectivityEditor.Window;

import java.util.ArrayList;
import java.util.Locale;

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

import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.CollisionConvexHull;
import Connectivity.CollisionCylinder;
import Connectivity.CollisionShape;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.ICustom2DField;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.AxleT;
import ConnectivityEditor.Connectivity.FixedT;
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
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.ConnectivityDidSelected);
	}
	
	public void terminate(){
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.ConnectivityDidAdded);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.ConnectivityDidRemoved);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.ConnectivityDidChanged);
		NotificationCenter.getInstance().removeSubscriber(this,
				NotificationMessageT.ConnectivityDidSelected);
	}

	private void drawTree() {
		if (fileInfoTreeComponent.isDisposed())
			return;
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
		if (RightPanel.isShowCollision) {
			// Collision
			TreeItem treeItem_Collision = new TreeItem(fileInfoTreeComponent,
					SWT.NONE);
			treeItem_Collision.setText("Collision");
			if (cEditor.getWorkingPart() != null
					&& cEditor.getWorkingPart().getCollisionShapeList() != null)
				for (CollisionShape collisionShape : cEditor.getWorkingPart()
						.getCollisionShapeList()) {
					if (collisionShape instanceof CollisionConvexHull)
						continue;
					treeItem = new TreeItem(treeItem_Collision, SWT.NONE);
					treeItem.setText(getDescription(collisionShape));
					treeItem.setData(collisionShape);
				}
			treeItem_Collision.setExpanded(true);
		}

		treeItem_Connectivity.setExpanded(true);
		fileInfoTreeComponent.setVisible(true);

		setSelection();
	}

	private boolean isUpdateCompleted = true;
	private boolean needOneMoreUpdate = false;

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {

		if (messageType == NotificationMessageT.ConnectivityDidSelected) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					setSelection();
				}
			});
			return;
		}
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
			for (TreeItem subItem : item.getItems()) {
				if (subItem.getData() == null)
					continue;
				else {
					conn = (Connectivity) subItem.getData();
					if (selected.contains(conn))
						selectedItems.add(subItem);
				}
			}
		}

		return selectedItems;
	}

	private String getDescription(Connectivity conn) {
		StringBuilder strBuilder = new StringBuilder();
		if (conn instanceof ICustom2DField) {
			ICustom2DField custom2d = (ICustom2DField) conn;
			if (custom2d instanceof Hole)
				strBuilder.append("Hole ");
			else
				strBuilder.append("Stud ");

			strBuilder.append(" Size: ");
			strBuilder.append(custom2d.getheight() / 2);
			strBuilder.append(",");
			strBuilder.append(custom2d.getwidth() / 2);
			strBuilder.append(String.format(Locale.US,
					" Position: %.2f, %.2f, %.2f", conn.getCurrentPos().getX(),
					conn.getCurrentPos().getY(), conn.getCurrentPos().getZ()));
		} else if (conn instanceof Axle) {
			strBuilder.append("Axle");
			strBuilder.append(" Type: ");
			strBuilder.append(AxleT.byValue(conn.gettype()));
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

		} else if (conn instanceof Ball) {
			strBuilder.append("Ball");
			strBuilder.append(" Type: ");
			strBuilder.append("Ball_" + (conn.gettype() % 2 == 0 ? "f" : "m")
					+ "_" + conn.gettype() / 2);
		} else if (conn instanceof CollisionBox) {
			strBuilder.append("Box");
		} else if (conn instanceof CollisionSphere) {
			strBuilder.append("Sphere");
		} else if (conn instanceof CollisionCylinder) {
			strBuilder.append("Cylinder");
		} else if (conn instanceof CollisionConvexHull) {
			strBuilder.append("ConvexHull");
		} else {
			strBuilder.append(conn.getName());
			strBuilder.append(" ");
			strBuilder.append(conn.toString());
		}
		strBuilder.append(String.format(Locale.US,
				" Position: %.2f, %.2f, %.2f", conn.getCurrentPos().getX(),
				conn.getCurrentPos().getY(), conn.getCurrentPos().getZ()));
		return strBuilder.toString();
	}
}
