package Window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

import OtherTools.Syringe;
import Resource.ResourceManager;

public class EventHandlerForCursor implements KeyListener, MouseMoveListener {
	private static final String DefaultResourcePath = "/Resource/Image/";
	private Composite parent;

	private HashMap<CursorT, Cursor> cursorMap;

	enum CursorT {
		Default, Syringe, Selectable, Stamp, CameraMove
	};

	public EventHandlerForCursor(Composite parent) {
		this.parent = parent;
		cursorMap = new HashMap<CursorT, Cursor>();

		loadCursorImage();
	}

	private void loadCursorImage() {
		cursorMap.put(
				CursorT.Syringe,
				new Cursor(parent.getDisplay(),
						ResourceManager
								.getInstance()
								.getImage(
										parent.getDisplay(),
										DefaultResourcePath
												+ "active_colordropper.png")
								.getImageData(), 0, 0));
		cursorMap.put(
				CursorT.Stamp,
				new Cursor(parent.getDisplay(), ResourceManager
						.getInstance()
						.getImage(parent.getDisplay(),
								DefaultResourcePath + "stamp.png")
						.getImageData(), 0, 0));
		cursorMap.put(
				CursorT.CameraMove,
				new Cursor(parent.getDisplay(), ResourceManager
						.getInstance()
						.getImage(parent.getDisplay(),
								DefaultResourcePath + "cameraMove.png")
						.getImageData(), 0, 0));
	}

	@Override
	public void mouseMove(MouseEvent arg0) {
		if ((arg0.stateMask & SWT.SHIFT) != 0)
			parent.setCursor(cursorMap.get(CursorT.CameraMove));
		else {
			if (Syringe.getInstance().isActivated())
				if (Syringe.getInstance().getColorCode() == null)
					parent.setCursor(cursorMap.get(CursorT.Syringe));
				else
					parent.setCursor(cursorMap.get(CursorT.Stamp));
			else
				parent.setCursor(null);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if ((arg0.stateMask & SWT.SHIFT) != 0 && arg0.keyCode == SWT.SHIFT)
			parent.setCursor(null);
		if ((arg0.stateMask & SWT.SHIFT) == 0)
			parent.setCursor(null);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.keyCode == SWT.SHIFT)
			parent.setCursor(cursorMap.get(CursorT.CameraMove));
	}

}
