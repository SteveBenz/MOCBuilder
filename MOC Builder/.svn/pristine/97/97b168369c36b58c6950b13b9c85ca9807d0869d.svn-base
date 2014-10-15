package OtherTools;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

import Command.LDrawColor;
import Command.LDrawColorT;
import Command.LDrawPart;
import LDraw.Support.ColorLibrary;
import Window.MOCBuilder;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;

public class ColorThemeCustomizeDlg extends Dialog implements SelectionListener {

	private HashMap<LDrawPart, LDrawColor> originalColor;

	protected Object result;
	protected Shell shell;

	Slider slider_R;
	Slider slider_G;
	Slider slider_B;
	Slider slider_A;
	private Button btnApply;
	private Button btnNewButton;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorThemeCustomizeDlg(Shell parent, int style) {
		super(parent, style);
		setText("Customize Set Color");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		initVariable();
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

	private void initVariable() {
		originalColor = new HashMap<LDrawPart, LDrawColor>();

		for (LDrawPart part : MOCBuilder.getInstance().getAllPartInFile()) {
			LDrawColor color = part.getLDrawColor();
			originalColor.put(part, color);
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				for (LDrawPart part : MOCBuilder.getInstance()
						.getAllPartInFile()) {
					part.setLDrawColor(originalColor.get(part));
				}
			}
		});
		shell.setSize(300, 171);
		shell.setText(getText());

		slider_R = new Slider(shell, SWT.NONE);
		slider_R.addSelectionListener(this);
		slider_R.setBounds(37, 10, 233, 17);
		slider_R.setData("R");

		slider_G = new Slider(shell, SWT.NONE);
		slider_G.addSelectionListener(this);
		slider_G.setBounds(37, 33, 233, 17);
		slider_G.setData("G");

		slider_B = new Slider(shell, SWT.NONE);
		slider_B.addSelectionListener(this);
		slider_B.setBounds(37, 56, 233, 17);
		slider_B.setData("B");

		slider_A = new Slider(shell, SWT.NONE);
		slider_A.addSelectionListener(this);
		slider_A.setBounds(37, 79, 233, 17);
		slider_A.setData("A");

		Label lblR = new Label(shell, SWT.NONE);
		lblR.setBounds(10, 12, 21, 15);
		lblR.setText("R");

		Label lblG = new Label(shell, SWT.NONE);
		lblG.setText("G");
		lblG.setBounds(10, 35, 21, 15);

		Label lblB = new Label(shell, SWT.NONE);
		lblB.setText("B");
		lblB.setBounds(10, 58, 21, 15);

		Label lblA = new Label(shell, SWT.NONE);
		lblA.setText("A");
		lblA.setBounds(10, 81, 21, 15);

		slider_R.setIncrement(1);
		slider_G.setIncrement(1);
		slider_B.setIncrement(1);
		slider_A.setIncrement(1);

		slider_R.setMaximum(520);
		slider_G.setMaximum(520);
		slider_B.setMaximum(520);
		slider_A.setMaximum(520);

		slider_R.setSelection(255);
		slider_G.setSelection(255);
		slider_B.setSelection(255);
		slider_A.setSelection(255);

		btnApply = new Button(shell, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (LDrawPart part : MOCBuilder.getInstance()
						.getAllPartInFile()) {
					LDrawColor color = part.getLDrawColor();
					originalColor.put(part, color);
				}
			}
		});
		btnApply.setBounds(80, 108, 75, 25);
		btnApply.setText("Apply");

		btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (LDrawPart part : MOCBuilder.getInstance()
						.getAllPartInFile()) {
					part.setLDrawColor(originalColor.get(part));
				}
				slider_R.setSelection(255);
				slider_G.setSelection(255);
				slider_B.setSelection(255);
				slider_A.setSelection(255);
			}
		});
		btnNewButton.setBounds(164, 108, 75, 25);
		btnNewButton.setText("Calcel");
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		float adjustRGBA[] = new float[4];
		adjustRGBA[0] = (slider_R.getSelection() - 255) / 255.0f;
		adjustRGBA[1] = (slider_G.getSelection() - 255) / 255.0f;
		adjustRGBA[2] = (slider_B.getSelection() - 255) / 255.0f;
		adjustRGBA[3] = (slider_A.getSelection() - 255) / 255.0f;

		for (LDrawPart part : MOCBuilder.getInstance().getAllPartInFile()) {
			float[] rgba = new float[4];
			LDrawColor color = originalColor.get(part);
			color.getColorRGBA(rgba);

			for (int i = 0; i < 4; i++)
				rgba[i] += adjustRGBA[i];
			LDrawColor newColor = findClosedLDrawColor(rgba);
			part.setLDrawColor(newColor);
		}

	}

	private LDrawColor findClosedLDrawColor(float[] rgba) {
		float minimumDistance = Float.MAX_VALUE;
		LDrawColorT closedColor = null;
		for (LDrawColorT colorT : LDrawColorT.values()) {
			if (colorT.getValue() <= 0)
				continue;
			float[] tempRGBA = new float[4];
			LDrawColor color = ColorLibrary.sharedColorLibrary().colorForCode(
					colorT);
			color.getColorRGBA(tempRGBA);
			float tempDistance = getDistance(rgba, tempRGBA);
			if (minimumDistance > tempDistance) {
				minimumDistance = tempDistance;
				closedColor = colorT;
			}
		}
		return ColorLibrary.sharedColorLibrary().colorForCode(closedColor);
	}

	public int getDistance(float[] rgba1, float[] rgba2) {
		int distance = 0;
		for (int i = 0; i < 4; i++)
			distance += (int) Math.pow(255 * (rgba1[i] - rgba2[i]), 2);

		return distance;
	}
}
