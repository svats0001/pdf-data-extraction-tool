import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
 * This class encompasses all the functionality for generating a list of all the tables found in a PDF document as 
 * well as the option to export individual tables to a CSV file. It contains methods for generating a list of 
 * tables, creating relevant widgets for viewing each table and exporting individual tables.
 * @author     svats0001
 * @version    1.0
 * @since      28/04/22
 */
public class PdfTable {
	/**
	 * The title of the table
	 */
	private String title;
	/**
	 * A list of strings containing all the data for the rows and columns of the table
	 */
	private ArrayList<String> uiTable;
	/**
	 * A table widget for displaying the table information in the user interface
	 */
	private Table displayTable = null;
	/**
	 * Each row in the table divided into individual cells so that the data in the Table widget can be filled
	 */
	private ArrayList<ArrayList<String>> rowsAndCells = new ArrayList<ArrayList<String>>();
	/**
	 * Constructor that sets the title of the PdfTable object and initialises the uiTable property with the table
	 * data
	 * @param table     The data for the table, which is a list of rows including the column titles
	 */
	public PdfTable(ArrayList<String> table) {
		title = "";
		uiTable = table;
	}
	/**
	 * Sets the title property with a user provided value
	 * @param title     A title string
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * Returns the title property of the PdfTable object
	 * @return     A title string
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Sets the value of the uiTable property with a provided list of table data
	 * @param table     A list of table data
	 */
	public void setUiTable(ArrayList<String> table) {
		uiTable = table;
	}
	/**
	 * Get the value of the uiTable property
	 * @return         A list of strings of table data
	 */
	public ArrayList<String> getUiTable() {
		return uiTable;
	}
	/**
	 * Sets the value of the displayTable property
	 * @param table    An SWT table widget
	 */
	public void setDisplayTable(Table table) {
		displayTable = table;
	}
	/**
	 * Returns the value of the displayTable property
	 * @return         The value of the displayTable property, which is an SWT table widget  
	 */
	public Table getDisplayTable() {
		return displayTable;
	}
	/**
	 * Sets the value of the rowsAndCells property
	 * @param cellData     A list of rows containing table data and a list of cells for each row
	 */
	public void setRowsAndCells(ArrayList<ArrayList<String>> cellData) {
		rowsAndCells = cellData;
	}
	/**
	 * Get the value of the rowsAndCells property
	 * @return     The value of the rowsAndCells property, which is a list of lists containing table data rows and 
	 * their respective list of cells
	 */
	public ArrayList<ArrayList<String>> getRowsAndCells() {
		return rowsAndCells;
	}
	/**
	 * Given all the lines from the standard output of a Python file that was used to generate table data, returns
	 * a list of lists containing the table data for every individual table from the pool of all tables
	 * @param combinedTables     The table data of all the tables combined
	 * @return
	 */
	public static ArrayList<ArrayList<String>> conjureTables(ArrayList<String> combinedTables) {
		/**
		 * The resulting list of table data, split for each individual table in the overall pool of tables provided
		 */
		ArrayList<ArrayList<String>> individualTables = new ArrayList<ArrayList<String>>();
		/**
		 * The number of rows in one table
		 */
		int numRows = 0;
		for (int i = 0; i < combinedTables.size(); i++) {
			if (combinedTables.get(i).contains("DIMENSIONS*** OF** TABLE*")) {
				/**
				 * Table date for one table
				 */
				ArrayList<String> table = new ArrayList<String>();
				/**
				 * The dimensions of one table, which is information purposefully provided in particular lines 
				 * (the line before the table data begins) of the standard output of the Python file that is 
				 * used to generate the table data
				 */
				String[] dimensions = combinedTables.get(i).split(" ");
				if (dimensions.length == 5) {
				    numRows = Integer.parseInt(combinedTables.get(i).split(" ")[3]);
				}
			
				for (int j = i+1; j <= i+numRows+1; j++) {
					table.add(combinedTables.get(j)+" ");
			    }
				
				if (table.size() > 0) {
					//System.out.println(table);
					individualTables.add(table);
				}
				i = i + numRows;
			}
		}
		
		return individualTables;
	}
	/**
	 * Given the row data of a table, this method splits each of the rows into its individual cells based on the 
	 * fact that every cell ends at the same character index in the row. Sets the value of the rowsAndCells property
	 * of the PdfTable object at the end of the method.
	 */
	public void generateRowsAndCells() {
		rowsAndCells.clear();
		/**
		 * The table data of the PdfTable object
		 */
		ArrayList<String> tableToView = getUiTable();
		/**
		 * A list of indexes in each row whereby a split needs to occur in order to split the row into its
		 * individual cells
		 */
		ArrayList<Integer> splitIndexes = new ArrayList<Integer>();
		/**
		 * The number of rows in the table data
		 */
		int sizeOfTable = tableToView.size();
		/**
		 * The first row in the table data, usually for the column titles
		 */
		String firstRow = tableToView.get(0);
		for (int l = 0; l < firstRow.length()-1; l++) {
			if (firstRow.charAt(l) != ' ' && firstRow.charAt(l+1) == ' ') {
				/**
				 * Returns true if the current character is potentially the end of the cell, based on the fact that
				 * there is a non-space character followed by a space character
				 */
				boolean endCell = true;
				for (int m = 1; m < sizeOfTable; m++) {
					if (tableToView.get(m).charAt(l) != ' ' && tableToView.get(m).charAt(l+1) == ' ') {
						continue;
					}
					else {
						endCell = false;
						break;
					}
				}
				if (endCell) {
					splitIndexes.add(l+1);
					l++;
				}
		    }
		}
		
		for (int n = 0; n < sizeOfTable; n++) {
			/**
			 * A list of resulting cells in a row of the table
			 */
			ArrayList<String> oneRow = new ArrayList<String>();
			/**
			 * A row of the table
			 */
			String row = tableToView.get(n);
			/**
			 * The number of indexes where a split needs to occur within the row string
			 */
			int numPointsToSplit = splitIndexes.size();
			for (int o = 0; o < numPointsToSplit; o++) {
				if (o == 0) {
					oneRow.add(row.substring(0, splitIndexes.get(o)));
				}
				else {
					oneRow.add(row.substring(splitIndexes.get(o-1)+1, splitIndexes.get(o)));
				}
			}
			
			rowsAndCells.add(oneRow);
		}
	}
	/**
	 * Create a table widget from the table data provided and set the value of the displayTable property of the
	 * PdfTable object to the resulting SWT table widget
	 * @param content     The parent composite of the table widget to be created
	 */
	public void generateTable(Composite content) {
		generateRowsAndCells();
		/**
		 * The table widget to be created
		 */
		Table uiTable = null;
		
		if (rowsAndCells.size() > 0) {
			uiTable = new Table(content, SWT.BORDER | SWT.FULL_SELECTION);
			uiTable.setHeaderVisible(true);
			/**
			 * GridData object for the table that allows the table to fill any extra horizontal space
			 */
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
			gridData.horizontalSpan = 4;
			/**
			 * The height of every item in the table
			 */
			int tableItemHeight = uiTable.getItemHeight();
			gridData.heightHint = 20 * tableItemHeight;
			uiTable.setLayoutData(gridData);
			/**
			 * The number of columns in the table
			 */
			int numCols = 0;
			
			for (int i = 0; i < rowsAndCells.size(); i++) {
				/**
				 * A row of cells in the table data
				 */
				ArrayList<String> row = rowsAndCells.get(i);
				/**
				 * One row in the SWT table
				 */
				TableItem tItem = null;
				
				for (int j = 0; j < row.size(); j++) {
					if (i == 0) {
						numCols = row.size();
						/**
						 * A new column in the SWT table for each cell in the first row of table data
						 */
						TableColumn col = new TableColumn(uiTable, SWT.NULL);
						col.setText(row.get(j));
					}
					else {
						if (j == 0) {
							tItem = new TableItem(uiTable, SWT.NULL);
						}
						tItem.setText(j, row.get(j));
					}
				}
			}
			
			for (int k = 0; k < numCols; k++) {
				uiTable.getColumn(k).pack();
			}
		}
		
		displayTable = uiTable;
	}
	/**
	 * Generates an SWT list, SWT export button and SWT label to enable selection of individual tables in the user
	 * interface, as well as initiating the export of tables to a CSV file.
	 * @param shell               The parent shell of the widgets to be generated
	 * @param content             The parent composite of the list, button and label
	 * @param superContent        The parent composite of param content and the SWT table corresponding to the 
	 *                            displayTable property of the PdfTable object
	 * @param tables              A list of PdfTable objects for which items will be present in the list to be 
	 *                            created
	 */
	public static void generateList(Shell shell, Composite content, Composite superContent, ArrayList<PdfTable> tables) {
		/**
		 * Counter for iteration through the list of PdfTable objects in the tables parameter 
		 */
		int i = 0;
		/**
		 * An SWT List widget
		 */
		org.eclipse.swt.widgets.List titleList = null;
		while (i < tables.size()) {
			if (i == 0) {
				titleList = new org.eclipse.swt.widgets.List(content, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
				/**
				 * A GridData object for allowing the list to fill any extra horizontal space
				 */
				GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
				gridData.horizontalSpan = 5;
				titleList.setLayoutData(gridData);
			}
			
			titleList.add("Table " + (i+1));
			i++;
		}
		/**
		 * Button for initiating export of tables to CSV file
		 */
		Button exportTblBtn = new Button(content, SWT.PUSH);
		/**
		 * GridData object to allow the export button to fill any extra horizontal space
		 */
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 5;
		exportTblBtn.setLayoutData(gridData);
		exportTblBtn.setText("Export tables");
		/**
		 * A new label that provides instructions and results for the export table feature
		 */
		Label exportResultLabel = new Label(content, SWT.WRAP);
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 5;
		exportResultLabel.setLayoutData(gridData);
		exportResultLabel.setText("Select tables to export to CSV file");
		/**
		 * A copy of the SWT list to be used inside of the selection listener for the list
		 */
		final org.eclipse.swt.widgets.List _titleList = titleList; 
		titleList.addSelectionListener(new SelectionListener() {
			/**
			 * When the list is selected, dispose of any currently displayed tables and display the value of the
			 * displayTable of a selected PdfTable object if only one object is selected
			 * @param event     The click (or other type of selection) of an item in the list
			 */
			public void widgetSelected(SelectionEvent event) {
				exportResultLabel.setText("Select tables to export to CSV file");
				/**
				 * Array of the indices in the list that have been selected by the user
				 */
		        int[] selections = _titleList.getSelectionIndices();
		        /**
		         * int[] of selection indices converted to Integer[] of selection indices
		         */
		        Integer[] selectionsForList = Arrays.stream(selections).boxed().toArray(Integer[]::new);
		        /**
		         * Integer[] of selection indices converted to list of integers of selection indices
		         */
		        List<Integer> selectionsList = Arrays.asList(selectionsForList);
		        if (selections.length > 0) {
		        	for (int i = 0; i < tables.size(); i++) {
		        		/**
		        		 * The value of the displayTable property for each pdfTable object
		        		 */
		        		Table visibleTable = tables.get(i).getDisplayTable();
		        		if (visibleTable != null) {
		        		    if(!visibleTable.isDisposed()){
		        		    	visibleTable.dispose();
		        		    }
		        		    superContent.layout(true, true);
		        		}
		        		if (selectionsList.contains(i)) {
		        			tables.get(i).generateRowsAndCells();
		        		}
		        	}
		        }
		        if (selections.length == 1) {
		        	tables.get(selections[0]).generateTable(superContent);
		        	superContent.layout(true, true);
		        	shell.layout();
		        }
		    }
            /**
             * If there is a default selection on the list of tables, there is no change to the state of the system
             * @param event     Any default selection event, which is platform specific
             */
		    public void widgetDefaultSelected(SelectionEvent event) {
		        
		    }
		});
		
		exportTblBtn.addSelectionListener(new SelectionListener() {
			/**
			 * When the export table button is clicked, a new CSV file is created containing the tables that the
			 * user has selected
			 * @param event     Click of the button
			 */
			public void widgetSelected(SelectionEvent event) {
				int[] selections = _titleList.getSelectionIndices();
				try {
					if (selections.length > 0) {
					    generateTableCsv(tables, selections);
					    exportResultLabel.setText("CSV successfully generated at: " + FileInputScreen.getSaveLocationPath() + "\\" + FileInputScreen.fileName + "-tables-" + java.time.LocalDate.now() + ".csv");
					    content.layout(true, true);
					    superContent.layout(true, true);
					}
					else {
						exportResultLabel.setText("Select a table before clicking on export");
					}
				} 
				catch (IOException e) {
					exportResultLabel.setText("Unable to generate CSV file");
				}
            }
            /**
             * Upon default selection of the export tables button, there is no change to the state of the system
             */
            public void widgetDefaultSelected(SelectionEvent event) {
               
            }
		});
	}
	/**
	 * Replaces all carriage return characters with a space and any single double quotes with two consecutive
	 * double quotes
	 * @param data     Any string
	 * @return
	 */
	public static String escapeSpecialCharacters(String data) {
	    /**
	     * String without carriage return characters
	     */
		String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
	/**
	 * Converts a string array into a string whereby all values from the array are separated by a comma
	 * @param data     String array of data
	 * @return         String of comma separated values
	 */
	public static String convertToCsv(String[] data) {
		/**
		 * List of data to convert to CSV
		 */
		List<String> dataList = Arrays.asList(data);
		return dataList.stream().map(str -> escapeSpecialCharacters(str)).collect(Collectors.joining(","));
	}
	/**
	 * Generates a new file containing comma separated values for every table selected by the user to export
	 * @param tables               ArrayList of PdfTable objects
	 * @param selections           The selection indices of PdfTable objects in a list
	 * @throws IOException         Occurs if the file specified is unable to be found by the PrintWriter object
	 */
	public static void generateTableCsv(ArrayList<PdfTable> tables, int[] selections) throws IOException {
		/**
		 * A new CSV file in the same directory as the PDF file for which the tables were generated
		 */
		File csvOutputFile = new File(FileInputScreen.getSaveLocationPath() + "/" + FileInputScreen.fileName + "-tables-" + java.time.LocalDate.now() + ".csv");
		/**
		 * A new PrintWriter object for writing data to the newly created CSV file
		 */
		PrintWriter pw = new PrintWriter(csvOutputFile);
		
		for (int i = 0; i < selections.length; i++) {
			/**
			 * The row and cell data from each PdfTable selected by the user
			 */
			ArrayList<ArrayList<String>> tableData = tables.get(selections[i]).getRowsAndCells();
			/**
			 * A list of string arrays that will hold the row and cell data from each PdfTable selected by the user
			 */
			List<String[]> newTableData = new ArrayList<String[]>();
			
			for (int j = 0; j < tableData.size(); j++) {
				/**
				 * A row in the row and cell data of a PdfTable object selected by the user
				 */
				ArrayList<String> tableDataRow = tableData.get(j);
				newTableData.add(tableDataRow.toArray(new String[tableDataRow.size()]));
			}
			
			pw.println("Table " + (selections[i]+1));
			newTableData.stream().map(a -> convertToCsv(a)).forEach(pw::println);
			pw.println("--------------------***********************---------------------");
			pw.println("");
		}
		
		pw.close();
	}
}