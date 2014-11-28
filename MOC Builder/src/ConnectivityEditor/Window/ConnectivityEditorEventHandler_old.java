package ConnectivityEditor.Window;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import BrickControlGuide.IGuideRenderer;
import BrickControlGuide.RotationGuide;
import Builder.BuilderConfigurationManager;
import Builder.MainCamera;
import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector2f;
import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import Connectivity.Direction6T;
import Connectivity.IConnectivity;
import ConnectivityEditor.Connectivity.ConnectivityRendererForConnectivityEditor;
import ConnectivityEditor.ConnectivityControlGuide.ConnectivityMovementGuideRenderer;
import ConnectivityEditor.UndoRedo.AddNDeleteConnAction;
import ConnectivityEditor.UndoRedo.ConnectivityEditorUndoRedoManager;
import ConnectivityEditor.UndoRedo.MoveConnectivityAction;
import LDraw.Support.type.LDrawGridTypeT;

public class ConnectivityEditorEventHandler_old implements MouseListener,
		MouseTrackListener, MouseMoveListener, MouseWheelListener, KeyListener {

	@Override
	public void mouseScrolled(MouseEvent e) {
		camera.zoom(e);
	}

	ConnectivitySelectionManager selectionManager;

	@Override
	public void mouseMove(MouseEvent e) {
		GlobalMousePositionForConnectivityEditor.getInstance().setPos(e.x, e.y);
		if (isLeftPressed) {
			isLeftDragged = true;
		}
		if (isRightPressed) {
			isRightDragged = true;
		}
		handleMouseDragged(e);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	private void handleMouseWheelButtonDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1) { // left button pressed
			isLeftPressed = true;
			handleMouseLeftButtonPressed(e);
		} else if (e.button == 3) { // right button
									// pressed
			isRightPressed = true;
			handleMouseRightButtonPressed(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1) { // left button released
			isLeftPressed = false;
			if (isLeftDragged == false) {// mouse clicked.
				handleMouseLeftButtonClicked(e);
			} else {
				handleMouseLeftButtonReleased(e);
				isLeftDragged = false;
			}
		} else if (e.button == 2) {
			handleMouseWheelButtonDoubleClick(e);
		} else if (e.button == 3) { // Right Button

			// released
			isRightPressed = false;
			if (isRightDragged == false) {// mouse clicked.
				handleMouseRightButtonClicked(e);
			} else {
				handleMouseRightButtonReleased(e);
				isRightDragged = false;
			}
		}
		connectivityMovementGuideRenderer.axisSelectedType(null);
	}

	@Override
	public void mouseEnter(MouseEvent e) {

		// GLCanvas canvas = ((GLCanvas) e.widget);
		// canvas.setFocus();
	}

	@Override
	public void mouseExit(MouseEvent e) {
		GlobalMousePositionForConnectivityEditor.getInstance().setPos(400, 400);
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	private MainCamera camera = null;
	private ConnectivityMovementGuideRenderer connectivityMovementGuideRenderer;
	private ConnectivityRendererForConnectivityEditor connectivityRenderer;

	private Vector3f startMoveWorldPos = null;
	private Vector3f startMoveConnPos = null;
	private IConnectivity startMoveConn = null;
	private Vector2f startMoveMousePos = null;

	private boolean isLeftPressed = false;
	private boolean isLeftDragged = false;
	private boolean isRightPressed = false;
	private boolean isRightDragged = false;

	public ConnectivityEditorEventHandler_old(ConnectivityEditor editor) {
		camera = editor.getCamera();
		connectivityMovementGuideRenderer = editor
				.getConnMovementGuideRenderer();
		connectivityRenderer = editor.getConnectivityRenderer();
		selectionManager = ConnectivitySelectionManager.getInstance();
	}

	private void handleMouseDragged(MouseEvent e) {
		// System.out.println("handleMouseDragged");
		// Right button pressed
		if (isRightPressed) {
			camera.rotate(e.x, e.y);
		}

		if (isLeftPressed) {
			switch (ConnectivityControlModeT.currentControlMode) {
			case None:
				ConnectivityControlModeT.currentControlMode = ConnectivityControlModeT.ConnectivitySelectingDrag;
			case ConnectivitySelectingDrag:
				break;
			case ConnectivityControl_Direct: {
				if (startMoveWorldPos == null || startMoveConn == null) {
					return;
				}

				Vector3f currentMoveWorldPos = ConnectivityEditor.getInstance()
						.getHittedPos(e.x, e.y, false);

				startMoveWorldPos = LDrawGridTypeT.getSnappedPos(
						startMoveWorldPos, LDrawGridTypeT.Fine);

				currentMoveWorldPos = LDrawGridTypeT.getSnappedPos(
						currentMoveWorldPos, LDrawGridTypeT.Fine);

				if (currentMoveWorldPos == null)
					return;

				Vector3f moveBy = currentMoveWorldPos.sub(startMoveWorldPos);

				if (moveBy.length() > 0) {
					startMoveConn.moveBy(moveBy);
					selectionManager.moveSelectedConnectivityBy(startMoveConn
							.getConnectivity());
					startMoveWorldPos = currentMoveWorldPos;
				}
			}
				break;
			case ConnectivityControl_Guide:
				if (startMoveWorldPos == null)
					return;
				Vector3f currentMoveWorldPos = camera.screenToWorldXZ(e.x, e.y,
						0);
				if (currentMoveWorldPos == null)
					return;
				Vector2f directionForX = null;
				Vector2f directionForZ = null;
				Vector2f directionForY = null;
				Vector2f temp = camera.getWorldToScreenPos(new Vector3f(),
						false);
				Vector2f tempY = camera.getWorldToScreenPos(new Vector3f(0, -1,
						0), false);
				Vector2f tempX = camera.getWorldToScreenPos(new Vector3f(1, 0,
						0), false);
				Vector2f tempZ = camera.getWorldToScreenPos(new Vector3f(0, 0,
						1), false);
				if (temp != null && tempX != null) {
					directionForX = temp.sub(tempX);
					directionForX.scale(1 / directionForX.length());
				}
				if (temp != null && tempY != null) {
					directionForY = temp.sub(tempY);
					directionForY.scale(1 / directionForY.length());
				}
				if (temp != null && tempZ != null) {
					directionForZ = temp.sub(tempZ);
					directionForZ.scale(1 / directionForZ.length());
				}
				// System.out.println(movedByY);
				Vector3f moveByInWorld = new Vector3f();
				moveByInWorld.y = directionForY.getY()
						* (startMoveMousePos.getY() - e.y);
				moveByInWorld.x = directionForX.getX()
						* (startMoveMousePos.getX() - e.x)
						- directionForX.getY()
						* (startMoveMousePos.getY() - e.y);
				moveByInWorld.z = directionForZ.getX()
						* (startMoveMousePos.getX() - e.x)
						- directionForZ.getY()
						* (startMoveMousePos.getY() - e.y);

				moveByInWorld = moveByInWorld.scale(0.5f);

				IGuideRenderer selectedGuide = connectivityMovementGuideRenderer
						.getSelectedGuide();

				if (selectedGuide != null) {
					boolean isRotation = false;
					if (selectedGuide instanceof RotationGuide)
						isRotation = true;

					Vector3f guideDirection = new Vector3f(
							selectedGuide.getAxisDirectionVector());

					float projectedDistance = moveByInWorld.dot(guideDirection);
					moveByInWorld = guideDirection.scale(projectedDistance);
					// moveByInWorld.round();

					if (isRotation) {
						projectedDistance = Math.abs(projectedDistance);
						connectivityMovementGuideRenderer.getConn().rotateBy(
								projectedDistance / 10, moveByInWorld);
						selectionManager
								.moveSelectedConnectivityBy(connectivityMovementGuideRenderer
										.getConn().getConnectivity());

					} else {
						float scale = camera.getScreenToWorldDistance(1)*2;
						moveByInWorld = guideDirection.scale(projectedDistance);
						moveByInWorld = moveByInWorld.scale(scale);
						moveByInWorld.round();			
						
						connectivityMovementGuideRenderer.getConn().moveTo(
								new Vector3f(connectivityMovementGuideRenderer
										.getConn().getCurrentPos())
										.add(moveByInWorld));

						selectionManager
								.moveSelectedConnectivityBy(connectivityMovementGuideRenderer
										.getConn().getConnectivity());
					}
					startMoveWorldPos = currentMoveWorldPos;
					startMoveMousePos = new Vector2f(e.x, e.y);
				}
				break;
			default:
				break;
			}
		}
	}

	private void handleMouseRightButtonPressed(MouseEvent e) {
		// System.out.println("handleMouseRightButtonPressed");
		camera.startRotate(e.x, e.y);
	}

	private void handleMouseLeftButtonPressed(MouseEvent e) {
		IConnectivity pointingConn = connectivityRenderer
				.getHittedConnectivity(camera, e.x, e.y);

		switch (ConnectivityControlModeT.currentControlMode) {
		case None:
		case ConnectivityControl:
		case ConnectivityControl_Direct:
		case ConnectivityControl_Guide:
			startMoveMousePos = new Vector2f(e.x, e.y);
			// is Pointing a brick
			startMoveConn = pointingConn;
			// isPointing a guide mark
			IGuideRenderer selectedAxisGuide = connectivityMovementGuideRenderer
					.getHittedAxisArrow(e.x, e.y);
			connectivityMovementGuideRenderer
					.axisSelectedType(selectedAxisGuide);

			if (selectedAxisGuide != null) {// guiding mark is
				// pressed
				startMoveWorldPos = camera.screenToWorldXZ(e.x, e.y, 0);
				startMoveWorldPos.y = camera.screenToWorldXY(e.x, e.y, 0).y;
				ConnectivityControlModeT.currentControlMode = ConnectivityControlModeT.ConnectivityControl_Guide;
			} else if (startMoveConn != null) {// a brick is pointing
				// init startPos to Move Brick
				ConnectivityControlModeT.currentControlMode = ConnectivityControlModeT.ConnectivityControl_Direct;
				startMoveWorldPos = connectivityRenderer.getHittedPos(camera,
						e.x, e.y);
				Connectivity conn = pointingConn.getConnectivity();

				if (selectionManager.containsInSelection(conn) == false) {
					selectionManager.addConnectivityToSelection(conn);
				}

			} else if (pointingConn == null) {
				ConnectivityControlModeT.currentControlMode = ConnectivityControlModeT.None;
			}
			break;
		default:
			break;
		}
	}

	private void handleMouseLeftButtonClicked(MouseEvent e) {
		IConnectivity conn = ConnectivityEditor.getInstance()
				.getConnectivityRenderer()
				.getHittedConnectivity(camera, e.x, e.y);
		handleBrickControlGuideDisplay(conn);
		if (conn != null) {
			selectionManager.addConnectivityToSelection(conn.getConnectivity());
		} else {
			selectionManager.clearSelection();
		}
	}

	private void handleMouseRightButtonReleased(MouseEvent e) {
	}

	private void handleMouseLeftButtonReleased(MouseEvent e) {
		switch (ConnectivityControlModeT.currentControlMode) {
		case None:
			break;
		case ConnectivityControl:
		case ConnectivityControl_Direct:
		case ConnectivityControl_Guide:
			handleConnectivityMove();
			break;
		default:
			break;
		}

		startMoveConn = null;
		ConnectivityControlModeT.currentControlMode = ConnectivityControlModeT.None;
	}

	private void handleBrickControlGuideDisplay(IConnectivity conn) {
		connectivityMovementGuideRenderer.setConn(conn);
	}

	private void handleMouseRightButtonClicked(MouseEvent e) {
	}

	private boolean isShiftKeyPressed = false;
	private boolean isCtrlKeyPressed = false;
	private boolean isAltKeyPressed = false;

	ArrayList<LDrawPart> instructionSequence = null;
	int indexForTest = 0;

	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.keyCode) {
		case SWT.ESC:
			isAltKeyPressed = isCtrlKeyPressed = isShiftKeyPressed = false;
			ConnectivitySelectionManager.getInstance().clearSelection();
			break;
		case SWT.CONTROL:
			isCtrlKeyPressed = true;
			break;
		case SWT.SHIFT:
			isShiftKeyPressed = true;
			break;
		case SWT.ALT:
			isAltKeyPressed = true;
			break;
		case SWT.DEL:
			AddNDeleteConnAction action = new AddNDeleteConnAction();
			LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
			if (part == null)
				return;
			for (Connectivity conn : selectionManager
					.getSelectedConnectivityList()) {
				action.removeConnectivity(conn);
				if (conn instanceof CollisionBox)
					part.getCollisionBoxList().remove(conn.getConnectivity());
				else
					part.getConnectivityList().remove(conn.getConnectivity());
			}
			handleBrickControlGuideDisplay(null);
			ConnectivityEditorUndoRedoManager.getInstance().pushUndoAction(
					action);
			break;

		case SWT.ARROW_RIGHT:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 5, 0));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 45, 0));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 15, 0));
				else
					handleRotateSelectedConnectivity(new Vector3f(0, 90, 0));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.X_Plus);
				}
			}
			break;
		case SWT.ARROW_LEFT:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, -5, 0));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, -45, 0));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, -15, 0));
				else
					handleRotateSelectedConnectivity(new Vector3f(0, -90, 0));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.X_Minus);
				}
			}
			break;
		case SWT.ARROW_UP:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(5, 0, 0));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(45, 0, 0));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(15, 0, 0));
				else
					handleRotateSelectedConnectivity(new Vector3f(90, 0, 0));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.Z_Plus);
				}
			}
			break;
		case SWT.ARROW_DOWN:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(-5, 0, 0));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(-45, 0, 0));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(-15, 0, 0));
				else
					handleRotateSelectedConnectivity(new Vector3f(-90, 0, 0));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.Z_Minus);
				}
			}
			break;
		case SWT.PAGE_UP:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, 5));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, 45));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, 15));
				else
					handleRotateSelectedConnectivity(new Vector3f(0, 0, 90));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.Y_Minus);
				}
			}
			break;

		case SWT.PAGE_DOWN:
			if (isCtrlKeyPressed == false) {
				if (isShiftKeyPressed && isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, -5));
				else if (isShiftKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, -45));
				else if (isAltKeyPressed)
					handleRotateSelectedConnectivity(new Vector3f(0, 0, -15));
				else
					handleRotateSelectedConnectivity(new Vector3f(0, 0, -90));
			} else {
				if (isShiftKeyPressed == false && isAltKeyPressed == false) {
					handleMoveSelectedConnectivity(Direction6T.Y_Plus);
				}
			}
			break;
		case 'y':
			if (isCtrlKeyPressed)
				ConnectivityEditorUndoRedoManager.getInstance().redo();
			break;
		case 'z':
			if (isCtrlKeyPressed)
				ConnectivityEditorUndoRedoManager.getInstance().undo();
			break;
		default:
		}
	}

	private void handleRotateSelectedConnectivity(Vector3f rotationVector) {
		if (connectivityMovementGuideRenderer.getConn() != null) {
			connectivityMovementGuideRenderer.getConn().rotateBy(
					(float) Math.toRadians(rotationVector.length()),
					rotationVector.scale(1 / rotationVector.length()));
			selectionManager
					.moveSelectedConnectivityBy(connectivityMovementGuideRenderer
							.getConn().getConnectivity());
			handleConnectivityMove();
		} else if (selectionManager.isEmpty() == false) {
			selectionManager
					.getSelectedConnectivityList()
					.get(0)
					.rotateBy((float) Math.toRadians(rotationVector.length()),
							rotationVector.scale(1 / rotationVector.length()));
			selectionManager.moveSelectedConnectivityBy(selectionManager
					.getSelectedConnectivityList().get(0));
			handleConnectivityMove();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		switch (event.keyCode) {
		case SWT.SHIFT:
			isShiftKeyPressed = false;
			break;
		case SWT.CONTROL:
			isCtrlKeyPressed = false;
			break;
		case SWT.ALT:
			isAltKeyPressed = false;
			break;
		}
	}

	public void handleConnectivityMove() {
		MoveConnectivityAction action = new MoveConnectivityAction();
		Matrix4 originalMatrix;
		ArrayList<Connectivity> connList = selectionManager
				.getSelectedConnectivityList();

		for (Connectivity conn : connList) {
			originalMatrix = selectionManager
					.getInitialMoveTransformMatrix(conn);
			action.addMoveConnectivity(conn, originalMatrix,
					conn.getTransformMatrix());
		}
		ConnectivityEditorUndoRedoManager.getInstance().pushUndoAction(action);
	}

	private void handleMoveSelectedConnectivity(Direction6T direction) {
		if (selectionManager.isEmpty())
			return;
		Connectivity conn = selectionManager.getSelectedConnectivityList().get(
				0);
		Vector3f moveByInWorld = null;
		switch (direction) {
		case X_Minus:
			moveByInWorld = new Vector3f(-1, 0, 0)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getXZValue());
			break;
		case X_Plus:
			moveByInWorld = new Vector3f(1, 0, 0)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getXZValue());
			break;
		case Y_Minus:
			moveByInWorld = new Vector3f(0, -1, 0)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getYValue());
			break;
		case Y_Plus:
			moveByInWorld = new Vector3f(0, 1, 0)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getYValue());
			break;
		case Z_Minus:
			moveByInWorld = new Vector3f(0, 0, -1)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getXZValue());
			break;
		case Z_Plus:
			moveByInWorld = new Vector3f(0, 0, 1)
					.scale(BuilderConfigurationManager.getInstance()
							.getGridUnit().getXZValue());
			break;

		}

		conn.moveBy(moveByInWorld);
		selectionManager.moveSelectedConnectivityBy(conn);
		handleConnectivityMove();
	}

}
