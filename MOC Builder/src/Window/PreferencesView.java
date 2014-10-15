package Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

public class PreferencesView {
	
	Shell	shell;
	
	public PreferencesView() {
	}
	
	public void showDialog(Display display) {
		if (shell == null || shell.isDisposed()) {
			shell = new Shell(display, SWT.CLOSE | SWT.TOOL);
			shell.setText("Preferences");
			shell.setLayout(new FormLayout());
			
			TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
			tabFolder.setLayout(new RowLayout(SWT.HORIZONTAL));
			FormData fd_group = new FormData();
			fd_group.top = new FormAttachment(0, 0);
			fd_group.left = new FormAttachment(0, 0);
			fd_group.bottom = new FormAttachment(0, 600);
			fd_group.right = new FormAttachment(0, 600);
			tabFolder.setLayoutData(fd_group);
			
			shell.pack();
			shell.open();
		} else if (shell.isVisible()) {
			shell.setVisible(false);
		} else {
			shell.open();
		}
	}

}
