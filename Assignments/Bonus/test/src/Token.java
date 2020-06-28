public class Token {
    private int tokenID;
    private String sequence;

    // Assign each token a variable instead of a number (for readability)
    public static final int SEMICOLON = 0;
    public static final int USE = 1;
    public static final int CREATE = 2;
    public static final int DROP = 3;
    public static final int ALTER = 4;
    public static final int INSERT = 5;
    public static final int SELECT = 6;
    public static final int UPDATE = 7;
    public static final int DELETE = 8;
    public static final int JOIN = 9;
    public static final int DATABASE = 10;
    public static final int TABLE = 11;
    public static final int INTO = 12;
    public static final int VALUES = 13;
    public static final int FROM = 14;
    public static final int WHERE = 15;
    public static final int SET = 16;
    public static final int AND = 17;
    public static final int ON = 18;
    public static final int ADD = 19;
    public static final int TRUE = 20;
    public static final int FALSE = 21;
    public static final int ALL = 22;
    public static final int LEFTBRACKET = 23;
    public static final int RIGHTBRACKET = 24;
    public static final int OR = 25;
    public static final int OPERATOR = 26;
    public static final int VARIABLE = 27;
    public static final int NUMBER = 28;
    public static final int STRINGLITERAL = 29;
    public static final int COMMA = 30;
    public static final int EQUALS = 31;

    public Token(int tokenID, String sequence) {
        super();
        this.tokenID = tokenID;
        this.sequence = sequence;
    }

    public int getTokenID() {
        return tokenID;
    }

    public String getSequence() {
        return sequence;
    }

}
