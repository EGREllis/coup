package net.coup.model;

import java.util.*;

public enum Action {
    INCOME(0, 1, null, Constants.EMPTY_CARD_SET),
    FOREIGN_AID(0, 2, null, Collections.singleton(Card.DUKE)),
    TAX(0, 3, Card.DUKE, Constants.EMPTY_CARD_SET),
    COUP(7, 0, null, Constants.EMPTY_CARD_SET),
    STEAL(0, 2, Card.CAPTAIN, asSet(Card.CAPTAIN, Card.AMBASSADOR)),
    EXCHANGE(0, 0, Card.AMBASSADOR, asSet()),
    ASSASSINATE(3, 0, Card.ASSASSIN, Collections.singleton(Card.CONTESSA));

    private static Set<Card> asSet(Card... cards) {
        Set<Card> result = new HashSet<>();
        for (Card card : cards) {
            result.add(card);
        }
        return Collections.unmodifiableSet(result);
    }

    private int cost;
    private int income;
    private Card implies;
    private Set<Card> blocks;

    Action(int cost, int income, Card implies, Set<Card> blocks) {
        this.cost = cost;
        this.income = income;
        this.implies = implies;
        this.blocks = blocks;
    }

    public int getCost() {
        return cost;
    }

    public int getIncome() {
        return income;
    }

    public Card getImplies() {
        return implies;
    }

    public Set<Card> getBlocks() {
        return blocks;
    }
}