package ConnectivityEditor.Window;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Builder.BuilderConfigurationManager;
import Builder.DragSelectionInfoRenderer;
import Builder.MainCamera;
import Builder.MetaInfoRenderer;
import Command.LDrawPart;
import Common.Size2;
import Common.Vector3f;
import ConnectivityEditor.Connectivity.ConnectivityRendererForConnectivityEditor;
import ConnectivityEditor.ConnectivityControlGuide.ConnectivityMovementGuideRenderer;
import LDraw.Support.LDrawGLRenderer;
import Resource.ResourceManager;

import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class ConnectivityEditorUI implements GLEventListener {

	FPSAnimator animator;
	LDrawGLRenderer glRenderer;
	MetaInfoRenderer metaInfoRenderer;
	ConnectivityMovementGuideRenderer connectivityMovementGuideRenderer;
	ConnectivityRendererForConnectivityEditor connectivityRenderer;
	ConnectivityBrowserUI browserUI;
	FileInfoTreeViewForConnectivityEditor fileInfoTreeView;
	// FileInfoUI fileInfoUI;
	GLCanvas glcanvas;
	MainCamera camera;
	ConnectivityEditor connectivityEditor = null;
	DragSelectionInfoRenderer brickSelectionInfoRenderer;
	StatusBarForConnectivityEditor statusBar;

	Shell shell;
	SashForm sashForm;
	Composite mainView;

	private static ConnectivityEditorUI _instance = null;

	public synchronized static ConnectivityEditorUI getInstance(String partName) {
		if (_instance == null){
			_instance = new ConnectivityEditorUI();
			_instance.open(partName);
		}else{
			ConnectivityEditor.getInstance().openFile(partName);
		}		
		
		return _instance;
	}
	
	private ConnectivityEditorUI() {
		this.connectivityEditor = ConnectivityEditor.getInstance();
	}

	private void open(String partName) {
		Display display = Display.getDefault();
		shell = new Shell(display);
		shell.setText(ConnectivityEditor.APP_NAME);
		
		BuilderConfigurationManager configurationManager = BuilderConfigurationManager.getInstance();
		
		shell.setSize((int) configurationManager.getWindowSize().getWidth(),
				(int) configurationManager.getWindowSize().getHeight());
		shell.setLocation(new Point((int) configurationManager
				.getWindowPosition().getX(), (int) configurationManager
				.getWindowPosition().getY()));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent event) {
				if (!connectivityEditor.checkChanged(shell)) {
					event.doit = false;
				}
				super.shellClosed(event);
			}
		});
		shell.setImage(ResourceManager.getInstance().getImage(display,
				"/Resource/Image/bl_new_icon.png"));
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				if (shell.getMaximized() || shell.getMinimized())
					return;

				fileInfoTreeView.terminate();
				animator.stop();
				statusBar.dispose();
			}
		});
				
		camera = connectivityEditor.getCamera();

		// generate window component
		generateComposite();
		shell.open();

		GlobalFocusManagerForConnectivityEditor.getInstance()
				.forceFocusToMainView();
		
		connectivityEditor.openFile(partName);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		browserUI.close();
		fileInfoTreeView.terminate();
		_instance=null;
	}

	private void generateComposite() {
		shell.setLayout(new GridLayout(1, false));
		// Menu bar
		new MenuHandlerForConnectivityEditor(connectivityEditor, shell)
				.generateMenu();

		// Toolbar
		new ToolBarHandlerForConnectivityEditor(connectivityEditor, shell)
				.generateToolbar();

		sashForm = new SashForm(shell, SWT.NONE);
		sashForm.setLocation(10, 40);
		sashForm.setSashWidth(5);
		sashForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));

		browserUI = new ConnectivityBrowserUI(sashForm, SWT.NONE);

		mainView = new Composite(sashForm, SWT.NONE);
		mainView.setLayout(new FillLayout());
		mainView.setBounds(10, 40, 728, 519);

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		glcanvas = new GLCanvas(mainView, SWT.NO_BACKGROUND, glcapabilities,
				null);
		initEventListener();
		animator = new FPSAnimator(glcanvas, 30);
		GlobalFocusManagerForConnectivityEditor.getInstance(glcanvas);

		// FileInfoTreeView
		fileInfoTreeView = new FileInfoTreeViewForConnectivityEditor(
				connectivityEditor);
		fileInfoTreeView.generateView(sashForm);

		setDragTarget(mainView);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		statusBar = new StatusBarForConnectivityEditor(
				shell, SWT.BORDER, connectivityEditor);
		statusBar.setLayoutData(gridData);

	}

	public void setDragTarget(Control control) {
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		DropTarget target = new DropTarget(control, DND.DROP_COPY
				| DND.DROP_MOVE | DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { fileTransfer, textTransfer });
		target.addDropListener(new DropTargetListener() {
			LDrawPart part;

			@Override
			public void dropAccept(DropTargetEvent event) {
			}

			@Override
			public void drop(DropTargetEvent event) {
				mainView.setFocus();
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				if (part != null) {
					Point coordinates = mainView.toControl(new Point(event.x,
							event.y));
					GlobalMousePositionForConnectivityEditor.getInstance()
							.setPos(coordinates.x, coordinates.y);

					Vector3f hitPos = connectivityEditor.getHittedPos(
							coordinates.x, coordinates.y, true);
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}

				if (fileTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				if (part != null) {
				}
				event.operations = DND.DROP_NONE;
				event.detail = DND.DROP_NONE;
			}

			@Override
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				for (TransferData data : event.dataTypes) {
					if (fileTransfer.isSupportedType(data)) {
						event.currentDataType = data;
						if (event.detail != DND.DROP_COPY) {
							event.detail = DND.DROP_NONE;
						}
						return;
					}
				}
				if (textTransfer.isSupportedType(event.currentDataType)) {
					event.detail = DND.DROP_NONE;
				}
			}
		});
	}

	@Override
	public void dispose(GLAutoDrawable glautodrawable) {
		animator.stop();
	}

	@Override
	public void init(GLAutoDrawable glautodrawable) {		
		GL2 gl2 = (GL2) glautodrawable.getGL(); // get the OpenGL graphics

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		// gl2.glBindVertexArray(0);

		// init gl related component
		glRenderer = connectivityEditor.getGLRenderer();
		
		metaInfoRenderer = connectivityEditor.getMetaInfoRenderer();
		connectivityMovementGuideRenderer = connectivityEditor
				.getConnMovementGuideRenderer();
		connectivityRenderer = connectivityEditor.getConnectivityRenderer();
		brickSelectionInfoRenderer = connectivityEditor
				.getConnSelectionInfoRenderer();

		connectivityEditor.initGLReleatedComponent(new Size2(800f, 600f), gl2);
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable glautodrawable) {
		GL2 gl2 = (GL2) glautodrawable.getGL(); // get the OpenGL graphics

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl2.glLoadIdentity(); // Reset The Modelview Matrix
		// long t = System.nanoTime();
		glRenderer.draw(gl2);
		// System.out.println((System.nanoTime()-t)/1000000.0f);
		metaInfoRenderer.draw(gl2);

		connectivityMovementGuideRenderer.draw(gl2);

		connectivityRenderer.draw(gl2);

		camera.tickle();

		brickSelectionInfoRenderer.draw(gl2);
	}

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		GL2 gl = (GL2) glautodrawable.getGL(); // get the OpenGL graphics
												// context

		if (height == 0)
			height = 1; // prevent divide by zero
		camera.setScreenSize(width, height);
		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		brickSelectionInfoRenderer.setCanvasSize(width, height);
	}

	private void initEventListener() {
		ConnectivityEditorEventHandler eventHandler = new ConnectivityEditorEventHandler(
				connectivityEditor);
		glcanvas.addMouseListener(eventHandler);
		glcanvas.addMouseMoveListener(eventHandler);
		glcanvas.addMouseWheelListener(eventHandler);
		glcanvas.addMouseTrackListener(eventHandler);
		// init keyboard event handler for test
		glcanvas.addKeyListener(eventHandler);
		glcanvas.addGLEventListener(this);
	}

}
