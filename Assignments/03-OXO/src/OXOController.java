class OXOController
{
    private OXOModel model;
    private int turns = 0;
    private int numRows;
    private int numCols;

    public OXOController(OXOModel model)
    {
        this.model = model;
        model.setCurrentPlayer(model.getPlayerByNumber(0));
        numRows = model.getNumberOfRows();
        numCols = model.getNumberOfColumns();

    }

    public void handleIncomingCommand(String command) throws InvalidCellIdentifierException, CellAlreadyTakenException, CellDoesNotExistException
    {
        // If the game is over, don't accept any more user input
        if (model.getWinner() != null || model.isGameDrawn()) return;
        if (command.length() == 2) {
            int x = command.toLowerCase().charAt(0) - 'a';
            int y = command.charAt(1) - '0' - 1;
            if (checkValidInput(command, x, y)) {
                turns++;
                model.setCellOwner(x, y, model.getCurrentPlayer());
                model.setCurrentPlayer(model.getPlayerByNumber(turns % model.getNumberOfPlayers()));

                checkWinConditions();
            }
        }
        else {
            System.out.println(new InvalidCellIdentifierException("length of input", command).toString());
        }
    }

    public boolean checkValidInput(String input, int x, int y) {
        if (x < 0 ||  x > numCols - 1 || y < 0 ||  y > numRows - 1) {
            System.out.println(new CellDoesNotExistException(x,y).toString());
        }
        else if (model.getCellOwner(x,y) != null) {
            System.out.println(new CellAlreadyTakenException(x,y).toString());
        }
        else {
            return true;
        }
        return false;
    }

    public void checkWinConditions() {
        OXOPlayer currOwner;
        int i, j;
        // Check for win in columns
        for (i = 0; i < numCols; i++) {
            currOwner = model.getCellOwner(0, i);
            j = 1;
            while (currOwner == model.getCellOwner(j, i)) {
                if (j == numRows - 1) {
                    model.setWinner(currOwner);
                    return;
                }
                j++;
            }
        }

        // Check for win in rows
        for (i = 0; i < numRows; i++) {
            currOwner = model.getCellOwner(i, 0);
            j = 1;
            while (currOwner == model.getCellOwner(i, j)) {
                if (j == numCols - 1) {
                    model.setWinner(currOwner);
                    return;
                }
                j++;
            }
        }

        // Check for diagonal wins
        currOwner = model.getCellOwner(0, numCols - 1);
        i = 1;
        j = numCols - 1 - i;
        while (currOwner == model.getCellOwner(i, j)) {
            if (j == 0) {
                model.setWinner(currOwner);
                return;
            }
            i++;
            j--;
        }
        currOwner = model.getCellOwner(0, 0);
        i = 1;
        j = 1;
        while (currOwner == model.getCellOwner(i, j)) {
            if (j == numCols - 1) {
                model.setWinner(currOwner);
                return;
            }
            i++;
            j++;
        }

        if (turns == numRows*numCols) {
            model.setGameDrawn();
        }

    }

}
