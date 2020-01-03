package net.coup;

import net.coup.model.Board;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        List<String> names = Arrays.asList("One", "Two", "Three", "Four", "Five");
        Board board = Board.newGame(names);
        System.out.println("New game:\n"+board);
        while (!board.isGameOver()) {
            for (String name : names) {
                
            }
        }
    }
}
