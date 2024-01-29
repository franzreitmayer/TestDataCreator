/**
 * 
 */
package eu.reitmayer.testdatacreator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import eu.reitmayer.testdatacreator.model.ITestdataModel;
import eu.reitmayer.testdatacreator.model.TestdataModel;

/**
 * @author reitmayer
 *
 */
public class FormatInfoTableModel implements TableModel {

	private ITestdataModel testData = new TestdataModel();
	
	public FormatInfoTableModel() {
		
	}
	
	public FormatInfoTableModel(ITestdataModel testData) {
		this.testData = testData;
	}
	
	private List<TableModelListener> tableModelListeners =
		new ArrayList<TableModelListener>();
	
	public void addTableModelListener(TableModelListener l) {
		if (l != null) tableModelListeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return testData.getFroms().size();
	}

	public String getColumnName(int columnIndex) {
		return "Column " + columnIndex;
	}

	public int getRowCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (rowIndex) {
		case 0: {
			return testData.getFroms().get(columnIndex);
		}
		case 1: {
			return testData.getTos().get(columnIndex);
		}
		case 2: {
			return testData.getColumnJustification().get(columnIndex);
		}
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
		if (l != null) tableModelListeners.remove(l);
	}
	
	private void fireTableModelUpdated(TableModelEvent tmev) {
		for (TableModelListener l: tableModelListeners) {
			if (l != null) {
				l.tableChanged(tmev);
			}
		}
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		switch (rowIndex) {
		case 0: {
			testData.getFroms().set(columnIndex, (Integer)value);
			break;
		}
		case 1: {
			testData.getTos().set(columnIndex, (Integer)value);
			break;
		}
		case 2: {
			testData.getColumnJustification().set(columnIndex, (Character)value);
			break;
		}
		}
		fireTableModelUpdated(new TableModelEvent(this, rowIndex, columnIndex));
	}

	public ITestdataModel getTestData() {
		return testData;
	}

	public void setTestData(ITestdataModel testData) {
		this.testData = testData;
		fireTableModelUpdated(new TableModelEvent(this));
	}
}
