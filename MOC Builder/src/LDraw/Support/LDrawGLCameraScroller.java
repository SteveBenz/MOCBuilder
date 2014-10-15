package LDraw.Support;

import Common.Box2;
import Common.Size2;
import Common.Vector2f;

//made for Test
public class LDrawGLCameraScroller implements ILDrawGLCameraScroller {

	Size2 documentSize;
	Box2 visibleRect;
	float scaleFactor;

	public LDrawGLCameraScroller() {
		documentSize = Size2.getZeroSize2();
		visibleRect = Box2.getZeroBox2();
		scaleFactor = 1.0f;
	}

	@Override
	public Size2 getDocumentSize() {
		return documentSize;
	}

	@Override
	public void setDocumentSize(Size2 newDocumentSize) {
		documentSize = newDocumentSize;
		visibleRect.setSize(documentSize);

	}

	@Override
	public Box2 getVisibleRect() {
		return visibleRect;
	}

	@Override
	public Size2 getMaxVisibleSizeDoc() {
		return documentSize;
	}

	@Override
	public Size2 getMaxVisibleSizeGL() {
		return documentSize;
	}

	@Override
	public void setScaleFactor(float newScaleFactor) {
		// TODO Auto-generated method stub
		scaleFactor = newScaleFactor;
	}

	@Override
	public void setScrollOrigin(Vector2f visibleOrigin) {
		visibleRect.setOrigin(visibleOrigin);
	}
}
