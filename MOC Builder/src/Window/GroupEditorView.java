package Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
import Builder.DirectiveSelectionManager;
import Builder.MainCamera;
import Command.CameraTransformCommand;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import Command.LDrawPart;
import Command.PartTypeT;
import Grouping.GroupingManager;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawMetaCommand;
import LDraw.Support.PartCache;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.LDrawDirectiveDidAdded;
import Notification.LDrawDirectiveDidChanged;
import Notification.LDrawDirectiveDidRemoved;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Resource.ResourceManager;
import UndoRedo.MOCBuilderUndoWrapper;

public class GroupEditorView implements ILDrawSubscriber, Runnable, Listener {
	private Tree groupTreeComponent = null;
	private TreeEditor editor;
	private MOCBuilder mocBuilder = null;
	private boolean isDraging;
	private HashMap<NotificationMessageT, Boolean> flags;
	private HashMap<NotificationMessageT, ArrayList<INotificationMessage>> messageListMap;
	private boolean isTerminate = false;
	private Image isExistImage;
	private Image ldrawPartImage;
	private Image ldrawCommentImage;
	private Image ldrawLSynthImage;
	private Image cameraTransformImage;
	private Image noConnectivityImage;
	private Image folderImage;
	private Display display;

	public GroupEditorView(MOCBuilder builder, Composite parent) {
		display = parent.getDisplay();
		this.mocBuilder = builder;

		flags = new HashMap<NotificationMessageT, Boolean>();
		messageListMap = new HashMap<NotificationMessageT, ArrayList<INotificationMessage>>();

		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidAdded);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidRemoved);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidChanged);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidSelected);
		NotificationCenter.getInstance().addSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChanged);

		flags.put(NotificationMessageT.LDrawDirectiveDidAdded, false);
		flags.put(NotificationMessageT.LDrawDirectiveDidRemoved, false);
		flags.put(NotificationMessageT.LDrawDirectiveDidChanged, false);
		flags.put(NotificationMessageT.LDrawDirectiveDidSelected, false);
		flags.put(NotificationMessageT.LDrawFileActiveModelDidChanged, false);

		messageListMap.put(NotificationMessageT.LDrawDirectiveDidAdded,
				new ArrayList<INotificationMessage>());
		messageListMap.put(NotificationMessageT.LDrawDirectiveDidRemoved,
				new ArrayList<INotificationMessage>());
		messageListMap.put(NotificationMessageT.LDrawDirectiveDidChanged,
				new ArrayList<INotificationMessage>());
		messageListMap.put(NotificationMessageT.LDrawDirectiveDidSelected,
				new ArrayList<INotificationMessage>());
		messageListMap.put(NotificationMessageT.LDrawFileActiveModelDidChanged,
				new ArrayList<INotificationMessage>());

		initTree();
		startUpdateViewThread();
		generateView(parent);
	}

	public void terminate() {
		this.isTerminate = true;
	}

	private void startUpdateViewThread() {
		new Thread(this).start();
	}

	protected ArrayList<INotificationMessage> getMessageList(
			NotificationMessageT messageType) {
		ArrayList<INotificationMessage> copy = new ArrayList<INotificationMessage>();
		synchronized (messageListMap) {
			ArrayList<INotificationMessage> original = messageListMap
					.get(messageType);
			copy.addAll(original);
			original.clear();
		}
		return copy;
	}

	protected void updateStep(final LDrawStep step) {
		display.asyncExec(new Runnable() {
			public void run() {
				if (mocBuilder == null)
					return;

				TreeItem treeItem_Step = null;

				for (TreeItem tempItem : groupTreeComponent.getItems()) {
					if (tempItem.getData() == step) {
						treeItem_Step = tempItem;
						break;
					}
				}

				if (treeItem_Step == null) {
					initTree();
					return;
				}

				groupTreeComponent.setVisible(false);
				treeItem_Step.removeAll();

				ArrayList<LDrawDirective> directives = step.subdirectives();
				TreeItem treeItem = null;
				for (LDrawDirective directive : directives) {
					if (LDrawPart.class.isInstance(directive)) {
						treeItem = new TreeItem(treeItem_Step, SWT.NONE);
						treeItem.setFont(FontManager.getInstance().getFont(
								"Arial", 11, SWT.NORMAL));
						String description = PartCache.getInstance()
								.getPartName(
										((LDrawPart) directive).displayName());
						if (description == null)
							description = ((LDrawPart) directive).displayName();
						else
							description = ((LDrawPart) directive).displayName()
									+ " : " + description;
						treeItem.setText(description
								+ " "
								+ ((LDrawPart) directive).getLDrawColor()
										.getColorCode());
						treeItem.setData(directive);
						LDrawPart part = (LDrawPart) directive;
						if (part.isPartDataExist() == false)
							treeItem.setImage(isExistImage);
						else if (part.isConnectivityInfoExist()) {
							treeItem.setImage(ldrawPartImage);
						} else {
							treeItem.setImage(noConnectivityImage);
						}
					} else if (directive instanceof LDrawMetaCommand) {
						LDrawMetaCommand metaCommand = (LDrawMetaCommand) directive;
						treeItem = new TreeItem(treeItem_Step, SWT.NONE);
						treeItem.setFont(FontManager.getInstance().getFont(
								"Arial", 11, SWT.NORMAL));

						String description = "";
						if (metaCommand.write().length() > 1)
							description = metaCommand.write().substring(2);

						treeItem.setText(description);
						treeItem.setData(metaCommand);
						treeItem.setImage(ldrawCommentImage);
					} else if (directive instanceof CameraTransformCommand) {
						CameraTransformCommand cCommand = (CameraTransformCommand) directive;
						treeItem = new TreeItem(treeItem_Step, SWT.NONE);
						treeItem.setFont(FontManager.getInstance().getFont(
								"Arial", 11, SWT.NORMAL));

						String description = String.format(Locale.US,
								"%s P: %.1f, %.1f, %.1f A: %.1f, %.1f D: %.1f",
								cCommand.description(), cCommand.getLookAt().x,
								cCommand.getLookAt().y, cCommand.getLookAt().z,
								cCommand.getRotation().getX(), cCommand
										.getRotation().getY(), cCommand
										.getDistanceToObject());

						treeItem.setText(description);
						treeItem.setData(cCommand);
						treeItem.setImage(cameraTransformImage);
					} else if (LDrawLSynth.class.isInstance(directive)) {
						LDrawLSynth lsynth = (LDrawLSynth) directive;
						treeItem = new TreeItem(treeItem_Step, SWT.NONE);
						treeItem.setFont(FontManager.getInstance().getFont(
								"Arial", 11, SWT.NORMAL));

						String description = lsynth.lsynthType() + " "
								+ lsynth.getLDrawColor().colorCode();
						for (LDrawDirective constraint : lsynth.subdirectives()) {
							if (constraint instanceof LDrawPart) {
								String description_constraint = PartCache
										.getInstance().getPartName(
												((LDrawPart) constraint)
														.displayName());
								if (description_constraint == null)
									description_constraint = ((LDrawPart) constraint)
											.displayName();
								else
									description_constraint = ((LDrawPart) constraint)
											.displayName()
											+ " : "
											+ description_constraint;

								TreeItem treeItem_constraint = new TreeItem(
										treeItem, SWT.NONE);
								treeItem_constraint.setFont(FontManager
										.getInstance().getFont("Arial", 11,
												SWT.NORMAL));

								treeItem_constraint
										.setText("constraint: "
												+ description_constraint
												+ " "
												+ ((LDrawPart) constraint)
														.getLDrawColor()
														.getColorCode());

								treeItem_constraint.setData(constraint);
							}
							treeItem.setImage(ldrawLSynthImage);
						}

						for (LDrawPart synthesized : lsynth.synthesizedParts()) {
							String description_constraint = PartCache
									.getInstance().getPartName(
											synthesized.displayName());
							if (description_constraint == null)
								description_constraint = synthesized
										.displayName();
							else
								description_constraint = synthesized
										.displayName()
										+ " : "
										+ description_constraint;

							TreeItem treeItem_synthsis = new TreeItem(treeItem,
									SWT.NONE);
							treeItem_synthsis.setFont(FontManager.getInstance()
									.getFont("Arial", 11, SWT.NORMAL));

							treeItem_synthsis.setText("synthesized: "
									+ description_constraint
									+ " "
									+ synthesized.getLDrawColor()
											.getColorCode());

							treeItem_synthsis.setData(synthesized);
							treeItem.setExpanded(true);
						}

						treeItem.setText(description);
						treeItem.setData(lsynth);
					}
					if (directive instanceof LDrawDrawableElement) {
						if (((LDrawDrawableElement) directive).isHidden())
							treeItem.setForeground(display
									.getSystemColor(SWT.COLOR_GRAY));
						else
							treeItem.setForeground(display
									.getSystemColor(SWT.COLOR_BLACK));
					}
				}
				treeItem_Step.setExpanded(true);
				groupTreeComponent.setVisible(true);
			}
		});
	}

	private void initTree() {
		if (mocBuilder != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (groupTreeComponent.isDisposed())
						return;
					groupTreeComponent.setVisible(false);
					groupTreeComponent.removeAll();

					int index = 0;
					for (LDrawDirective directive : mocBuilder
							.getWorkingLDrawFile().activeModel()
							.subdirectives()) {
						if (LDrawStep.class.isInstance(directive)) {
							index++;
							drawStep(index, (LDrawStep) directive);
						}
					}

					setSelection();
					setBold();
					groupTreeComponent.setVisible(true);
				}
			});
		}
	}

	private void drawStep(int index, LDrawStep step) {
		TreeItem treeItem_Step = new TreeItem(groupTreeComponent, SWT.NONE);
		String stepName = step.getStepName();
		if (stepName == null || "".equals(stepName)) {
			treeItem_Step.setText("Step " + index);
		} else {
			treeItem_Step.setText(stepName);
		}
		treeItem_Step.setData(step);
		treeItem_Step.setImage(folderImage);
		treeItem_Step.setFont(FontManager.getInstance().getFont("Arial", 12,
				SWT.NORMAL));
		updateStep(step);
	}

	private void setBold() {
		display.asyncExec(new Runnable() {
			public void run() {
				LDrawLSynth currentLSynth = DirectiveSelectionManager
						.getInstance().getLastSelectedLSynth();

				CameraTransformCommand currentCameraTransformCommand = DirectiveSelectionManager
						.getInstance().getLastSelectedCameraTransformCommand();
				LDrawStep currentStep = mocBuilder.getCurrentStep();

				LDrawDirective directive;
				for (TreeItem item_step : groupTreeComponent.getItems()) {
					directive = (LDrawDirective) item_step.getData();
					if (directive instanceof LDrawStep) {
						if (directive == currentStep) {
							FontData fontData = item_step.getFont()
									.getFontData()[0];
							item_step.setFont(FontManager.getInstance()
									.getFont(fontData.getName(),
											fontData.getHeight(), SWT.BOLD));
						} else {
							FontData fontData = item_step.getFont()
									.getFontData()[0];
							item_step.setFont(FontManager.getInstance()
									.getFont(fontData.getName(),
											fontData.getHeight(), SWT.NORMAL));
						}

						for (TreeItem item : item_step.getItems()) {
							if (item.getData() == currentLSynth
									|| item.getData() == currentCameraTransformCommand) {
								FontData fontData = item.getFont()
										.getFontData()[0];
								item.setFont(FontManager.getInstance().getFont(
										fontData.getName(),
										fontData.getHeight(), SWT.BOLD));
							} else {
								FontData fontData = item.getFont()
										.getFontData()[0];
								item.setFont(FontManager.getInstance().getFont(
										fontData.getName(),
										fontData.getHeight(), SWT.NORMAL));
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void receiveNotification(NotificationMessageT messageType,
			INotificationMessage msg) {
		flags.put(messageType, true);
		if (msg != null) {
			synchronized (messageListMap) {
				messageListMap.get(messageType).add(msg);
			}
		}
	}

	public void generateView(final Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginTop = -5;
		layout.marginLeft = -5;
		layout.marginRight = -5;
		layout.marginBottom = -5;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(layout);
		composite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Groups");
		label.setFont(FontManager.getInstance().getFont("Arial", 12, SWT.BOLD));
		label.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false));

		groupTreeComponent = new Tree(composite, SWT.MULTI);
		groupTreeComponent.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));
		groupTreeComponent.setMenu(createPopupMenu(parent.getShell()));

		isExistImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/not_exist.png");
		ldrawPartImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/brick.png");
		noConnectivityImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/brick_chain.png");
		ldrawLSynthImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/lsynth.png");
		ldrawCommentImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/comment.png");
		folderImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/folder_brick.png");
		cameraTransformImage = ResourceManager.getInstance().getImage(display,
				"/Resource/Image/Camera.png");
		groupTreeComponent.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				TreeItem item = groupTreeComponent.getItem(new Point(e.x, e.y));
				if (item == null) {
					DirectiveSelectionManager.getInstance().clearSelection();
					groupTreeComponent.setSelection(new TreeItem[0]);
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem[] selectedItems = groupTreeComponent.getSelection();
				if (selectedItems.length == 1) {
					Object object = selectedItems[0].getData();
					if (object instanceof LDrawStep) {
						DirectiveSelectionManager.getInstance()
								.clearSelection();
						LDrawStep step = (LDrawStep) object;
						for (LDrawDirective subDirective : step.subdirectives()) {
							DirectiveSelectionManager.getInstance()
									.addDirectiveToSelection(subDirective);
						}
					} else if (object instanceof LDrawLSynth) {
						DirectiveSelectionManager.getInstance()
								.clearSelection();
						LDrawLSynth lsynth = (LDrawLSynth) object;
						for (LDrawDirective subDirective : lsynth
								.subdirectives()) {
							DirectiveSelectionManager.getInstance()
									.addDirectiveToSelection(subDirective);
						}
					} else if (object instanceof CameraTransformCommand) {
						handleDoubleClickForCameraTransformCommand((CameraTransformCommand) object);

					}
					GlobalFocusManager.getInstance().forceFocusToMainView();
				}
			}
		});

		groupTreeComponent.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] selectedItems = groupTreeComponent.getSelection();
				DirectiveSelectionManager.getInstance().clearSelection();
				LDrawDirective directive = null;
				for (TreeItem selectedItem : selectedItems) {
					directive = (LDrawDirective) selectedItem.getData();
					DirectiveSelectionManager.getInstance()
							.addDirectiveToSelection(directive);
				}
				if (selectedItems.length == 1) {
					if (directive instanceof LDrawPart) {
						if (directive.enclosingDirective() instanceof LDrawLSynth == false) {

							LDrawPart part = (LDrawPart) directive;
							mocBuilder.getBrickMovementGuideRenderer()
									.setLDrawPart(part);
							MOCBuilder.getInstance().getCamera()
									.moveTo(part.position());
						}
					} else if (directive instanceof LDrawStep) {
						mocBuilder.setCurrentStep((LDrawStep) directive);
					}
					setBold();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		switch (BuilderConfigurationManager.getOSType()) {
		case Mac:
			groupTreeComponent.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent arg0) {

					switch (arg0.keyCode) {
					case SWT.CR:
						if (groupTreeComponent.getSelectionCount() == 1)
							if (groupTreeComponent.getSelection()[0].getData() instanceof LDrawStep)
								renameStep();
							else if (groupTreeComponent.getSelection()[0]
									.getData() instanceof LDrawMetaCommand)
								modifyComment();
						break;
					}
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
				}
			});
			break;
		default:
			groupTreeComponent.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent arg0) {

					switch (arg0.keyCode) {
					case SWT.F5:
						initTree();
						break;
					case SWT.F2:
						if (groupTreeComponent.getSelectionCount() == 1)
							renameStep();
						break;
					}
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
				}
			});
			break;
		}

		groupTreeComponent.addKeyListener(new BuilderEventHandler(mocBuilder));
		setDragAndDrop();

		editor = new TreeEditor(groupTreeComponent);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
	}

	protected void handleDoubleClickForCameraTransformCommand(
			CameraTransformCommand cCommand) {
		if (groupTreeComponent.getSelectionCount() != 1)
			return;

		MainCamera camera = mocBuilder.getCamera();
		camera.moveTo(cCommand.getLookAt());
		camera.setRotation(cCommand.getRotation());
		camera.setDistanceBetweenObjectToCamera(cCommand.getDistanceToObject());

		TreeItem selectedItem = groupTreeComponent.getSelection()[0];
		DirectiveSelectionManager.getInstance().clearSelection();
		boolean flag = false;
		for (TreeItem item : groupTreeComponent.getItems()) {
			for (TreeItem subItem : item.getItems()) {
				if (selectedItem == subItem) {
					flag = true;
					continue;
				}
				if (flag) {
					DirectiveSelectionManager.getInstance()
							.addDirectiveToSelection(
									(LDrawDirective) subItem.getData(), false);
				}
			}
		}
		mocBuilder.showAllStep();
		MOCBuilderUndoWrapper.getInstance().hideSelectedDirectives();
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

	}

	protected void modifyComment() {
		for (final TreeItem item : groupTreeComponent.getSelection()) {
			if (item.getData() instanceof LDrawMetaCommand) {
				final Text newEditor = new Text(groupTreeComponent, SWT.NONE);
				newEditor.setText(item.getText());
				newEditor.addListener(SWT.FocusOut, this);
				newEditor.addListener(SWT.KeyDown, this);
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item);
			}
		}
	}

	private Menu createPopupMenu(Decorations parent) {
		Menu menu = new Menu(parent, SWT.POP_UP);
		MenuItem addCommentItem = new MenuItem(menu, SWT.PUSH);
		addCommentItem.setText("New Comment");
		addCommentItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selectedItems = groupTreeComponent.getSelection();

				TextInputDialog dlg = new TextInputDialog(Display.getCurrent()
						.getActiveShell(), SWT.DIALOG_TRIM);
				dlg.setText("New Comment");
				String inputString = (String) dlg.open();
				if (inputString != null) {
					if (inputString.startsWith("0 ") == false)
						inputString = "0 " + inputString;

					LDrawMetaCommand comment = new LDrawMetaCommand();
					comment.setStringValue(inputString);

					if (selectedItems.length == 1) {
						Object object = selectedItems[0].getData();
						if (object instanceof LDrawStep) {
							MOCBuilderUndoWrapper.getInstance()
									.addDirectiveToWorkingFile(
											(LDrawStep) object, comment);
						} else {
							MOCBuilderUndoWrapper.getInstance()
									.addDirectiveToWorkingFile(comment);
						}
					} else {
						MOCBuilderUndoWrapper.getInstance()
								.addDirectiveToWorkingFile(comment);
					}
				}

				GlobalFocusManager.getInstance().forceFocusToMainView();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final MenuItem modifyCommentItem = new MenuItem(menu, SWT.PUSH);
		modifyCommentItem.setText("Modify Comment");
		modifyCommentItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyComment();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem addStepItem = new MenuItem(menu, SWT.PUSH);
		addStepItem.setText("New Group");
		addStepItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selectedItems = groupTreeComponent.getSelection();
				if (selectedItems.length == 1) {
					Object object = selectedItems[0].getData();
					if (object instanceof LDrawStep) {
						LDrawMPDModel model = mocBuilder.getWorkingLDrawFile()
								.activeModel();
						MOCBuilderUndoWrapper
								.getInstance()
								.addNewGroupToWorkingFile(
										model.indexOfDirective((LDrawStep) object));
					} else {
						MOCBuilderUndoWrapper.getInstance()
								.addNewGroupToWorkingFile();
					}
				} else {
					MOCBuilderUndoWrapper.getInstance()
							.addNewGroupToWorkingFile();
				}

				GlobalFocusManager.getInstance().forceFocusToMainView();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
		renameItem.setText("Rename Group");
		renameItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				renameStep();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem deleteItem = new MenuItem(menu, SWT.PUSH);
		deleteItem.setText("Delete");
		deleteItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MOCBuilderUndoWrapper.getInstance().removeSelectedDirective();
				GlobalFocusManager.getInstance().forceFocusToMainView();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem hideStep = new MenuItem(menu, SWT.PUSH);
		hideStep.setText("Hide");
		hideStep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleHide();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		MenuItem showStep = new MenuItem(menu, SWT.PUSH);
		showStep.setText("Show");
		showStep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleShow();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		MenuItem hideAllStep = new MenuItem(menu, SWT.PUSH);
		hideAllStep.setText("Hide All");
		hideAllStep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleHideAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		MenuItem showAllStep = new MenuItem(menu, SWT.PUSH);
		showAllStep.setText("Show All");
		showAllStep.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleShowAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem makeGroupItem = new MenuItem(menu, SWT.PUSH);
		makeGroupItem.setText("Group Selected Parts");
		makeGroupItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MOCBuilderUndoWrapper.getInstance().makeNewStepFromSeletion();
				GlobalFocusManager.getInstance().forceFocusToMainView();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		// final MenuItem seprateStep = new MenuItem(menu, SWT.PUSH);
		// seprateStep.setText("Separate Into Subgroups based on Connection");
		// seprateStep.addSelectionListener(new SelectionListener() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// handleSeparateStep();
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		//
		// }
		// });

		MenuItem mergeStep = new MenuItem(menu, SWT.PUSH);
		mergeStep.setText("Put All Into a Single Group");
		mergeStep.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMergeStep();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);
		final MenuItem makeModel = new MenuItem(menu, SWT.PUSH);
		makeModel.setText("Make Selected Parts Into a Submodel");
		makeModel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMakeASubmodel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		makeModel.setEnabled(false);

		final MenuItem extractModel = new MenuItem(menu, SWT.PUSH);
		extractModel.setText("Extract Parts From a Submodel");
		extractModel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleExtractPartsFromASubmodel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new MenuItem(menu, SWT.SEPARATOR);
		final MenuItem newCameraTransform = new MenuItem(menu, SWT.PUSH);
		newCameraTransform.setText("Insert A New Camera Position");
		newCameraTransform.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleInsertANewCameraTransform();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final MenuItem updateCameraTransform = new MenuItem(menu, SWT.PUSH);
		updateCameraTransform.setText("Overwrite Camera Position");
		updateCameraTransform.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleUpdateCameraTransform();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		menu.addMenuListener(new MenuListener() {
			@Override
			public void menuShown(MenuEvent e) {
				int count = groupTreeComponent.getSelectionCount();
				if (count > 1) {
					modifyCommentItem.setEnabled(false);
					// seprateStep.setEnabled(true);
					deleteItem.setEnabled(true);
					makeModel.setEnabled(true);
					updateCameraTransform.setEnabled(false);
					LDrawDirective directive;
					for (TreeItem item : groupTreeComponent.getSelection()) {
						directive = (LDrawDirective) item.getData();
						if (directive instanceof LDrawStep) {
							makeGroupItem.setEnabled(false);
							return;
						}
					}
					makeGroupItem.setEnabled(true);
					renameItem.setEnabled(false);
					updateCameraTransform.setEnabled(false);
				} else if (count == 1) {
					makeModel.setEnabled(true);
					updateCameraTransform.setEnabled(false);
					if (groupTreeComponent.getSelection()[0].getData() instanceof LDrawPart) {
						if (((LDrawPart) groupTreeComponent.getSelection()[0]
								.getData()).getCacheType() == PartTypeT.PartTypeSubmodel)
							updateCameraTransform.setEnabled(true);
					} else if (groupTreeComponent.getSelection()[0].getData() instanceof LDrawMetaCommand) {
						modifyCommentItem.setEnabled(true);
					} else if (groupTreeComponent.getSelection()[0].getData() instanceof CameraTransformCommand) {
						updateCameraTransform.setEnabled(true);
					}

					// seprateStep.setEnabled(true);
					deleteItem.setEnabled(true);
					makeGroupItem.setEnabled(false);
					if (groupTreeComponent.getSelection()[0].getData() instanceof LDrawStep)
						renameItem.setEnabled(true);
					else
						renameItem.setEnabled(false);
				} else {
					makeModel.setEnabled(false);
					updateCameraTransform.setEnabled(false);
					// seprateStep.setEnabled(false);
					deleteItem.setEnabled(false);
					makeGroupItem.setEnabled(false);
					renameItem.setEnabled(false);
					modifyCommentItem.setEnabled(false);
					updateCameraTransform.setEnabled(false);
				}
			}

			@Override
			public void menuHidden(MenuEvent e) {
			}
		});

		return menu;
	}

	protected void handleInsertANewCameraTransform() {
		TextInputDialog dlg = new TextInputDialog(Display.getCurrent()
				.getActiveShell(), SWT.DIALOG_TRIM);
		dlg.setText("New Camera Position");
		String inputString = (String) dlg.open();
		if (inputString != null) {
			String description = inputString;
			if (groupTreeComponent.getSelectionCount() == 1) {
				Object data = groupTreeComponent.getSelection()[0].getData();
				if (data instanceof LDrawStep) {
					MOCBuilderUndoWrapper.getInstance().addANewCameraTransform(
							(LDrawStep) data, description);
				} else {
					LDrawStep step = ((LDrawDirective) data).enclosingStep();
					int index = step.indexOfDirective((LDrawDirective) data);
					MOCBuilderUndoWrapper
							.getInstance()
							.insertANewCameraTransform(step, index, description);
				}

			} else {
				MOCBuilderUndoWrapper.getInstance().insertANewCameraTransform(
						null, 0, description);
			}

		}
	}

	protected void handleUpdateCameraTransform() {
		if (groupTreeComponent.getSelection().length != 1)
			return;
		TreeItem item = groupTreeComponent.getSelection()[0];
		CameraTransformCommand cCommand = (CameraTransformCommand) item
				.getData();

		MOCBuilderUndoWrapper.getInstance().updateCameraTransform(cCommand);
	}

	protected void handleMakeASubmodel() {
		for (final TreeItem item : groupTreeComponent.getSelection()) {
			if (item.getData() instanceof LDrawStep) {
				LDrawStep step = (LDrawStep) item.getData();
				for (LDrawDirective directive : step.subdirectives()) {
					if (directive instanceof LDrawPart) {
						DirectiveSelectionManager.getInstance()
								.addDirectiveToSelection((LDrawPart) directive);
					}
				}
			} else if (item.getData() instanceof LDrawPart) {
				LDrawPart part = (LDrawPart) item.getData();
				DirectiveSelectionManager.getInstance()
						.addDirectiveToSelection(part);
			}
		}
		MOCBuilderUndoWrapper.getInstance().makeASubmodelFromSelection();
	}

	protected void handleMergeStep() {
		GroupingManager.getInstance().mergeAll();
	}

	protected void handleSeparateStep() {
		for (final TreeItem item : groupTreeComponent.getSelection()) {
			if (item.getData() instanceof LDrawStep) {
				LDrawStep step = (LDrawStep) item.getData();
				GroupingManager.getInstance().doGrouping(step);
			}
		}

	}

	protected void handleExtractPartsFromASubmodel() {
		if (groupTreeComponent.getSelection().length != 1)
			return;
		TreeItem item = groupTreeComponent.getSelection()[0];
		LDrawPart part = (LDrawPart) item.getData();

		MOCBuilderUndoWrapper.getInstance()
				.extractDirectivesFromASubmodel(part);
	}

	protected void handleShowAll() {
		MOCBuilderUndoWrapper.getInstance().showAllStep();
	}

	protected void handleHideAll() {
		MOCBuilderUndoWrapper.getInstance().hideAllStep();
	}

	protected void handleHide() {
		MOCBuilderUndoWrapper.getInstance().hideSelectedDirectives();
	}

	protected void handleShow() {
		MOCBuilderUndoWrapper.getInstance().showSelectedDirectives();
	}

	void setDragAndDrop() {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE;
		DragSource source = new DragSource(groupTreeComponent, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				if (groupTreeComponent.getSelectionCount() == 0) {
					event.doit = false;
				} else {
					isDraging = true;
					event.image = null;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = "DRAG";
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				if (isDraging) {
					isDraging = false;
					initTree();
				}
			}
		});

		DropTarget target = new DropTarget(groupTreeComponent, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {

			@Override
			public void dropAccept(DropTargetEvent event) {
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (event.data == null
						|| groupTreeComponent.getSelectionCount() == 0) {
					event.detail = DND.DROP_NONE;
					isDraging = false;
					return;
				}

				TreeItem[] selectedItems = groupTreeComponent.getSelection();
				Object data;
				LDrawDirective targetDirective = null;
				LDrawContainer targetStep = null;
				boolean targetIsStep = false;

				if (event.item != null) {
					if (event.item.equals(selectedItems[0])) {
						event.detail = DND.DROP_NONE;
						isDraging = false;
						return;
					}
					data = event.item.getData();
					targetDirective = (LDrawDirective) data;
					if (data instanceof LDrawStep) {
						targetStep = (LDrawStep) data;
						targetIsStep = true;
					} else if (data instanceof LDrawContainer) {
						targetStep = targetDirective.enclosingStep();
					} else
						targetStep = targetDirective.enclosingStep();
				} else
					return;

				mocBuilder.setCurrentStep((LDrawStep) targetStep);

				data = selectedItems[0].getData();
				LDrawDirective srcDirective = null;
				LDrawContainer srcParent = null;

				boolean srcIsStep = false;
				boolean srcDirectiveHasSameStep = true;
				if (data instanceof LDrawStep) {
					srcParent = (LDrawStep) data;
					srcIsStep = true;
				}

				if (srcIsStep) {
					LDrawContainer parent = srcParent.enclosingDirective();
					int newIndex = parent.indexOfDirective(targetStep);

					HashMap<LDrawDirective, Integer> indexMap = new HashMap<LDrawDirective, Integer>();
					indexMap.put(srcParent, newIndex);
					MOCBuilderUndoWrapper.getInstance().changeDirectiveIndex(
							srcParent.enclosingDirective(), indexMap);

					GlobalFocusManager.getInstance().forceFocusToMainView();

				} else {
					LDrawStep tempStep = null;
					for (int i = 0; i < selectedItems.length; i++) {
						srcDirective = (LDrawDirective) (selectedItems[i]
								.getData());
						tempStep = srcDirective.enclosingStep();
						if (srcParent != null && srcParent != tempStep) {
							srcDirectiveHasSameStep = false;
							break;
						}
						srcParent = tempStep;
					}

					if (srcDirectiveHasSameStep && srcParent == targetStep) {
						HashMap<LDrawDirective, Integer> newIndexMap = new HashMap<LDrawDirective, Integer>();
						int newIndex;
						for (int i = 0; i < selectedItems.length; i++) {
							srcDirective = (LDrawDirective) selectedItems[i]
									.getData();
							if (targetIsStep)
								newIndex = i;
							else
								newIndex = targetStep
										.indexOfDirective(targetDirective)
										+ i
										+ 1;
							newIndexMap.put(srcDirective, newIndex);
						}
						MOCBuilderUndoWrapper.getInstance()
								.changeDirectiveIndex(srcParent, newIndexMap);
						GlobalFocusManager.getInstance().forceFocusToMainView();
						DirectiveSelectionManager.getInstance().clearSelection(
								false);
					} else {
						HashMap<LDrawDirective, LDrawContainer> oldParentMap = new HashMap<LDrawDirective, LDrawContainer>();
						for (int i = 0; i < selectedItems.length; i++) {
							srcDirective = (LDrawDirective) selectedItems[i]
									.getData();
							if (srcDirective instanceof LDrawStep)
								continue;

							srcParent = (LDrawStep) (srcDirective
									.enclosingDirective());
							oldParentMap.put(srcDirective, srcParent);
						}
						if (targetDirective instanceof LDrawContainer)
							MOCBuilderUndoWrapper.getInstance()
									.changeParentOfDirectiveAction(
											(LDrawContainer) targetDirective,
											oldParentMap);
						else
							MOCBuilderUndoWrapper.getInstance()
									.changeParentOfDirectiveAction(targetStep,
											oldParentMap);
						DirectiveSelectionManager.getInstance().clearSelection(
								false);
						GlobalFocusManager.getInstance().forceFocusToMainView();
					}
				}
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				if (isDraging) {
					event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
					if (event.item != null) {
						TreeItem item = (TreeItem) event.item;
						Object data = item.getData();
						if (groupTreeComponent.getSelection().length == 0)
							return;
						Object selectedData = groupTreeComponent.getSelection()[0]
								.getData();
						if (data.equals(selectedData)
								|| (selectedData instanceof LDrawStep && data instanceof LDrawPart)) {
							event.feedback = DND.FEEDBACK_NONE;
						} else {
							event.feedback |= DND.FEEDBACK_SELECT;
						}
					}
				} else {
					event.detail = DND.DROP_NONE;
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {

			}

			@Override
			public void dragLeave(DropTargetEvent event) {

			}

			@Override
			public void dragEnter(DropTargetEvent event) {

			}
		});
	}

	private void setSelection() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!groupTreeComponent.isDisposed()) {
					ArrayList<TreeItem> list = getSelectedItems(groupTreeComponent
							.getItems());
					final TreeItem[] items = new TreeItem[list.size()];
					list.toArray(items);
					groupTreeComponent.setSelection(items);
				}
			}
		});
	}

	private ArrayList<TreeItem> getSelectedItems(TreeItem[] parentItems) {
		ArrayList<TreeItem> selectedItems = new ArrayList<TreeItem>();
		ArrayList<LDrawDirective> selected = DirectiveSelectionManager
				.getInstance().getSelectedDirectiveList();
		LDrawDirective directive;

		for (TreeItem item : parentItems) {
			directive = (LDrawDirective) item.getData();
			if (directive == null)
				continue;
			else if (directive instanceof LDrawStep) {
				if (selected.contains(directive))
					selectedItems.add(item);
				selectedItems.addAll(getSelectedItems(item.getItems()));
			} else if (directive instanceof LDrawDirective
					&& selected.contains(directive)) {
				selectedItems.add(item);
			}
		}

		return selectedItems;
	}

	private void renameStep() {
		for (final TreeItem item : groupTreeComponent.getSelection()) {
			if (item.getData() instanceof LDrawStep) {
				final Text newEditor = new Text(groupTreeComponent, SWT.NONE);
				newEditor.setText(item.getText());
				newEditor.addListener(SWT.FocusOut, this);
				newEditor.addListener(SWT.KeyDown, this);
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item);
			}
		}
	}

	private boolean handleMessageForUpdateTree(
			HashMap<LDrawStep, Boolean> stepMapToBeUpdated,
			NotificationMessageT type, INotificationMessage msg) {
		LDrawDirective directive = null;
		LDrawStep parentStep = null;

		switch (type) {
		case LDrawDirectiveDidAdded:
			directive = ((LDrawDirectiveDidAdded) msg).getDirective();
			parentStep = directive.enclosingStep();
			break;
		case LDrawDirectiveDidRemoved:
			directive = ((LDrawDirectiveDidRemoved) msg).getDirective();
			LDrawDirective tempDirective = ((LDrawDirectiveDidRemoved) msg)
					.getParent();
			if (tempDirective != null && tempDirective instanceof LDrawStep)
				parentStep = (LDrawStep) tempDirective;
			else if (directive instanceof LDrawStep == false)
				System.out.println("Error!!!!: handleMessageForUpdateTree");
			break;
		case LDrawDirectiveDidChanged:
			directive = ((LDrawDirectiveDidChanged) msg).getDirective();
			parentStep = directive.enclosingStep();
			break;
		default:
			return false;
		}

		if (directive instanceof LDrawStep) {
			return true;
		} else {
			stepMapToBeUpdated.put(parentStep, true);
		}
		return false;
	}

	@Override
	public void run() {
		long lastRedrawTreeTime = System.currentTimeMillis();
		HashMap<LDrawStep, Boolean> stepMapToBeUpdated = new HashMap<LDrawStep, Boolean>();
		Boolean isAllTreeToBeUpdated = false;
		while (isTerminate == false) {
			stepMapToBeUpdated.clear();
			isAllTreeToBeUpdated = false;
			if (flags.get(NotificationMessageT.LDrawDirectiveDidSelected)) {
				flags.put(NotificationMessageT.LDrawDirectiveDidSelected, false);

				setSelection();
			}

			if (System.currentTimeMillis() - lastRedrawTreeTime > 100) {
				if (flags
						.get(NotificationMessageT.LDrawFileActiveModelDidChanged)) {
					flags.put(
							NotificationMessageT.LDrawFileActiveModelDidChanged,
							false);
					if (isAllTreeToBeUpdated == false)
						isAllTreeToBeUpdated = true;
				}

				if (flags.get(NotificationMessageT.LDrawDirectiveDidAdded)) {
					ArrayList<INotificationMessage> msgList = null;
					msgList = getMessageList(NotificationMessageT.LDrawDirectiveDidAdded);
					flags.put(NotificationMessageT.LDrawDirectiveDidAdded,
							false);
					if (isAllTreeToBeUpdated == false && msgList != null) {
						for (INotificationMessage msg : msgList) {
							isAllTreeToBeUpdated |= handleMessageForUpdateTree(
									stepMapToBeUpdated,
									NotificationMessageT.LDrawDirectiveDidAdded,
									msg);
							if (isAllTreeToBeUpdated)
								break;
						}
					}
				}

				if (flags.get(NotificationMessageT.LDrawDirectiveDidRemoved)) {
					ArrayList<INotificationMessage> msgList = null;
					msgList = getMessageList(NotificationMessageT.LDrawDirectiveDidRemoved);
					flags.put(NotificationMessageT.LDrawDirectiveDidRemoved,
							false);
					if (isAllTreeToBeUpdated == false && msgList != null) {
						for (INotificationMessage msg : msgList) {
							isAllTreeToBeUpdated |= handleMessageForUpdateTree(
									stepMapToBeUpdated,
									NotificationMessageT.LDrawDirectiveDidRemoved,
									msg);
							if (isAllTreeToBeUpdated)
								break;
						}
					}
				}
				if (flags.get(NotificationMessageT.LDrawDirectiveDidChanged)) {
					ArrayList<INotificationMessage> msgList = null;
					msgList = getMessageList(NotificationMessageT.LDrawDirectiveDidChanged);
					flags.put(NotificationMessageT.LDrawDirectiveDidChanged,
							false);
					if (isAllTreeToBeUpdated == false && msgList != null) {
						for (INotificationMessage msg : msgList) {
							isAllTreeToBeUpdated |= handleMessageForUpdateTree(
									stepMapToBeUpdated,
									NotificationMessageT.LDrawDirectiveDidChanged,
									msg);
							if (isAllTreeToBeUpdated)
								break;
						}
					}
				}

				if (isAllTreeToBeUpdated) {
					initTree();
					setSelection();
				} else {
					boolean needDraw = false;
					for (Entry<LDrawStep, Boolean> entry : stepMapToBeUpdated
							.entrySet()) {
						if (entry.getValue() == true) {
							updateStep(entry.getKey());
							needDraw = true;
						}
					}
					if (needDraw) {
						setSelection();
						setBold();
					}
				}

				lastRedrawTreeTime = System.currentTimeMillis();
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		Text text = (Text) editor.getEditor();
		if (event.keyCode == 0 || event.keyCode == SWT.CR) {
			String str = text.getText();
			TreeItem item = editor.getItem();
			if (item.getData() instanceof LDrawStep) {
				LDrawStep step = (LDrawStep) item.getData();
				MOCBuilderUndoWrapper.getInstance().renameGroup(step, str);
				if ("".equals(str)) {
					str = "Step "
							+ (step.enclosingDirective().indexOfDirective(step) + 1);
				}
				item.setText(str);
			} else if (item.getData() instanceof LDrawMetaCommand) {
				LDrawMetaCommand comment = (LDrawMetaCommand) item.getData();
				if (str != null) {
					if (str.startsWith("0 ") == false)
						str = "0 " + str;

					MOCBuilderUndoWrapper.getInstance().modifyComment(comment,
							str);
				}
				GlobalFocusManager.getInstance().forceFocusToMainView();
			}
			text.dispose();
		} else if (event.keyCode == SWT.ESC) {
			text.dispose();
		}
	}
}
