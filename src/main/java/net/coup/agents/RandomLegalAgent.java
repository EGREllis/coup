package net.coup.agents;

import net.coup.engine.Agent;
import net.coup.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomLegalAgent implements Agent {
    @Override
    public Card selectCardToSacrafice(Board board, Player player) {
        List<Card> cards = new ArrayList<>();
        cards = player.getOptions(cards);
        if (cards.size() == 1) {
            return cards.get(0);
        }
        int index = cards.size();
        while (index >= cards.size()) {
            index = (int) (Math.random() * (cards.size()));
        }
        Card cardToRemove = cards.get(index);
        return cardToRemove;
    }

    @Override
    public boolean challengeMove(Board board, Move move, Player player) {
        return Math.random() > 0.5 && player.isAlive();
    }

    @Override
    public boolean challengeBlock(String blocker, Board board, Move move) {
        return Math.random() > 0.5;
    }

    @Override
    public boolean blockMove(Board board, Move move, Player player) {
        boolean isValidBlock = false;
        List<Card> hand = player.getOptions(new ArrayList<Card>());
        for (Card block : move.getAction().getBlocks()) {
            if (hand.contains(block)) {
                isValidBlock = true;
                break;
            }
        }
        return isValidBlock;
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
