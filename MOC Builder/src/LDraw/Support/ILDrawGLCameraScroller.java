package LDraw.Support;

import Common.Box2;
import Common.Size2;
import Common.Vector2f;
////////////////////////////////////////////////////////////////////////////////
//
//LDrawGLCameraScroller
//
////////////////////////////////////////////////////////////////////////////////
//
//The camera scroller protocol abstracts a scrolling view that the camera
//works within.  The camera does not get to own scrolling information; rather
//it has to go to the protocol to get current state and make changes.  (We do
//this because getting in a fight with NSClipView over scrolling is futile; if
//there can be only one copy of scroll state AppKit has to own it.)
//

public interface ILDrawGLCameraScroller {
	Size2 getDocumentSize();

	void setDocumentSize(Size2 newDocumentSize);

	// Scrolling
	Box2 getVisibleRect(); // From this we get our scroll position and visible
							// area, in doc units.

	Size2 getMaxVisibleSizeDoc(); // Max size we can show in doc units before we
									// scroll.

	Size2 getMaxVisibleSizeGL(); // Max size we can show in GL viewport pixels
									// units before we scroll.

	void setScaleFactor(float newScaleFactor); // This sets the scale factor
												// from UI points to doc units -
												// 2.0 makes our model look
												// twice as big on screen.

	void setScrollOrigin(Vector2f visibleOrigin); // This scrolls the scroller
													// so that the model point
													// "visibleOrigin" is in the
													// upper right corner of the
													// visible screen.

}
