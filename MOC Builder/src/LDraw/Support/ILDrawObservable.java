package LDraw.Support;

public interface ILDrawObservable {
	void addObserver(ILDrawObserver observer);

	void removeObserver(ILDrawObserver observer);
}
