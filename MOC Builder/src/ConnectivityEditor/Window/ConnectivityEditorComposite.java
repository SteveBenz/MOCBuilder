package ConnectivityEditor.Window;

import org.eclipse.swt.widgets.Composite;

import Connectivity.Connectivity;

public class ConnectivityEditorComposite extends Composite {

	protected Connectivity conn = null;

	public ConnectivityEditorComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public void setConnectivity(Connectivity conn){
		this.conn = conn;
	}

}
