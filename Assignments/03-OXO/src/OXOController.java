class OXOController
{
    private OXOModel model;
    private int turns = 0;
    private int numRows;
    private int numCols;
    private int winThreshold;

    public OXOController(OXOModel model) {
        this.model = model;
        model.setCurrentPlayer(model.getPlayerByNumber(0));
        numRows = model.getNumberOfRows();
        numCols = model.getNumberOfColumns();
        winThreshold = model.getWinThreshold();
        if (winThreshold > numRows && winThreshold > numCols) {
            System.out.println("INVALID WIN THRESHOLD (" + winThreshold + "): Threshold is larger than the grid size");
            System.exit(0);
        }
    }

    public void handleIncomingCommand(String command) throws InvalidCellIdentifierException, CellAlreadyTakenException, CellDoesNotExistException {
        // If the game is over, don't accept any more user input
        if (model.getWinner() != null || model.isGameDrawn()) return;
        // Grid only goes up to 9x9, so only 2 char input is valid
        if (command.length() == 2) {
            int x = command.toLowerCase().charAt(0) - 'a';
            int y = command.charAt(1) - '0' - 1;
            if (checkValidInput(x, y)) {
                turns++;
                model.setCellOwner(x, y, model.getCurrentPlayer());
                checkWinConditions();
                // Change player AFTER checking win conditions
                model.setCurrentPlayer(model.getPlayerByNumber(turns % model.getNumberOfPlayers()));
            }
        }
        else {
            throw new InvalidCellIdentifierException("length of input", command);
        }
    }

    public boolean checkValidInput(int x, int y) throws CellAlreadyTakenException, CellDoesNotExistException {
        if (!checkBounds(x, y)) {
            throw new CellDoesNotExistException(x,y);
        }
        else if (model.getCellOwner(x,y) != null) {
            throw new CellAlreadyTakenException(x,y);
        }
        else {
            return true;
        }
    }

    //Returns true if co-ordinate is within bounds, false if not
    private boolean checkBounds(int x, int y) {
        return (x >= 0 && x < numRows) && (y >= 0 && y < numCols);
    }

    // Win conditions designed for any win threshold (<= n) for any n x n grid up to 9 x 9
    public void checkWinConditions() {
        OXOPlayer currPlayer = model.getCurrentPlayer();

        // Check for win in columns and rows - checkCols/checkRows changed to 1/0 and 0/1
        if (checkLines(numCols, numRows, 1, 0) ||
                checkLines(numRows, numCols, 0, 1)) {
            model.setWinner(currPlayer);
            return;
        }

        // Both diagonal directions need to be checked to find a winner - multiplier can be -1/1
        if (checkDiagonals(-1) || checkDiagonals(1)) {
            model.setWinner(currPlayer);
            return;
        }

        // Check if game is a draw
        if (turns == numRows*numCols) {
            model.setGameDrawn();
        }

    }

    // If checking columns, needs to be getCellOwner(j,i) checkCols = 1, checkRows = 0
    // If checking rows, needs to be getCellOwner(i,j) - checkCols = 0, checkRows = 1
    public boolean checkLines(int firstDirection, int secondDirection, int checkCols, int checkRows) {
        OXOPlayer currPlayer = model.getCurrentPlayer();
        for (int i = 0; i < firstDirection; i++) {
            int lineSum = 0;
            for (int j = 0; j < secondDirection; j++) {
                if (model.getCellOwner(j*checkCols + i*checkRows,
                        i*checkCols + j*checkRows) == currPlayer) {
                    lineSum++;
                }
                else {
                    lineSum = 0;
                }
                if (lineSum == winThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    //Multiplier can be either -1 or 1 and determines which direction to check:
    // -1 = i increasing, j decreasing
    //  1 = i increasing, j increasing
    public boolean checkDiagonals(int multiplier) {
        OXOPlayer currPlayer = model.getCurrentPlayer();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (model.getCellOwner(i, j) == currPlayer) {
                    int lineSum = 1;
                    boolean checked = false;
                    while (!checked) {
                        // If the cell to the bottom right or bottom left is a valid cell
                        if (checkBounds(i + lineSum, j + (lineSum * multiplier))) {
                            if (model.getCellOwner(i + lineSum,
                                    j + (lineSum * multiplier)) == currPlayer) {
                                lineSum++;
                            }
                            else {
                                checked = true;
                            }
                            if (lineSum == winThreshold) {
                                return true;
                            }
                        }
                        else {
                            checked = true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
