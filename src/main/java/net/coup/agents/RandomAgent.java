package net.coup.agents;

import net.coup.engine.Agent;
import net.coup.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomAgent implements Agent {
    @Override
    public Card selectCardToSacrafice(Board board, Player player) {
        List<Card> cards = new ArrayList<>();
        cards = player.getOptions(cards);
        if (cards.size() == 1) {
            return cards.get(0);
        } else if (cards.size() == 2) {
            return cards.get((int)Math.round(Math.random() * 1));
        } else {
            throw new IllegalStateException(String.format("This should never be called! %1$s", player));
        }
    }

    @Override
    public boolean challengeMove(Board board, Move move, Player player) {
        return Math.random() > 0.5 && player.isAlive();
    }

    @Override
    public boolean challengeBlock(String blocker, Board board, Move move) {
        return false;
    }

    @Override
    public boolean blockMove(Board board, Move move, Player player) {
        return false;
    }

    @Override
    public List<Card> selectHand(Board board, List<Card> options, int cards) {
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < cards) {
            int index = (int)(Math.random() * options.size());
            indices.add(index);
        }
        List<Card> result = new ArrayList<>(Constants.HAND_SIZE);
        for (int index : indices) {
            result.add(options.get(index));
        }
        return result;
    }

    @Override
    public Move selectMove(List<Move> validMoves, Board board, Player player) {
        int index = validMoves.size();
        while (index >= validMoves.size()) {
            index = (int) (validMoves.size() * Math.random());
        }
        return validMoves.get(index);
    }
}
