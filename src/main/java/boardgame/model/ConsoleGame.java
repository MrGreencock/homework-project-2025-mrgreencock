package boardgame.model;

import game.console.TwoPhaseMoveGame;

import java.util.Scanner;

/**
 * This is the console representation of the boardgame.
 */

public class ConsoleGame {
    public static void main(String[] args) {
        var state = new BoardGameModel();
        var game = new TwoPhaseMoveGame<>(state, ConsoleGame::parseMove);
        Position p = new Position(1, 2);

        game.start();

    }

    /**
     * Parse a string to a Position record.
     * @param s input string.
     * @return the position that the player gave.
     */

    public static Position parseMove(String s) {
        s = s.trim();
        if(!s.matches("\\d+\\s+\\d+")) {
            throw new IllegalArgumentException();
        }
        var scanner = new Scanner(s);
        return new Position(scanner.nextInt(), scanner.nextInt());
    }

}
