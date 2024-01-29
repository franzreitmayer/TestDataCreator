/**
 * 
 */
package eu.reitmayer.testdatacreator.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Franz Reitmayer
 *
 */
public interface ITestdataModel extends Serializable {

	public List<List<Object>> getData();
	
	public void setData(List<List<Object>> data);
	
	public List<Integer> getFroms();
	
	public void setFroms(List<Integer> froms);
	
	public List<Integer> getTos();
	
	public void setTos(List<Integer> tos);
	
	public List<Character> getColumnJustification();
	
	public void setColumnJustification(List<Character> columnJustification);
	
	public String getFileEncoding();
	
	public void setFileEncoding(String encoding);
	
	public String getCsvSeparator();
	
	public void setCsvSeparator(String csvSeparator);
}
