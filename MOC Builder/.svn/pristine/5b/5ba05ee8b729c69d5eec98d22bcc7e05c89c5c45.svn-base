package Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import Builder.BuilderConfigurationManager;
import Command.LDrawColor;
import Command.LDrawColorT;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawMPDModel;
import LDraw.Support.ColorLibrary;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Resource.ResourceManager;
import UndoRedo.MOCBuilderUndoWrapper;

public class ModelListView implements ILDrawSubscriber, Listener,
		DragSourceListener {
	private Display display;
	private TreeEditor editor;

	private Tree treeComponent = null;
	private Image folderImage;

	public ModelListView(Composite parent, int style) {
		display = parent.getDisplay();

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChanged);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawModelDidChanged);

		generateView(parent);
		drawTree();

	}

	private void drawTree() {
		display.asyncExec(new Runnable() {
			public void run() {
				MOCBuilder builder = MOCBuilder.getInstance();
				LDrawFile file = builder.getWorkingLDrawFile();
				if (file == null)
					return;
				if (!treeComponent.isDisposed()) {
					treeComponent.setVisible(false);
					treeComponent.removeAll();

					LDrawMPDModel mainModel = null;
					try {
						mainModel = file.submodels().get(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (mainModel == null)
						return;

					TreeItem mainModelTreeItem = new TreeItem(treeComponent,
							SWT.NONE);
					if (mainModel.modelName() == null
							|| mainModel.modelName().equals(""))
						mainModelTreeItem.setText("Untitled.ldr");
					else
						mainModelTreeItem.setText(mainModel.modelName());
					mainModelTreeItem.setData(mainModel);
					mainModelTreeItem.setImage(folderImage);
					mainModelTreeItem.setFont(FontManager.getInstance()
							.getFont("Arial", 11, SWT.NORMAL));

					TreeItem activeItem = mainModelTreeItem;
					for (LDrawMPDModel model : file.submodels()) {
						if (model == mainModel)
							continue;
						TreeItem treeItem = new TreeItem(mainModelTreeItem,
								SWT.NONE);
						treeItem.setText(model.modelName().trim());
						treeItem.setData(model);
						treeItem.setFont(FontManager.getInstance().getFont(
								"Arial", 11, SWT.NORMAL));
						if (MOCBuilder.getInstance().getActiveModel() == model)
							activeItem = treeItem;
					}
					setBold(activeItem);
					mainModelTreeItem.setExpanded(true);
					treeComponent.setVisible(true);
				}
			}
		});
	}

	private void generateView(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginTop = -5;
		layout.marginLeft = -5;
		layout.marginRight = -5;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(layout);
		composite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Submodels");
		label.setFont(FontManager.getInstance().getFont("Arial", 12, SWT.BOLD));

		treeComponent = new Tree(composite, SWT.MULTI);
		treeComponent.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		treeComponent.setMenu(createPopupMenu(parent.getShell()));

		folderImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/folder_brick.png");

		treeComponent.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem[] selectedItems = treeComponent.getSelection();
				if (selectedItems.length == 1) {
					Object object = selectedItems[0].getData();
					if (object instanceof LDrawMPDModel == false)
						return;

					MOCBuilder.getInstance().changeActiveModel(
							(LDrawMPDModel) object);

					setBold(selectedItems[0]);

				}
			}
		});

		treeComponent.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				switch (BuilderConfigurationManager.getOSType()) {
				case Mac:
					switch (arg0.keyCode) {
					case SWT.BS:
						handleDelete();
						break;
					case SWT.CR:
						handleRename();
						break;
					}
					break;
				default:
					switch (arg0.keyCode) {
					case SWT.DEL:
						handleDelete();
						break;
					case SWT.F2:
						handleRename();
						break;
					}
					break;
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}
		});

		DragSource source = new DragSource(treeComponent, DND.DROP_COPY
				| DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		source.addDragListener(this);

		editor = new TreeEditor(treeComponent);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
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

	private Menu createPopupMenu(Decorations parent) {
		Menu menu = new Menu(parent, SWT.POP_UP);
		final MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
		renameItem.setText("Rename");
		renameItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRename();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		final MenuItem deleteItem = new MenuItem(menu, SWT.PUSH);
		deleteItem.setText("Delete");
		deleteItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDelete();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		menu.addMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent e) {
				int count = treeComponent.getSelectionCount();
				if (count > 1) {
					renameItem.setEnabled(false);
					deleteItem.setEnabled(true);
				} else if (count == 1) {
					renameItem.setEnabled(true);
					deleteItem.setEnabled(true);
				} else {
					renameItem.setEnabled(false);
					deleteItem.setEnabled(false);
				}
			}

			@Override
			public void menuHidden(MenuEvent e) {
			}
		});

		return menu;
	}

	private void renameModel(TreeItem item, LDrawMPDModel model) {
		final Text newEditor = new Text(treeComponent, SWT.NONE);
		newEditor.setText(model.modelName());
		newEditor.addListener(SWT.FocusOut, this);
		newEditor.addListener(SWT.KeyDown, this);
		newEditor.selectAll();
		newEditor.setFocus();
		editor.setEditor(newEditor, item);
	}

	@Override
	public void handleEvent(Event event) {
		Text text = (Text) editor.getEditor();
		if (event.keyCode == 0 || event.keyCode == SWT.CR) {
			String name = text.getText();
			TreeItem item = editor.getItem();
			LDrawMPDModel model = (LDrawMPDModel) item.getData();
			MOCBuilderUndoWrapper.getInstance().renameModel(model, name);
			item.setText(name);
			text.dispose();
		} else if (event.keyCode == SWT.ESC) {
			text.dispose();
		}

	}

	private void setBold(TreeItem selectedItem) {
		for (TreeItem item : treeComponent.getItems()) {
			if (item.equals(selectedItem)) {
				FontData fontData = item.getFont().getFontData()[0];
				item.setFont(FontManager.getInstance().getFont(
						fontData.getName(), fontData.getHeight(), SWT.BOLD));
			} else {
				FontData fontData = item.getFont().getFontData()[0];
				item.setFont(FontManager.getInstance().getFont(
						fontData.getName(), fontData.getHeight(), SWT.NORMAL));

				if (item.getItems().length == 0)
					return;
				for (TreeItem item2 : item.getItems()) {
					if (item2.equals(selectedItem)) {
						fontData = item2.getFont().getFontData()[0];
						item2.setFont(FontManager.getInstance().getFont(
								fontData.getName(), fontData.getHeight(),
								SWT.BOLD));
					} else {
						fontData = item2.getFont().getFontData()[0];
						item2.setFont(FontManager.getInstance().getFont(
								fontData.getName(), fontData.getHeight(),
								SWT.NORMAL));
					}
				}

			}
		}
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		if (treeComponent.getSelection().length != 1)
			return;

		LDrawColor color = ColorLibrary.sharedColorLibrary().colorForCode(
				LDrawColorT.LDrawCurrentColor);

		DNDTransfer.getInstance().setColor(color);
		event.image = null;

		Object object = null;
		Control control = ((DragSource) event.getSource()).getControl();
		if (control.equals(treeComponent)) {
			object = treeComponent.getSelection()[0].getData();
		}
		if (object == null) {
			return;
		} else {
			DNDTransfer.getInstance().setData(
					((LDrawMPDModel) object).modelName());
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = DNDTransfer.getInstance().getData();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		DNDTransfer.getInstance().end();
	}

	public void handleDelete() {
		TreeItem[] selectedItems = treeComponent.getSelection();
		if (selectedItems.length == 1) {
			Object object = selectedItems[0].getData();
			if (object instanceof LDrawMPDModel) {
				LDrawMPDModel model = (LDrawMPDModel) object;
				MOCBuilderUndoWrapper.getInstance().removeModel(model);
			}
		}
		GlobalFocusManager.getInstance().forceFocusToMainView();
	}

	public void handleRename() {
		TreeItem[] selectedItems = treeComponent.getSelection();
		if (selectedItems.length == 1) {
			Object object = selectedItems[0].getData();
			if (object instanceof LDrawMPDModel) {
				LDrawMPDModel model = (LDrawMPDModel) object;
				renameModel(selectedItems[0], model);
			}
		}
	}
}
