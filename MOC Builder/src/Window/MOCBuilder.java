package Window;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipFile;

import javax.media.opengl.GL2;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import BrickControlGuide.BrickMovementGuideRenderer;
import Builder.AutoSaveManager;
import Builder.BuilderConfigurationManager;
import Builder.DirectiveSelectionManager;
import Builder.DragSelectionInfoRenderer;
import Builder.MainCamera;
import Builder.MetaInfoRenderer;
import Command.LDrawColor;
import Command.LDrawDrawableElement;
import Command.LDrawLSynth;
import Command.LDrawLSynthDirective;
import Command.LDrawPart;
import Command.PartTypeT;
import Common.Box3;
import Common.Matrix4;
import Common.Ray3;
import Common.Size2;
import Common.Vector3f;
import Connectivity.Direction6T;
import Connectivity.GlobalConnectivityManager;
import Connectivity.GlobalConnectivityRenderer;
import Exports.UpdateManager;
import Grouping.GroupingManager;
import LDD.bricks.Part;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawModel;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawGLCameraScroller;
import LDraw.Support.LDrawGLRenderer;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;
import LDraw.Support.PartCache;
import LDraw.Support.type.LDrawGridTypeT;
import LddToLdr.Ldd2Ldr;
import Notification.LDrawDirectiveDidAdded;
import Notification.LDrawDirectiveDidChanged;
import Notification.LDrawDirectiveDidRemoved;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Resource.SoundEffectManager;
import UndoRedo.LDrawUndoRedoManager;

public class MOCBuilder {
	public final static String APP_NAME = "MOC Builder";
	private LDrawGLRenderer glRenderer;
	private MetaInfoRenderer metaInfoRenderer;
	private BrickMovementGuideRenderer brickMovementGuideRenderer;
	private GlobalConnectivityRenderer connectivityRenderer;
	private DragSelectionInfoRenderer brickSelectionInfoRenderer;
	private GlobalConnectivityManager globalConnectivityManager;
	private MainCamera camera;
	private LDrawStep currentStep = null;
	private boolean isChanged;

	private LDrawUndoRedoManager undoRedoManager;

	private LDrawFile ldrawFile;

	private BuilderConfigurationManager configurationManager = null;
	private DirectiveSelectionManager directiveSelectionManager = null;

	private static MOCBuilder mocBuilder;

	private Shell shell;

	public static synchronized MOCBuilder getInstance() {
		if (mocBuilder == null) {
			mocBuilder = new MOCBuilder();
		}
		return mocBuilder;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public static void main(String[] args) {
		Display.setAppName(APP_NAME);
		Display display = Display.getDefault();
		mocBuilder = new MOCBuilder();
		MOCBuilderUI brickBuilderUI = new MOCBuilderUI(mocBuilder);
		brickBuilderUI.open(display);
	}

	long average = 0;

	private MOCBuilder() {
		UpdateManager.getInstance();
		undoRedoManager = LDrawUndoRedoManager.getInstance();
		configurationManager = BuilderConfigurationManager.getInstance();
		directiveSelectionManager = DirectiveSelectionManager.getInstance();
		globalConnectivityManager = GlobalConnectivityManager.getInstance();
		SoundEffectManager.getInstance();
		// UpdateManager.getInstance();
		newLDrawFile();
		initRenderer();

		camera.setDistanceBetweenObjectToCamera(400);
		AutoSaveManager.getInstance();
	}

	public void changeStepIndex(LDrawStep step, int newIndex) {
		LDrawModel model = ldrawFile.activeModel();
		model.removeDirective(step);
		model.insertDirective(step, newIndex);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(step));
	}

	public void insertDirectiveToWorkingFile(int index, LDrawContainer step,
			LDrawDirective directive) {
		insertDirectiveToWorkingFile(index, step, directive, true);
	}

	public void addDirectiveToWorkingFile(LDrawDirective directive) {
		addDirectiveToWorkingFile(directive, true);
	}

