package net.coup.engine;

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
        int index = cards.size();
        while (index >= cards.size()) {
            index = (int) (Math.random() * (cards.size()));
        }
        Card cardToRemove = cards.get(index);
        return cardToRemove;
    }

    @Override
    public boolean challengeMove(Board board, Move move) {
        return false;
    }

    @Override
    public boolean challengeBlock(String blocker, Board board, Move move) {
        return false;
    }

    @Override
    public boolean blockMove(Board board, Move move) {
        return false;
    }

    @Override
    public List<Card> selectHand(Board board, List<Card> options) {
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < Constants.HAND_SIZE) {
            int index = (int)(Math.random() * options.size());
            indices.add(index);
        }
        List<Card> result = new ArrayList<>(Constants.HAND_SIZE);
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
