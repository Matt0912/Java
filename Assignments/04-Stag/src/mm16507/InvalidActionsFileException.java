public class InvalidActionsFileException extends Exception {
    private String missingValue;

    public InvalidActionsFileException(String missingValue) {
        this.missingValue = missingValue;
    }

    public String toString() {
        return "InvalidActionsFileException {" +
                "File is missing: '" + missingValue + "' }\n" +
                "PLAYER WILL NOT BE ABLE TO ACCESS THIS ACTION!";
    }
}
