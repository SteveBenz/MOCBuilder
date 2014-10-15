package ConnectivityEditor.Window;

import org.eclipse.swt.widgets.Composite;

public class GlobalFocusManagerForConnectivityEditor {
	private static GlobalFocusManagerForConnectivityEditor _instance = null;
	private Composite mainView;

	private GlobalFocusManagerForConnectivityEditor(Composite mainView) {
		this.mainView = mainView;
	}

	public synchronized static GlobalFocusManagerForConnectivityEditor getInstance(Composite mainView) {
			_instance = new GlobalFocusManagerForConnectivityEditor(mainView);
		return _instance;
	}

	public static GlobalFocusManagerForConnectivityEditor getInstance() {
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
