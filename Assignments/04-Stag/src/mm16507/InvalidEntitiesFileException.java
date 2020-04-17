public class InvalidEntitiesFileException extends Exception {
    private String missingValue;

    public InvalidEntitiesFileException(String missingValue) {
        this.missingValue = missingValue;
    }

    public String toString() {
        return "InvalidEntitiesFileException {" +
                "File is missing: '" + missingValue + "' }\n" +
                "GAME WILL NOT RUN CORRECTLY WITHOUT THIS!";
    }
}
