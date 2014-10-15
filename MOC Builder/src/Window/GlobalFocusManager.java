package Window;

import org.eclipse.swt.widgets.Composite;

public class GlobalFocusManager {
	private static GlobalFocusManager _instance = null;
	private Composite mainView;

	private GlobalFocusManager(Composite mainView) {
		this.mainView = mainView;
	}

	public synchronized static GlobalFocusManager getInstance(Composite mainView) {
		if (_instance == null)
			_instance = new GlobalFocusManager(mainView);
		return _instance;
	}

	public static GlobalFocusManager getInstance() {
		return _instance;
	}
	
	public void forceFocusToMainView(){
		if(mainView!=null){
			mainView.getDisplay().asyncExec(new Runnable(){

				@Override
				public void run() {
					if (!mainView.isDisposed()){
						mainView.getParent().forceFocus();
						mainView.forceFocus();			
					}
				}				
			});
		}
	}
}
