package boardgame.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import game.TwoPhaseMoveState;

/**
 * This class represents the base of two phase move games.
 * The game is played on a board with a given {@code BOARD_SIZE} constant, respectively.
 * On each turn, a disk must be moved to an 8-adjacent empty square.
 * There are 4 obstacle squares which are not allowed to move to.
 * The player who reaches the opponent's side with all disks wins the game.
 */

public class BoardGameModel implements TwoPhaseMoveState<Position> {

    public static final int BOARD_SIZE = 5;

    private Player player;

    private ReadOnlyObjectWrapper<Player> nextPlayer = new ReadOnlyObjectWrapper<>(Player.PLAYER_1);

    private final ReadOnlyObjectWrapper<Square>[][] board;

    private TwoPhaseMove<Position> pendingMove = null;

    private int countSteps;

    private String player1Name;
    private String player2Name;

    /**
     * Constructor of the boardgame. Initialize the default state of the game.
     * It sets the colour of squares. Types of squares are in the {@link Square} Enum class.
     */
    public BoardGameModel() {
        board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<>(
                        ((i == 1 || i == BOARD_SIZE - 2) && (j == 1 || j == 3)) ? Square.BLACK :
                                switch(i) {
                                    case 0 -> Square.BLUE;
                                    case BOARD_SIZE - 1 -> Square.RED;
                                    default -> Square.NONE;
                                }
                );
            }
        }
    }
    /**
     * Get a {@code Square} in a given position.
     *
     * @param p the position of the square
     * @return a square in the given position
     */
    public Square getSquare(Position p) {
        return board[p.row()][p.col()].get();
    }

    private void setSquare(Position p, Square square) {
        board[p.row()][p.col()].set(square);
    }



    /**
     * Check if the move is legal or not. It depends on:
     * - whether it is legal to move from the current position,
     * - whether the next position is within board limits,
     * - whether the target position is empty,
     * - and whether the move follows the rules of a "king" in chess (1 step in any direction).
     *
     * @param move a move from one position to another
     * @return {@code true} if the move is legal, otherwise {@code false}
     */
    public boolean isLegalMove(TwoPhaseMove<Position> move) {
        return isLegalToMoveFrom(move.from())
                && isOnBoard(move.to())
                && isEmpty(move.to())
                && isKingMove(move.from(), move.to());
    }
    /**
     * Actually performs the move. You can step twice with your disks.
     * Conditions:
     * 1) Both disks move in the same direction.
     * 2) You must use two different disks.
     *
     * @param move a move from one position to another
     */
    @Override
    public void makeMove(TwoPhaseMove<Position> move) {
        if (pendingMove == null) {
            if (isLegalMove(move)) {
                setSquare(move.to(), getSquare(move.from()));
                setSquare(move.from(), Square.NONE);
                pendingMove = move;
                countSteps++;
                isGameOver();
            }
        } else {

            if (isLegalMove(move) && isSameDirection(move) && isDifferentPieces(move)) {
                setSquare(move.to(), getSquare(move.from()));
                setSquare(move.from(), Square.NONE);
                countSteps++;

                if(countSteps == 2) {
                    nextPlayer.set(nextPlayer.get().opponent());
                    pendingMove = null;
                    countSteps = 0;
                }
            }
        }
    }

    private boolean isDifferentPieces(TwoPhaseMove<Position> move) {
        return !pendingMove.to().equals(move.from());
    }


    private boolean isSameDirection(TwoPhaseMove<Position> move) {
        return pendingMove.to().row() - pendingMove.from().row() == move.to().row() - move.from().row()
                && pendingMove.to().col() - pendingMove.from().col() == move.to().col() - move.from().col();
    }

    private boolean isEmpty(Position p) {
        return getSquare(p) == Square.NONE;
    }

    private static boolean isOnBoard(Position p) {
        return 0 <= p.row() && p.row() < BOARD_SIZE && 0 <= p.col() && p.col() < BOARD_SIZE;
    }

    private static boolean isKingMove(Position from, Position to) {
        var dx = Math.abs(to.row() - from.row());
        var dy = Math.abs(to.col() - from.col());
        return dx + dy == 1 || dx * dy == 1;
    }

    /**
     * Get the next player.
     *
     * @return who is the next player
     */

    @Override
    public Player getNextPlayer() {
        return nextPlayer.get();
    }

    /**
     * Checks if the game is over. The game ends when all of your disks are on the opponent's side.
     * However, if you make a big mistake, for example, you move your disk down, but you are not able
     * to move your another disks with the same direction
     * the OPPONENT will win the game.
     * @return {@code true} if the game is over, otherwise {@code false}
     */
    @Override
    public boolean isGameOver() {
        int redInLastRow = 0;
        int bluInFirstRow = 0;
        Player currentPlayer = getNextPlayer();


        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                Square s = board[i][j].get();
                if (s == Square.RED && i == 0) {
                    redInLastRow++;
                } else if (s == Square.BLUE && i == BOARD_SIZE - 1) {
                    bluInFirstRow++;
                }
            }
        }
        if(pendingMove != null) {
            int dx = pendingMove.to().row() - pendingMove.from().row();
            int dy = pendingMove.to().col() - pendingMove.from().col();
            boolean hasValidSecondMove = false;
            Square playerDisk = currentPlayer == Player.PLAYER_1 ? Square.RED : Square.BLUE;

            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Position from = new Position(i, j);
                    if (getSquare(from) == playerDisk && !from.equals(pendingMove.to())) {
                        Position to = new Position(i + dx, j + dy);
                        if (isOnBoard(to) && isEmpty(to) && isKingMove(from, to)) {
                            hasValidSecondMove = true;
                            break;
                        }
                    }
                }
            }
            if (!hasValidSecondMove) {
                player = getNextPlayer().opponent();
                System.out.println("Game Over! You don't have any second move to continue the game");
                return true;
            }
        }
        if(redInLastRow == 5) {
            player = currentPlayer;
            return true;
        }
        if (bluInFirstRow == 5) {
            player = currentPlayer;
            return true;
        }
        return false;
    }


    /**
     * Get the status of the game.
     *
     * @return the game status (in progress or someone has won)
     */
    @Override
    public Status getStatus() {
        if(!isGameOver()) {
            return Status.IN_PROGRESS;
        }
        return player == Player.PLAYER_1 ? Status.PLAYER_1_WINS : Status.PLAYER_2_WINS;
    }

    /**
     * Check if the position is legal to move from.
     *
     * @param from the position where you want to move from
     * @return {@code true} if legal, otherwise {@code false}
     */
    public boolean isLegalToMoveFrom(Position from) {
        return isOnBoard(from) && !isEmpty(from)
                && ((nextPlayer.get() == Player.PLAYER_1 && getSquare(from) == Square.RED)
                || (nextPlayer.get() == Player.PLAYER_2 && getSquare(from) == Square.BLUE));
    }


    /**
     * Get the name of the winner player
     * @return name of the winner
     */

    public String getWinnerName() {
        return switch (player) {
            case PLAYER_1 -> player1Name;
            case PLAYER_2 -> player2Name;
        };
    }

    /**
     * Get the name of the loser player
     * @return name of the loser
     */

    public String getLoserName() {
        return switch (player) {
            case PLAYER_1 -> player2Name;
            case PLAYER_2 -> player1Name;
        };
    }

    /**
     * Set names of players
     * @param player1Name name of player 1
     * @param player2Name name of player 2
     */

    public void setPlayers(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    /**
     * Get the property of a square. Useful for UI binding and visualization.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return the observable property of the square
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int row, int col) {
        return board[row][col].getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Player> nextPlayerProperty() {
        return nextPlayer.getReadOnlyProperty();
    }

    //For testing isGameOver() method.
    void setTestSquare(Position p, Square s) {
        board[p.row()][p.col()].set(s);
    }


    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
