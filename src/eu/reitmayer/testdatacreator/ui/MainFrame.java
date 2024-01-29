/**
 * 
 */
package eu.reitmayer.testdatacreator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

import eu.reitmayer.testdatacreator.model.ITestdataModel;
import eu.reitmayer.testdatacreator.model.TestdataModel;

/**
 * @author reitmayer
 *
 */
public class MainFrame extends JFrame {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}

	private static final byte[][] BYTE_ORDER_MARKS = {
		{}, // NONE
		{(byte)0xEF, (byte)0xBB, (byte)0xBF}, 	// UTF-8
		{(byte)0xFF, (byte)0xFE}, 				// UTF-16
		{(byte)0xFF, (byte)0xFE}, 				// UTF-16LE
		{(byte)0xFE, (byte)0xFF}				// UTF-16BE
	};
	
	private ITestdataModel model = new TestdataModel();
	
	private TestDataTableModel testDataTableModel = new TestDataTableModel();
	
	private FormatInfoTableModel formatInfoTableModel = new FormatInfoTableModel();
	
	private JTable tableData = null;
	
	private JTable tableFormat = null;
	
	private JPanel panelFormat = null;
	
	private JComboBox comboBoxFileEncoding = null;
	
	private JTextField textFieldSeparationSequence = null;
	
	private JComboBox comboBoxLineSeparator = null;
	
	private JComboBox comboBoxByteOrderMark = null;
	
	private JScrollPane scPaneDataTable = null;
	
	private JScrollPane scPaneFormatTable = null;
	
	private ActionMap actionMap = new ActionMap();
	
	private JFileChooser fileChooser;
	
	public MainFrame() {
		super("TestDataCreator");
		ArrayList<Object> d = new ArrayList<Object>();
		d.add("Hallo");
		d.add("Welt!!!");
		model.getData().clear();
		model.getData().add(d);
		model.getFroms().add(1);
		model.getTos().add(10);
		model.getColumnJustification().add('L');
		model.getFroms().add(10);
		model.getTos().add(15);
		model.getColumnJustification().add('L');
		initialize();
	}

	private void initialize() {
		initializeActions();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		testDataTableModel.setTestData(model);
		formatInfoTableModel.setTestData(model);
		setLayout(new BorderLayout());
		panelFormat = new JPanel();
		panelFormat.setLayout(new BorderLayout());
		panelFormat.add(scPaneFormatTable = new JScrollPane(tableFormat = new JTable(formatInfoTableModel)), BorderLayout.CENTER);
		add(scPaneDataTable = new JScrollPane(tableData = new JTable(testDataTableModel)), BorderLayout.CENTER);
		add(panelFormat, BorderLayout.NORTH);
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableFormat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JMenuItem menuPastAsData = new JMenuItem(actionMap.get("paste-data"));
		JMenu menuPaste = new JMenu("Einfügen");
		menuPaste.add(menuPastAsData);
		menuPaste.add(new JMenuItem(actionMap.get("paste-format")));
		JMenu menuFile = new JMenu("Datei");
		JMenuItem menuItemExitApp = new JMenuItem(actionMap.get("exit-application"));
		menuFile.add(menuItemExitApp);
		JMenu menuCreate = new JMenu("Erzeugen");
		menuCreate.add(actionMap.get("create-csv"));
		menuCreate.add(actionMap.get("create-flat"));
		JMenuBar mb = new JMenuBar();
		mb.add(menuFile);
		mb.add(menuPaste);
		mb.add(menuCreate);
		setJMenuBar(mb);

		JPanel panelOtherSettings = new JPanel();
		panelOtherSettings.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelOtherSettings.add(new JLabel("Encoding:"));
		comboBoxFileEncoding = new JComboBox(Charset.availableCharsets().keySet().toArray(new String[0]));
		panelOtherSettings.add(comboBoxFileEncoding);
		panelOtherSettings.add(new JLabel("Zeilenende:"));
		String[] lineSeps = {"Windows", "UNIX"};
		comboBoxLineSeparator = new JComboBox(lineSeps);
		panelOtherSettings.add(comboBoxLineSeparator);
		panelOtherSettings.add(new JLabel("CSV-Separator:"));
		panelOtherSettings.add(textFieldSeparationSequence = new JTextField(3));
		textFieldSeparationSequence.setText(";");
		panelFormat.add(panelOtherSettings, BorderLayout.NORTH);
		
		panelOtherSettings.add(new JLabel("Byte Order Mark:"));
		String[] availableByteOrderMarks = {"NONE", "UTF-8", "UTF-16", "UTF-16LE", "UTF-16BE"};
		panelOtherSettings.add(comboBoxByteOrderMark = new JComboBox(availableByteOrderMarks));
		
		fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		
		pack();
	}

	private void initializeActions() {
		actionMap.put("exit-application", new AbstractAction("Beenden") {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		actionMap.put("paste-data", new AbstractAction("Daten einfügen") {

			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard.isDataFlavorAvailable(DataFlavor.plainTextFlavor)) {
					try {
						StringReader sr = (StringReader)clipboard.getData(DataFlavor.plainTextFlavor);
						StringBuilder sb = new StringBuilder();
						int c;
						while ((c = sr.read()) != -1) {
							sb.append((char)c);
						}
						String text = sb.toString();
						String[] lines = text.split("\n");
						List<List<Object>> data = new ArrayList<List<Object>>();
						Pattern p = Pattern.compile("\t");
						for (String line: lines) {
							String[] elements = p.split(line, -1); // the limit param with -1 forces the pattern to leave trailing nullcols
							System.out.println("Element Count: " + elements.length);
							List<Object> lineData = new ArrayList<Object>();
							for (String elem: elements) {
								lineData.add(elem == null ? "" : elem);
							}
							data.add(lineData);
						}
						model.setData(data);
						tableData.setModel(testDataTableModel = new TestDataTableModel(model));
						tableData.updateUI();
					} catch (UnsupportedFlavorException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		

		actionMap.put("paste-format", new AbstractAction("Format einfügen"){
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard.isDataFlavorAvailable(DataFlavor.plainTextFlavor)) {
					try {
						StringReader sr = (StringReader)clipboard.getData(DataFlavor.plainTextFlavor);
						StringBuilder sb = new StringBuilder();
						int c;
						while ((c = sr.read()) != -1) { // -1 indicates EOF
							sb.append((char)c);
						}
						String text = sb.toString();
						String[] textLines = text.split("\n");
						if (textLines.length != 3) {
							JOptionPane.showMessageDialog(MainFrame.this, 
									"Die Formatbeschreibung muss genau 3 Zeilen enthalten", 
									"Falsche Zeilenanzahl", JOptionPane.ERROR_MESSAGE);
							return;
						}
						List<Integer> froms = new ArrayList<Integer>();
						List<Integer> tos = new ArrayList<Integer>();
						List<Character> justification = new ArrayList<Character>();
						
						Pattern p = Pattern.compile("\t", Pattern.CASE_INSENSITIVE);

						// parsing "from" line
						String[] stringFroms = p.split(textLines[0]);
						for (String s: stringFroms) {
							froms.add(Integer.parseInt(s));
						}
						
						// parsing "to" line
						String[] stringTos = p.split(textLines[1]);
						for (String s: stringTos) {
							tos.add(Integer.parseInt(s));
						}
						
						// parsing justification
						String[] stringJustify = p.split(textLines[2]);
						for (String s: stringJustify) {
							justification.add(s.charAt(0));
						}
						
						model.setColumnJustification(justification);
						model.setFroms(froms);
						model.setTos(tos);
						tableFormat.setModel(formatInfoTableModel = new FormatInfoTableModel(model));
						tableFormat.updateUI();
					} catch (UnsupportedFlavorException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		actionMap.put("create-csv", new AbstractAction("CSV Erzeugen") {

			public void actionPerformed(ActionEvent e) {
				createCsv();
			}

			
		});
		
		actionMap.put("create-flat", new AbstractAction("Flat File Erzeugen"){

			public void actionPerformed(ActionEvent e) {
				createFlat();
			}

		});
	}

	private void createCsv() {
		File f;
		if (! checkModel()) return;
		if ((f = openSaveFileDialog()) == null) return;
		try {
			FileOutputStream unencodedStream = new FileOutputStream(f);
			unencodedStream.write(BYTE_ORDER_MARKS[comboBoxByteOrderMark.getSelectedIndex()]);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(unencodedStream, comboBoxFileEncoding.getSelectedItem().toString()));
			String separator = textFieldSeparationSequence.getText();
			String lineSeparator = System.getenv("line.separator");
			if (comboBoxLineSeparator.getSelectedItem().equals("Windows")) {
				lineSeparator = new String(new char[]{13,10});
				System.out.println("windows");
			} else {
				lineSeparator = new String(new char[]{10});
				System.out.println("unix");
			}
			Iterator<List<Object>> iterObjects = model.getData().iterator();
			while (iterObjects.hasNext()) {
				List<Object> objects = iterObjects.next();
				Iterator<Object> iter = objects.iterator();
				while (iter.hasNext()) {
					Object o = iter.next();
					bw.write(o == null ? "" : o.toString());
					if (iter.hasNext()) bw.write(separator);
				}
				if (iterObjects.hasNext()) bw.write(lineSeparator);
			}
			bw.flush();
			bw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void createFlat() {
		File f;
		if (! checkModel()) return;
		if ((f = openSaveFileDialog()) == null) return;
		for (int i=1; i<model.getFroms().size(); i++) {
			if (model.getFroms().get(i) - model.getTos().get(i - 1) > 1) {
				System.out.println("gap detected");
			}
		}
		List<Integer> lengths = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<model.getFroms().size(); i++) {
			lengths.add(model.getTos().get(i) - (model.getFroms().get(i)-1));
			System.out.println(model.getTos().get(i) - (model.getFroms().get(i)-1));
			sb.append("%");
			sb.append(i+1);
			sb.append("$");
			if (model.getColumnJustification().get(i).equals('L')) sb.append("-");
			sb.append(model.getTos().get(i) - (model.getFroms().get(i)-1));
			sb.append("s");
		}
		String formatString = sb.toString();
		String lineSeparator = System.getenv("line.separator");
		if (comboBoxLineSeparator.getSelectedItem().equals("Windows")) {
			lineSeparator = new String(new char[]{13,10});
			System.out.println("windows");
		} else {
			lineSeparator = new String(new char[]{10});
			System.out.println("unix");
		}
		System.out.println(formatString);
		try {
			FileOutputStream unencodedStream = new FileOutputStream(f);
			unencodedStream.write(BYTE_ORDER_MARKS[comboBoxByteOrderMark.getSelectedIndex()]);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(unencodedStream, comboBoxFileEncoding.getSelectedItem().toString()));
			for (List<Object> l: model.getData()) {
				bw.write(String.format(formatString, l.toArray(new Object[0])));
				bw.write(lineSeparator);
			}
			bw.flush();
			bw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkModel() {
		if (model.getData().size() == 0) {
			JOptionPane.showMessageDialog(this, 
					"Keine Datenzum Exportieren", 
					"Keine Daten", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		int dataColumns = model.getData().get(0).size();
		if (model.getColumnJustification().size() == dataColumns &&
				model.getFroms().size() == dataColumns &&
				model.getTos().size() == dataColumns) return true;

		JOptionPane.showMessageDialog(this, 
				"Die Anzahl der Spalten in der Formatbeschreibung stimmt nicht mit der \nAnzahl der Spalten in den Daten �berein.",
				"Unterschiedliche Anzahl an Spalten", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	private File openSaveFileDialog() {
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) return null;
		return fileChooser.getSelectedFile();
	}
}
