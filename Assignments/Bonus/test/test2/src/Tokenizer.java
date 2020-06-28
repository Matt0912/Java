import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Tokenizer was created by following the guide and Github code from:
// http://cogitolearning.co.uk/2013/04/writing-a-parser-in-java-the-tokenizer/
public class Tokenizer {
    // List to store the rules that the tokenizer will follow
    private LinkedList<TokenInfo> tokenInfos;
    // List of tokens generated when splitting input
    private LinkedList<Token> tokens;
    private static Tokenizer currentTokenizer = null;

    // Tokenizer constructor - private because Tokenizer is instantiated
    // using the getTokenizer class
    private Tokenizer() {
        super();
        tokenInfos = new LinkedList<TokenInfo>();
        tokens = new LinkedList<Token>();
    }

    // Private class used to store the rules that the tokenizer will follow
    // e.g. If the expression "USE" is found, create token and assign it tokenID
    private class TokenInfo {
        // regex = regular expression (string) that tokenizer is matching input against
        public final Pattern regex;
        public final int tokenID;

        // Constructor
        public TokenInfo(Pattern regex, int tokenID) {
            super();
            this.regex = regex;
            this.tokenID = tokenID;
        }
    }

    // Prevents multiple tokenizers being generated
    // If tokenizer exists, return it,
    // else initialise a tokenizer and return that
    public static Tokenizer getTokenizer() {
        if (currentTokenizer == null) {
            currentTokenizer = initialiseTokenizer();
        }
        return currentTokenizer;
    }

    // Instantiates and adds grammar rules for tokenizer to follow
    private static Tokenizer initialiseTokenizer() {
        Tokenizer tokenizer = new Tokenizer();

        // Some have spaces after to prevent them being tokenized when inside other words (e.g. 'add'ress, 'set'ting)
        tokenizer.addTokenInfo("\\;", Token.SEMICOLON);
        tokenizer.addTokenInfo("USE", Token.USE);
        tokenizer.addTokenInfo("CREATE", Token.CREATE);
        tokenizer.addTokenInfo("DROP ", Token.DROP);
        tokenizer.addTokenInfo("ALTER ", Token.ALTER);
        tokenizer.addTokenInfo("INSERT ", Token.INSERT);
        tokenizer.addTokenInfo("SELECT ", Token.SELECT);
        tokenizer.addTokenInfo("UPDATE", Token.UPDATE);
        tokenizer.addTokenInfo("DELETE", Token.DELETE);
        tokenizer.addTokenInfo("JOIN ", Token.JOIN);
        tokenizer.addTokenInfo("DATABASE", Token.DATABASE);
        tokenizer.addTokenInfo("TABLE", Token.TABLE);
        tokenizer.addTokenInfo("INTO ", Token.INTO);
        tokenizer.addTokenInfo("VALUES", Token.VALUES);
        tokenizer.addTokenInfo("FROM ", Token.FROM);
        tokenizer.addTokenInfo("WHERE ", Token.WHERE);
        tokenizer.addTokenInfo("SET ", Token.SET);
        tokenizer.addTokenInfo("AND ", Token.AND);
        tokenizer.addTokenInfo("ON ", Token.ON);
        tokenizer.addTokenInfo("ADD ", Token.ADD);
        tokenizer.addTokenInfo("true", Token.TRUE);
        tokenizer.addTokenInfo("false", Token.FALSE);
        tokenizer.addTokenInfo("\\*", Token.ALL);
        tokenizer.addTokenInfo("\\(", Token.LEFTBRACKET);
        tokenizer.addTokenInfo("\\)", Token.RIGHTBRACKET);
        tokenizer.addTokenInfo(",", Token.COMMA);
        tokenizer.addTokenInfo("OR ", Token.OR);
        tokenizer.addTokenInfo("==|>=|<=|>|<|!=|LIKE ", Token.OPERATOR);
        tokenizer.addTokenInfo("=", Token.EQUALS);
        tokenizer.addTokenInfo("[a-zA-Z][a-zA-Z0-9_]*", Token.VARIABLE);
        tokenizer.addTokenInfo("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?", Token.NUMBER);
        tokenizer.addTokenInfo("\\'[a-zA-Z0-9_ ]*\\'", Token.STRINGLITERAL);

        return tokenizer;
    }


     // regex = the user input expression to compare against
     // tokenID = the token id that the string relates to (based on the 'rules' added to tokenInfo)
    public void addTokenInfo(String regex, int tokenID) {
        tokenInfos.add(new TokenInfo(Pattern.compile("^(" + regex+")", Pattern.CASE_INSENSITIVE), tokenID));
    }

    // Splits an input string into it's individual tokens (as specified
    // by the rules added in 'initialiseTokenizer')
    public void tokenize(String str) {
        // Remove leading/trailing spaces
        String s = str.trim();
        tokens.clear();
        while (!s.equals("")) {
            boolean match = false;
            // Loop through token rules until one matches
            for (TokenInfo info : tokenInfos) {
                Matcher m = info.regex.matcher(s);
                if (m.find()) {
                    match = true;
                    String tok = m.group().trim();
                    s = m.replaceFirst("").trim();
                    tokens.add(new Token(info.tokenID, tok));
                    break;
                }
            }
            // If no rules match user input, throw an exception
            if (!match) throw new RuntimeException("Unexpected character in input: " + s);
        }
    }

    // Returns list of tokens generated from running tokenize function
    public LinkedList<Token> getTokens() {
        return tokens;
    }
}
