class OXOController
{
    private OXOModel model;
    private int turns = 0;

    public OXOController(OXOModel model)
    {
        this.model = model;
        model.setCurrentPlayer(model.getPlayerByNumber(0));

    }

    public void handleIncomingCommand(String command) throws InvalidCellIdentifierException, CellAlreadyTakenException, CellDoesNotExistException
    {
        // If the game is over, don't accept any more user input
        if (model.getWinner() != null || model.isGameDrawn()) return;
        if (command.length() != 2) {
            System.out.println(new InvalidCellIdentifierException("length of input", command).toString());
            return;
        }
        int x = command.toLowerCase().charAt(0) - 'a';
        int y = command.charAt(1) - '0' - 1;
        if (x < 0 ||  x > model.getNumberOfColumns() - 1 || y < 0 ||  y > model.getNumberOfRows() - 1) {
            System.out.println(new CellDoesNotExistException(x,y).toString());
        }
        else if (model.getCellOwner(x,y) != null) {
            System.out.println(new CellAlreadyTakenException(x,y).toString());
        }
        else {
            turns++;
            model.setCellOwner(x, y, model.getCurrentPlayer());
            model.setCurrentPlayer(model.getPlayerByNumber(turns % model.getNumberOfPlayers()));

            checkWinConditions();

        }
    }

    public void checkWinConditions() {
        for (int i = 0; i < model.getNumberOfColumns(); i++) {
            if (model.getCellOwner(i, 0) == model.getCellOwner(i, 1) &&
                    model.getCellOwner(i, 1) == model.getCellOwner(i, 2)) {
                model.setWinner(model.getCellOwner(i,0));
            }
        }
        for (int i = 0; i < model.getNumberOfRows(); i++) {
            if (model.getCellOwner(0, i) == model.getCellOwner(1, i) &&
                    model.getCellOwner(1, i) == model.getCellOwner(2, i)) {
                model.setWinner(model.getCellOwner(0, i));
            }
        }

        if (turns == model.getNumberOfRows()*model.getNumberOfColumns()) {
            model.setGameDrawn();
        }

    }

}