	public void addDirectiveToWorkingFile(LDrawDirective directive,
			boolean updateConnectivityManager) {
		if (LDrawStep.class.isInstance(directive)) {
			LDrawStep step = (LDrawStep) directive;
			ldrawFile.activeModel().addStep(step);

			NotificationCenter.getInstance()
					.postNotification(
							NotificationMessageT.LDrawDirectiveDidAdded,
							new LDrawDirectiveDidAdded(step
									.enclosingDirective(), step));

			if (updateConnectivityManager) {
				for (LDrawDirective tempDirective : step.subdirectives()) {
					if (tempDirective instanceof LDrawPart)
						globalConnectivityManager.addPart(
								((LDrawPart) tempDirective),
								updateConnectivityManager);
					if (tempDirective instanceof LDrawPart
							|| tempDirective instanceof LDrawLSynth)
						directiveSelectionManager.addDirective(tempDirective,
								updateConnectivityManager);
				}
			}
		} else if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;

			String description = PartCache.getInstance().getPartName(
					part.getDisplayName());
			if (description != null
					&& description.toLowerCase().startsWith("lsynth")
					&& directiveSelectionManager.getLastSelectedLSynth() != null) {
				addDirectiveToWorkingFile(
						directiveSelectionManager.getLastSelectedLSynth(),
						directive, updateConnectivityManager);
			} else {
				if (currentStep == null)
					currentStep = ldrawFile.activeModel().visibleStep();

				addDirectiveToWorkingFile(currentStep, directive,
						updateConnectivityManager);
			}
		} else {
			if (currentStep == null)
				currentStep = ldrawFile.activeModel().visibleStep();

			addDirectiveToWorkingFile(currentStep, directive,
					updateConnectivityManager);
		}

	}

	public void addDirectiveToWorkingFile(LDrawStep step,
			LDrawDirective directive) {
		addDirectiveToWorkingFile(step, directive, true);
	}

	public void insertDirectiveToWorkingFile(int index,
			LDrawContainer container, LDrawDirective directive,
			boolean updateConnectivityManager) {
		if (container == null || container.subdirectives().contains(directive)) {
			return;
		}
		container.insertDirective(directive, index);

		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;

			globalConnectivityManager.addPart(part, updateConnectivityManager);

		} else if (LDrawStep.class.isInstance(directive)) {
			for (LDrawDirective subDirective : ((LDrawStep) directive)
					.subdirectives())
				if (LDrawPart.class.isInstance(subDirective))
					globalConnectivityManager.addPart((LDrawPart) subDirective,
							updateConnectivityManager);
		}
		updateGridRange();
		directiveSelectionManager.addDirective(directive,
				updateConnectivityManager);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(directive.enclosingDirective(),
						directive));
	}

	public void addDirectiveToWorkingFile(LDrawContainer container,
			LDrawDirective directive, boolean updateConnectivityManager) {
		if (container == null || container.subdirectives().contains(directive)) {
			return;
		}
		container.addDirective(directive);

		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;
			globalConnectivityManager.addPart(part, updateConnectivityManager);
		}

		directiveSelectionManager.addDirective(directive,
				updateConnectivityManager);

		updateGridRange();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(directive.enclosingDirective(),
						directive));

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void addDirectiveToWorkingFileForDragAndDrop(LDrawDirective directive) {
		if (currentStep == null)
			currentStep = ldrawFile.activeModel().visibleStep();
		currentStep.addDirective(directive);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public LDrawStep newStepToWorkingFile() {
		LDrawStep step = ldrawFile.activeModel().addStep();
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(step.enclosingDirective(), step));
		setCurrentStep(step);

		return step;
	}

	public void insertStepToWorkingFileAt(int index, LDrawStep step) {
		ldrawFile.activeModel().insertDirective(step, index);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(step.enclosingDirective(), step));
	}

	public LDrawStep addStepToWorkingFileAt(int index) {
		LDrawStep step = ldrawFile.activeModel().addStep(index);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(step.enclosingDirective(), step));
		return step;
	}

	public BrickMovementGuideRenderer getBrickMovementGuideRenderer() {
		return brickMovementGuideRenderer;
	}

	public DragSelectionInfoRenderer getBrickSelectionInfoRenderer() {
		return brickSelectionInfoRenderer;
	}

	public MainCamera getCamera() {
		return camera;
	}

	public GlobalConnectivityRenderer getConnectivityRenderer() {
		return this.connectivityRenderer;
	}

	public LDrawStep getCurrentStep() {
		if (currentStep == null) {
			return ldrawFile.activeModel().visibleStep();
		} else {
			return currentStep;
		}
	}

	public LDrawGLRenderer getGLRenderer() {
		return glRenderer;
	}

	private Ray3 prevRay = null;
	private Vector3f prevHitPos;
	private LDrawPart prevHitPart;
	private boolean prevIgnoreSelected;
	private float lastHittedDistance;

	public LDrawPart getHittedPart(float screenX, float screenY,
			boolean ignoreSelectedParts) {
		updateHittedInfoCache(screenX, screenY, ignoreSelectedParts);

		return prevHitPart;
	}

	public Vector3f getHittedPos(float screenX, float screenY,
			boolean ignoreSelectedParts) {
		updateHittedInfoCache(screenX, screenY, ignoreSelectedParts);

		return prevHitPos;
	}

	public float getLastHittedDistance() {
		return lastHittedDistance;
	}

	private void updateHittedInfoCache(float screenX, float screenY,
			boolean ignoreSelectedParts) {
		Ray3 ray = camera.getRay(screenX, screenY);

		if (ray.equals(prevRay) && prevIgnoreSelected == ignoreSelectedParts) {
			return;
		}

		Matrix4 transform = new Matrix4();
		transform.setIdentity();
		HashMap<LDrawDirective, Float> hits = new HashMap<LDrawDirective, Float>();
		ldrawFile.hitTest(ray, transform, null, hits);
		float dist = 200000.0f;
		Vector3f hitPos = null;
		LDrawPart hittedPart = null;
		for (Entry<LDrawDirective, Float> entry : hits.entrySet()) {
			if (entry.getKey() instanceof LDrawPart == false)
				continue;

			LDrawPart part = (LDrawPart) entry.getKey();
			Float distance = entry.getValue();
			if (ignoreSelectedParts == true && part.isDraggingPart())
				continue;
			if (dist > distance.floatValue()) {
				dist = distance.floatValue();
				hitPos = ray.getOrigin()
						.add(ray.getDirection().scale(distance));
				hittedPart = part;
			}
			// System.out.println("part " + part.displayName() + " distance"
			// + distance +
			// "Position: "+ray.getOrigin().add(ray.getDirection().scale(distance)));
		}

		if (hitPos == null)
			hitPos = camera.screenToWorldXZ(screenX, screenY, 0);
		prevHitPos = hitPos;
		prevRay = ray;
		prevHitPart = hittedPart;
		prevIgnoreSelected = ignoreSelectedParts;
		lastHittedDistance = dist;
	}

	public MetaInfoRenderer getMetaInfoRenderer() {
		return metaInfoRenderer;
	}

	public LDrawFile getWorkingLDrawFile() {
		return ldrawFile;
	}

	public void initGLReleatedComponent(Size2 size, GL2 gl2) {
		glRenderer.initWithBoundsCamera(size, camera);

		LDrawGLCameraScroller scroller = new LDrawGLCameraScroller();
		scroller.setDocumentSize(new Size2(800f, 600f));
		glRenderer.setDelegate(null, scroller);
		glRenderer.setLDrawDirective(ldrawFile);
		glRenderer.prepareOpenGL(gl2);
	}

	private void initRenderer() {
		camera = new MainCamera();
		camera.setCurrentCameraRotation(new Vector3f((float) Math.PI, -0.52f, 0));

		glRenderer = new LDrawGLRenderer();
		metaInfoRenderer = new MetaInfoRenderer(this, camera);
		brickMovementGuideRenderer = BrickMovementGuideRenderer
				.getInstance(camera);
		connectivityRenderer = new GlobalConnectivityRenderer(camera);
		brickSelectionInfoRenderer = new DragSelectionInfoRenderer();
	}

	public void isVisibleStudHoleMatrix(boolean flag) {
		connectivityRenderer.isVisible(flag);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	private void ldrawFileChanged() {
		if (glRenderer == null || glRenderer.isReadyToUse() == false)
			return;

		if (glRenderer != null)
			glRenderer.setLDrawDirective(ldrawFile);

		camera.setDefault();
		currentStep = null;

		globalConnectivityManager.clear(false);
		directiveSelectionManager.clearAllDirective(false);
		undoRedoManager.clear();
		GroupingManager.getInstance().clear();

		for (LDrawDirective directive : ldrawFile.activeModel().subdirectives()) {
			if (LDrawStep.class.isInstance(directive)) {
				LDrawStep step = (LDrawStep) directive;
				for (LDrawDirective subDirective : step.subdirectives()) {
					if (LDrawPart.class.isInstance(subDirective)) {
						LDrawPart part = (LDrawPart) subDirective;
						globalConnectivityManager.addPart(part, false);
						globalConnectivityManager.updateMatrix(part);
					}
				}
			}
			directiveSelectionManager.addDirective(directive, false);
		}
		brickMovementGuideRenderer.setLDrawPart(null);
		// globalConnectivityManager.updateMatrixAll();
		directiveSelectionManager.updateScreenProjectionVerticesMapAll();
		updateGridRange();
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawFileActiveModelDidChanged);
		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (ldrawFile != null) {
					if (ldrawFile.path() != null)
						shell.setText(APP_NAME + ": " + ldrawFile.path());
					else
						shell.setText(APP_NAME + ": untitled");
				}
			}
		});
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public boolean moveDirectiveBy(LDrawDirective directive, Vector3f pos) {
		return moveDirectiveBy(directive, pos, true);
	}

	public boolean moveDirectiveBy(LDrawDirective directive, Vector3f pos,
			boolean useSnap) {
		boolean isMoved = false;
		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;
			if (pos != null)
				isMoved = part.moveBy(new Vector3f(pos.x, pos.y, pos.z),
						configurationManager.getGridUnit(), useSnap);
			updateGridRange();
		}
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return isMoved;
	}

	public void moveDirectiveTo(LDrawDirective directive, Vector3f pos) {
		moveDirectiveTo(directive, pos, true);
	}

	public void moveDirectiveTo(LDrawDirective directive, Vector3f pos,
			boolean useSnap) {
		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;
			if (pos != null) {
				Matrix4 newTransform = null;

				long t = System.nanoTime();

				newTransform = GlobalConnectivityManager.getInstance()
						.getClosestConnectablePos(part, pos);

				long diff = (System.nanoTime() - t);
				average = (long) (average * 0.8 + diff * 0.2);
				// System.out.println("avr: " + average + ", " + diff);

				if (newTransform == null) {
					newTransform = new Matrix4(
							directiveSelectionManager
									.getStartMoveTransformMatrix(part));

					if (useSnap)
						pos = LDrawGridTypeT.getSnappedPos(pos,
								configurationManager.getGridUnit());
					newTransform.element[3][0] = pos.x;
					newTransform.element[3][1] = pos.y;
					newTransform.element[3][2] = pos.z;
				}

				part.setTransformationMatrix(newTransform);
			}
			updateGridRange();

			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.NeedRedraw);
		}
	}

	public void moveDirectiveToWithoutConnectivity(LDrawDirective directive,
			Vector3f pos) {
		boolean useConnectivity = configurationManager.isUseConnectivity();
		configurationManager.setUseConnectivity(false);
		moveDirectiveTo(directive, pos, false);
		configurationManager.setUseConnectivity(useConnectivity);
	}

	public void newLDrawFile() {
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();
		if (shell == null || checkChanged(shell)) {
			ldrawFile = LDrawFile.newEditableFile();
			ldrawFile.activeModel().addStep();
			ldrawFile.setPath(null);
			ldrawFile.activeModel().setModelName("Untitled.ldr");
			ldrawFile.activeModel().setFileName("Untitled.ldr");
			ldrawFileChanged();
		}
	}

	public void openFile(final String path) {
		BackgroundThreadManager.getInstance().add(new Runnable() {
			@Override
			public void run() {
				long t = System.nanoTime();
				if (path.toLowerCase().endsWith(".lxfml")
						|| path.toLowerCase().endsWith(".lxf")) {
					try {
						Ldd2Ldr ldd2ldr = new Ldd2Ldr(
								BuilderConfigurationManager
										.getDefaultDataDirectoryPath()
										+ "Data/ldraw.xml");
						String outputFile = BuilderConfigurationManager
								.getDefaultDataDirectoryPath()
								+ "convertedLdr.ldr";
						if (new File(outputFile).exists())
							new File(outputFile).delete();

						ldd2ldr.convert(path, outputFile);
						if (new File(outputFile).exists())
							ldrawFile = LDrawFile
									.fileFromContentsAtPath(outputFile);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					ldrawFile = LDrawFile.fileFromContentsAtPath(path);
				if (ldrawFile == null) {
					ldrawFile = LDrawFile.newEditableFile();
					ldrawFile.activeModel().addStep();
					ldrawFile.setPath(path);
				}
				ldrawFileChanged();
				System.out.println((System.nanoTime() - t) / 1000000000.0f
						+ " seconds to load.");
			}
		});
		if (BackgroundThreadManager.getInstance().isAllFinish() == false)
			new ProgressDlg(shell, SWT.NONE).open();
	}

	public void importFile(String path, Vector3f pos) {
		LDrawFile file = LDrawFile.fileFromContentsAtPath(path);
		file.activeModel().setModelName(path);

		for (LDrawMPDModel model : file.submodels()) {
			ldrawFile.addSubmodel(model);
		}

		if (pos == null) {
			pos = new Vector3f(0, 0, 0);
		}

		String displayName = null;
		if (file.activeModel().modelName().contains("/"))
			displayName = file.activeModel().modelName()
					.substring(file.activeModel().modelName().lastIndexOf("/"));
		if (displayName == null
				&& file.activeModel().modelName().contains("\\"))
			displayName = file
					.activeModel()
					.modelName()
					.substring(
							file.activeModel().modelName().lastIndexOf("\\") + 1);
		if (displayName == null)
			displayName = file.activeModel().modelName();
		file.activeModel().setModelName(displayName);

		LDrawPart part = new LDrawPart();
		part.initWithPartName(displayName, pos);
		part.setDisplayName(displayName);
		part.resolvePart();
		addDirectiveToWorkingFile(part);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);
	}

	public void importLXFMLFile(String fileName) {
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory
				.newInstance();
		Document docXML = null;

		try {
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			if (fileName.endsWith("lxf")) {
				ZipFile zip = new ZipFile(fileName);
				docXML = docBuild.parse(zip.getInputStream(zip
						.getEntry("IMAGE100.LXFML")));
				zip.close();
			} else {
				File file = new File(fileName);
				docXML = docBuild.parse(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		docXML.getDocumentElement().normalize();

		ArrayList<Part> lddParts = new ArrayList<Part>();

		NodeList brickList = docXML.getElementsByTagName("Brick");
		NodeList partList;
		Element brickNode, partNode;
		ArrayList<String> parts = new ArrayList<String>();
		String designID;
		Part part;
		for (int i = 0, j; i < brickList.getLength(); i++) {
			brickNode = (Element) brickList.item(i);
			designID = brickNode.getAttribute("designID");
			partList = brickNode.getElementsByTagName("Part");
			for (j = 0; j < partList.getLength(); j++) {
				partNode = (Element) partList.item(j);
				designID = ((Element) partNode).getAttribute("designID");
				if (!parts.contains(designID)) {
					parts.add(designID);
				}
				part = new Part((Element) partNode);
				lddParts.add(part);
			}
		}
	}

	public void removeDirectiveFromWorkingFile(LDrawDirective directive) {
		removeDirectiveFromWorkingFile(directive, true);
	}

	public void removeDirectiveFromWorkingFile(LDrawDirective directive,
			boolean updateConnectivityManager) {
		LDrawDirective parent = directive.enclosingDirective();

		if (directive instanceof LDrawStep) {
			LDrawStep step = (LDrawStep) directive;
			if (ldrawFile.activeModel().subdirectives().size() > 1) {
				ldrawFile.activeModel().removeDirective(step);
				currentStep = null;
			}

			for (LDrawDirective subDirectives : step.subdirectives()) {
				if (subDirectives instanceof LDrawPart) {
					globalConnectivityManager.removePart(
							(LDrawPart) subDirectives,
							updateConnectivityManager);
				}
				directiveSelectionManager.removeDirective(subDirectives);
			}

			DirectiveSelectionManager.getInstance().clearSelection(
					updateConnectivityManager);

		} else if (directive instanceof LDrawPart) {
			if (directive.enclosingDirective() != null)
				directive.enclosingDirective().removeDirective(directive);
			globalConnectivityManager.removePart((LDrawPart) directive,
					updateConnectivityManager);

			directiveSelectionManager.removeDirective(directive);
		} else {
			if (directive.enclosingDirective() != null)
				directive.enclosingDirective().removeDirective(directive);
			directiveSelectionManager.removeDirective(directive);
		}

		updateGridRange();
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidRemoved,
				new LDrawDirectiveDidRemoved(parent, directive));
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void removeSelectedDirective() {
		for (LDrawDirective directive : directiveSelectionManager
				.getSelectedDirectiveList()) {
			removeDirectiveFromWorkingFile(directive);
			if (directive instanceof LDrawPart)
				globalConnectivityManager.removePart((LDrawPart) directive);
		}
		directiveSelectionManager.clearSelection(true);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void rotateSelectedDirectiveBy(Vector3f degree) {
		if (directiveSelectionManager.isEmpty())
			return;

		LDrawDirective centeredPart = brickMovementGuideRenderer.getLDrawPart();
		if (centeredPart == null)
			centeredPart = directiveSelectionManager
					.getFirstSelectedDirective();

		if (centeredPart instanceof LDrawDrawableElement == false)
			return;

		Matrix4 newTransform = getResultTransformMatrixOfRotateDirectiveByFromCenter(
				centeredPart, Math.abs(degree.length()), degree,
				((LDrawDrawableElement) centeredPart).position());
		directiveSelectionManager.getBrickGroupForTransform().applyTransform(
				centeredPart, newTransform);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public boolean saveAs(Shell shell, String filepath) {
		if (shell != null && filepath == null) {
			FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] { "*.ldr", "*.mpd",
					"*.dat" });
			fileDialog.setFileName("untitled");
			filepath = fileDialog.open();
		}
		if (filepath == null)
			return false;
		File file = new File(filepath);
		if (shell != null && file.exists() == true) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);
			messageBox
					.setMessage("Selected file is already exists. \r\n Do you want to overwrite it?");
			if (messageBox.open() != SWT.YES) {
				return false;
			}
		}

		FileOutputStream fos;
		try {
			String str = ldrawFile.write();
			fos = new FileOutputStream(file);
			fos.write(str.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		ldrawFile.setPath(filepath);
		isChanged = false;

		if (ldrawFile != null)
			if (ldrawFile.path() != null)
				this.shell.setText(APP_NAME + ": " + ldrawFile.path());
			else
				this.shell.setText(APP_NAME + ": untitled");
		return true;
	}

	public void saveFile() {
		removeEmptyStep();
		if (ldrawFile.path() != null)
			saveAs(null, ldrawFile.path());
		else
			saveAs(Display.getDefault().getActiveShell(), ldrawFile.path());
	}

	public void setCurrentStep(LDrawStep step) {
		currentStep = step;
	}

	public void snapToGrid() {
		boolean useConnectivity = configurationManager.isUseConnectivity();
		configurationManager.setUseConnectivity(false);
		ArrayList<LDrawDirective> selectedPartList = directiveSelectionManager
				.getSelectedDirectiveList();
		if (selectedPartList.size() == 0)
			return;
		LDrawDirective lastSelectedPart = selectedPartList.get(selectedPartList
				.size() - 1);

		if (lastSelectedPart instanceof LDrawPart == false)
			return;
		Vector3f pos = ((LDrawPart) lastSelectedPart).position();
		lastSelectedPart.setTransformationMatrix(Direction6T
				.getSnappedTransformMatrix(lastSelectedPart
						.transformationMatrix()));
		((LDrawPart) lastSelectedPart).moveBy(pos,
				configurationManager.getGridUnit());

		directiveSelectionManager.getBrickGroupForTransform().applyTransform(
				lastSelectedPart, lastSelectedPart.transformationMatrix());
		directiveSelectionManager.updateStartMoveTransformMatrixMap();
		configurationManager.setUseConnectivity(useConnectivity);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void updateGridRange() {
		float[] range = new float[6];
		range[0] = 3.40282347E+38f; // minx
		range[1] = -3.40282347E+38f; // maxx
		range[2] = 3.40282347E+38f; // minz
		range[3] = -3.40282347E+38f; // maxz
		range[4] = 3.40282347E+38f; // bottom y
		range[5] = -3.40282347E+38f; // top y

		Matrix4 transform = new Matrix4();
		transform.setIdentity();

		ldrawFile.getRange(transform, range);
		range[4] = 3.40282347E+38f; // bottom y
		range[5] = -3.40282347E+38f; // top y

		metaInfoRenderer.setRange(range);
	}

	public void changeDirectiveIndex(LDrawContainer container,
			LDrawDirective directive, int newIndex) {
		container.removeDirective(directive);
		container.insertDirective(directive, newIndex);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(directive));
	}

	public void ChangeParentOfDirectiveAction(LDrawDirective directive,
			LDrawContainer oldStep, LDrawContainer newStep) {
		oldStep.removeDirective(directive);
		newStep.addDirective(directive);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidRemoved,
				new LDrawDirectiveDidRemoved(oldStep, directive));
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(newStep, directive));
	}

	public void ChangeParentOfDirectiveAction(LDrawDirective directive,
			LDrawContainer oldStep, LDrawContainer newStep, int index) {
		oldStep.removeDirective(directive);
		newStep.insertDirective(directive, index);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidRemoved,
				new LDrawDirectiveDidRemoved(oldStep, directive));
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidAdded,
				new LDrawDirectiveDidAdded(newStep, directive));
	}

	public ArrayList<LDrawPart> getAllPartInActiveModel() {
		return getAllPartInActiveModel(false);
	}

	public ArrayList<LDrawPart> getAllPartInActiveModel(boolean extractSubmodel) {
		LDrawModel model = ldrawFile.activeModel();
		return LDrawUtilities.extractLDrawPartListModel(model, extractSubmodel);
	}

	public ArrayList<LDrawLSynth> getAllLSynthInActiveModel(boolean extractMPD) {
		LDrawModel model = ldrawFile.activeModel();
		return getAllLSynthInActiveModel(model, extractMPD);
	}

	private ArrayList<LDrawLSynth> getAllLSynthInActiveModel(LDrawModel model,
			boolean extractMPD) {
		if (model == null)
			return null;
		ArrayList<LDrawLSynth> retList = new ArrayList<LDrawLSynth>();
		for (LDrawStep step : model.steps()) {
			for (LDrawDirective directive : step.subdirectives()) {
				if (directive instanceof LDrawLSynth) {
					LDrawLSynth lsynth = (LDrawLSynth) directive;
					retList.add(lsynth);
				} else if (directive instanceof LDrawPart && extractMPD == true) {
					LDrawPart part = (LDrawPart) directive;
					if (part.getCacheType() == PartTypeT.PartTypeSubmodel) {
						ArrayList<LDrawLSynth> tempList = getAllLSynthInActiveModel(
								part.getCacheModel(), extractMPD);
						if (tempList != null)
							retList.addAll(tempList);
					}
				}
			}
		}
		return retList;
	}

	public ArrayList<LDrawPart> getAllPartInFile() {

		ArrayList<LDrawPart> retList = new ArrayList<LDrawPart>();
		if (ldrawFile == null)
			return retList;

		for (LDrawModel model : ldrawFile.submodels())
			retList.addAll(LDrawUtilities.extractLDrawPartListModel(model,
					false));

		return retList;
	}

	public Matrix4 getResultTransformMatrixOfRotateDirectiveByFromCenter(
			LDrawDirective directive, float angle, Vector3f rotationVector,
			Vector3f center) {
		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;
			if (MatrixMath.compareFloat(angle, 0) != 0) {
				return part.getTransformMatrixForRotateByDegrees(angle,
						rotationVector, center);
			}
			return part.transformationMatrix();
		}
		return null;
	}

	public Matrix4 getResultTransformMatrixOfMoveDirectiveBy(
			LDrawDirective directive, Vector3f pos, boolean useSnap) {
		if (LDrawPart.class.isInstance(directive)) {
			LDrawPart part = (LDrawPart) directive;
			if (pos != null)
				return part.getTransformMatrixForMoveBy(pos,
						configurationManager.getGridUnit(), useSnap);

			return part.transformationMatrix();
		}
		return null;
	}

	public void setChanged() {
		isChanged = true;
	}

	public boolean checkChanged(Shell shell) {
		if (isChanged) {
			MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO
					| SWT.CANCEL | SWT.APPLICATION_MODAL | SWT.ICON_QUESTION);
			box.setText("Your changes will be lost if you don't save");
			box.setMessage("Do you want to save the changes you made to Model?");
			switch (box.open()) {
			case SWT.YES:
				return saveAs(shell, ldrawFile.path());
			case SWT.NO:
				return true;
			case SWT.CANCEL:
				return false;
			}
		}
		return true;
	}

	public void autoGrouping() {
		ArrayList<LDrawPart> initialLists = new ArrayList<LDrawPart>();
		for (LDrawDirective directive : directiveSelectionManager
				.getSelectedDirectiveList())
			if (directive instanceof LDrawPart)
				initialLists.add((LDrawPart) directive);

		if (initialLists.size() == 0) {
			return;
		}
		ArrayList<LDrawPart> groupedLists = new ArrayList<LDrawPart>(
				initialLists.size());
		autoGrouping(initialLists, groupedLists);
	}

	private void autoGrouping(ArrayList<LDrawPart> initialLists,
			ArrayList<LDrawPart> groupedLists) {
		ArrayList<LDrawPart> connectedParts;
		LDrawStep oldStep, newStep;
		for (LDrawPart part : initialLists) {
			if (groupedLists.contains(part))
				continue;
			newStep = ldrawFile.activeModel().addStep();
			connectedParts = GlobalConnectivityManager.getInstance()
					.getConnectedPart(part, null, null, true);
			for (LDrawPart connectedPart : connectedParts) {
				oldStep = connectedPart.enclosingStep();
				if (oldStep == null)
					continue;
				removeDirectiveFromWorkingFile(connectedPart);
				if (oldStep.size() == 0) {
					removeDirectiveFromWorkingFile(oldStep);
				}
				addDirectiveToWorkingFile(newStep, connectedPart);
				groupedLists.add(connectedPart);
			}
		}
	}

	public void removeEmptyStep() {
		ArrayList<LDrawStep> emptyStepList = new ArrayList<LDrawStep>();
		for (LDrawStep step : ldrawFile.activeModel().steps()) {
			boolean isContainPart = false;
			for (LDrawDirective directive : step.subdirectives())
				if (directive instanceof LDrawPart) {
					isContainPart = true;
					break;
				}
			if (isContainPart == false)
				emptyStepList.add(step);
		}

		for (LDrawStep step : emptyStepList)
			removeDirectiveFromWorkingFile(step, false);
	}

	public ArrayList<LDrawDrawableElement> hideAllStep() {
		ArrayList<LDrawDrawableElement> retList = new ArrayList<LDrawDrawableElement>();
		for (LDrawStep step : getWorkingLDrawFile().activeModel().steps()) {
			for (LDrawDirective directive : step.subdirectives())
				if (directive instanceof LDrawDrawableElement) {
					((LDrawDrawableElement) directive).setHidden(true);
					retList.add((LDrawDrawableElement) directive);
					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.LDrawDirectiveDidChanged,
							new LDrawDirectiveDidChanged(directive));

				}
		}
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return retList;
	}

	public int getNumOfSteps() {
		return getWorkingLDrawFile().activeModel().steps().size();
	}

	public ArrayList<LDrawDrawableElement> showAllStep() {
		ArrayList<LDrawDrawableElement> retList = new ArrayList<LDrawDrawableElement>();
		for (LDrawStep step : getWorkingLDrawFile().activeModel().steps()) {
			for (LDrawDirective directive : step.subdirectives())
				if (directive instanceof LDrawDrawableElement) {
					((LDrawDrawableElement) directive).setHidden(false);
					retList.add((LDrawDrawableElement) directive);
					NotificationCenter.getInstance().postNotification(
							NotificationMessageT.LDrawDirectiveDidChanged,
							new LDrawDirectiveDidChanged(directive));
				} else if (directive instanceof LDrawLSynth) {
					for (LDrawDirective subDirective : ((LDrawLSynth) directive)
							.synthesizedParts())
						if (subDirective instanceof LDrawDrawableElement) {
							((LDrawDrawableElement) subDirective)
									.setHidden(false);
							retList.add((LDrawDrawableElement) subDirective);
							NotificationCenter
									.getInstance()
									.postNotification(
											NotificationMessageT.LDrawDirectiveDidChanged,
											new LDrawDirectiveDidChanged(
													subDirective));
						}
				}
		}

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return retList;
	}

	public ArrayList<LDrawDrawableElement> showSelectedDirectives() {
		ArrayList<LDrawDrawableElement> retList = new ArrayList<LDrawDrawableElement>();
		for (LDrawDirective directive : directiveSelectionManager
				.getSelectedDirectiveList())
			if (directive instanceof LDrawDrawableElement) {
				((LDrawDrawableElement) directive).setHidden(false);
				retList.add((LDrawDrawableElement) directive);
			} else if (directive instanceof LDrawStep) {
				for (LDrawDirective subDirective : ((LDrawStep) directive)
						.subdirectives())
					if (subDirective instanceof LDrawDrawableElement) {
						((LDrawDrawableElement) subDirective).setHidden(false);
						retList.add((LDrawDrawableElement) subDirective);
					}
			}
		directiveSelectionManager.clearSelection();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return retList;

	}

	public void adjustRotationMatrixForSelectedDirective() {
		if (directiveSelectionManager.isEmpty())
			return;

		ArrayList<LDrawDirective> selectedPartList = directiveSelectionManager
				.getSelectedDirectiveList();

		Matrix4 rotationMatrix = selectedPartList.get(
				selectedPartList.size() - 1).transformationMatrix();

		rotationMatrix.element[3][0] = rotationMatrix.element[3][1] = rotationMatrix.element[3][2] = 0;

		for (LDrawDirective part : selectedPartList) {
			Matrix4 partTransform = part.transformationMatrix();
			Matrix4 newTransform = new Matrix4(rotationMatrix);
			for (int i = 0; i < 3; i++)
				newTransform.element[3][i] = partTransform.element[3][i];
			part.setTransformationMatrix(newTransform);
		}

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void alignSelectedDirective(Direction6T direction) {
		ArrayList<LDrawDirective> selectedPartList = directiveSelectionManager
				.getSelectedDirectiveList();
		if (selectedPartList.size() < 2)
			return;

		LDrawDirective basis = null;
		for (LDrawDirective part : selectedPartList) {
			if (basis == null) {
				basis = part;
				continue;
			}
			Box3 boundingBox = part.boundingBox3();
			Vector3f min = boundingBox.getMax();
			Vector3f max = boundingBox.getMin();
			boolean flag = false;
			switch (direction) {
			case X_Minus:
				if (min.x < basis.boundingBox3().getMin().x)
					flag = true;
				break;
			case X_Plus:
				if (max.x > basis.boundingBox3().getMax().x)
					flag = true;
				break;
			case Y_Minus:
				if (min.y < basis.boundingBox3().getMin().y)
					flag = true;
				break;
			case Y_Plus:
				if (max.y > basis.boundingBox3().getMax().y)
					flag = true;
				break;
			case Z_Minus:
				if (min.z < basis.boundingBox3().getMin().z)
					flag = true;
				break;
			case Z_Plus:
				if (max.z > basis.boundingBox3().getMax().z)
					flag = true;
				break;
			}
			if (flag == true) {
				basis = part;
			}
		}
		Box3 boundingBox_basis = basis.boundingBox3();

		for (LDrawDirective part : selectedPartList) {
			if (part == basis)
				continue;
			Box3 boundingBox = part.boundingBox3();
			Vector3f min = boundingBox.getMin();
			Vector3f max = boundingBox.getMax();
			Matrix4 newTransform = part.transformationMatrix();
			switch (direction) {
			case X_Minus:
				newTransform.translate(boundingBox_basis.getMin().x - min.x, 0,
						0);
				break;
			case X_Plus:
				newTransform.translate(boundingBox_basis.getMax().x - max.x, 0,
						0);
				break;
			case Y_Minus:
				newTransform.translate(0, boundingBox_basis.getMin().y - min.y,
						0);
				break;
			case Y_Plus:
				newTransform.translate(0, boundingBox_basis.getMax().y - max.y,
						0);
				break;
			case Z_Minus:
				newTransform.translate(0, 0, boundingBox_basis.getMin().z
						- min.z);
				break;
			case Z_Plus:
				newTransform.translate(0, 0, boundingBox_basis.getMax().z
						- max.z);
				break;
			}
			part.setTransformationMatrix(newTransform);
		}

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public LDrawStep makeNewStepFromSeletion() {
		LDrawStep newStep = newStepToWorkingFile();
		ArrayList<LDrawDirective> selectedPartList = directiveSelectionManager
				.getSelectedDirectiveList();

		for (LDrawDirective directive : selectedPartList) {
			removeDirectiveFromWorkingFile(directive);
			addDirectiveToWorkingFile(newStep, directive);
		}

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return newStep;
	}

	public void setAuthor(String author) {
		if (this.ldrawFile != null && this.ldrawFile.activeModel() != null) {
			this.ldrawFile.activeModel().setAuthor(author);
		}
	}

	public String getAuthor() {
		if (this.ldrawFile != null && this.ldrawFile.activeModel() != null)
			return this.ldrawFile.activeModel().author();
		return "";
	}

	public void setModelName(String name) {
		if (this.ldrawFile != null && this.ldrawFile.activeModel() != null) {
			this.ldrawFile.activeModel().setFileName(name);
		}
	}

	public String getModelName() {
		if (this.ldrawFile != null && this.ldrawFile.activeModel() != null)
			return this.ldrawFile.activeModel().fileName();
		return "";
	}

	public void changeActiveModel(LDrawMPDModel model) {
		if (model == null)
			return;

		ldrawFile.setActiveModel(model);
		ldrawFileChanged();
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawFileActiveModelDidChanged);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public LDrawMPDModel getActiveModel() {
		if (ldrawFile == null)
			return null;
		return ldrawFile.activeModel();
	}

	public void renameModel(LDrawMPDModel model, String newName) {
		if (ldrawFile == null)
			return;

		ldrawFile.renameModel(model, newName);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);
	}

	public void removeModel(LDrawModel model) {
		if (ldrawFile == null)
			return;
		ldrawFile.removeDirective(model);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);

	}

	public LDrawMPDModel makeASubmodel() {
		if (ldrawFile == null)
			return null;

		LDrawMPDModel newModel = LDrawMPDModel.model();
		int counter = 0;
		while (ldrawFile.modelNames().contains(newModel.modelName())) {
			newModel.setModelName("UntitledModel_" + counter);
			counter++;
		}

		newModel.addStep();

		ldrawFile.addSubmodel(newModel);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return newModel;
	}

	public LDrawPart makeASubmodelFromSelection() {
		if (ldrawFile == null)
			return null;

		if (directiveSelectionManager.isEmpty())
			return null;

		LDrawMPDModel newModel = LDrawMPDModel.model();
		int counter = 0;
		while (ldrawFile.modelNames().contains(newModel.modelName())) {
			newModel.setModelName("UntitledModel_" + counter);
			counter++;
		}

		LDrawStep newStep = new LDrawStep();

		ArrayList<LDrawDirective> selectedDirectiveList = directiveSelectionManager
				.getSelectedDirectiveList();

		Vector3f centerPos = null;
		try {
			centerPos = directiveSelectionManager.getPartHavingMinY()
					.boundingBox3().getMax();
		} catch (Exception e) {
			centerPos = new Vector3f();
		}
		removeSelectedDirective();

		for (LDrawDirective part : selectedDirectiveList) {
			try {
				LDrawDirective copy = (LDrawDirective) part.clone();
				if (copy instanceof LDrawPart)
					((LDrawPart) copy).moveTo(((LDrawPart) copy).position()
							.sub(centerPos), LDrawGridTypeT.Fine);
				else if (copy instanceof LDrawLSynthDirective)
					((LDrawLSynthDirective) copy).moveTo(
							((LDrawLSynthDirective) copy).position().sub(
									centerPos), LDrawGridTypeT.Fine);

				newStep.addDirective(copy);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		newModel.addStep(newStep);
		ldrawFile.addSubmodel(newModel);

		LDrawPart part = new LDrawPart();
		part.initWithPartName(newModel.modelName(), centerPos);
		part.setDisplayName(newModel.modelName());

		addDirectiveToWorkingFile(part);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return part;
	}

	public ArrayList<LDrawDirective> extractDirectivesFromASubmodel(
			LDrawPart part) {
		if (ldrawFile == null)
			return null;
		if (part == null)
			return null;
		if (part.getCacheType() != PartTypeT.PartTypeSubmodel)
			return null;
		if (part.getCacheModel() == null)
			return null;

		ArrayList<LDrawDirective> retList = new ArrayList<LDrawDirective>();

		Matrix4 partTransform = part.transformationMatrix();
		for (LDrawDirective directive_Step : part.getCacheModel()
				.subdirectives()) {
			try {
				LDrawDirective clone_Step = (LDrawDirective) (directive_Step
						.clone());
				if (clone_Step instanceof LDrawStep) {
					for (LDrawDirective directive_part : ((LDrawStep) clone_Step)
							.subdirectives()) {
						if (directive_part instanceof LDrawPart) {
							LDrawPart tempPart = (LDrawPart) directive_part;
							Matrix4 newTransform = tempPart
									.transformationMatrix();
							newTransform.multiply(partTransform);
							tempPart.setTransformationMatrix(newTransform);
						} else if (directive_part instanceof LDrawLSynth) {
							for (LDrawDirective lsynthDirective : ((LDrawLSynth) directive_part)
									.subdirectives()) {
								LDrawPart tempPart = (LDrawPart) lsynthDirective;
								Matrix4 newTransform = tempPart
										.transformationMatrix();
								newTransform.multiply(partTransform);
								tempPart.setTransformationMatrix(newTransform);
							}
						}
					}
				}
				addDirectiveToWorkingFile(clone_Step);
				retList.add(clone_Step);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		LDrawDirective parent = part.enclosingDirective();
		removeDirectiveFromWorkingFile(part);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidRemoved,
				new LDrawDirectiveDidRemoved(parent, part));
		for (LDrawDirective directive : retList)
			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.LDrawDirectiveDidAdded,
					new LDrawDirectiveDidAdded(directive.enclosingDirective(),
							directive));
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return retList;
	}

	public ArrayList<LDrawDrawableElement> hideSelectedDirectives() {
		ArrayList<LDrawDrawableElement> retList = new ArrayList<LDrawDrawableElement>();
		for (LDrawDirective directive : directiveSelectionManager
				.getSelectedDirectiveList())
			if (directive instanceof LDrawDrawableElement) {
				((LDrawDrawableElement) directive).setHidden(true);
				retList.add((LDrawDrawableElement) directive);

				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.LDrawDirectiveDidChanged,
						new LDrawDirectiveDidChanged(directive));
			} else if (directive instanceof LDrawLSynth) {
				for (LDrawDirective subDirective : ((LDrawLSynth) directive)
						.synthesizedParts())
					if (subDirective instanceof LDrawDrawableElement) {
						((LDrawDrawableElement) subDirective).setHidden(true);
						retList.add((LDrawDrawableElement) subDirective);
						NotificationCenter.getInstance().postNotification(
								NotificationMessageT.LDrawDirectiveDidChanged,
								new LDrawDirectiveDidChanged(subDirective));
					}
			} else if (directive instanceof LDrawStep) {
				for (LDrawDirective subDirective : ((LDrawStep) directive)
						.subdirectives())
					if (subDirective instanceof LDrawDrawableElement) {
						((LDrawDrawableElement) subDirective).setHidden(true);
						retList.add((LDrawDrawableElement) subDirective);
						NotificationCenter.getInstance().postNotification(
								NotificationMessageT.LDrawDirectiveDidChanged,
								new LDrawDirectiveDidChanged(subDirective));
					}
			}
		directiveSelectionManager.clearSelection();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);

		return retList;

	}

	public void synthesizeLSynth() {
		for (LDrawDirective step : getActiveModel().subdirectives()) {
			if (step instanceof LDrawStep) {
				for (LDrawDirective directive : ((LDrawStep) step)
						.subdirectives()) {
					if (directive instanceof LDrawLSynth) {
						((LDrawLSynth) directive).synthesize();
					}
				}
			}
		}
	}

	public void addSubModel(LDrawMPDModel model) {
		if (ldrawFile == null)
			return;

		ldrawFile.addSubmodel(model);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawModelDidChanged);
	}

	public void replaceDirective(LDrawDirective from, LDrawDirective to) {
		if (from == null || to == null)
			return;
		if (from.enclosingDirective() == null)
			return;
		int index = from.enclosingDirective().subdirectives().indexOf(from);

		insertDirectiveToWorkingFile(index, from.enclosingDirective(), to);
		removeDirectiveFromWorkingFile(from);
	}

	public void changeColor(LDrawDirective directive, LDrawColor newColor) {
		if (directive == null)
			return;
		if (directive instanceof LDrawDrawableElement)
			((LDrawDrawableElement) directive).setLDrawColor(newColor);
		if (directive instanceof LDrawLSynth)
			((LDrawLSynth) directive).setLDrawColor(newColor);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(directive));

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public Shell getShell() {
		return this.shell;
	}
}
