package nl.codingtime.minesweeperbot.generator;

public class MinesweeperPuzzle {
    private MinesweeperIcon[][] puzzle;

    public MinesweeperPuzzle(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException();
        }
        this.puzzle = new MinesweeperIcon[columns][rows];
    }

    public void placeNumbers() {
        // First, replace all numbers with zero
        for (int column = 0; column < puzzle.length; column++) {
            for (int row = 0; row < puzzle[column].length; row++) {
                if (puzzle[column][row] != MinesweeperIcon.MINE) {
                    puzzle[column][row] = MinesweeperIcon.ZERO;
                }
            }
        }

        // Increase the numbers around a mine by one
        for (int column = 0; column < puzzle.length; column++) {
            for (int row = 0; row < puzzle[column].length; row++) {
                if (puzzle[column][row] == MinesweeperIcon.MINE) {
                    // Loop through all fields around the mine
                    for (int relativeRow = -1; relativeRow <= 1; relativeRow++) {
                        for (int relativeColumn = -1; relativeColumn <= 1; relativeColumn++) {
                            try {
                                if (puzzle[column + relativeColumn][row + relativeRow] != MinesweeperIcon.MINE) {

                                    // TODO this is quite a weird way of doing this, probably
                                    int number = Integer.parseInt(String.valueOf(puzzle[column + relativeColumn][row + relativeRow].getCharacter()));
                                    MinesweeperIcon increased = MinesweeperIcon.valueOf(String.valueOf(number + 1).charAt(0));
                                    puzzle[column + relativeColumn][row + relativeRow] = increased;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                // Do nothing
                            }
                        }
                    }
                }
            }
        }
    }

    public void placeMine(int row, int column) {
        puzzle[column][row] = MinesweeperIcon.MINE;
    }

    public MinesweeperIcon getCellAt(int row, int column) {
        return puzzle[column][row];
    }

    @Override
    public String toString() {
        placeNumbers();
        StringBuilder result = new StringBuilder();
        for (int column = 0; column < puzzle.length; column++) {
            for (int row = 0; row < puzzle[column].length; row++) {
                result.append("||").append(puzzle[column][row]).append("||");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
