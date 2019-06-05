package nl.codingtime.minesweeperbot.generator;

import java.util.Random;

public class MinesweeperPuzzleBuilder {
    private final static Random random = new Random();
    private int width = 5;
    private int height = 5;
    private int mines = 7;

    public MinesweeperPuzzleBuilder withWidth(int width) {
        this.width = width;
        return this;
    }

    public MinesweeperPuzzleBuilder withHeight(int height) {
        this.height = height;
        return this;
    }

    public MinesweeperPuzzleBuilder withAmountOfMines(int amount) {
        this.mines = amount;
        return this;
    }

    public MinesweeperPuzzle build() {
        if (width * height < mines) {
            throw new IllegalArgumentException();
        }

        MinesweeperPuzzle result = new MinesweeperPuzzle(width, height);

        for (int i = 0; i < mines; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            while (result.getCellAt(x, y) == MinesweeperIcon.MINE) {
                x = random.nextInt(width);
                y = random.nextInt(height);
            }
            result.placeMine(x, y);
        }

        result.placeNumbers();

        return result;
    }
}
