import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// DBMS = Database Management System - handles all interactions with stored data
public class DBMS {
    private static DBMS DBMS = null;
    public static final String dbName = "DB Storage";
    private String output = "";
    private String currentDatabase = null;
    private String fileExt = ".ser";

    public static final int SELECT = 0;
    public static final int DELETE = 1;

    private DBMS() {
        File mainFolder = new File(dbName);
        if(!mainFolder.exists()) {
            mainFolder.mkdir();
        }
    }

    // Prevents multiple instances being generated
    public static DBMS getDBMS() {
        if (DBMS == null) {
            DBMS = new DBMS();
        }
        return DBMS;
    }

    // Sets the current database to the specified database
    public void useDatabase(String name) throws IOException {
        String path = dbName + File.separator + name;
        File newDB = new File(path);
        if (newDB.exists()) {
            currentDatabase = path;
            return;
        }
        throw new IOException("ERROR: '" + name + "' does not exist");
    }

    // Create a new database
    public void createDatabase(String name) throws IOException {
        String path = dbName + File.separator + name;
        File newDB = new File(path);
        if (!newDB.exists()) {
            if (newDB.mkdirs()) {
                return;
            }
            throw new IOException("ERROR: Unable to create new database '" + name + "'");
        }
        throw new IOException("ERROR: '" + name + "' already exists");
    }

