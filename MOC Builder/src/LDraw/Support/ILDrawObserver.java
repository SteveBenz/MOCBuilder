package LDraw.Support;

import LDraw.Support.type.CacheFlagsT;
import LDraw.Support.type.MessageT;

public interface ILDrawObserver {
	void observableSaysGoodbyeCruelWorld(ILDrawObservable doomedObservable);

	void statusInvalidated(CacheFlagsT flag, ILDrawObservable observable);

	void receiveMessage(MessageT msg, ILDrawObservable observable);
}
