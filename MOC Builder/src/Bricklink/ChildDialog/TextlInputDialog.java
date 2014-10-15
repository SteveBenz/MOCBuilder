package Bricklink.ChildDialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TextlInputDialog extends Dialog {

	protected String result;
	protected Shell shell;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TextlInputDialog(Shell parent, int style) {
		super(parent, style);
		setText("Import from Set Inventory");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(231, 99);
		shell.setText(getText());
		
		Label lblUrl = new Label(shell, SWT.NONE);
		lblUrl.setBounds(10, 10, 40, 20);
		lblUrl.setText("SetNo:");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(56, 7, 156, 21);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = text.getText();				
				shell.dispose();
			}
		});
		btnNewButton.setBounds(56, 34, 75, 25);
		btnNewButton.setText("Import");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result=null;
				shell.dispose();
			}
		});
		btnNewButton_1.setBounds(137, 34, 75, 25);
		btnNewButton_1.setText("Cancel");
		
		text.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				switch(arg0.keyCode){
				case SWT.KEYPAD_CR:
				case SWT.CR:
					result = text.getText();				
					shell.dispose();
					break;
				case SWT.ESC:
					result=null;
					shell.dispose();
					break;
				}								
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
}
