package OtherTools;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import Builder.BuilderConfigurationManager;
import Builder.ConnectivityRendererForBrickViewer;
import Builder.DirectiveSelectionManager;
import Builder.MainCamera;
import Command.CameraTransformCommand;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import Common.Size2;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawGLCameraScroller;
import LDraw.Support.LDrawGLRenderer;
import LDraw.Support.LDrawUtilities;
import Window.MOCBuilder;

import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.awt.Screenshot;

public class ScreenShotGenerator implements GLEventListener {
	LDrawGLRenderer glRenderer;
	GLCanvas glcanvas;
	MainCamera camera;
	MOCBuilder mocBuilder = null;
	Display display;
	Shell shell;
	Composite mainView;
	LDrawFile ldrawFile;

	public ScreenShotGenerator() {
		this.mocBuilder = MOCBuilder.getInstance();
	}

	public void open(Display display, LDrawFile file) {
		if (file == null)
			return;

		this.display = display;
		shell = new Shell(display);

		try {
			this.ldrawFile = (LDrawFile) file.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera = new MainCamera();
		// generate window component
		generateComposite();

		// fileInfoUI = new FileInfoUI(brickBuilder);
		// fileInfoUI.open();
		shell.open();

		int count = 0;
		for (LDrawDirective directive : ldrawFile.activeModel().subdirectives()) {
			LDrawStep step = (LDrawStep) directive;
			for (LDrawDirective subDirective : step.subdirectives()) {
				if (subDirective instanceof CameraTransformCommand) {
					takeScreenShot((CameraTransformCommand) subDirective);
					count++;
				}
			}
		}

		MessageBox mb = new MessageBox(shell);
		mb.setText("Screenshot");
		mb.setMessage(String.format(Locale.US, "Saved %d scenes.", count));
		mb.open();

		// while (!shell.isDisposed()) {
		// if (!display.readAndDispatch())
		// display.sleep();
		// }
		shell.dispose();
		// display.dispose();
	}

	private void generateComposite() {
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 1;
		shell.setLayout(gridlayout);

		shell.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WIDGET_LIGHT_SHADOW));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		mainView = new Composite(shell, SWT.BORDER);
		mainView.setLayout(new FillLayout());
		mainView.setBounds(10, 40, 728, 519);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainView.setLayoutData(gridData);

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		glcanvas = new GLCanvas(mainView, SWT.NO_BACKGROUND, glcapabilities,
				null);
		initEventListener();
	}

	@Override
	public void dispose(GLAutoDrawable glautodrawable) {
	}

	@Override
	public void init(GLAutoDrawable glautodrawable) {
		GL2 gl2 = (GL2) glautodrawable.getGL(); // get the OpenGL graphics
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		// gl2.glBindVertexArray(0);

		// init gl related component
		glRenderer = new LDrawGLRenderer();
		Size2 size = new Size2(800f, 600f);
		glRenderer.initWithBoundsCamera(size, camera);

		LDrawGLCameraScroller scroller = new LDrawGLCameraScroller();
		scroller.setDocumentSize(new Size2(800f, 600f));
		glRenderer.setDelegate(null, scroller);
		glRenderer.setLDrawDirective(ldrawFile);
		glRenderer.prepareOpenGL(gl2);
		// animator.start();
	}

	ConnectivityRendererForBrickViewer testRenderer = null;
	boolean takeScreenShotFlag = false;

	@Override
	public void display(GLAutoDrawable glautodrawable) {
		GL2 gl2 = (GL2) glautodrawable.getGL(); // get the OpenGL graphics

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl2.glLoadIdentity(); // Reset The Modelview Matrix
		camera.tickle();
		try {
			glRenderer.draw(gl2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (takeScreenShotFlag)
			takeScreenShot();
		takeScreenShotFlag = false;
	}

	private int width, height;

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		GL2 gl = (GL2) glautodrawable.getGL(); // get the OpenGL graphics
												// context
		if (height == 0)
			height = 1; // prevent divide by zero
		camera.setScreenSize(width, height);
		DirectiveSelectionManager.getInstance()
				.updateScreenProjectionVerticesMapAll();
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
		gl.glEnable(GL2.GL_COLOR_MATERIAL);

		this.width = width;
		this.height = height;
	}

	private void initEventListener() {
		glcanvas.addGLEventListener(this);
	}

	private String description;

	private void takeScreenShot(CameraTransformCommand cCommand) {
		boolean flag = false;
		for (LDrawDirective directive : ldrawFile.activeModel().subdirectives()) {
			LDrawStep step = (LDrawStep) directive;
			for (LDrawDirective subDirective : step.subdirectives()) {
				if (subDirective == cCommand)
					flag = true;
				
				if (subDirective instanceof LDrawDrawableElement) {
					((LDrawDrawableElement) subDirective).setHidden(flag);
				} else if (subDirective instanceof LDrawLSynth) {
					for (LDrawDirective lsynthDirective : ((LDrawLSynth) subDirective)
							.synthesizedParts())
						if (lsynthDirective instanceof LDrawDrawableElement) {
							((LDrawDrawableElement) lsynthDirective)
									.setHidden(flag);
						}
				}
			}
		}
		
		camera.moveTo(cCommand.getLookAt());
		camera.setRotation(cCommand.getRotation());
		camera.setDistanceBetweenObjectToCamera(cCommand.getDistanceToObject());
		
		takeScreenShotFlag = true;
		description = LDrawUtilities.excludePartName(ldrawFile.path()) + "_"
				+ cCommand.description();
		glcanvas.display();
	}

	private void takeScreenShot() {
		File file = new File(
				BuilderConfigurationManager.getDefaultDataDirectoryPath()
						+ "screenshot");
		if (file.exists() == false) {
			file.mkdir();
		}

		String tFileName = BuilderConfigurationManager
				.getDefaultDataDirectoryPath()
				+ "screenshot/"
				+ description
				+ ".png";
		// file = new File(tFileName);
		// if (file.exists()) {
		// tFileName = BuilderConfigurationManager
		// .getDefaultDataDirectoryPath()
		// + "screenshot/"
		// + description + "_" + System.currentTimeMillis() + ".png";
		// }
		System.out.println(tFileName);
		BufferedImage tScreenshot = Screenshot.readToBufferedImage(width,
				height);
		File tScreenCaptureImageFile = new File(tFileName);
		try {
			ImageIO.write(tScreenshot, "png", tScreenCaptureImageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
