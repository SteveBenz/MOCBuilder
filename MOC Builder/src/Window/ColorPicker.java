package Window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import Color.ColorCategoryT;
import Command.LDrawColor;
import Command.LDrawColorT;
import LDraw.Support.ColorLibrary;

public class ColorPicker {
	private static HashMap<LDrawColorT, Image> imageMap = null;
	Shell shell;
	Widget widget;
	String title;
	SelectionListener listener;

	public ColorPicker(Widget widget, String title) {
		this.widget = widget;
		this.title = title;

		if (imageMap == null) {
			initColors();
		}
		setColor(LDrawColorT.LDrawCurrentColor);
	}

	public void addSelectionListener(SelectionListener listener) {
		this.listener = listener;
	}

	public void setColor(LDrawColorT color) {
		widget.setData(color);
		if (widget instanceof Button) {
			((Button) widget).setImage(imageMap.get(color));
			((Button) widget).setToolTipText(color.name().substring(5));
		} else if (widget instanceof ToolItem) {
			((ToolItem) widget).setImage(imageMap.get(color));
			((ToolItem) widget).setToolTipText(color.name().substring(5));
		}
	}
	
	public LDrawColorT getSelectedColor(){
		return (LDrawColorT) widget.getData();
	}

	public void setColor(LDrawColor color) {
		setColor(color.colorCode());
	}

	public void showDialog() {
		if (shell == null || shell.isDisposed()) {
			Display display = widget.getDisplay();
			shell = new Shell(display, SWT.CLOSE | SWT.TOOL);
			shell.setText(title);
			shell.setLayout(new FormLayout());
			TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
			tabFolder.setLayout(new RowLayout(SWT.HORIZONTAL));
			FormData fd_group = new FormData();
			fd_group.top = new FormAttachment(0, 0);
			fd_group.left = new FormAttachment(0, 0);
			fd_group.bottom = new FormAttachment(0, 210);
			fd_group.right = new FormAttachment(0, 600);
			tabFolder.setLayoutData(fd_group);						
			int numOfColumns = 16;
			Button button;
			Composite unit;
			int counter = 0;
			for (ColorCategoryT colorCategoryT : ColorCategoryT.values()) {
				if (ColorLibrary.sharedColorLibrary()
						.getColorTList(colorCategoryT).size() == 0)
					continue;				
				unit = new Composite(tabFolder, SWT.NONE);
				unit.setLayout(new GridLayout(numOfColumns,  true));				
				TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);			
				tbtmNewItem.setControl(unit);
				tbtmNewItem.setText(""+colorCategoryT);				
				counter = 0;
				for (LDrawColorT colorT : ColorLibrary.sharedColorLibrary()
						.getColorTList(colorCategoryT)) {
					button = new Button(unit, SWT.FLAT);
					button.setImage(imageMap.get(colorT));
					button.setData(colorT);
					button.setToolTipText(colorT.name().substring(5));
					button.addSelectionListener(listener);
					counter++;
				}
				if (counter >= numOfColumns)
					unit.setLayout(new GridLayout(numOfColumns, true));
				else
					unit.setLayout(new GridLayout(counter, true));				
				unit.pack();
			}
			tabFolder.pack();
			shell.pack();
			shell.open();
		} else if (shell.isVisible()) {
			shell.setVisible(false);
		} else {
			shell.open();
		}
	}

	private void initColors() {
		imageMap = new HashMap<LDrawColorT, Image>();

		LDrawColor color;
		ColorLibrary library = ColorLibrary.sharedColorLibrary();
		LDrawColorT colors[] = LDrawColorT.values();
		for (int i = 0; i < colors.length; i++) {
			color = library.colorForCode(colors[i]);
			imageMap.put(colors[i], colorImage(color));
		}
	}

	Image colorImage(LDrawColor color) {
		Display display = widget.getDisplay();
		Image image = new Image(display, 20, 20);
		Rectangle bounds = image.getBounds();
		GC gc = new GC(image);
		float rgba[] = new float[4];
		
		color.getEdgeColorRGBA(rgba);				
		gc.setBackground(new Color(display, (int) (rgba[0] * 255),
				(int) (rgba[1] * 255), (int) (rgba[2] * 255)));		
		gc.setAlpha((int) (rgba[3] * 255));
		gc.fillRectangle(0, 0, bounds.width, bounds.height);
		
		color.getColorRGBA(rgba);		
		gc.setBackground(new Color(display, (int) (rgba[0] * 255),
				(int) (rgba[1] * 255), (int) (rgba[2] * 255)));
		gc.setAlpha((int) (rgba[3] * 255));
		
		
		gc.fillRectangle(2, 2, bounds.width-4, bounds.height-4);
		
		gc.dispose();
		return image;
	}
}
