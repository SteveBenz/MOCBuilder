package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Gear;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import Connectivity.Rail;
import Connectivity.Slider;
import ConnectivityEditor.Connectivity.AxleT;
import ConnectivityEditor.Connectivity.FixedT;
import ConnectivityEditor.ConnectivityControlGuide.ConnectivityMovementGuideRenderer;

public class FileInfoTreeViewForConnectivityEditor implements Runnable {
	private Tree fileInfoTreeComponent = null;
	private ConnectivityEditor connectivityEditor = null;
	private boolean isTerminate = false;

	public FileInfoTreeViewForConnectivityEditor(ConnectivityEditor builder) {

		this.connectivityEditor = builder;
		startUpdateViewThread();
	}

	public void terminate() {
		this.isTerminate = true;
	}

	private void startUpdateViewThread() {
		new Thread(this).start();
	}

	private void drawInfo() {
		fileInfoTreeComponent.setRedraw(false);
		fileInfoTreeComponent.setVisible(false);
		fileInfoTreeComponent.removeAll();
		// connectivity
		TreeItem treeItem_Connectivity = new TreeItem(fileInfoTreeComponent,
				SWT.NONE);
		treeItem_Connectivity.setText("Connectivity");
		TreeItem treeItem;
		if (connectivityEditor.getWorkingPart() != null
				&& connectivityEditor.getWorkingPart().getConnectivityList() != null)
			for (Connectivity conn : connectivityEditor.getWorkingPart()
					.getConnectivityList()) {
				treeItem = new TreeItem(treeItem_Connectivity, SWT.NONE);
				treeItem.setText(getDescription(conn));
				treeItem.setData(conn);

			}
		// Collision
		TreeItem treeItem_Collision = new TreeItem(fileInfoTreeComponent,
				SWT.NONE);
		treeItem_Collision.setText("Collision");
		if (connectivityEditor.getWorkingPart() != null
				&& connectivityEditor.getWorkingPart().getCollisionBoxList() != null)
			for (CollisionBox collisionBox : connectivityEditor
					.getWorkingPart().getCollisionBoxList()) {
				treeItem = new TreeItem(treeItem_Collision, SWT.NONE);
				treeItem.setText(collisionBox.toString());
				treeItem.setData(collisionBox);

			}

		treeItem_Connectivity.setExpanded(true);
		treeItem_Collision.setExpanded(true);
		fileInfoTreeComponent.setRedraw(true);
		fileInfoTreeComponent.setVisible(true);
	}

	private void setBold(TreeItem selectedItem) {
		Display display = fileInfoTreeComponent.getDisplay();
		FontData datas[] = fileInfoTreeComponent.getFont().getFontData();
		for (FontData data : datas) {
			data.setStyle(SWT.NORMAL);
		}
		Font normalFont = new Font(display, datas);
		for (FontData data : datas) {
			data.setStyle(SWT.BOLD);
		}
		Font boldFont = new Font(display, datas);
		for (TreeItem item : fileInfoTreeComponent.getItems()) {
			if (item.equals(selectedItem)) {
				item.setFont(boldFont);
			} else {
				item.setFont(normalFont);
			}
		}
	}

	public void generateView(final Composite parent) {
		fileInfoTreeComponent = new Tree(parent, SWT.MULTI);
		fileInfoTreeComponent.setLayout(new GridLayout());
		fileInfoTreeComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));

		fileInfoTreeComponent.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				TreeItem item = fileInfoTreeComponent.getItem(new Point(e.x,
						e.y));
				if (item == null) {
					fileInfoTreeComponent.setSelection(new TreeItem[0]);

				} else
					setBold(item);

				handleItemSelection(item);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		fileInfoTreeComponent.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println(arg0.keyCode);
				switch (arg0.keyCode) {
				case SWT.DEL:
					for (TreeItem item : fileInfoTreeComponent.getSelection()) {
						if (item.getData() instanceof CollisionBox) {
							connectivityEditor.getWorkingPart()
									.getCollisionBoxList()
									.remove(item.getData());
						} else {
							connectivityEditor.getWorkingPart()
									.getConnectivityList()
									.remove(item.getData());
						}
					}
					break;
				case SWT.F5:
					drawInfo();
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}
		});
	}

	protected void handleItemSelection(TreeItem item) {
		if (item == null)
			return;
		ConnectivitySelectionManager.getInstance().clearSelection();
		if (item.getData() instanceof Connectivity) {
			Connectivity conn = (Connectivity) item.getData();
			ConnectivityMovementGuideRenderer.getInstance().setConn(conn);
			ConnectivitySelectionManager.getInstance().clearSelection();
			ConnectivitySelectionManager.getInstance()
					.addConnectivityToSelection(conn);
		}

		GlobalFocusManagerForConnectivityEditor.getInstance()
				.forceFocusToMainView();
	}

	@Override
	public void run() {
		boolean needRedraw = false;
		String prevContents = "";
		String currentContents = "";
		while (isTerminate == false) {
			currentContents = "";
			if (connectivityEditor.getWorkingPart() != null
					&& connectivityEditor.getWorkingPart()
							.getConnectivityList() != null)
				for (Connectivity conn : connectivityEditor.getWorkingPart()
						.getConnectivityList()) {
					currentContents += conn.toString();

				}
			// Collision
			if (connectivityEditor.getWorkingPart() != null
					&& connectivityEditor.getWorkingPart()
							.getCollisionBoxList() != null)
				for (CollisionBox collisionBox : connectivityEditor
						.getWorkingPart().getCollisionBoxList()) {
					currentContents += collisionBox.toString();
				}

			if (prevContents.equals(currentContents) == false)
				needRedraw = true;

			if (needRedraw == true) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						drawInfo();
					}

				});
				needRedraw = false;
				prevContents = currentContents;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			strBuilder.append(custom2d.getheight()/2);
			strBuilder.append(",");
			strBuilder.append(custom2d.getwidth()/2);
			strBuilder.append(" MatrixInfo: ");
			MatrixItem[][] matrixItem = custom2d.getMatrixItem();
			
			for (int column = 0; column < custom2d.getheight() + 1; column++) {
				for (int row = 0; row < custom2d.getwidth() + 1; row++) {
					strBuilder.append(matrixItem[column][row].getAltitude() + ":"
							+ matrixItem[column][row].getOccupiedArea() + ":"
							+ matrixItem[column][row].getShape() + ",");
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
			
		}else if(conn instanceof Ball){
			strBuilder.append("Ball");
			strBuilder.append(" Type: ");
			strBuilder.append("Ball_" + (conn.gettype() % 2 == 0 ? "f" : "m")
					+ "_" + conn.gettype() / 2);
		}else {
			strBuilder.append(conn.getName());
			strBuilder.append(" ");
			strBuilder.append(conn.toString());
		}		
		return strBuilder.toString();
	}
}
