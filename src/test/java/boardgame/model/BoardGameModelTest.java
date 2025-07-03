package boardgame.model;

import common.TwoPhaseMoveState;
import game.State;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardGameModelTest {


    BoardGameModel model;

    @BeforeEach
    void generateModel() {
        model = new BoardGameModel();
    }
    @Test
    void testIsLegalMove() {
        Position from = new Position(4, 0);
        Position to = new Position(3, 0);
        TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(from, to);

        boolean result = model.isLegalMove(move);

        assertTrue(result);
    }

    @Test
    void testMakeMove() {
        Position from = new Position(4, 0);
        Position to = new Position(3, 0);
        TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(from, to);

        Square fromSquareBefore = model.getSquare(from);

        model.makeMove(move);

        Square fromSquareAfter = model.getSquare(from);
        Square toSquareAfter = model.getSquare(to);

        assertEquals(Square.NONE, fromSquareAfter);
        assertEquals(fromSquareBefore, toSquareAfter);
    }

    @Test
    void testIsSameDirection() {
        Position from = new Position(0, 0);
        Position to = new Position(1, 0);
        TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(from, to);

        model.makeMove(move);

        Position fromAgain = new Position(0, 4);
        Position toAgain = new Position(1, 4);
        TwoPhaseMoveState.TwoPhaseMove<Position> moveAgain = new TwoPhaseMoveState.TwoPhaseMove<>(fromAgain, toAgain);

        boolean result = move.to().row() - move.from().row() == moveAgain.to().row() - moveAgain.from().row()
                && move.to().col() - move.from().col() == moveAgain.to().col() - moveAgain.from().col();

        assertTrue(result);
    }

    @Test
    void testIsEmpty() {
        boolean result = model.getSquare(new Position(1,0)) == Square.NONE;
        boolean resultFalse = model.getSquare(new Position(0,0)) == Square.NONE;
        assertTrue(result);
        assertFalse(resultFalse);
    }

    @Test
    void testIsGameOver() {
        for (int col = 0; col < BoardGameModel.BOARD_SIZE; col++) {
            model.setTestSquare(new Position(4, col), Square.NONE);
        }

        for (int col = 0; col < BoardGameModel.BOARD_SIZE; col++) {
            model.setTestSquare(new Position(4, col), Square.BLUE);
        }
        Position from = new Position(4,4);
        Position to = new Position(3,4);
        model.makeMove(new TwoPhaseMoveState.TwoPhaseMove<>(from, to));

        assertTrue(model.isGameOver());
    }

    @Test
    void testIsLegalMoveFrom() {
        Position emptyPos = new Position(2, 2);
        model.setTestSquare(emptyPos, Square.NONE);

        assertFalse(model.isLegalToMoveFrom(emptyPos));
    }

    @Test
    void testNextPlayerSwitchAfterTwoMoves() {
        Position firstFrom = new Position(4, 0);
        Position firstTo = new Position(3, 0);
        model.makeMove(new TwoPhaseMoveState.TwoPhaseMove<>(firstFrom, firstTo));

        assertEquals(State.Player.PLAYER_1, model.getNextPlayer());

        Position secondFrom = new Position(4, 2);
        Position secondTo = new Position(3, 2);
        model.makeMove(new TwoPhaseMoveState.TwoPhaseMove<>(secondFrom, secondTo));

        assertEquals(State.Player.PLAYER_2, model.getNextPlayer());
    }

    @Test
    void testIllegalSecondMoveDifferentDirection() {
        Position firstFrom = new Position(0, 0);
        Position firstTo = new Position(1, 0);
        model.makeMove(new TwoPhaseMoveState.TwoPhaseMove<>(firstFrom, firstTo));

        Position secondFrom = new Position(0, 1);
        Position secondTo = new Position(0, 2);

        model.makeMove(new TwoPhaseMoveState.TwoPhaseMove<>(secondFrom, secondTo));

        assertEquals(State.Player.PLAYER_1, model.getNextPlayer());
    }

    @Test
    void testGetWinnerAndLoserName() {
        model.setPlayers("Beni", "Béla");
        for (int col = 0; col < BoardGameModel.BOARD_SIZE; col++) {
            model.setTestSquare(new Position(4, col), Square.BLUE);
        }
        model.isGameOver();
        assertEquals("Beni", model.getWinnerName());
        assertEquals("Béla", model.getLoserName());
    }

}