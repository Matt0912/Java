class OXOController
{
    private OXOModel model;
    private int turns = 0;
    private int numRows;
    private int numCols;
    private int winThreshold;

    public OXOController(OXOModel model)
    {
        this.model = model;
        model.setCurrentPlayer(model.getPlayerByNumber(0));
        numRows = model.getNumberOfRows();
        numCols = model.getNumberOfColumns();
        winThreshold = model.getWinThreshold();

    }

    public void handleIncomingCommand(String command) throws InvalidCellIdentifierException, CellAlreadyTakenException, CellDoesNotExistException
    {
        // If the game is over, don't accept any more user input
        if (model.getWinner() != null || model.isGameDrawn()) return;
        if (command.length() == 2) {
            int x = command.toLowerCase().charAt(0) - 'a';
            int y = command.charAt(1) - '0' - 1;
            if (checkValidInput(x, y)) {
                turns++;
                model.setCellOwner(x, y, model.getCurrentPlayer());
                checkWinConditions();
                // Change player after checking win conditions
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
        return (x >= 0 && x < numCols) && (y >= 0 && y < numRows);
    }

    // Win conditions designed for any win threshold for any n x n grid up to 9 x 9
    public void checkWinConditions() {
        OXOPlayer currPlayer = model.getCurrentPlayer();
        int i, j, lineSum;
        // Check for win in columns
        for (i = 0; i < numCols; i++) {
            lineSum = 0;
            for (j = 0; j < numRows; j++) {
                if (model.getCellOwner(j, i) == currPlayer) {
                    lineSum++;
                }
                else {
                    lineSum = 0;
                }
                if (lineSum == winThreshold) {
                    model.setWinner(currPlayer);
                    return;
                }
            }
        }

        // Check for win in rows
        for (i = 0; i < numRows; i++) {
            lineSum = 0;
            for (j = 0; j < numCols; j++) {
                if (model.getCellOwner(i, j) == currPlayer) {
                    lineSum++;
                }
                else {
                    lineSum = 0;
                }
                if (lineSum == winThreshold) {
                    model.setWinner(currPlayer);
                    return;
                }
            }
        }

        if (checkDiagonals(-1) || checkDiagonals(1)) {
            model.setWinner(currPlayer);
            return;
        }

        if (turns == numRows*numCols) {
            model.setGameDrawn();
        }

    }

    //Multiplier ( -1 or 1) determines which direction to check: -1 = i increasing, j decreasing
    //                                                            1 = i increasing, j increasing
    public boolean checkDiagonals(int multiplier) {
        int lineSum = 1;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (model.getCellOwner(i, j) == model.getCurrentPlayer()) {
                    lineSum = 0;
                    boolean checked = false;
                    while (!checked) {
                        if (checkBounds(i + lineSum, j + (lineSum * multiplier))) {
                            if (model.getCellOwner(i + lineSum,
                                    j + (lineSum * multiplier)) == model.getCurrentPlayer()) {
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
