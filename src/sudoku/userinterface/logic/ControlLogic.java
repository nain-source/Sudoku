package sudoku.userinterface.logic;

import sudoku.constants.GameState;
import sudoku.constants.Messages;
import sudoku.computationlogic.GameLogic;
import sudoku.problemdomain.IStorage;
import sudoku.problemdomain.SudokuGame;
import sudoku.userinterface.IUserInterfaceContract;

import java.io.IOException;

public class ControlLogic implements IUserInterfaceContract.EventListener {

    private IStorage storage;
    private IUserInterfaceContract.View view;

    private int movesCount = 0;

    public ControlLogic(IStorage storage, IUserInterfaceContract.View view) {
        this.storage = storage;
        this.view = view;
    }

    @Override
    public void onSudokuInput(int x, int y, int input) {
        try {
            SudokuGame gameData = storage.getGameData();
            int[][] newGridState = gameData.getCopyOfGridState();


            if (newGridState[x][y] != input) {
                movesCount++; // increment moves count
                view.updateMoves(movesCount);
            }

            newGridState[x][y] = input;

            gameData = new SudokuGame(
                    GameLogic.checkForCompletion(newGridState),
                    newGridState
            );

            storage.updateGameData(gameData);
            view.updateSquare(x, y, input);

            if (gameData.getGameState() == GameState.COMPLETE) {
                view.showDialog(Messages.GAME_COMPLETE);
                view.stopTimer();
            }
        } catch (IOException e) {
            e.printStackTrace();
            view.showError(Messages.ERROR);
        }
    }

    @Override
    public void onDialogClick() {
        try {
            storage.updateGameData(
                    GameLogic.getNewGame()
            );
            movesCount = 0;
            view.updateMoves(movesCount);
            view.updateBoard(storage.getGameData());
            view.startTimer();
        } catch (IOException e) {
            view.showError(Messages.ERROR);
        }
    }

    @Override
    public void onResetClick() {
        onDialogClick();
    }
}