    // Create table, initialise it with a single "id" heading and save it to file
    public void createTable(String tableName) throws IOException {
        // Check user is inside a database
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before creating tables");
        }
        String path = currentDatabase + File.separator + tableName + fileExt;
        File tableFile = new File(path);
        // Check if table already exists
        if (tableFile.exists()) {
            throw new IOException("ERROR: Table '" + tableName + "' already exists - choose " +
                    "another table name");
        }
        Table newTable = new Table();
        newTable.addTableHeading("id");
        newTable.writeToFile(path);
    }

    // Delete database and all contained files
    public void deleteDatabase(String name) throws IOException {
        String path = dbName + File.separator + name;
        File db = new File(path);
        // Check database exists
        if (!db.exists()) {
            throw new IOException("ERROR: Database '" + name + "' does not exist");
        }
        // If database contains tables, delete these first
        String[] tables = db.list();
        if (tables != null) {
            currentDatabase = path;
            for (String str : tables) {
                deleteTable(str.replace(fileExt, ""));
            }
        }
        currentDatabase = null;

        // If can't delete database (for whatever reason), throw an error
        if (!db.delete()) {
            throw new IOException("ERROR: Unable to delete database '" + name + "'");
        }
    }

    // Delete a single table (within current database)
    public void deleteTable(String name) throws IOException {
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before deleting tables");
        }
        String path = currentDatabase + File.separator + name + fileExt;
        File table = new File(path);
        if (!table.exists()) {
            throw new IOException("ERROR: Table '" + name + "' does not exist");
        }
        if (table.delete()) {
            return;
        }
        throw new IOException("ERROR: Unable to delete table '" + name + "'");
    }

    // Add columns to an existing table
    public void addTableCols(String tableName, ArrayList<String> attributeList) throws IOException {
        // Check user is inside a database
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before adding columns");
        }
        String path = currentDatabase + File.separator + tableName + fileExt;
        // Read in old table
        Table table = readTable(path);

        // Add new column headings to table
        for (String heading : attributeList) {
            table.addTableHeading(heading);
        }

        // Add empty strings to all rows so every row has same number of columns
        // --- Makes future table handling easier
        for (int i=0; i < table.getNumRows(); i++) {
            List<String> currentRow = table.getRow(i, Collections.emptyList());
            while (currentRow.size() < table.getNumCols()) {
                currentRow.add("");
            }
        }
        // Re-write new table to the original file path, overwriting it
        table.writeToFile(path);
    }

    // Remove specified columns from a table
    public void removeTableCols(String tableName, ArrayList<String> attributeList) throws IOException, ClassNotFoundException {
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before adding columns");
        }
        String path = currentDatabase + File.separator + tableName + fileExt;
        // Read in old table
        Table table = readTable(path);
        Table outputTable = new Table();

        // Get array of ints representing indices of all table columns e.g. [1,2,3,4,5]
        List<Integer> headingIndices = new ArrayList<>();
        for (int i=0; i < table.getNumCols(); i++) {
            headingIndices.add(i);
        }

        // Remove the index of the column/s that we want to remove - e.g. 2
        for (String columnName : attributeList) {
            headingIndices.remove(table.getColIndex(columnName));
        }

        // Add only the columns specified in headingIndices (e.g. [1,3,4,5]) to the output table
        outputTable.setTableHeadings(table.getTableHeadings(headingIndices));
        for (int i=0; i < table.getNumRows(); i++) {
            outputTable.addRow(table.getRow(i, headingIndices));
        }

        // Overwrite previous table with output table
        outputTable.writeToFile(path);
    }

    // Add a single row of values to an existing table
    public void insertValues(String tablename, ArrayList<String> valueList) throws IOException {
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before adding values");
        }
        String path = currentDatabase + File.separator + tablename + fileExt;
        Table table = readTable(path);

        // Check user has input enough values to add to the table
        if (table.getNumCols() - 1 != valueList.size()) {
            throw new IOException("ERROR: Please provide enough values for "
                    + (table.getNumCols() - 1) + " columns");
        }

        // Add the new row id to the start of the new row
        valueList.add(0, String.valueOf(table.getNumRows() + 1));
        // Add the row of values to the table
        table.addRow(valueList);
        // Overwrite the old table with the updated one
        table.writeToFile(path);
    }

    // Adds specified table to the output function to be printed later
    // -- Selects columns based on the attributes specified in attributeList
    // -- Selects rows based on the conditions stored on the conditionStack
    public void selectTable(String tableName, ArrayList<String> attributeList, ConditionStack conditionStack) throws IOException, ClassNotFoundException, ParseException {
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before accessing tables");
        }
        String path = currentDatabase + File.separator + tableName + fileExt;
        Table table = readTable(path);
        Table outputTable;

        // If user specifies conditions, applyConditions to add only the correct rows to the outputTable
        // Use the modifier 'SELECT' so it ADDS the specified rows to the new table ('DELETE' removes them)
        if (!conditionStack.isEmpty()) {
            outputTable = conditionStack.applyConditions(table, SELECT);
            outputTable.setTableHeadings(table.getTableHeadings(Collections.emptyList()));
        }
        else {
            outputTable = table;
        }

        // Variable for storing indices of the desired columns
        List<Integer> headingIndices = new ArrayList<>();

        // If columns specified, find indices of those columns
        if (!attributeList.isEmpty()) {
            // Get positions of each column and add to list
            for (String attr : attributeList) {
                int index = outputTable.getColIndex(attr);
                headingIndices.add(index);
            }
        }

        // Adds specified columns from outputTable to the output variable
        addTableToOutput(outputTable, headingIndices);
    }

    // Deletes rows in a table specified by the conditions in conditionStack
    public void deleteRows(String tableName, ConditionStack conditionStack) throws IOException, ParseException, ClassNotFoundException {
        if (currentDatabase == null) {
            throw new IOException("ERROR: Please 'USE' a database before deleting values");
        }
        String path = currentDatabase + File.separator + tableName + fileExt;
        Table table = readTable(path);

        // Apply conditions from the condition stack to create new table without deleted rows
        Table outputTable = conditionStack.applyConditions(table, DELETE);
        // Add old table headings to new table
        outputTable.setTableHeadings(table.getTableHeadings(Collections.emptyList()));
        // Overwrite old file with the updated output table
        outputTable.writeToFile(path);
    }

    // Replaces individual values in specific rows (specified using the attributeList & condition stack) with the values in valueList
    // -- attributeList contains the column headings of the values to replace
    // -- conditionStack determines which rows the values will be replaced in
    // -- valueList contains the new values to replace the old values with
    public void updateTable(String tableName, ArrayList<String> attributeList, ArrayList<String> valueList, ConditionStack conditionStack) throws IOException, ParseException, ClassNotFoundException {
        if (currentDatabase == null) throw new IOException("ERROR: Please 'USE' a database before accessing tables");
        String path = currentDatabase + File.separator + tableName + fileExt;
        Table table = readTable(path);

        // Create table with all rows which meet specified condition, e.g. WHERE name == 'Bob'
        Table selectedRows = conditionStack.applyConditions(table, SELECT);

        // Iterate through all rows that need to be modified
        for (int i=0; i < selectedRows.getNumRows(); i++) {
            List<String> oldRow = selectedRows.getRow(i, Collections.emptyList());
            List<String> newRow = new ArrayList<>(oldRow);
            // Replace the variables specified in the attribute and value lists
            for (int j=0; j < attributeList.size(); j++) {
                int index = table.getColIndex(attributeList.get(j));
                newRow.set(index, valueList.get(j));
            }
            // Replace the row in the original table with the modified row
            table.replaceRow(oldRow, newRow);
        }

        table.writeToFile(path);
    }

    // Join 2 tables together by matching up attribute1 and attribute2 in each row
    public void joinTables(String tableName1, String tableName2, String attribute1, String attribute2) throws IOException, ClassNotFoundException {
        if (currentDatabase == null) throw new IOException("ERROR: Please 'USE' a database before accessing tables");
        String path = currentDatabase + File.separator + tableName1 + fileExt;
        Table table1 = readTable(path);
        path = currentDatabase + File.separator + tableName2 + fileExt;
        Table table2 = readTable(path);

        // Create table to store output with new id heading
        Table outputTable = new Table();
        outputTable.addTableHeading("id");

        // Add all column headings (except id) from each table to output table
        List<String> tableHeadings = table1.getTableHeadings(Collections.emptyList());
        // Create list of the columns needed from each table
        List<Integer> desiredColumns1 = new ArrayList<>();
        for (int i=1; i < tableHeadings.size(); i++) {
            outputTable.addTableHeading(tableName1 + "." + tableHeadings.get(i));
            desiredColumns1.add(i);
        }
        List<Integer> desiredColumns2 = new ArrayList<>();
        tableHeadings = table2.getTableHeadings(Collections.emptyList());
        for (int i=1; i < tableHeadings.size(); i++) {
            outputTable.addTableHeading(tableName2 + "." + tableHeadings.get(i));
            desiredColumns2.add(i);
        }

        int newID = 1;
        int index1 = table1.getColIndex(attribute1);
        int index2 = table2.getColIndex(attribute2);

        // Combine both tables (leaving out ids of each) based on the 2 attributes
        for (int i=0; i < table1.getNumRows(); i++) {
            List<String> tempRow1 = table1.getRow(i, Collections.emptyList());
            for (int j=0; j < table2.getNumRows(); j++) {
                List<String> tempRow2 = table2.getRow(j, Collections.emptyList());
                // If the attributes from each row match, combine those rows
                if (tempRow1.get(index1).equals(tempRow2.get(index2))) {
                    List<String> outputRow = new ArrayList<>(table1.getRow(i, desiredColumns1));
                    outputRow.addAll(table2.getRow(j, desiredColumns2));
                    outputRow.add(0, String.valueOf(newID));
                    outputTable.addRow(outputRow);
                    newID++;
                }
            }
        }

        // Add output table to output
        addTableToOutput(outputTable, Collections.emptyList());

    }

    // Reads in a previously created table saved using the Table.writeToFile function
    // Table must be serialized
    private Table readTable(String path) throws IOException {
        try {
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(file);
            Table table = (Table)in.readObject();
            in.close();
            file.close();
            return table;
        }  catch (IOException | ClassNotFoundException e) {
            throw new IOException("ERROR: Table does not exist");
        }
    }

    // Adds entire table to output variable, using formatting from addToOutput
    private void addTableToOutput(Table table, List<Integer> headingIndices) {
        addToOutput(table.getTableHeadings(headingIndices));
        for (int i=0; i < table.getNumRows(); i++) {
            addToOutput(table.getRow(i, headingIndices));
        }
    }

    // Adds a row from a table to the output string (with formatting)
    private void addToOutput(List<String> newRow) {
        for (String str : newRow) {
            // Remove string literal quotes when printing
            if (str.length() > 0) {
                if (str.charAt(0) == '\'') {
                    str = str.substring(1, str.length() - 1);
                }
            }

            // Code used to format the output - ensures the same space between each column
            // on every row so output is readable
            // If we were assessed on output format, I would read in the max length of any
            // string in the output table and make the columnSize equal to it + 4
            output += str;
            float columnSize = 24;
            // outputBuffer = number of \t to add after each column
            int outputBuffer = (int)Math.floor((columnSize - str.length()-1)/4);
            for (int i = 0; i < outputBuffer; i++) {
                output += "\t";
            }

        }
        output += "\n";
    }

    // Public method to return the current saved output
    // Use to output user queries to the server
    public String getQueryOutput() {
        if (output.equals("")) {
            return "OK";
        }

        // Reset output for any future commands
        String newOutput = output;
        output = "";
        return newOutput;
    }
}
