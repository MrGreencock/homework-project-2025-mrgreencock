package boardgame.model;

/**
 * It is a record class of position. Only implement a toString method.
 * @param row row of position
 * @param col column of position
 */
public record Position(int row, int col) {
    /**
     * Format the text of position.
     * @return
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}