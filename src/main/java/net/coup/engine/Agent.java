package net.coup.engine;

import net.coup.model.Board;
import net.coup.model.Card;
import net.coup.model.Move;
import net.coup.model.Player;

import java.util.List;

public interface Agent {
    Card selectCardToSacrafice(Board board, Player player);
    boolean challengeMove(Board board, Move move);
    boolean challengeBlock(String blocker, Board board, Move move);
    boolean blockMove(Board board, Move move, Player player);
    List<Card> selectHand(Board board, List<Card> options, int cards);
    Move selectMove(List<Move> validMoves, Board board, Player player);
}
