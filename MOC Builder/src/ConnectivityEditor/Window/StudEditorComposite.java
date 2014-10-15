package ConnectivityEditor.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import Command.LDrawPart;
import Connectivity.Connectivity;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import ConnectivityEditor.Connectivity.ConnectivityGenerator;
import ConnectivityEditor.Connectivity.StudT;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.RowData;

public class StudEditorComposite extends Composite {
	private Table table;
	private Combo combo_nColumns;
	private Combo combo_nRows;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StudEditorComposite(Composite parent, int style) {
		super(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		setLayout(rowLayout);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new RowData(224, 52));

		combo_nColumns = new Combo(group, SWT.READ_ONLY);
		combo_nColumns.setLocation(10, 31);
		combo_nColumns.setSize(90, 23);
		combo_nColumns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				generateTableContents();
			}
		});

		

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLocation(110, 10);
		lblNewLabel.setSize(38, 15);
		lblNewLabel.setText("# Rows");

		combo_nRows = new Combo(group, SWT.READ_ONLY);
		combo_nRows.setLocation(108, 32);
		combo_nRows.setSize(90, 23);
		combo_nRows.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				generateTableContents();
			}
		});
		

		Label lblColumn = new Label(group, SWT.NONE);
		lblColumn.setBounds(10, 10, 53, 15);
		lblColumn.setText("# Column");

		for (int i = 1; i < 20; i++)
			combo_nColumns.add("" + i);

		for (int i = 1; i < 20; i++)
			combo_nRows.add("" + i);
		
		combo_nRows.select(0);
		combo_nColumns.select(0);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		Button btnGenerate = new Button(this, SWT.NONE);
		btnGenerate.setLayoutData(new RowData(96, 47));
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleGenerateStud();
			}
		});
		btnGenerate.setText("Generate");

		generateTableContents();
	}

	protected void handleGenerateStud() {
		int nColumns = Integer.parseInt(combo_nColumns.getText());
		int nRows = Integer.parseInt(combo_nRows.getText());

		StudT[][] types = new StudT[nColumns][nRows];
		for (int row = 0; row < nRows; row++) {
			TableItem tableItem = table.getItem(row);
			for (int column = 0; column < nColumns; column++) {
				types[column][row] = StudT.valueOf(((CCombo) tableItem.getData("" + column)).getText());
			}
		}

		Connectivity newStud = ConnectivityGenerator.getInstance()
				.generateStud(nColumns, nRows, types);
		newStud.setParent(ConnectivityEditor.getInstance().getWorkingPart());
		MatrixItem[][] matrixItmes = ((ICustom2DField) newStud).getMatrixItem();
		for (int column = 0; column < matrixItmes.length; column++)
			for (int row = 0; row < matrixItmes[column].length; row++) {
				matrixItmes[column][row].setParent(newStud);
				matrixItmes[column][row].setColumnIndex(column);
				matrixItmes[column][row].setRowIndex(row);
			}

		ConnectivityEditor.getInstance().addConnectivity(newStud);
	}

	private void generateTableContents() {
		table.setVisible(false);
		table.setRedraw(false);

		while (table.getColumnCount() > 0) {
			table.getColumns()[0].dispose();
		}
		table.removeAll();

		int nColumns = Integer.parseInt(combo_nColumns.getText());
		int nRows = Integer.parseInt(combo_nRows.getText());

		for (int j = 0; j < nColumns; j++) {
			final TableColumn column = new TableColumn(table, SWT.NULL);
			column.setWidth(80);
		}
		for (int i = 0; i < nRows; i++) {
			final TableItem item = new TableItem(table, SWT.NULL);
			for (int j = 0; j < nColumns; j++) {
				CCombo combo = new CCombo(table, SWT.READ_ONLY);
				for(StudT type : StudT.values())
					combo.add(type.toString());
				combo.select(0);
				combo.pack();
				TableEditor editor = new TableEditor(table);
				editor.grabHorizontal = true;
				editor.setEditor(combo, item, j);
				item.setData("" + j, combo);
			}
			item.setData("nColumns", new Integer(nColumns));
			item.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					for (int i = 0; i < (Integer) (item.getData("nColumns")); i++) {
						((CCombo) (item.getData("" + i))).dispose();
					}
				}
			});
		}
		table.pack();
		table.setRedraw(true);
		table.setVisible(true);
		table.redraw();
		this.pack();
		this.layout();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
