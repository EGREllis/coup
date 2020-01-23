package net.coup.agents;

import net.coup.engine.Agent;
import net.coup.model.*;

import java.util.*;

public class RandomRationalLegalAgent implements Agent {
    private final List<Card> values;

    public RandomRationalLegalAgent(List<Card> values) {
        this.values = values;
    }

    @Override
    public Card selectCardToSacrafice(Board board, Player player) {
        List<Card> hand = player.getOptions(new ArrayList<Card>(2));
        if (hand.size() == 1) {
            return hand.get(0);
        } else if (hand.size() == 2){
            int firstCardIndex = values.indexOf(hand.get(0));
            int lastCardIndex = values.indexOf(hand.get(1));
            if (firstCardIndex < lastCardIndex) {
                return hand.get(1);
            } else {
                return hand.get(0);
            }
        } else {
            throw new IllegalStateException(String.format("Why am I being called with a hand of: %1$s", hand));
        }
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
        List<Card> result = new ArrayList<>();
        Map<Integer,Set<Card>> valuedOptions = new TreeMap<>();
        for (Card option : options) {
            int value = values.indexOf(option);
            if (valuedOptions.containsKey(value)) {
                valuedOptions.get(value).add(option);
            } else {
                Set<Card> set = new HashSet<>();
                set.add(option);
                valuedOptions.put(value, set);
            }
        }
        for (Map.Entry<Integer,Set<Card>> entry : valuedOptions.entrySet()) {
            for (Card card : entry.getValue()) {
                if (result.size() >= cards) {
                    return result;
                } else {
                    result.add(card);
                }
            }
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
