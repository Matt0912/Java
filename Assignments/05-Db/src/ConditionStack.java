import java.text.ParseException;
import java.util.*;

// Values and operators are added as they are encountered when parsing
// user input (in the parser class)
// These conditions are then evaluated for each row in a table when called from the DBMS class
public class ConditionStack {
    private List<String> operators;
    private List<String> values;
    private Stack<Boolean> booleans;
    private Stack<String> connectors;
    private Table currentTable;

    public ConditionStack() {
        operators = new LinkedList<>();
        values = new LinkedList<>();
        booleans = new Stack<>();
    }

    // Method takes in current table, returns new table with user conditions applied
    // If command modifier == SELECT, add all matching rows to return table
    // If command modifier == DELETE, only add rows which don't match to return table (effectively
    // deleting them)
    public Table applyConditions(Table table, int commandModifier) throws ClassNotFoundException, ParseException {
        currentTable = table;
        Table newTable = new Table();
        // Iterate through all rows
        for (int i=0; i < currentTable.getNumRows(); i++) {
            List<String> currentRow = currentTable.getRow(i, Collections.emptyList());
            if (commandModifier == DBMS.SELECT) {
                if (evaluateRow(currentRow)) {
                    newTable.addRow(currentRow);
                }
            }
            if (commandModifier == DBMS.DELETE) {
                if (!evaluateRow(currentRow)) {
                    newTable.addRow(currentRow);
                }
            }
        }
        return newTable;
    }

    // Evaluates each row by:
    // --- checking each condition already stored (in order) in values & operators lists
    // --- adding the result of each condition check to the booleans stack
    // --- adding to the connectors (AND/OR) stack
    // --- popping 2 x booleans and a connector, then add the result back to the booleans stack:
    // ------- e.g. TRUE OR FALSE = TRUE
    // --- Returning the value on the boolean stack when it has size == 1
    private boolean evaluateRow(List<String> row) throws ClassNotFoundException, ParseException {
        Iterator<String> valueIt = values.iterator();
        Iterator<String> opIt = operators.iterator();
        // Create new stack for connectors (AND/OR) for each row so that values can be popped and pushed
        connectors = new Stack<>();

        // While there are values in the list, keep evaluating conditions
        while (valueIt.hasNext()) {
            evaluateCondition(row, valueIt, opIt);
        }

        // If user specifies one condition it doesn't need evaluating at the end
        // e.g. WHERE name == 'Bob' simply adds true/false to the stack, which is the final result;
        if (booleans.size() > 1) {
            evaluateBooleans();
        }

        return booleans.pop();
    }

    // Results of evaluate condition are stored in booleans list - evaluate booleans after to determine
    // whether row should be included
    private void evaluateCondition(List<String> row, Iterator<String> valueIt, Iterator<String> opIt) throws ParseException, ClassNotFoundException {
        String attribute = valueIt.next();
        // Checks if an attribute is a connector or closing bracket
        if (isSpecialValue(attribute)) return;

        String operator = opIt.next();
        String value = valueIt.next();
        int booleansSize = booleans.size();
        int attributeIndex = currentTable.getColIndex(attribute);

        // If value is an integer or float
        try {
            Float numValue = Float.parseFloat(value);
            Float numAttribute = Float.parseFloat(row.get(attributeIndex));

            // For each operator, push true/false to boolean stack depending on result
            if (operator.equals("==")) booleans.push(numAttribute.equals(numValue));
            if (operator.equals("!=")) booleans.push(!numAttribute.equals(numValue));
            if (operator.equals(">")) booleans.push(numAttribute > numValue);
            if (operator.equals("<")) booleans.push(numAttribute < numValue);
            if (operator.equals(">=")) booleans.push(numAttribute >= numValue);
            if (operator.equals("<=")) booleans.push(numAttribute <= numValue);
        } catch (NumberFormatException e) {
            // If it doesn't convert to a float, it's a boolean or string
        }

        // If value is a string
        if (value.charAt(0) == '\'') {
            // Remove single quotes for LIKE comparison
            String likeValue = value.substring(1, value.length()-1);
            if (operator.equals("LIKE") || (operator.equals("like"))) {
                booleans.push(row.get(attributeIndex).contains(likeValue));
            }
        }

        // If value is string or boolean
        if (operator.equals("==")) booleans.push(row.get(attributeIndex).equals(value));
        if (operator.equals("!=")) booleans.push(!row.get(attributeIndex).equals(value));

        // If booleans is the same size, no functions were called so throw error
        if (booleansSize == booleans.size()) {
            throw new ParseException("ERROR: Invalid data type comparison between '" +
                    attribute + "' values and '" + value + "'", 0);
        }
    }

    // Removes last 2 boolean values from stack and evaluates them based on the connectors list
    // e.g. TRUE OR FALSE = TRUE, add back to booleans list
    private void evaluateBooleans() throws ParseException {
        try {
            boolean bool1 = booleans.pop();
            boolean bool2 = booleans.pop();
            String connector = connectors.pop();

            if (connector.equals("AND")) booleans.push(bool1 && bool2);
            else if (connector.equals("OR")) booleans.push(bool1 || bool2);

        } catch (EmptyStackException e) {
            // If method is called but stack is empty, the wrong number of brackets were used
            throw new ParseException("ERROR: Invalid syntax - too many brackets used for conditional statements", 0);
        }

    }

    // Checks if an attribute is a connector or closing bracket
    private boolean isSpecialValue(String attribute) throws ParseException {
        // Closing bracket indicates end of expression, so evaluate booleans on the stack
        if (attribute.equals(")")) {
            evaluateBooleans();
            return true;
        }
        // Add connectors to connectors stack as they appear
        else if (attribute.equals("AND") || attribute.equals("and")) {
            connectors.push("AND");
            return true;
        }
        else if (attribute.equals("OR") || attribute.equals("or")) {
            connectors.push("OR");
            return true;
        }
        return false;
    }

    public void addOperator(String op) {
        operators.add(op);
    }

    public void addValue(String val) {
        values.add(val);
    }

    public boolean isEmpty() {
        return operators.isEmpty() && values.isEmpty();
    }

}
