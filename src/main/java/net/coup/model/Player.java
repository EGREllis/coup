package net.coup.model;

import java.util.LinkedList;
import java.util.List;

public class Player {
    private List<Card> privateCards;
    private List<Card> publicCards;
    private String name;
    private int coins;

    public Player(List<Card> cards, List<Card> publicCards, String name, int coins) {
        this.privateCards = cards;
        this.publicCards = publicCards;
        this.name = name;
        this.coins = coins;
    }

    public Player setCoins(int newCoins) {
        return new Player(privateCards, publicCards, name, newCoins);
    }

    public Player setHand(List<Card> cards) {
        return new Player(cards, publicCards, name, coins);
    }

    public Player removeCardFromHand(Card card) {
        List<Card> newPublicCards = new LinkedList<>();
        newPublicCards.addAll(publicCards);
        newPublicCards.add(card);
        List<Card> newPrivateCards = new LinkedList<>();
        newPrivateCards.addAll(privateCards);
        newPrivateCards.remove(card);
        return new Player(newPrivateCards, newPublicCards, name, coins);
    }

    public List<Card> getOptions(List<Card> cards) {
        cards.addAll(this.privateCards);
        return cards;
    }

    public String getName() {
        return name;
    }

    public int getLife() {
        return privateCards.size();
    }

    public int getCoins() {
        return coins;
    }

    public List<Card> getPublicCards() {
        return publicCards;
    }

    public String toString() {
        return String.format("Name: %1$s Coins: %2$d Card: %3$s, %4$s", name, coins, privateCards.size() >= 1 ? privateCards.get(0) : "", privateCards.size() >= 2 ? privateCards.get(1) : "");
    }
}
