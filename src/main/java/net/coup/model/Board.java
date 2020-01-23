package net.coup.model;

import java.util.*;

public class Board {
    private static final List<Card> CARDS = Arrays.asList(
            Card.AMBASSADOR,   Card.AMBASSADOR,   Card.AMBASSADOR,
            Card.ASSASSIN,     Card.ASSASSIN,     Card.ASSASSIN,
            Card.CAPTAIN,      Card.CAPTAIN,      Card.CAPTAIN,
            Card.CONTESSA,     Card.CONTESSA,     Card.CONTESSA,
            Card.DUKE,         Card.DUKE,         Card.DUKE
    );
    private Map<String, Player> players;
    private LinkedList<Card> court;

    Board(Map<String, Player> players, LinkedList<Card> court) {
        this.players = players;
        this.court = court;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(court);
    }

    public List<Card> getExchangeCards() {
        List<Card> exchange = new ArrayList<>(2);
        exchange.add(court.pop());
        exchange.add(court.pop());
        return exchange;
    }

    public Board returnExchangeCards(List<Card> returnCards) {
        court.addAll(returnCards);
        Collections.shuffle(court);
        return this;
    }

    public static Board newGame(List<String> names) {
        LinkedList<Card> deck = new LinkedList<>();
        deck.addAll(CARDS);
        Collections.shuffle(deck);
        Map<String, Player> players = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            List<Card> playerCards = new ArrayList<>(Constants.HAND_SIZE);
            List<Card> publicCards = new ArrayList<>(Constants.HAND_SIZE);
            for (int j = 0; j < Constants.HAND_SIZE; j++) {
                playerCards.add(deck.pop());
            }
            players.put(name, new Player(playerCards, publicCards, name, Constants.STARTING_COINS));
        }
        return new Board(players, deck);
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            message.append("Player (").append(entry.getKey()).append("):\n\t").append(entry.getValue()).append("\n");
        }
        StringBuilder deck = new StringBuilder();
        for (Card card : court) {
            deck.append("\t").append(card).append("\n");
        }
        return String.format("Board:\n%1$s\nDeck:\n%2$s", message, deck);
    }

    public Board replacePlayer(Player player) {
        Map<String, Player> newPlayerMap = new HashMap<>();
        newPlayerMap.putAll(players);
        newPlayerMap.put(player.getName(), player);
        return new Board(newPlayerMap, court);
    }

    public boolean isGameOver() {
        int deadPlayers = 0;
        for (Player player : players.values()) {
            if (!player.isAlive()) {
                deadPlayers++;
            }
        }
        return deadPlayers >= players.size() - 1;
    }

    public List<Player> getOtherPlayers(String name) {
        List<Player> others = new ArrayList<>();
        for (Map.Entry<String,Player> entry : players.entrySet()) {
            if (entry.getKey().equals(name)) {
                continue;
            }
            others.add(entry.getValue());
        }
        return others;
    }
}
