/**
 * 
 */
package eu.reitmayer.testdatacreator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author reitmayer
 *
 */
public class TestdataModel implements ITestdataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<List<Object>> data = new ArrayList<List<Object>>();
	
	private List<Integer> froms = new ArrayList<Integer>();
	
	private List<Integer> tos = new ArrayList<Integer>();
	
	private List<Character> columnJustification = new ArrayList<Character>();
	
	private String fileEncoding = System.getProperty("file.encoding");
	
	private String csvSeparator = ";";

	public TestdataModel() {
		List<Object> emptyRow = new ArrayList<Object>();
		data.add(emptyRow);
	}
	
	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		data.forEach( r -> r.forEach( c -> { 
			String s = (String)c;
			s.trim();
			c = s;
		}));
		this.data = data;
	}

	public List<Integer> getFroms() {
		return froms;
	}

	public void setFroms(List<Integer> froms) {
		this.froms = froms;
	}

	public List<Integer> getTos() {
		return tos;
	}

	public void setTos(List<Integer> tos) {
		this.tos = tos;
	}

	public List<Character> getColumnJustification() {
		return columnJustification;
	}

	public void setColumnJustification(List<Character> columnJustification) {
		this.columnJustification = columnJustification;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public String getCsvSeparator() {
		return csvSeparator;
	}

	public void setCsvSeparator(String csvSeparator) {
		this.csvSeparator = csvSeparator;
	}
	
}
