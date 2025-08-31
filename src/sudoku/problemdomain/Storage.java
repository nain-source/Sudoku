package sudoku.problemdomain;

import java.io.*;

public class Storage implements IStorage {

    private static final String FILE_NAME = "game.dat";

    @Override
    public SudokuGame getGameData() throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (SudokuGame) ois.readObject();
        } catch (FileNotFoundException e) {
            // No saved file, return new game or empty grid
            return new SudokuGame();
        } catch (ClassNotFoundException e) {
            throw new IOException("Data corrupted");
        }
    }

    @Override
    public void updateGameData(SudokuGame game) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(game);
        }
    }
}
