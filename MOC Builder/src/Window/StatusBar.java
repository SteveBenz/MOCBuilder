package Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class StatusBar extends Composite {

	private Label message;
	private ProgressBar progressBar;

	public StatusBar(Composite parent, int style) {
		super(parent, SWT.BAR);		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		setLayout(gridLayout);
		gridLayout.marginTop=-5;
		gridLayout.marginBottom=-5;
		
		GridData gridData = new GridData();		
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan=4;
		gridData.grabExcessHorizontalSpace=true;
				
		message = new Label(this, SWT.READ_ONLY);
		message.setLayoutData(gridData);
		
		gridData = new GridData();		
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan=1;
		
		progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setLayoutData(gridData);
		gridData.heightHint=20;
		
		ProgressBarManager.getInstance().add(progressBar);
		ProgressBarManager.getInstance().setProgress(0);
		ProgressBarManager.getInstance().setProgress(-1);
		
		pack();				
	}

	 /**
     * Displays a message as information.
     * Displays the Info-Icon in front of the text.
     * 
     * @param text the message to display
     */
    public void setInfo(String text) {
        message.setText(text);
        layout(true);
    }    

    /**
     * Displays a message as warning.
     * Displays the Warning-Icon in front of the text.
     * 
     * @param text the message to display
     */
    public void setWarning(String text) {
        message.setText(text);
        layout(true);
    }    

    /**
     * Displays a message as error.
     * Displays the Error-Icon in front of the text.
     * 
     * @param text the message to display
     */
    public void setError(String text) {
        message.setText(text);
        layout(true);
    }    

    /**
     * Clears the StatusBar. No Icon or Message is displayed.
     */
    public void clear() {
        message.setText("");
        layout(true);
    }

}
