package sudoku.problemdomain;

import sudoku.constants.GameState;
import sudoku.computationlogic.SudokuUtilities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SudokuGame implements Serializable {
    private final GameState gameState;
    private final int[][] gridState;

    // Track fixed cells as coordinates "x,y"
    private final Set<String> fixedCells;

    public static final int GRID_BOUNDARY = 9;

    public SudokuGame() {
        this.gameState = GameState.NEW;
        this.gridState = new int[GRID_BOUNDARY][GRID_BOUNDARY];
        this.fixedCells = new HashSet<>();
    }

    public SudokuGame(GameState gameState, int[][] gridState) {
        this.gameState = gameState;
        this.gridState = SudokuUtilities.copyToNewArray(gridState);
        this.fixedCells = new HashSet<>();
        identifyFixedCells();
    }

    private void identifyFixedCells() {
        for (int x = 0; x < GRID_BOUNDARY; x++) {
            for (int y = 0; y < GRID_BOUNDARY; y++) {
                if (gridState[x][y] != 0) {
                    fixedCells.add(keyFor(x, y));
                }
            }
        }
    }

    private String keyFor(int x, int y) {
        return x + "," + y;
    }

    public boolean isCellFixed(int x, int y) {
        return fixedCells.contains(keyFor(x, y));
    }

    public GameState getGameState() {
        return gameState;
    }

    public int[][] getCopyOfGridState() {
        return SudokuUtilities.copyToNewArray(gridState);
    }
}
