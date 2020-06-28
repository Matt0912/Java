import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table implements java.io.Serializable{
    private List<String> tableHeadings;
    private List<List<String>> tableData;

    public Table() {
        tableData = new ArrayList<>();
        tableHeadings = new ArrayList<>();
    }

    // Returns column headings for columns specified in the integer list - pass an empty list if
    // all headings are required
    public List<String> getTableHeadings(List<Integer> columnIndices) {
        if (columnIndices.isEmpty()) {
            return tableHeadings;
        }

        // For each index specified in columnIndices, add respective heading
        List<String> heading = new ArrayList<>();
        for (int index : columnIndices) {
            heading.add(tableHeadings.get(index));
        }
        return heading;

    }

    public int getNumCols() {
        return tableHeadings.size();
    }

    public int getNumRows() {
        return tableData.size();
    }

    // Return the position of a column heading - throws exception if not found
    public int getColIndex(String columnHeading) throws ClassNotFoundException {
        for (String str : tableHeadings) {
            if (columnHeading.equals(str)) {
                return tableHeadings.indexOf(str);
            }
        }
        throw new ClassNotFoundException("ERROR: Invalid table heading '" + columnHeading + "'");
    }

    // Specify row number and the columns required in an integer list
    // - pass an empty list if entire row is required
    public List<String> getRow(int rowNumber, List<Integer> columnIndices) {
        List<String> row = tableData.get(rowNumber);
        if (columnIndices.isEmpty()) {
            return row;
        }

        // For each index specified in columnIndices, add respective row value
        List<String> newRow = new ArrayList<>();
        for (int index : columnIndices) {
            newRow.add(row.get(index));
        }
        return newRow;

    }

    // Finds the old row and replaces with the new row
    public void replaceRow(List<String> oldRow, List<String> newRow) throws IOException {
        for (int i=0; i < tableData.size(); i++) {
            if (oldRow.equals(tableData.get(i))) {
                tableData.remove(i);
                tableData.add(i, newRow);
                return;
            }
        }
        throw new IOException("ERROR: Unable to replace row in table - couldn't find '" + oldRow.toString() + "'");
    }

    public void setTableHeadings(List<String> newHeadings) {
        tableHeadings = newHeadings;
    }

    public void addTableHeading(String newHeading) {
        tableHeadings.add(newHeading);
    }

    public void addRow(List<String> rowData) {
        tableData.add(rowData);
    }

    public void writeToFile(String path) throws IOException {
        try {
            FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(this);

            out.close();
            file.close();
        } catch (IOException e) {
            throw new IOException("ERROR: Unable to write data to file");
        }
    }


}
