package Window;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class ConsoleView {
	private static ConsoleView _instance = null;

	private Text consoleTextComponent = null;

	private ConsoleView() {
	}

	private ConsoleView(Text consoleTextComponent) {
		this.consoleTextComponent = consoleTextComponent;
	}

	public synchronized static ConsoleView getInstance() {
		return _instance;
	}

	public synchronized static ConsoleView getInstance(Text consoleTextComponent) {
		if (_instance == null) {
			_instance = new ConsoleView(consoleTextComponent);
		} else {
			System.out.println("Denied re-initiallization");
		}
		return _instance;
	}

	public void println(String str) {
		Display.getDefault().asyncExec(new Runnable() {
			String str;

			public Runnable init(String pstr) {
				this.str = pstr;
				return this;
			}

			public void run() {
				if (!consoleTextComponent.isDisposed()){
					consoleTextComponent.append(str + "\r\n");
				}
			}
		}.init(str));
	}
	
	public void print(String str) {
		Display.getDefault().asyncExec(new Runnable() {
			String str;

			public Runnable init(String pstr) {
				this.str = pstr;
				return this;
			}

			public void run() {
				if (!consoleTextComponent.isDisposed()){
					consoleTextComponent.append(str);
				}
			}
		}.init(str));
	}

	public void clear() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (!consoleTextComponent.isDisposed()){
					consoleTextComponent.setText("");
				}
			}
		});
	}
}
