import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {
    private static Parser parser = null;
    private LinkedList<Token> tokens;
    private LinkedList<Token> removedTokens;
    private Token currentToken;

    private DBMS DBMS;
    private Tokenizer tokenizer;

    private ArrayList<String> attributeList;
    private ArrayList<String> valueList;
    private ConditionStack conditionStack;

    // Constructor private - use getParser to return parser
    private Parser() {
        tokens = new LinkedList<Token>();
        removedTokens = new LinkedList<Token>();
        this.tokenizer = Tokenizer.getTokenizer();
        this.DBMS = DBMS.getDBMS();
    }

    // Prevents duplicate parsers being created
    public static Parser getParser() {
        if (parser == null) {
            return new Parser();
        }
        return parser;
    }

    // Parses (and interprets) user input, stores results of any database queries in DBMS.output
    public void parse(String input) throws ParseException, IOException, ClassNotFoundException {
        // Split input string in to tokens
        tokenizer.tokenize(input);
        this.tokens = tokenizer.getTokens();
        currentToken = tokens.getFirst();

        // Every time new command is parsed reset the different storage lists
        attributeList = new ArrayList<>();
        valueList = new ArrayList<>();
        conditionStack = new ConditionStack();

        // All queries begin with a command, so parsing starts by calling commandType method
        commandType();

        // If final token isn't equal to semicolon, an error has occurred when parsing
        if (currentToken.getTokenID() != Token.SEMICOLON) {
            throw new ParseException("ERROR: Invalid query - Check syntax at '"
                    + currentToken.getSequence() + "'",0);
        }
    }

    private void commandType() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.USE) {
            nextToken();
            useCommand();
        }
        else if (currentToken.getTokenID() == Token.CREATE) {
            nextToken();
            createCommand();
        }
        else if (currentToken.getTokenID() == Token.DROP) {
            nextToken();
            dropCommand();
        }
        else if (currentToken.getTokenID() == Token.ALTER) {
            nextToken();
            alterCommand();
        }
        else if (currentToken.getTokenID() == Token.INSERT) {
            nextToken();
            insertCommand();
        }
        else if (currentToken.getTokenID() == Token.SELECT) {
            nextToken();
            selectCommand();
        }
        else if (currentToken.getTokenID() == Token.UPDATE) {
            nextToken();
            updateCommand();
        }
        else if (currentToken.getTokenID() == Token.DELETE) {
            nextToken();
            deleteCommand();
        }
        else if (currentToken.getTokenID() == Token.JOIN) {
            nextToken();
            joinCommand();
        }
        else {
            throw new ParseException("ERROR: Invalid command - '" + currentToken.getSequence() + "'",0);
        }
    }

    private void useCommand() throws ParseException, IOException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            String dbName = currentToken.getSequence();
            // Call next token to check for semicolon at the end (before calling DBMS function)
            nextToken();
            DBMS.useDatabase(dbName);
            return;
        }
        throw new ParseException("ERROR: " + currentToken.getSequence() +
                " is not a valid database name", 0);
    }

    private void createCommand() throws ParseException, IOException {
        // If user enters CREATE DATABASE
        if (currentToken.getTokenID() == Token.DATABASE) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String dbName = currentToken.getSequence();
                nextToken();
                DBMS.createDatabase(dbName);
                return;
            }
            throw new ParseException("ERROR: " + currentToken.getSequence() +
                    " is not a valid database name", 0);
        }
        // If user enters CREATE TABLE
        else if (currentToken.getTokenID() == Token.TABLE) {
            nextToken();
            createTableCommand();
        }

        else {
            throw new ParseException("ERROR: Expected user to specify" +
                    "'DATABASE' or 'TABLE'",0);
        }
    }

    private void createTableCommand() throws ParseException, IOException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            String tableName = currentToken.getSequence();
            nextToken();

            // If the statement ends after the table name, create new basic table
            if (currentToken.getTokenID() == Token.SEMICOLON) {
                DBMS.createTable(tableName);
                return;
            }

            // Or the user inputs a list of attributes for the column names
            else if (currentToken.getTokenID() == Token.LEFTBRACKET) {
                nextToken();
                if (isAttributeList()) {
                    if (currentToken.getTokenID() == Token.RIGHTBRACKET) {
                        nextToken();
                        // Create new basic table
                        DBMS.createTable(tableName);
                        // If a list of attributes is specified, add them to the table
                        DBMS.addTableCols(tableName, attributeList);
                        return;
                    }
                }
                throw new ParseException("ERROR: Expected format for attributes is e.g.'(A,B,C)'", 0);
            }

            else {
                throw new ParseException("ERROR: Expected either ';' OR " +
                        "'(AttributeList)' after table name", 0);
            }
        }
        throw new ParseException("ERROR: " + currentToken.getSequence() +
                " is not a valid table name", 0);
    }

    private void dropCommand() throws ParseException, IOException {
        // If user inputs DROP DATABASE
        if (currentToken.getTokenID() == Token.DATABASE) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String dbName = currentToken.getSequence();
                nextToken();
                DBMS.deleteDatabase(dbName);
                return;
            }
            throw new ParseException("ERROR: " + currentToken.getSequence() +
                    " is not a valid database name", 0);
        }

        // If user inputs DROP TABLE
        else if (currentToken.getTokenID() == Token.TABLE) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String tableName = currentToken.getSequence();
                nextToken();
                DBMS.deleteTable(tableName);
                return;
            }
            throw new ParseException("ERROR: " + currentToken.getSequence() +
                    " is not a valid table name", 0);
        }

        else {
            throw new ParseException("ERROR: Expected user to specify" +
                    "'DATABASE' or 'TABLE'",0);
        }
    }

    // Either adds or removes columns from an existing table
    private void alterCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.TABLE) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String tableName = currentToken.getSequence();
                nextToken();

                // User can either ADD or DROP columns
                if (currentToken.getTokenID() == Token.ADD) {
                    nextToken();
                    if (currentToken.getTokenID() == Token.VARIABLE) {
                        attributeList.add(currentToken.getSequence());
                        nextToken();
                        DBMS.addTableCols(tableName, attributeList);
                        return;
                    }
                }

                else if (currentToken.getTokenID() == Token.DROP) {
                    nextToken();
                    if (currentToken.getTokenID() == Token.VARIABLE) {
                        attributeList.add(currentToken.getSequence());
                        nextToken();
                        DBMS.removeTableCols(tableName, attributeList);
                        return;
                    }
                }
            }
        }

        throw new ParseException("ERROR: Correct format is 'ALTER TABLE " +
                "<tablename> ADD/DROP <columnname>;'",0);
    }

    // Add rows to the end of an existing table
    private void insertCommand() throws ParseException, IOException {
        if (currentToken.getTokenID() == Token.INTO) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String tableName = currentToken.getSequence();
                nextToken();
                if (currentToken.getTokenID() == Token.VALUES) {
                    nextToken();
                    if (currentToken.getTokenID() == Token.LEFTBRACKET) {
                        nextToken();
                        if (isValueList()) {
                            if (currentToken.getTokenID() == Token.RIGHTBRACKET) {
                                nextToken();
                                DBMS.insertValues(tableName, valueList);
                                return;
                            }
                        }

                        throw new ParseException("ERROR: Expected format is e.g." +
                                "'('Bob',56,true)'", 0);
                    }
                }
            }
        }
        throw new ParseException("ERROR: Correct format is 'INSERT INTO <TableName> " +
                "VALUES (<ValueList>)'",0);
    }

    // Retrieves data from tables according to attributes and conditions listed
    private void selectCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.ALL) {
            nextToken();
            selectTableCommand();
        }
        else if (isAttributeList()) {
            selectTableCommand();
        }
        else throw new ParseException("ERROR: Specify tables to select", 0);
    }

    private void selectTableCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.FROM) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String tableName = currentToken.getSequence();
                nextToken();

                // If statement ends, no conditions so an empty conditionStack is passed
                if (currentToken.getTokenID() == Token.SEMICOLON) {
                    DBMS.selectTable(tableName, attributeList, conditionStack);
                    return;
                }

                // If next word is WHERE, then conditions are added to conditionStack
                else if (currentToken.getTokenID() == Token.WHERE) {
                    nextToken();
                    if (isCondition()) {
                        nextToken();
                        DBMS.selectTable(tableName, attributeList, conditionStack);
                        return;
                    }
                }
            }
        }
        throw new ParseException("ERROR: Invalid query - Didn't expect '" +
                currentToken.getSequence() + "'",0);
    }

    // Updates a table values specified by attributes, values and conditions
    private void updateCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            String tableName = currentToken.getSequence();
            nextToken();
            if (currentToken.getTokenID() == Token.SET) {
                nextToken();
                if (isNameValueList()) {
                    if (currentToken.getTokenID() == Token.WHERE) {
                        nextToken();
                        if (isCondition()) {
                            nextToken();
                            DBMS.updateTable(tableName, attributeList, valueList, conditionStack);
                            return;
                        }
                    }
                }
            }
            throw new ParseException("ERROR: Invalid query at '" +
                    currentToken.getSequence() + "'\nExample format: 'UPDATE tablename" +
                    " SET variable = value WHERE name == 'bob'",0);
        }
        throw new ParseException("ERROR: Specify tables to update", 0);
    }

    // Deletes row/s from an existing table
    private void deleteCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.FROM) {
            nextToken();
            if (currentToken.getTokenID() == Token.VARIABLE) {
                String tableName = currentToken.getSequence();
                nextToken();
                if (currentToken.getTokenID() == Token.WHERE) {
                    nextToken();
                    if (isCondition()) {
                        nextToken();
                        DBMS.deleteRows(tableName, conditionStack);
                        return;
                    }
                }
            }
        }
        throw new ParseException("ERROR: Invalid query at - '" +
                currentToken.getSequence() + "'\nExample format: 'DELETE FROM" +
                " tablename WHERE name == 'bob'",0);
    }

    // Stores the two tables to join and the attributes to join them on, then passes to the
    // DBMS jointables function
    private void joinCommand() throws ParseException, IOException, ClassNotFoundException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            String tableName1 = currentToken.getSequence();
            nextToken();
            if (currentToken.getTokenID() == Token.AND) {
                nextToken();
                if (currentToken.getTokenID() == Token.VARIABLE) {
                    String tableName2 = currentToken.getSequence();
                    nextToken();
                    if (currentToken.getTokenID() == Token.ON) {
                        nextToken();
                        if (currentToken.getTokenID() == Token.VARIABLE) {
                            String attribute1 = currentToken.getSequence();
                            nextToken();
                            if (currentToken.getTokenID() == Token.AND) {
                                nextToken();
                                if (currentToken.getTokenID() == Token.VARIABLE) {
                                    String attribute2 = currentToken.getSequence();
                                    nextToken();
                                    DBMS.joinTables(tableName1, tableName2, attribute1, attribute2);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new ParseException("ERROR: Invalid query at '" +
                currentToken.getSequence() + "'\nExample format: 'JOIN tablename1 AND " +
                "tablename2 ON tablename1.id AND tablename2.id'",0);
    }

    // Adds variables to attributeList, and returns true if syntax is correct
    private boolean isAttributeList() throws ParseException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            attributeList.add(currentToken.getSequence());
            nextToken();
            if (currentToken.getTokenID() == Token.COMMA) {
                nextToken();
                return isAttributeList();
            }
            return true;
        }
        return false;
    }

    // Adds variables to valueList, and returns true if syntax is correct
    private boolean isValueList() throws ParseException {
        if (isValue()) {
            valueList.add(currentToken.getSequence());
            nextToken();
            if (currentToken.getTokenID() == Token.COMMA) {
                nextToken();
                return isValueList();
            }
            return true;
        }
        return false;
    }

    // Values can be any of: string, boolean or number
    private boolean isValue() {
        return currentToken.getTokenID() == Token.STRINGLITERAL ||
                currentToken.getTokenID() == Token.TRUE ||
                currentToken.getTokenID() == Token.FALSE ||
                currentToken.getTokenID() == Token.NUMBER;
    }

    // Returns true if a sequence of tokens represents a condition
    // e.g. (name == 'Bob')
    private boolean isCondition() throws ParseException {
        // If single condition (no parentheses)
        if (currentToken.getTokenID() == Token.VARIABLE) {
            conditionStack.addValue(currentToken.getSequence());
            nextToken();
            if (currentToken.getTokenID() == Token.OPERATOR) {
                conditionStack.addOperator(currentToken.getSequence());
                nextToken();
                if (isValue()) {
                    conditionStack.addValue(currentToken.getSequence());
                    return true;
                }
            }
            throw new ParseException("ERROR: Invalid query at '" +
                    currentToken.getSequence() + "'",0);
        }

        // If multiple conditions
        else if (currentToken.getTokenID() == Token.LEFTBRACKET) {
            nextToken();
            if (isCondition()) {
                nextToken();
                if (currentToken.getTokenID() == Token.RIGHTBRACKET) {
                    nextToken();

                    if (currentToken.getTokenID() == Token.RIGHTBRACKET) {
                        // If multiple ')', add to conditionStack to tell it to evaluate the previous
                        // 2 condition results
                        conditionStack.addValue(currentToken.getSequence());
                        previousToken();
                        return true;
                    }

                    // If AND or OR, add to value list
                    if (currentToken.getTokenID() == Token.AND ||
                            currentToken.getTokenID() == Token.OR) {
                        conditionStack.addValue(currentToken.getSequence());
                        nextToken();
                        return isCondition();
                    }

                    return currentToken.getTokenID() == Token.SEMICOLON;
                }
            }
        }
        return false;
    }

    // Returns true if syntax is correct
    private boolean isNameValueList() throws ParseException {
        if (isNameValuePair()) {
            nextToken();
            if (currentToken.getTokenID() == Token.COMMA) {
                nextToken();
                return isNameValueList();
            }
            return true;
        }
        return false;
    }

    // Adds attributes to attributeList & values to valueList
    private boolean isNameValuePair() throws ParseException {
        if (currentToken.getTokenID() == Token.VARIABLE) {
            attributeList.add(currentToken.getSequence());
            nextToken();
            if (currentToken.getTokenID() == Token.EQUALS) {
                nextToken();
                if (isValue()) {
                    valueList.add(currentToken.getSequence());
                    return true;
                }
            }
        }
        return false;
    }

    // Iterates through the list of tokens
    private void nextToken() throws ParseException {
        removedTokens.addFirst(tokens.removeFirst());
        if (tokens.isEmpty()) {
            // If tokenList is empty (i.e. end of query),
            // the currentToken should be a semicolon, else throw an error
            if (currentToken.getTokenID() != Token.SEMICOLON) {
                throw new ParseException("ERROR: Missing ';' at end of query", 0);
            }
            return;
        }
        currentToken = tokens.getFirst();
    }

    // Adds the last removed token back to the token list
    private void previousToken() {
        try {
            tokens.addFirst(removedTokens.removeFirst());
            currentToken = tokens.getFirst();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage() + "\nTried to access empty array of removed tokens");
        }
    }
}
