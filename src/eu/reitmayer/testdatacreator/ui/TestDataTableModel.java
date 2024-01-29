/**
 * 
 */
package eu.reitmayer.testdatacreator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import eu.reitmayer.testdatacreator.model.ITestdataModel;
import eu.reitmayer.testdatacreator.model.TestdataModel;

/**
 * @author reitmayer
 *
 */
public class TestDataTableModel implements TableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ITestdataModel testData = new TestdataModel();

	private List<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
	
	public void addTableModelListener(TableModelListener l) {
		if (l != null) tableModelListeners.add(l);
	}

	public TestDataTableModel() {
		
	}
	
	public TestDataTableModel(ITestdataModel testData) {
		this.testData = testData;
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		if (testData.getData().size() == 0) return 0;
		int colCount = testData.getData().get(0).size();
		System.out.println(colCount);
		return colCount;
	}

	public String getColumnName(int columnIndex) {
		return "Column " + columnIndex;
	}

	public int getRowCount() {
		return testData.getData().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return testData.getData().get(rowIndex).get(columnIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
		if (l != null) tableModelListeners.remove(l);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		testData.getData().get(rowIndex).set(columnIndex, value);
		fireTableModelUpdated(new TableModelEvent(this, rowIndex, columnIndex));
	}
	
	private void fireTableModelUpdated(TableModelEvent tmev) {
		for (TableModelListener l: tableModelListeners) {
			if (l != null) {
				l.tableChanged(tmev);
			}
		}
	}

	public ITestdataModel getTestData() {
		return testData;
	}

	public void setTestData(ITestdataModel testData) {
		this.testData = testData;
		fireTableModelUpdated(new TableModelEvent(this));
	}
	
}
